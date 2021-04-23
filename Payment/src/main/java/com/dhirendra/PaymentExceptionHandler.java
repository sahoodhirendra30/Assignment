package com.dhirendra;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.dhirendra.exception.PaymentError;
import com.dhirendra.exception.PaymentException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class PaymentExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		log.debug("handleHttpRequestMethodNotSupported :: There was an exception", ex);
		final StringBuilder message = new StringBuilder();
		message.append(ex.getMethod());
		message.append(" HTTP Request method is not supported for this request. Supported methods are:");
		ex.getSupportedHttpMethods().forEach(supportedMethod -> message.append(supportedMethod + " "));

		return new ResponseEntity<Object>(new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED);
	}

	/**
	 * Fall-back handler
	 * 
	 * @param ex
	 * @param request
	 * @return
	 */

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
		PaymentError paymentError = new PaymentError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(),
				"error occurred");
		return new ResponseEntity<Object>(paymentError, new HttpHeaders(), paymentError.getStatus());
	}

	/**
	 * Handle All generic exception thrown by the application
	 *
	 * @param exception - Generic Exception thrown
	 * @param request   - the WebRequest
	 * @return {@link ResponseEntity} - the ResResponseEntity
	 */
	@ExceptionHandler({ PaymentException.class })
	public ResponseEntity<Object> handleAllException(Exception exception) {
		log.warn("handleAllException :: There was an unknow exception", exception);
		log.debug("handleAllException :: There was an unknow exception");
		Error error = new Error("There was some unknown exception");
		return new ResponseEntity<Object>(error, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler({ NullPointerException.class })
	public ResponseEntity<Object> handleNullException(Exception exception) {
		log.warn("handleAllException :: There was an unknow exception", exception);
		log.debug("handleAllException :: There was an unknow exception");
		Error error = new Error("There was some unknown exception");
		return new ResponseEntity<Object>(error, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 
	 * @param ex
	 * @param request
	 * @return
	 */

	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
		List<String> errors = new ArrayList<String>();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": "
					+ violation.getMessage());
		}

		PaymentError paymentError = new PaymentError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
		return new ResponseEntity<Object>(paymentError, new HttpHeaders(), paymentError.getStatus());
	}

	/**
	 * 
	 * request missing parameter
	 */
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String error = ex.getParameterName() + " parameter is missing";

		PaymentError paymentError = new PaymentError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
		return new ResponseEntity<Object>(paymentError, new HttpHeaders(), paymentError.getStatus());
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<String> errors = new ArrayList<String>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getField() + ": " + error.getDefaultMessage());
		}
		for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
		}

		PaymentError paymentError = new PaymentError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
		return handleExceptionInternal(ex, paymentError, headers, paymentError.getStatus(), request);
	}

	/**
	 * method argument is not the expected type
	 * 
	 * @param ex
	 * @param request
	 * @return
	 */

	@ExceptionHandler({ MethodArgumentTypeMismatchException.class })
	public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
			WebRequest request) {
		String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();

		PaymentError paymentError = new PaymentError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
		return new ResponseEntity<Object>(paymentError, new HttpHeaders(), paymentError.getStatus());
	}

}
