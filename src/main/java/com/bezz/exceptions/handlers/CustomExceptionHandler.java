package com.bezz.exceptions.handlers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.bezz.dtos.responses.ErrorsResponse;
import com.bezz.exceptions.UserNotFoundException;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger LOG = LoggerFactory.getLogger(CustomExceptionHandler.class);

	@Override
	protected ResponseEntity<Object> handleBindException(BindException exception,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		LOG.error(request.getSessionId(), exception);
		List<String> errors = exception.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
		return ResponseEntity.status(status).headers(headers).body(new ErrorsResponse(errors));
	}
	
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception exception,
			Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		LOG.error(request.getSessionId(), exception);
 		ErrorsResponse errorResponse = new ErrorsResponse(Collections.singletonList(exception.getMessage()));
		return ResponseEntity.status(status).headers(headers).body(errorResponse);
	}

	@ExceptionHandler({ UserNotFoundException.class })
	protected ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException userNotFoundException, WebRequest request) {
		return handleExceptionInternal(userNotFoundException, null, null, HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler({ Exception.class })
	protected ResponseEntity<Object> handleAnyException(Exception exception, WebRequest request) {
		return handleExceptionInternal(exception, null, null, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}
}
