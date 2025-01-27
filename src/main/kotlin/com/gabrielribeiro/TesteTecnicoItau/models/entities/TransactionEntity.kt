package com.gabrielribeiro.TesteTecnicoItau.models.entities

import java.math.BigDecimal
import java.time.LocalDateTime

class TransactionEntity(
    val value: BigDecimal,
    val dateTime: LocalDateTime
)