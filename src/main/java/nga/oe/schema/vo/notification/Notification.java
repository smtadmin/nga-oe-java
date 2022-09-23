package nga.oe.schema.vo.notification;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.siliconmtn.io.api.base.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <b>Title:</b> Notification.java <b>Project:</b> Notification Management
 * <b>Description:</b> NOtification entity used to save a notification to the
 * database
 *
 * <b>Copyright:</b> 2022 <b>Company:</b> Silicon Mountain Technologies
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
@Table(name = "notification", schema = "hmf_notifications")
public class Notification implements BaseEntity {

	private static final long serialVersionUID = -3611774832481044370L;

	@Id
	@Column(name = "notification_id", columnDefinition = "uuid", nullable = false)
	private UUID notificationId;

	@Column(name = "owner_id", columnDefinition = "uuid")
	private UUID ownerId;

	@Column(name = "service_id", columnDefinition = "varchar")
	private String serviceId;

	@Column(name = "group_id", columnDefinition = "uuid")
	private UUID groupId;

	@Column(name = "severity_cd", columnDefinition = "varchar")
	private String severityCode;

	@Column(name = "title_txt", columnDefinition = "text")
	private String title;

	@Column(name = "message_txt", columnDefinition = "text")
	private String message;

	@Column(name = "microservice_id", columnDefinition = "varchar")
	private String microserviceId;

	@Column(name = "session_id", columnDefinition = "uuid")
	private UUID sessionId;

	@Column(name = "order_id", columnDefinition = "uuid")
	private UUID orderId;

	@Column(name = "environment_id", columnDefinition = "varchar(5)")
	private String environmentId;

	@Column(name = "simulation_id", columnDefinition = "uuid")
	private UUID simulationId;

	@Column(name = "transaction_id", columnDefinition = "uuid")
	private UUID transactionId;

	@Column(name = "tty_no", columnDefinition = "integer")
	private int ttyNumber;

	@Column(name = "clearance_level", columnDefinition = "varchar(32)")
	private String clearanceLevel;

	@Column(name = "aknowledged_dt", columnDefinition = "timestamp")
	private ZonedDateTime aknowledgedDt;

	@Column(name = "dismiss_dt", columnDefinition = "timestamp")
	private ZonedDateTime dismissDt;

	@OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<NotificationTypeXr> notificationType = new ArrayList<>();

	@Column(name = "create_dt", columnDefinition = "timestamp", nullable = false, updatable = false)
	private ZonedDateTime createDt;

	@Transient
	private List<UUID> emailIds = new ArrayList<>();

	@Transient
	private List<UUID> smsIds = new ArrayList<>();

	@Transient
	private boolean actionable;

	@PrePersist
	public void prePersist() {
		createDt = ZonedDateTime.now(ZoneOffset.UTC);
	}

}
