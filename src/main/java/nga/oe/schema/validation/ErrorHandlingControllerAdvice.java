package nga.oe.schema.validation;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.networknt.schema.ValidationMessage;

import nga.oe.schema.exception.AppSchemaException;
import nga.oe.schema.vo.ValidationErrorResponse;
import nga.oe.schema.vo.Violation;

/**
 * <b>Title:</b> ErrorHandlingControllerAdvice.java
 * <b>Project:</b> Notifications MicroService
 * <b>Description:</b> Advice for RESTControllers on how to handle Exceptions
 * that are thrown gracefully into a common format.
 *
 * <b>Copyright:</b> 2022
 * <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author raptor
 * @version 1.0
 * @since Jul 14, 2022
 * @updates
 *
 */
@ControllerAdvice
class ErrorHandlingControllerAdvice {

	/**
	 * Convert ConstraintViolationException thrown during DTO Validations
	 * @param e
	 * @return
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
		ValidationErrorResponse error = new ValidationErrorResponse();
		for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			error.getViolations().add(new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
		}
		return error;
	}

	/**
	 * Convert MethodArgumentNotValidException thrown during method level argument Validations
	 * @param e
	 * @return
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		ValidationErrorResponse error = new ValidationErrorResponse();
		for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
			error.getViolations().add(new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
		}
		return error;
	}

	/**
	 * Convert AppSchemaException thrown during internal processing/validation of json data against a schema.
	 * @param e
	 * @return
	 */
	@ExceptionHandler(AppSchemaException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ValidationErrorResponse onAppSchemaException(AppSchemaException e) {
		ValidationErrorResponse error = new ValidationErrorResponse();
		for (ValidationMessage issue : e.getIssues()) {
			error.getViolations().add(new Violation(issue.getPath(), issue.getMessage()));
		}
		return error;
	}
}