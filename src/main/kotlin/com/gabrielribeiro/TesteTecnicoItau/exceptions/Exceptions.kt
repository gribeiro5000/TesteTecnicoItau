package com.gabrielribeiro.TesteTecnicoItau.exceptions

class UnprocessableEntityException(
    message: String?
) : Exception(message)

class BadRequestException(
    message: String?
) : Exception(message)