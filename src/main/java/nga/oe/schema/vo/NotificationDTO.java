package nga.oe.schema.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.siliconmtn.io.api.base.BaseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <b>Title:</b> NotificationDTO.java
 * <b>Project:</b> Notification Retrieval
 * <b>Description:</b> Notification DTO used to get information from the request
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
public class NotificationDTO  implements BaseDTO {

	private static final long serialVersionUID = 6130717659567525565L;
	
	private UUID notificationId = UUID.randomUUID();

	private String severityCode;
	
	private UUID ownerId;

	private UUID serviceId;

	private UUID groupId;

	private String title;

	private String message;

	private UUID microserviceId;

	private UUID sessionId;

	private UUID orderId;

	private UUID environmentId;

	private UUID simulationId;

	private UUID transactionId;

	private int ttyNumber;

	private String clearanceLevel;

	private List<NotificationTypeDTO> notificationType = new ArrayList<>();
	
}