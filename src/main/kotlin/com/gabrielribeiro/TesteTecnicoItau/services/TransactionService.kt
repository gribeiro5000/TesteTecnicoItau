package com.gabrielribeiro.TesteTecnicoItau.services

import com.gabrielribeiro.TesteTecnicoItau.models.entities.TransactionEntity
import com.gabrielribeiro.TesteTecnicoItau.models.requests.TransactionRequest
import com.gabrielribeiro.TesteTecnicoItau.repositories.TransactionRepository
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.math.BigDecimal
import java.time.OffsetDateTime
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
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
            }
            logger.info("transaction with id ${transactionEntity.id} is valid")

            transactionRepository.saveTransaction(transactionEntity)
            logger.info("transaction with id ${transactionEntity.id} was saved successfully")

            return ResponseEntity.status(HttpStatus.CREATED).build()
        } catch (e: Exception) {
            logger.error("ERROR: " + e.toString())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    private fun convertTransactionToEntity(
        transactionRequest: TransactionRequest
    ): TransactionEntity {
        val transaction = TransactionEntity(
            id = UUID.randomUUID(),
            amount = transactionRequest.amount,
            dateTime = OffsetDateTime.parse(transactionRequest.dateTime)
        )
        return transaction
    }

    private fun transactionValidator(
        transactionEntity: TransactionEntity
    ): Boolean {
        var isValidTransaction: Boolean = true
        val minAmount = BigDecimal("0.00")
        val dateTimeNow = OffsetDateTime.now()

        if(transactionEntity.amount < minAmount) {
            isValidTransaction = false
        }
        if(transactionEntity.dateTime.isAfter(dateTimeNow)) {
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
}