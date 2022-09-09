package nga.oe.schema.aspect;

import java.time.Instant;
import java.util.UUID;

import org.apache.pulsar.client.api.PulsarClientException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.siliconmtn.data.util.EnumUtil;

import lombok.extern.log4j.Log4j2;
import nga.oe.config.ApplicationConfig;
import nga.oe.pulsar.MessageSender;
import nga.oe.schema.vo.MachineLogDTO;
import nga.oe.schema.vo.MachineLogDTO.ClassificationLevel;
import nga.oe.schema.vo.MachineLogDTO.LogLevel;

/**
 * <b>Title:</b> LoggerAoP.java
 * <b>Project:</b> hf-db-mgmt
 * <b>Description:</b> Logger Aspect for tracking start/end processing of messages.
 *
 * <b>Copyright:</b> 2022
 * <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author raptor
 * @version 1.0
 * @since Jul 20, 2022
 * @updates
 *
 */

@ConfigurationPropertiesScan
@Configuration
@ConfigurationProperties(prefix = "conf")
@Aspect
@Component
@Log4j2
public class LoggerAoP {

	@Autowired
	ApplicationConfig config;

	@Autowired
	MessageSender sender;

	/**
	 * Before advice for sending start events 
	 * @param jp
	 * @param dto
	 */
	@Before("@annotation(nga.hmf.notification.aspect.SendLogs) && args(.., dto)")
	public void beforeAdvice(JoinPoint jp, Object dto) throws PulsarClientException {
		log.info("BEFORE LOGGING ASPECT CALL " + dto);
		MachineLogDTO msg = generateMessage(dto, "START JOB + " + System.currentTimeMillis());
		sender.sendLog(msg);
	}

	/**
	 * After advice for sending end events
	 * @param jp
	 * @param dto
	 */
	@AfterReturning(pointcut="@annotation(nga.hmf.notification.aspect.SendLogs)", returning = "retVal")
	public void afterAdvice(Object retVal) throws PulsarClientException {
		log.info("AFTER LOGGING ASPECT CALL " + retVal);
		MachineLogDTO msg = generateMessage(retVal, "End JOB + " + System.currentTimeMillis());
		sender.sendLog(msg);
	}

	MachineLogDTO generateMessage(Object dto, String eventSummary) {
		MachineLogDTO msg = new MachineLogDTO();
		msg.setLogLevel(LogLevel.SYSTEM);
		msg.setServiceId(config.getServiceId());
		msg.setClassificationLevel(EnumUtil.safeValueOf(ClassificationLevel.class, config.getClassificationLevel()));
		msg.setEventName("Notification System Log");
		msg.setPayload(dto.toString());
		msg.setExecutionDateTime(Instant.now());
		msg.setEventSummary(eventSummary);
		msg.setMicroServiceId(config.getMicroServiceId());
		msg.setSessionId(UUID.randomUUID());
		return msg;
	}
}
