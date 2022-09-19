package nga.oe.schema.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.networknt.schema.ValidationMessage;

class AppSchemaExceptionTest {

	@Test
	void validateConstructorTest() {
		final String msg = "Test Message";
		Set<ValidationMessage> issues = new HashSet<>();
		issues.add(new ValidationMessage.Builder().customMessage("Test Issue").path("test").build());
		AppSchemaException ase = new AppSchemaException(msg, issues);

		assertEquals(msg, ase.getMessage());
		assertEquals(issues, ase.getIssues());
	}
}
