package com.gabrielribeiro.TesteTecnicoItau.services

import com.gabrielribeiro.TesteTecnicoItau.models.entities.TransactionEntity
import com.gabrielribeiro.TesteTecnicoItau.models.requests.TransactionRequest
import com.gabrielribeiro.TesteTecnicoItau.repositories.TransactionRepository
import io.mockk.*
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*
import kotlin.test.assertEquals

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

    }

    @Test
    fun `Should return 422 when transaction validator fail`() {

    }



    //testes ConvertTransactionToEntity
    @Test
    fun `Should return transaction converted when event was sent in correct format`() {

    }

    @Test
    fun `Should throw exception when event was sent in incorrect format`() {

    }



    //Testes transactionValidator
    @Test
    fun `Should return true when amount more or equal zero and transactionDateTime is before eventDateTime`() {

    }

    @Test
    fun `Should return false when transactionDateTime is after eventDateTime`() {

    }

    @Test
    fun `Should return false when amount is less then zero`() {

    }



    //Testes deleteTransactions
    @Test
    fun `Should return 200 when delete transaction was called`() {

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