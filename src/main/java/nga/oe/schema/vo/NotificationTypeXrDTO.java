package nga.oe.schema.vo;

import java.util.UUID;

import com.siliconmtn.io.api.base.BaseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <b>Title:</b> NotificationTypeXrDTO.java
 * <b>Description:</b> Notification DTO used to pull information from the request
 *
 * <b>Copyright:</b> 2022
 * <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author Eric Damschroder
 * @version 1.0
 * @since Sep 21, 2022
 * @updates
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class NotificationTypeXrDTO implements BaseDTO {

	private static final long serialVersionUID = 37184217119585672L;

	public NotificationTypeXrDTO(String notificationTypeCode) {
		this.notificationTypeCode = notificationTypeCode;
	}

	private UUID notificationTypeXrId = UUID.randomUUID();

	private UUID notification;

	private String notificationTypeCode;
}