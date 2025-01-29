package com.gabrielribeiro.TesteTecnicoItau.repositories

import com.gabrielribeiro.TesteTecnicoItau.models.entities.TransactionEntity
import java.time.OffsetDateTime

class TransactionRepository {
    val transactionsEntity = mutableListOf<TransactionEntity>()

    fun saveTransaction(transactionEntity: TransactionEntity) {
        transactionsEntity.add(transactionEntity)
    }

    fun deleteTransactions() {
        transactionsEntity.clear()
    }

    fun getByTime(limit: OffsetDateTime): List<TransactionEntity> {
        return transactionsEntity.filter { it.eventDateTime.isAfter(limit) }
    }
}