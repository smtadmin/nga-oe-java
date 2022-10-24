package nga.oe.schema.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.networknt.schema.ValidationMessage;
import com.siliconmtn.io.api.EndpointResponse;

import nga.oe.schema.exception.AppSchemaException;
import nga.oe.schema.exception.UnexpectedException;
import nga.oe.schema.vo.RequestDTO;

/**
 * <b>Title:</b> ErrorHandlingControllerAdviceTest.java <b>Project:</b>
 * Notifications MicroService <b>Description:</b> Unit Tests providing coverage
 * for the ErrorHandlingControllerAdvice Class
 *
 * <b>Copyright:</b> 2022 <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author raptor
 * @version 1.0
 * @since Jul 14, 2022
 * @updates
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ErrorHandlingControllerAdvice.class })
class ErrorHandlingControllerAdviceTest {

	@Autowired
	ErrorHandlingControllerAdvice advice;

	@SuppressWarnings("unchecked")
	@Test
	void onConstraintValidationExceptionTest() {
		Set<ConstraintViolation<RequestDTO>> violations = new HashSet<>();
		ConstraintViolation<RequestDTO> v = Mockito.mock(ConstraintViolation.class);
		Path p = Mockito.mock(Path.class);
		Mockito.when(v.getPropertyPath()).thenReturn(p);
		Mockito.when(p.toString()).thenReturn("Error");
		Mockito.when(v.getMessage()).thenReturn("MessageError");
		violations.add(v);

		RequestDTO dto = new RequestDTO();
		Validator validator = Mockito.mock(Validator.class);
		Mockito.when(validator.validate(dto)).thenReturn(violations);
		Set<ConstraintViolation<RequestDTO>> errs = validator.validate(dto);

		EndpointResponse error = advice.onConstraintValidationException(new ConstraintViolationException(errs));
		assertEquals(1, error.getFailedValidations().size());
	}

	@Test
	void onMethodArgumentNotValidExceptionTest() {
		List<FieldError> errs = new ArrayList<>();
		errs.add(new FieldError(RequestDTO.class.getSimpleName(), "submissionDate", "SubmissionDate Cannot be null"));
		errs.add(new FieldError(RequestDTO.class.getSimpleName(), "userId", "UserId invalid UUID"));
		BindingResult br = Mockito.mock(BindingResult.class);
		Mockito.when(br.getFieldErrors()).thenReturn(errs);

		EndpointResponse error = advice
				.onMethodArgumentNotValidException(new MethodArgumentNotValidException(null, br));
		assertEquals(2, error.getFailedValidations().size());
	}

	@Test
	void onAppSchemaExceptionTest() {
		Set<ValidationMessage> issues = new HashSet<>();
		issues.add(new ValidationMessage.Builder().path("#/required")
				.customMessage("Required properties are missing from object: extendedData.").build());

		EndpointResponse error = advice.onAppSchemaException(new AppSchemaException(issues));
		assertEquals(1, error.getFailedValidations().size());
		assertEquals(error.getFailedValidations().get(0).getElementId(), issues.iterator().next().getPath());
		assertEquals(error.getFailedValidations().get(0).getErrorMessage(), issues.iterator().next().getMessage());

	}

	@Test
	void onUnexpectedExceptionTest() {
		EndpointResponse error = advice
				.onUnexpectedException(new UnexpectedException("Unexpected", new Exception("Error")));
		assertEquals("Unexpected", error.getDebugMessage());
	}
}
