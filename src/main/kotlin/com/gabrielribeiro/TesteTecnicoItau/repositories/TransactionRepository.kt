package com.gabrielribeiro.TesteTecnicoItau.repositories

import com.gabrielribeiro.TesteTecnicoItau.models.entities.TransactionEntity

class TransactionRepository {
    val transactionsEntity = mutableListOf<TransactionEntity>()

    fun saveTransaction(transactionEntity: TransactionEntity) {
        transactionsEntity.add(transactionEntity)
    }

    fun deleteTransactions() {
        transactionsEntity.clear()
    }
}