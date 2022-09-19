package nga.oe.schema.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import nga.oe.schema.vo.NotificationTypeDTO.NotificationType;

/**
 * <b>Title:</b> NotificationReceiptVO.java <b>Project:</b> notification-system
 * <b>Description:</b> Small Receipt VO for emitting to MachineVO/Response
 *
 * <b>Copyright:</b> 2022 <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author raptor
 * @version 1.0
 * @since Aug 31, 2022
 * @updates
 *
 */

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class NotificationReceiptVO {

	NotificationType notificationType;
	String status;
	boolean succes;
	Object payload;
}
