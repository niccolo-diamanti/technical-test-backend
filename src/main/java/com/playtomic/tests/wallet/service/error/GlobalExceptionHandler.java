package com.playtomic.tests.wallet.service.error;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler implements ProblemHandling {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Problem.class))})
    public ResponseEntity<Problem> handleDefaultException(final Exception exception, final NativeWebRequest request) {
        return create(INTERNAL_SERVER_ERROR, exception, request);
    }

    @Override
    public boolean isCausalChainsEnabled() {
        return true;
    }
}
