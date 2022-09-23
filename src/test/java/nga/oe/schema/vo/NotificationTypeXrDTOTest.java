package nga.oe.schema.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NotificationTypeXrDTOTest {

	@Test
	void constructorTest() {
		NotificationTypeXrDTO dto = new NotificationTypeXrDTO("Hello World");
		assertEquals("Hello World", dto.getNotificationTypeCode());
	}
}
