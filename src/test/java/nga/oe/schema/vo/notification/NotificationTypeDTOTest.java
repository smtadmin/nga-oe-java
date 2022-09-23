package nga.oe.schema.vo.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import nga.oe.schema.vo.notification.NotificationTypeDTO.NotificationType;;


class NotificationTypeDTOTest {

	@Test
	void checkEnums() {
		assertEquals(4, NotificationType.values().length);
	}
}
