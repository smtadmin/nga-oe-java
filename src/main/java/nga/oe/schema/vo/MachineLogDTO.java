package nga.oe.schema.vo;

import java.time.Instant;
import java.util.UUID;

import com.siliconmtn.data.text.StringUtil;
import com.siliconmtn.io.api.base.BaseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import nga.oe.schema.Parseable;

/**
 * <b>Title:</b> LogEvent.java <b>Project:</b> HFDB MicroService
 * <b>Description:</b> Entity for managing Log Events
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
@ToString
@Schema(description = "The Base Data collection model for Machine Logging")
public class MachineLogDTO extends Parseable implements BaseDTO {

	private static final long serialVersionUID = 3231015195890050596L;

	public enum LogLevel {
		FATAL, SYSTEM, ERROR, WARNING, INFO, DEBUG, TRACE, SUCCESS
	}

	public enum ClassificationLevel {
		UNCLASSIFIED, SECRET, TOP_SECRET
	}

	public enum Environment {
		XC, UC
	}

	public enum EventTypeCd {
		EVENT_START, EVENT_IN_PROGRESS, EVENT_END, EVENT_INFO
	}

	private EventTypeCd eventTypeCd;
	private LogLevel logLevel;
	private String serviceId;
	private boolean simulationFlg;
	private Instant executionDateTime;
	private ClassificationLevel classificationLevel;
	private Environment environment;
	private int escalationLevel;
	private String eventName;
	private String eventSummary;
	private Object payload;
	private String originalOrderId;

	private UUID userId;
	private UUID orderId;
	private UUID sessionId;
	private UUID workflowId;
	private String microServiceId;
	private UUID uiTransactionId;

	public boolean isValid() {
		return !StringUtil.isEmpty(serviceId) && logLevel != null && executionDateTime != null
				&& classificationLevel != null && !StringUtil.isEmpty(eventName) && sessionId != null
				&& !StringUtil.isEmpty(microServiceId);
	}
}
