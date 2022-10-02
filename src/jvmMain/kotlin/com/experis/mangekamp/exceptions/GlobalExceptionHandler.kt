package com.experis.mangekamp.exceptions

import javax.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException::class, InvalidPropertyException::class)
    fun handleResourceNotFoundException(request: HttpServletRequest?, exception: Exception): GenericExceptionResponse {
        return when (exception) {
            is ResourceNotFoundException -> GenericExceptionResponse(exception.message ?: "null")
            is InvalidPropertyException -> GenericExceptionResponse(exception.message ?: "null")
            else -> GenericExceptionResponse(exception.message ?: "null")
        }
    }
}

class GenericExceptionResponse(val message: String)
class ResourceNotFoundException(message: String) : Exception(message)
class InvalidPropertyException(message: String) : Exception(message)
