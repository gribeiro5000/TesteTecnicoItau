package com.gabrielribeiro.TesteTecnicoItau.models.requests

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class TransactionRequest(
    @JsonProperty("valor")
    val value: BigDecimal,
    @JsonProperty("dataHora")
    val dateTime: String
)