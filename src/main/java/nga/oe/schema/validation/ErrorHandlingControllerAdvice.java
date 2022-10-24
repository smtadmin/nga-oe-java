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
import com.siliconmtn.io.api.EndpointResponse;
import com.siliconmtn.io.api.validation.ValidationErrorDTO;
import com.siliconmtn.io.api.validation.ValidationErrorDTO.ValidationError;

import nga.oe.schema.exception.AppSchemaException;
import nga.oe.schema.exception.UnexpectedException;

/**
 * <b>Title:</b> ErrorHandlingControllerAdvice.java <b>Project:</b>
 * Notifications MicroService <b>Description:</b> Advice for RESTControllers on
 * how to handle Exceptions that are thrown gracefully into a common format.
 *
 * <b>Copyright:</b> 2022 <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author raptor
 * @version 1.0
 * @since Jul 14, 2022
 * @updates
 *
 */
@ControllerAdvice
public class ErrorHandlingControllerAdvice {

	/**
	 * Convert ConstraintViolationException thrown during DTO Validations
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	EndpointResponse onConstraintValidationException(ConstraintViolationException e) {
		EndpointResponse response = new EndpointResponse(HttpStatus.BAD_REQUEST, e);
		for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			response.addFailedValidation(ValidationErrorDTO.builder().elementId(violation.getPropertyPath().toString()).value(violation.getMessage()).validationError(ValidationError.PARSE).build());
		}
		return response;
	}

	/**
	 * Convert MethodArgumentNotValidException thrown during method level argument
	 * Validations
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	EndpointResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		EndpointResponse response = new EndpointResponse(HttpStatus.BAD_REQUEST);
		for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
			response.addFailedValidation(ValidationErrorDTO.builder().elementId(fieldError.getField()).value(fieldError.getDefaultMessage()).validationError(ValidationError.PARSE).build());
		}
		return response;
	}

	/**
	 * Convert AppSchemaException thrown during internal processing/validation of
	 * json data against a schema.
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(AppSchemaException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	EndpointResponse onAppSchemaException(AppSchemaException e) {
		EndpointResponse response = new EndpointResponse(HttpStatus.BAD_REQUEST, e);
		for (ValidationMessage msg : e.getIssues()) {
			response.addFailedValidation(ValidationErrorDTO.builder().elementId(msg.getPath()).value(msg.getMessage()).validationError(ValidationError.PARSE).build());
		}
		return response;
	}

	/**
	 * Convert UnexpectedException thrown during internal processing/validation of
	 * json data against a schema.
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(UnexpectedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	EndpointResponse onUnexpectedException(UnexpectedException e) {
		return new EndpointResponse(HttpStatus.BAD_REQUEST, e);
	}
}