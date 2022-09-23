package nga.oe.schema.vo.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import nga.oe.schema.vo.notification.NotificationTypeDTO.NotificationType;

class NotificationDTOTest {

	@Test
	void testAddNotificationType() {
		NotificationDTO dto = new NotificationDTO();
		dto.addNotificationType(NotificationType.alert);

		assertFalse(dto.getNotificationType().isEmpty());
		assertEquals(NotificationType.alert.name(), dto.getNotificationType().get(0).getNotificationTypeCode());
	}
}
