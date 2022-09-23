package nga.oe.schema.vo.notification;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.siliconmtn.io.api.base.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <b>Title:</b> NotificationType.java
 * <b>Project:</b> Notification Management
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
@Entity
@NoArgsConstructor
@Setter
@Getter
@ToString
@Cacheable
@Table(name = "notification_type_xr", schema = "hmf_notifications")
public class NotificationTypeXr implements BaseEntity {
	

	private static final long serialVersionUID = -1984168339314226965L;

	@Id
	@Column(name = "notification_type_xr_id", columnDefinition = "uuid", nullable = false)
	private UUID notificationTypeXrId = UUID.randomUUID();

	@Column(name = "create_dt", columnDefinition = "timestamp", nullable = false, updatable = false)
	private ZonedDateTime createDt;
	
//	@Column(name = "notification_id", columnDefinition = "uuid", nullable = false)
//	private String notificationId;
//
//	@Column(name = "notification_type_cd", columnDefinition = "varchar(32)", nullable = false)
//	private String notificationTypeCode;

	@ToString.Exclude
    @ManyToOne
    @JoinColumn(name="notification_id", nullable=false)
	private Notification notification;

    @ManyToOne
    @JoinColumn(name="notification_type_cd", nullable=true)
	private NotificationType notificationTypeCode;
	
	@PrePersist
    public void prePersist() {
		createDt = ZonedDateTime.now(ZoneOffset.UTC);
    }
}
