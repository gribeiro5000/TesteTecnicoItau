package com.gabrielribeiro.TesteTecnicoItau.models.responses

class ErrorResponse (
    val status: Int,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)