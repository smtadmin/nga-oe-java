package nga.oe.schema;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;

import com.google.gson.JsonParseException;

class SchemaLoaderTest {

	@Test
	void getResourceAsJSONNull() {
		assertThrows(IllegalArgumentException.class, () -> SchemaLoader.getResourceAsJSON(null));
	}
	
	@Test
	void getResourceAsJSONEmpty() {
		assertThrows(IllegalArgumentException.class, () -> SchemaLoader.getResourceAsJSON(""));
	}
	
	@Test
	void getResourceAsJSONValid() {
		assertNotNull(SchemaLoader.getResourceAsJSON("machine_feedback_schema.json"));
	}
	
	@Test
	void getResourceAsJSONNotExists() {
		assertNull(SchemaLoader.getResourceAsJSON("machine_feedback_schema_missing.json"));
	}
	
	@Test
	void getResourceAsJSONInvalidJson() {
		assertThrows(JsonParseException.class, () -> SchemaLoader.getResourceAsJSON("machine_feedback_schema_invalid.json"));
	}
	
	@Test
	void asStringIoException() throws IOException {
		Resource mockRes = Mockito.mock(Resource.class);
		when(mockRes.getInputStream()).thenThrow(new IOException());
		assertThrows(UncheckedIOException.class, () -> SchemaLoader.asString(mockRes));
	}
}
