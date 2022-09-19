package nga.oe.schema.vo;

import com.siliconmtn.io.api.base.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <b>Title:</b> NotificationEvent.java <b>Project:</b> Notifications
 * MicroService <b>Description:</b> Entity for managing Notification Events
 *
 * <b>Copyright:</b> 2022 <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author raptor
 * @version 1.0
 * @since Jul 7, 2022
 * @updates
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BannerMessageDTO implements BaseEntity {

	private static final long serialVersionUID = 673593000566070840L;
	private String state;
	private String msg;
}
