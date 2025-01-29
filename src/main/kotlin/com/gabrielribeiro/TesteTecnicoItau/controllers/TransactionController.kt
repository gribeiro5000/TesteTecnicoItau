package com.gabrielribeiro.TesteTecnicoItau.controllers

import com.gabrielribeiro.TesteTecnicoItau.models.requests.TransactionRequest
import com.gabrielribeiro.TesteTecnicoItau.models.responses.StatisticsResponse
import com.gabrielribeiro.TesteTecnicoItau.services.TransactionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class TransactionController {
    var transactionService: TransactionService = TransactionService()

    @PostMapping("/transacao")
    fun postTransaction(
        @RequestBody transactionRequest: TransactionRequest
    ): ResponseEntity<Void> {
        val response = transactionService.createTransaction(transactionRequest)
        return response
    }

    @DeleteMapping("/transacao")
    fun deleteTransaction(): ResponseEntity<Void> {
        val response = transactionService.deleteTransactions()
        return response
    }

    @GetMapping("/estatistica")
    fun getStatistic(
        @RequestParam time: Long
    ): StatisticsResponse {
        val statistics = transactionService.getStatistics(time)
        return statistics
    }
}