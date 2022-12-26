package com.experis.mangekamp.exceptions

import javax.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException::class, InvalidPropertyException::class)
    fun handleResourceNotFoundException(request: HttpServletRequest?, exception: Exception): GenericExceptionResponse {
        return when (exception) {
            is ResourceNotFoundException -> GenericExceptionResponse(exception.message ?: "null")
            is InvalidPropertyException -> GenericExceptionResponse(exception.message ?: "null")
            else -> GenericExceptionResponse(exception.message ?: "null")
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(request: HttpServletRequest?, exception: BadRequestException) = GenericExceptionResponse(exception.message ?: "null")

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UnknownInternalServerError::class)
    fun handleUnknownInternalServerError(request: HttpServletRequest?, exception: UnknownInternalServerError) = GenericExceptionResponse(exception.message ?: "null", extras = exception.extras)
}

class GenericExceptionResponse(val message: String, val extras: Any? = null)
class ResourceNotFoundException(message: String) : Exception(message)
class InvalidPropertyException(message: String) : Exception(message)
class BadRequestException(message: String) : Exception(message)
class UnknownInternalServerError(message: String, val extras: Any? = null): Exception(message)
