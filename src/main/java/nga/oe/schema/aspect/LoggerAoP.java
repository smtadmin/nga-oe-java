package nga.oe.schema.aspect;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siliconmtn.data.text.StringUtil;

import lombok.extern.log4j.Log4j2;
import nga.oe.config.ApplicationConfig;
import nga.oe.pulsar.MessageSender;
import nga.oe.pulsar.RequestDTOMessageListener;
import nga.oe.schema.exception.UnexpectedException;
import nga.oe.schema.vo.MachineLogDTO;
import nga.oe.schema.vo.MachineLogDTO.EventTypeCd;
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

		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		Object data = null;
		if (!StringUtil.isEmpty(dto.getData())) {
			data = mapper.readValue(dto.getData(), Map.class);
		}
		// Generate and send the Starting MachineLog Message with initial Payload of the
		// Request Data value.
		MachineLogDTO msg = generateMessage(dto, data, "START JOB + " + System.currentTimeMillis(),
				EventTypeCd.EVENT_START);
		sender.sendLog(msg, props);

		log.info("Executing {}.{} with argument: {}", targetClass, targetMethod, dto);
		Object response = null;
		Throwable thr = null;
		try {
			// Execute wrapped method and capture the result
			response = joinPoint.proceed();

			log.info("Method returned: {}", response);

			// Generate and send the end MachineLog Message with payload of the response
			// value.
			msg = generateMessage(dto, response, "END JOB + " + System.currentTimeMillis(), EventTypeCd.EVENT_END);
			sender.sendLog(msg, props);
		} catch (Throwable t) {
			thr = t;
			sender.sendErrorLog(new Exception(t), "There was a problem Processing Request",
					MessageSender.extractProps(dto));
		} finally {
			// Generate and send the end MachineLog Message with payload of the response
			// value.
			msg = generateMessage(dto, response, "END JOB + " + System.currentTimeMillis(), EventTypeCd.EVENT_END);
			sender.sendLog(msg, props);
		}
		if (thr != null) {
			throw new UnexpectedException(thr.getMessage(), thr);
		}

		return response;
	}

	MachineLogDTO generateMessage(RequestDTO dto, Object payload, String eventSummary, EventTypeCd eventTypeCd) {
		MachineLogDTO msg = sender.generateBaseMachineLog(dto.getSessionId(), dto.getUiTransactionId());
		msg.setEventTypeCd(eventTypeCd);
		msg.setLogLevel(LogLevel.SYSTEM);
		msg.setEventName("System Log");
		msg.setEventSummary(eventSummary);
		msg.setSessionId(dto.getSessionId());
		msg.setUiTransactionId(dto.getUiTransactionId());
		msg.setPayload(payload);
		return msg;
	}
}
