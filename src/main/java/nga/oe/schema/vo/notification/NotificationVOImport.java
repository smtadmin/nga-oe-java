package nga.oe.schema.vo.notification;

import org.springframework.context.annotation.Import;

@Import({Notification.class, NotificationDTO.class, NotificationType.class, NotificationTypeDTO.class, NotificationTypeXr.class, NotificationTypeXrDTO.class})
public class NotificationVOImport {

}
