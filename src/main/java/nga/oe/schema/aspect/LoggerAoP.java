package nga.oe.schema.aspect;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.siliconmtn.data.util.EnumUtil;

import lombok.extern.log4j.Log4j2;
import nga.oe.config.ApplicationConfig;
import nga.oe.pulsar.MessageSender;
import nga.oe.pulsar.RequestDTOMessageListener;
import nga.oe.schema.vo.MachineLogDTO;
import nga.oe.schema.vo.MachineLogDTO.ClassificationLevel;
import nga.oe.schema.vo.MachineLogDTO.LogLevel;
import nga.oe.schema.vo.RequestDTO;

/**
 * <b>Title:</b> LoggerAoP.java <b>Project:</b> hf-db-mgmt <b>Description:</b>
 * Logger Aspect for tracking start/end processing of messages.
 *
 * <b>Copyright:</b> 2022 <b>Company:</b> Silicon Mountain Technologies
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

	@Around("@annotation(nga.oe.schema.aspect.SendAroundLogs) && args(.., dto)")
	public Object aroundAdvice(ProceedingJoinPoint joinPoint, RequestDTO dto) throws Throwable {
		String targetClass = joinPoint.getTarget().getClass().getSimpleName();
		String targetMethod = joinPoint.getSignature().getName();

		// Set the sessionId on t he request
		dto.setSessionId(UUID.randomUUID());

		Map<String, String> props = new HashMap<>();
		props.put(RequestDTOMessageListener.SESSION_ID, dto.getSessionId().toString());
		if (dto.getUiTransactionId() != null)
			props.put(RequestDTOMessageListener.TRANSACTION_ID, dto.getUiTransactionId().toString());

		// Generate and send the Starting MachineLog Message with initial Payload of the
		// Request Data value.
		MachineLogDTO msg = generateMessage(dto, "START JOB + " + System.currentTimeMillis());
		sender.sendLog(msg, props);

		log.info("Executing {}.{} with argument: {}", targetClass, targetMethod, dto);

		// Execute wrapped method and capture the result
		Object response = joinPoint.proceed();

		log.info("Method returned: {}", response);

		// Generate and send the end MachineLog Message with payload of the response
		// value.
		msg = generateMessage(dto, "END JOB + " + System.currentTimeMillis());
		msg.setPayload(response);
		sender.sendLog(msg, props);

		return response;
	}

	MachineLogDTO generateMessage(RequestDTO dto, String eventSummary) {
		MachineLogDTO msg = new MachineLogDTO();
		msg.setLogLevel(LogLevel.SYSTEM);
		msg.setServiceId(config.getServiceId());
		msg.setClassificationLevel(EnumUtil.safeValueOf(ClassificationLevel.class, config.getClassificationLevel()));
		msg.setEventName("Notification System Log");
		msg.setExecutionDateTime(Instant.now());
		msg.setEventSummary(eventSummary);
		msg.setMicroServiceId(config.getMicroServiceId());
		msg.setSessionId(dto.getSessionId());
		msg.setPayload(dto.getData());
		return msg;
	}
}
