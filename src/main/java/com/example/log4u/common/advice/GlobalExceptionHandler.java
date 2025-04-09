package com.example.log4u.common.advice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.log4u.common.exception.ApiErrorResponse;
import com.example.log4u.common.exception.CommonErrorCode;
import com.example.log4u.common.exception.base.ErrorCode;
import com.example.log4u.common.exception.base.ServiceException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	public ResponseEntity<Object> handleMethodArgumentNotValid(
		MethodArgumentNotValidException e,
		@NonNull HttpHeaders headers,
		@NonNull HttpStatusCode status,
		@NonNull WebRequest request) {
		HttpServletRequest servletRequest = ((ServletWebRequest)request).getRequest();

		String requestUrl = servletRequest.getRequestURI();
		String httpMethod = servletRequest.getMethod();
		List<String> errors = e.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
			.collect(Collectors.toList());

		log.warn("Validation failed for request to {} {}. Errors: {}",
			httpMethod, requestUrl, errors);
		CommonErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
		return handleExceptionInternal(e, errorCode);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
		String location = getExceptionLocation(e);

		log.warn("Illegal argument encountered at {}: {}", location, e.getMessage());

		CommonErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<ApiErrorResponse> handleGiveMeTiConException(ServiceException e) {
		String location = getExceptionLocation(e);
		log.warn("Error invoke in our app at {}: {} ErrorCode: {}", location, e.getMessage(), e.getErrorCode());
		ErrorCode errorCode = e.getErrorCode();
		return handleExceptionInternal(errorCode);
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<ApiErrorResponse> handleAllException(Exception e) {
		String location = getExceptionLocation(e);
		log.warn("Unhandled exception occurred at {}: {}", location, e.getMessage());

		CommonErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
		return handleExceptionInternal(errorCode);
	}

	private ResponseEntity<ApiErrorResponse> handleExceptionInternal(ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(makeErrorResponse(errorCode));
	}

	private ApiErrorResponse makeErrorResponse(ErrorCode errorCode) {
		return ApiErrorResponse.builder()
			.errorMessage(errorCode.getErrorMessage())
			.errorCode(errorCode.getHttpStatus().value())
			.build();
	}

	private ResponseEntity<Object> handleExceptionInternal(BindException e, ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(makeErrorResponse(e, errorCode));
	}

	private ApiErrorResponse makeErrorResponse(BindException e, ErrorCode errorCode) {
		List<ApiErrorResponse.ValidationError> validationErrorList = new ArrayList<>();
		for (FieldError fieldError : e.getBindingResult()
			.getFieldErrors()) {
			ApiErrorResponse.ValidationError validationError = ApiErrorResponse.ValidationError.of(fieldError);
			validationErrorList.add(validationError);
		}

		return ApiErrorResponse.builder()
			.errorMessage(errorCode.getErrorMessage())
			.errorCode(errorCode.getHttpStatus().value())
			.errors(validationErrorList)
			.build();
	}

	private String getExceptionLocation(Exception e) {
		StackTraceElement element = e.getStackTrace()[0];
		return element.getClassName() + "." + element.getMethodName() + ":" + element.getLineNumber();
	}
}
