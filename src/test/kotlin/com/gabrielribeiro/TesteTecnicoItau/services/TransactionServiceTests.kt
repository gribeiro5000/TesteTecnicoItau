package com.gabrielribeiro.TesteTecnicoItau.services

import com.gabrielribeiro.TesteTecnicoItau.exceptions.BadRequestException
import com.gabrielribeiro.TesteTecnicoItau.models.entities.TransactionEntity
import com.gabrielribeiro.TesteTecnicoItau.models.requests.TransactionRequest
import com.gabrielribeiro.TesteTecnicoItau.repositories.TransactionRepository
import io.mockk.*
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.lang.reflect.InvocationTargetException
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*
import kotlin.math.exp
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible
import kotlin.test.assertEquals
import kotlin.test.expect

class TransactionServiceTests {
    private val transactionService = spyk(TransactionService(), recordPrivateCalls = true)
    private val transactionRepository = mockk<TransactionRepository>()

    //Testes createTransaction
    @Test
    fun `Should return 200 when transaction created successfully`() {
        val mockedTransactionRequest = TransactionRequest(
            amount = BigDecimal(200),
            dateTime = "2020-08-07T12:34:56.789-03:00"
        )
        val mockedTransactionEntity = TransactionEntity(
            id = UUID.randomUUID(),
            amount = mockedTransactionRequest.amount,
            transactionDateTime = OffsetDateTime.parse(mockedTransactionRequest.dateTime),
            eventDateTime = OffsetDateTime.now()
        )
        val expected = ResponseEntity.status(HttpStatus.CREATED).build<Void>()

        every {
            transactionService invoke
                    "convertTransactionToEntity" withArguments listOf(mockedTransactionRequest)
        } returns mockedTransactionEntity
        every {
            transactionService invoke
                    "transactionValidator" withArguments listOf(mockedTransactionEntity)
        } returns true
        every { transactionRepository.saveTransaction(mockedTransactionEntity) } just runs


        val result = transactionService.createTransaction(mockedTransactionRequest)
        assertEquals(expected, result)
    }

    @Test
    fun `Should return 400 when convert transaction fail`() {
        val mockedTransactionRequest = TransactionRequest(
            amount = BigDecimal(200),
            dateTime = "20-07T12:34:56.789-03:00"
        )
        val expected = ResponseEntity.status(HttpStatus.BAD_REQUEST).build<Void>()

        every {
            transactionService invoke
                    "convertTransactionToEntity" withArguments listOf(mockedTransactionRequest)
        } throws BadRequestException("Invalid Request")

        val result = transactionService.createTransaction(mockedTransactionRequest)
        assertEquals(expected, result)
    }

    @Test
    fun `Should return 422 when transaction validator fail`() {
        val mockedTransactionRequest = TransactionRequest(
            amount = BigDecimal("200"),
            dateTime = "2010-01-07T12:34:56.789-03:00"
        )
        val mockedTransactionEntity = TransactionEntity(
            id = UUID.randomUUID(),
            amount = mockedTransactionRequest.amount,
            transactionDateTime = OffsetDateTime.parse(mockedTransactionRequest.dateTime),
            eventDateTime = OffsetDateTime.now()
        )
        val expected = ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build<Void>()

        every {
            transactionService invoke "convertTransactionToEntity" withArguments
                    listOf(mockedTransactionRequest)
        } returns mockedTransactionEntity
        every {
            transactionService invoke "transactionValidator" withArguments
                    listOf(mockedTransactionEntity)
        } returns false

        val result = transactionService.createTransaction(mockedTransactionRequest)
        assertEquals(expected, result)
    }



    //testes ConvertTransactionToEntity
    @Test
    fun `Should return transaction converted when event was sent in correct format`() {
        val mockedTransactionRequest = TransactionRequest(
            amount = BigDecimal("200"),
            dateTime = "2020-02-01T12:13:45.000-03:00"
        )
        mockkStatic(UUID::class)
        val mockedUUID = UUID.randomUUID()
        every { UUID.randomUUID() } returns mockedUUID
        mockkStatic(OffsetDateTime::class)
        val mockedDateTime = OffsetDateTime.now()
        every { OffsetDateTime.now() } returns mockedDateTime

        val expected = TransactionEntity(
            id = mockedUUID,
            amount = mockedTransactionRequest.amount,
            transactionDateTime = OffsetDateTime.parse(mockedTransactionRequest.dateTime),
            eventDateTime = mockedDateTime
        )

        val privateFun = transactionService::class.declaredFunctions.find {
            it.name == "convertTransactionToEntity"
        }!!
        privateFun.isAccessible = true

        val result = privateFun.call(
            transactionService,
            mockedTransactionRequest
            ) as TransactionEntity

        assertEquals(expected.id, result.id)
        assertEquals(expected.amount, result.amount)
        assertEquals(expected.transactionDateTime, result.transactionDateTime)
        assertEquals(expected.eventDateTime, result.eventDateTime)
    }

