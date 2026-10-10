package com.rentrix.rentrixserver.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	// ---------- Application exceptions ----------
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
		log.warn("ApiException: {} ({})", ex.getMessage(), ex.getStatus());
		return ResponseEntity
					 .status(ex.getStatus())
					 .body(new ErrorResponse(ex.getMessage(), ex.getStatus().value()));
	}
	
	// ---------- Validation errors ----------
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> fieldErrors = new HashMap<>();
		for (FieldError err : ex.getBindingResult().getFieldErrors()) {
			fieldErrors.put(err.getField(), err.getDefaultMessage());
		}
		String message = fieldErrors.values().stream().findFirst().orElse("Validation failed");
		log.error("Validation failed {} ", ex.getMessage());
		return ResponseEntity
					 .status(HttpStatus.BAD_REQUEST)
					 .body(new ErrorResponse(message, HttpStatus.BAD_REQUEST.value(), java.time.Instant.now(),
						 fieldErrors));
	}
	
	// ---------- Security ----------
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
		log.error("Invalid credentials {}", ex.getMessage());
		return ResponseEntity
					 .status(HttpStatus.UNAUTHORIZED)
					 .body(new ErrorResponse("Invalid credentials", HttpStatus.UNAUTHORIZED.value()));
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
		log.error("Access Denied {}", ex.getMessage());
		return ResponseEntity
					 .status(HttpStatus.FORBIDDEN)
					 .body(new ErrorResponse("Access denied", HttpStatus.FORBIDDEN.value()));
	}
	
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex) {
		return ResponseEntity
					 .status(HttpStatus.NOT_FOUND)
					 .body(new ErrorResponse("Not found: " + ex.getResourcePath(),
						 HttpStatus.NOT_FOUND.value()));
	}
	
	// ---------- Catch-all ----------
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
		log.error("Unhandled exception", ex);
		return ResponseEntity
					 .status(HttpStatus.INTERNAL_SERVER_ERROR)
					 .body(new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
	}
	
	// --------- File upload size ----------
	@ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponse> handleMaxSize(
		org.springframework.web.multipart.MaxUploadSizeExceededException ex) {
		return ResponseEntity
					 .status(HttpStatus.CONTENT_TOO_LARGE)
					 .body(new ErrorResponse("File too large (max 15 MB)", 413));
	}
	
}