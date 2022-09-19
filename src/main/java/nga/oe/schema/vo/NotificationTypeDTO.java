package nga.oe.schema.vo;

import java.util.UUID;

import com.siliconmtn.io.api.base.BaseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <b>Title:</b> NotificationTypeDTO.java
 * <b>Project:</b> Notification Retrieval
 * <b>Description:</b> Notification DTO used to pull information from the request
 *
 * <b>Copyright:</b> 2022
 * <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author Eric Damschroder
 * @version 1.0
 * @since Aug 26, 2022
 * @updates
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class NotificationTypeDTO implements BaseDTO {

	public enum NotificationType {alert, gumdrop, email, sms}
	private static final long serialVersionUID = 37184217119585672L;

	private UUID notificationTypeXrId = UUID.randomUUID();

	private NotificationType notificationTypeCode;
	
	private UUID notificationId;
	
}