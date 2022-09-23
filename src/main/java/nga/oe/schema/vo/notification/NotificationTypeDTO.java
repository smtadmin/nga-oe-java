package nga.oe.schema.vo.notification;

import com.siliconmtn.io.api.base.BaseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <b>Title:</b> NotificationTypeDTO.java
 * <b>Description:</b> Notification Type entity used to store notification types in the databse
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

	private static final long serialVersionUID = -1984168339314226965L;

	private String notificationTypeCode;
	
}