    @Test
    fun `Should throw exception when event was sent in incorrect format`() {
        val mockedTransactionRequest = TransactionRequest(
            amount = BigDecimal("200"),
            dateTime = "wrongDateTime"
        )

        val privateFun = transactionService::class.declaredFunctions.find {
            it.name == "convertTransactionToEntity"
        }!!
        privateFun.isAccessible = true

        val exception = assertThrows<InvocationTargetException> {
            privateFun.call(
                transactionService,
                mockedTransactionRequest
            )
        }
        val actualException = exception.cause
        assert(actualException is BadRequestException)
        assert(actualException?.message == "Invalid Request")
    }



    //Testes transactionValidator
    @Test
    fun `Should return true when amount more or equal zero and transactionDateTime is before eventDateTime`() {
        val mockedTransactionEntity = TransactionEntity(
            id = UUID.randomUUID(),
            amount = BigDecimal("200"),
            transactionDateTime = OffsetDateTime.parse("2020-08-07T12:34:56.789-03:00"),
            eventDateTime = OffsetDateTime.now()
        )
        val expected = true

        val privateFun = transactionService::class.declaredFunctions.find {
            it.name == "transactionValidator"
        }!!
        privateFun.isAccessible = true

        val result = privateFun.call(
            transactionService,
            mockedTransactionEntity
        ) as Boolean

        assertEquals(expected, result)
    }

    @Test
    fun `Should return false when transactionDateTime is after eventDateTime`() {
        val mockedTransactionEntity = TransactionEntity(
            id = UUID.randomUUID(),
            amount = BigDecimal("200"),
            transactionDateTime = OffsetDateTime.parse("2020-08-07T12:34:56.789-03:00"),
            eventDateTime = OffsetDateTime.parse("2019-08-07T12:34:56.789-03:00")
        )
        val expected = false

        val privateFun = transactionService::class.declaredFunctions.find {
            it.name == "transactionValidator"
        }!!
        privateFun.isAccessible = true

        val result = privateFun.call(
            transactionService,
            mockedTransactionEntity
        ) as Boolean

        assertEquals(expected, result)
    }

    @Test
    fun `Should return false when amount is less then zero`() {
        val mockedTransactionEntity = TransactionEntity(
            id = UUID.randomUUID(),
            amount = BigDecimal("-200"),
            transactionDateTime = OffsetDateTime.parse("2020-01-02T12:13:45.000-03:00"),
            eventDateTime = OffsetDateTime.now()
        )
        val expected = false

        val privateFun = transactionService::class.declaredFunctions.find {
            it.name == "transactionValidator"
        }!!
        privateFun.isAccessible = true

        val result = privateFun.call(
            transactionService,
            mockedTransactionEntity
        )

        assertEquals(expected, result)
    }



    //Testes deleteTransactions
    @Test
    fun `Should return 200 when delete transaction was called`() {
        every { transactionRepository.deleteTransactions() } just runs
        val expected = ResponseEntity.status(HttpStatus.OK).build<Void>()
        val result = transactionService.deleteTransactions()

        assertEquals(expected, result)
    }



    //Testes getStatistics
    @Test
    fun `Should return statistics when repository has transactions in last 60 seconds`() {

    }

    @Test
    fun `Should return statistics with zero when repository don't have events in last 60 seconds`() {

    }



    //Testes convertTimeToOffSetDateTime
    @Test
    fun `Should return last 120 seconds limit datetime`() {

    }



    //Testes calculateStatistics
    @Test
    fun `Should return statistics with values zero when a empty list send`() {

    }

    @Test
    fun `Should return statistics when a list send`() {

    }
}