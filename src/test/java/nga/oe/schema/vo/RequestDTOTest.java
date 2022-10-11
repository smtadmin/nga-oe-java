package nga.oe.schema.vo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class RequestDTOTest {

	@Test
	void containsKey() {
		RequestDTO dto = new RequestDTO("Hello", "World");
		dto.setProperty(RequestDTO.SESSION_ID, UUID.randomUUID().toString());
		
		assertTrue(dto.hasProperty(RequestDTO.SESSION_ID));
		assertFalse(dto.hasProperty(RequestDTO.TRANSACTION_ID));
	}
}
