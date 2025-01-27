package com.gabrielribeiro.TesteTecnicoItau.services

import com.gabrielribeiro.TesteTecnicoItau.models.entities.TransactionEntity
import com.gabrielribeiro.TesteTecnicoItau.models.requests.TransactionRequest
import java.time.LocalDateTime

class TransactionService {
    fun createTransaction(transactionRequest: TransactionRequest): String {
        try {
            convertTransactionToEntity(transactionRequest)
            return "succcess"
        }
        catch (e: Exception) {
            throw Exception("erro")
        }
    }

    fun convertTransactionToEntity(transactionRequest: TransactionRequest): TransactionEntity {
        val transaction = TransactionEntity(
            value = transactionRequest.value,
            dateTime = LocalDateTime.parse(transactionRequest.dateTime)
        )
        print(transaction)
        return transaction
    }
}