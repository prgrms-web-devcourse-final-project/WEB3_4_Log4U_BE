package com.example.log4u.common.exception;

import java.util.List;

import org.springframework.validation.FieldError;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ApiErrorResponse {
	private final String errorMessage;
	private final int errorCode;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private final List<ValidationError> errors;

	public record ValidationError(String field, String message) {

		public static ValidationError of(final FieldError fieldError) {
			return new ValidationError(fieldError.getField(), fieldError.getDefaultMessage());
		}
	}
}
