package me.rhysxia.explore.server.controller

import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@ControllerAdvice
@RestController
class ExceptionController {

  @ExceptionHandler(MethodArgumentNotValidException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun methodArgumentNotValidException(e: MethodArgumentNotValidException): LinkedHashMap<String, String?> {
    val errors = e.allErrors
    val map = LinkedHashMap<String, String?>()
    errors.forEach {
      val msg = it.defaultMessage
      when {
        (it is FieldError) -> {
          val field = it.field
          map[field] = msg
        }
      }
    }
    return map
  }
}