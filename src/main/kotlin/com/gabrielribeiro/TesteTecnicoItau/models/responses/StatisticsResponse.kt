package com.gabrielribeiro.TesteTecnicoItau.models.responses

import java.math.BigDecimal

class StatisticsResponse(
    var count: Int,
    var sum: BigDecimal,
    var avg: BigDecimal,
    var min: BigDecimal?,
    var max: BigDecimal?
)