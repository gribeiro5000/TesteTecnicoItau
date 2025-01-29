package com.gabrielribeiro.TesteTecnicoItau.models.entities

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

class TransactionEntity(
    val id: UUID,
    val amount: BigDecimal,
    val transactionDateTime: OffsetDateTime,
    val eventDateTime: OffsetDateTime
)