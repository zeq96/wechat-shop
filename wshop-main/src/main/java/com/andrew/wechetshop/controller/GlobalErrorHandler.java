package com.andrew.wechetshop.controller;

import com.andrew.wechetshop.entity.Response;
import com.andrew.wechetshop.api.exception.HttpException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class GlobalErrorHandler {
    @ExceptionHandler(HttpException.class)
    public Response<?> onError(HttpServletResponse response, HttpException e) {
        response.setStatus(e.getStatusCode());
        return Response.of(e.getMessage(), null);
    }
}
