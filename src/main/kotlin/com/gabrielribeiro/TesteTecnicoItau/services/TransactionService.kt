package com.gabrielribeiro.TesteTecnicoItau.services

import com.gabrielribeiro.TesteTecnicoItau.exceptions.BadRequestException
import com.gabrielribeiro.TesteTecnicoItau.exceptions.UnprocessableEntityException
import com.gabrielribeiro.TesteTecnicoItau.models.entities.TransactionEntity
import com.gabrielribeiro.TesteTecnicoItau.models.requests.TransactionRequest
import com.gabrielribeiro.TesteTecnicoItau.models.responses.ErrorResponse
import com.gabrielribeiro.TesteTecnicoItau.models.responses.StatisticsResponse
import com.gabrielribeiro.TesteTecnicoItau.repositories.TransactionRepository
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

class TransactionService {
    private val logger: Logger = LoggerFactory.getLogger(TransactionService::class.java)
    val transactionRepository: TransactionRepository = TransactionRepository()

    fun createTransaction(transactionRequest: TransactionRequest): ResponseEntity<Void> {
        logger.info("Starting process | create transaction")
        try {
            val transactionEntity = convertTransactionToEntity(transactionRequest)
            logger.info("transaction with id ${transactionEntity.id} was converted successfully")

            if(!transactionValidator(transactionEntity)) {
                throw UnprocessableEntityException("One of the values are not allowed")
            }
            logger.info("transaction with id ${transactionEntity.id} is valid")

            transactionRepository.saveTransaction(transactionEntity)
            logger.info("transaction with id ${transactionEntity.id} was saved successfully")

            return ResponseEntity.status(HttpStatus.CREATED).build()
        } catch (e: Exception) {
            logger.error(e.message)
            when (e) {
                is BadRequestException ->
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
                is UnprocessableEntityException ->
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
                else ->
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
            }
        }
    }

    private fun convertTransactionToEntity(
        transactionRequest: TransactionRequest
    ): TransactionEntity {
        try {
            val transaction = TransactionEntity(
                id = UUID.randomUUID(),
                amount = transactionRequest.amount,
                transactionDateTime = OffsetDateTime.parse(transactionRequest.dateTime),
                eventDateTime = OffsetDateTime.now()
            )
            return transaction
        } catch (ex: Exception) {
            throw BadRequestException("Invalid Request")
        }
    }

    private fun transactionValidator(
        transactionEntity: TransactionEntity
    ): Boolean {
        var isValidTransaction: Boolean = true
        val minAmount = BigDecimal("0.00")

        if(transactionEntity.amount < minAmount) {
            isValidTransaction = false
        }
        if(transactionEntity.transactionDateTime.isAfter(transactionEntity.eventDateTime)) {
            isValidTransaction = false
        }

        return isValidTransaction
    }

    fun deleteTransactions(): ResponseEntity<Void> {
        logger.info("Process starting | Delete transactions")
        transactionRepository.deleteTransactions()
        logger.info("All messages was deleted")
        return ResponseEntity.status(HttpStatus.OK).build()
    }

    fun getStatistics(time: Long): StatisticsResponse {
        val limit = convertTimeToOffSetDateTime(time)
        val transactions = transactionRepository.getByTime(limit)
        return calculateStatistics(transactions)
    }

    private fun convertTimeToOffSetDateTime(time: Long): OffsetDateTime {
        val now = OffsetDateTime.now()
        val limit = now.minus(time, ChronoUnit.SECONDS)
        return limit
    }

    private fun calculateStatistics(
        transactions: List<TransactionEntity>
    ): StatisticsResponse {
        var statistics = StatisticsResponse(
            count = 0,
            sum = BigDecimal("0"),
            avg = BigDecimal("0"),
            min = null,
            max = null
        )

        transactions.forEach {
            statistics.count = statistics.count.plus(1)
            statistics.sum = statistics.sum.plus(it.amount)
            statistics.avg = statistics.sum.divide(
                BigDecimal(statistics.count),
                2,
                RoundingMode.HALF_UP
            )
            if(statistics.min == null || it.amount < statistics.min) {
                statistics.min = it.amount
            }
            if(statistics.max == null || it.amount > statistics.max) {
                statistics.max = it.amount
            }
        }

        if (statistics.min == null) statistics.min = BigDecimal("0")
        if (statistics.max == null) statistics.max = BigDecimal("0")

        return statistics
    }
}