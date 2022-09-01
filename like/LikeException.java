package com.example.login.like;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LikeException {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseDto<T>
}



public class CustomExceptionHandler {


    public com.example.intermediate.controller.response.ResponseDto<?> handleValidationExceptions(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        return com.example.intermediate.controller.response.ResponseDto.fail("BAD_REQUEST", errorMessage);
    }

}