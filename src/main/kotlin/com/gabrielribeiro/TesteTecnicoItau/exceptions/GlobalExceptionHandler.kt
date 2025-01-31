package com.gabrielribeiro.TesteTecnicoItau.exceptions

import com.gabrielribeiro.TesteTecnicoItau.models.responses.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = "Internal Server Error: ${ex.message}"
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(UnprocessableEntityException::class)
    fun handleUnprocessableEntityException(
        ex: UnprocessableEntityException
    ): ResponseEntity<ErrorResponse> {
       val errorResponse = ErrorResponse(
           status = HttpStatus.UNPROCESSABLE_ENTITY.value(),
           message = ex.message ?: "One of the values are not allowed"
       )
        return ResponseEntity(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(
        ex: BadRequestException
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            message = ex.message ?: "Invalid Request"
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
}