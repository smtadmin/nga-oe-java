package nga.oe.schema.aspect;

// Junit 5
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Pulsar Libsa
import org.apache.pulsar.client.api.PulsarClientException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import nga.oe.config.ApplicationConfig;
import nga.oe.pulsar.MessageSender;
import nga.oe.schema.exception.AppSchemaException;
import nga.oe.schema.exception.UnexpectedException;
import nga.oe.schema.vo.MachineLogDTO;
import nga.oe.schema.vo.MachineLogDTO.EventTypeCd;
import nga.oe.schema.vo.MachineLogDTO.LogLevel;
import nga.oe.schema.vo.RequestDTO;

/****************************************************************************
 * <b>Title</b>: LoggerAoPTest.java <b>Project</b>: notification-system
 * <b>Description: </b> CHANGE ME! <b>Copyright:</b> Copyright (c) 2022
 * <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author James Camire
 * @version 3.0
 * @since Jul 28, 2022
 * @updates:
 ****************************************************************************/
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
class LoggerAoPTest {

	@Mock
	private MessageSender sender;

	@Mock
	ApplicationConfig config;

	@InjectMocks
	private LoggerAoP aop = new LoggerAoP();

	@BeforeEach
	void setup() {
		sender = Mockito.mock(MessageSender.class);
		when(sender.generateBaseMachineLog(any(), any())).thenReturn(new MachineLogDTO());
		aop.sender = sender;
	}

	@Test
	void testAroundAdvice() throws PulsarClientException {
		ProceedingJoinPoint jp = Mockito.mock(ProceedingJoinPoint.class);
		Signature sig = Mockito.mock(Signature.class);
		when(jp.getTarget()).thenReturn(new Object());
		when(jp.getSignature()).thenReturn(sig);
		assertDoesNotThrow(() -> aop.aroundAdvice(jp, new RequestDTO()));
	}

	@Test
	void testAroundAdviceWithTransactionId() throws PulsarClientException {
		ProceedingJoinPoint jp = Mockito.mock(ProceedingJoinPoint.class);
		Signature sig = Mockito.mock(Signature.class);
		when(jp.getTarget()).thenReturn(new Object());
		when(jp.getSignature()).thenReturn(sig);
		RequestDTO req = new RequestDTO();
		req.setUiTransactionId(UUID.randomUUID());
		assertDoesNotThrow(() -> aop.aroundAdvice(jp, req));
	}

	@Test
	void testAroundAdviceWithData() throws PulsarClientException {
		Map<String, String> payload = new HashMap<>();
		payload.put("name", "Hello World");
		Gson gson = new GsonBuilder().create();

		ProceedingJoinPoint jp = Mockito.mock(ProceedingJoinPoint.class);
		Signature sig = Mockito.mock(Signature.class);
		when(jp.getTarget()).thenReturn(new Object());
		when(jp.getSignature()).thenReturn(sig);
		RequestDTO req = new RequestDTO();
		req.setData(gson.toJson(payload));
		req.setUiTransactionId(UUID.randomUUID());
		assertDoesNotThrow(() -> aop.aroundAdvice(jp, req));
	}

	@Test
	void testAroundAdviceWithException() throws Throwable {
		Map<String, String> payload = new HashMap<>();
		payload.put("name", "Hello World");
		Gson gson = new GsonBuilder().create();

		ProceedingJoinPoint jp = Mockito.mock(ProceedingJoinPoint.class);
		Signature sig = Mockito.mock(Signature.class);
		when(jp.getTarget()).thenReturn(new Object());
		when(jp.getSignature()).thenReturn(sig);
		when(jp.proceed()).thenThrow(new AppSchemaException("Bad", new Exception()));
		RequestDTO req = new RequestDTO();
		req.setData(gson.toJson(payload));
		req.setUiTransactionId(UUID.randomUUID());
		assertThrows(UnexpectedException.class, () -> aop.aroundAdvice(jp, req));
	}

	@Test
	void testGenerateMessage() throws JsonProcessingException {
		RequestDTO dto = new RequestDTO();
		dto.setSessionId(UUID.randomUUID());
		String eventSummary = "Event Summary" + Math.random();
		Map<String, String> payload = new HashMap<>();
		payload.put("name", "Hello World");
		Gson gson = new GsonBuilder().create();

		MachineLogDTO msg = aop.generateMessage(dto, gson.toJson(payload), eventSummary, EventTypeCd.EVENT_INFO);

		assertNotNull(msg);
		assertEquals(LogLevel.SYSTEM, msg.getLogLevel());
		assertEquals(EventTypeCd.EVENT_INFO, msg.getEventTypeCd());
		assertEquals(eventSummary, msg.getEventSummary());
		assertEquals(gson.toJson(payload), msg.getPayload());
		assertEquals(dto.getSessionId(), msg.getSessionId());
	}
}
