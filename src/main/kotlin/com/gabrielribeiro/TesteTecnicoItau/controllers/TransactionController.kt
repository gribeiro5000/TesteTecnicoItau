package com.gabrielribeiro.TesteTecnicoItau.controllers

import com.gabrielribeiro.TesteTecnicoItau.models.requests.TransactionRequest
import com.gabrielribeiro.TesteTecnicoItau.services.TransactionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class TransactionController {
    var transactionService: TransactionService = TransactionService()

    @PostMapping("/transacao")
    fun postTransacao(
        @RequestBody transactionRequest: TransactionRequest
    ): ResponseEntity<Void> {
        val response = transactionService.createTransaction(transactionRequest)
        return response
    }
}