package nga.oe.schema.aspect;

// Junit 5
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.UUID;

// Pulsar Libsa
import org.apache.pulsar.client.api.PulsarClientException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import nga.oe.config.ApplicationConfig;
import nga.oe.pulsar.MessageSender;
import nga.oe.schema.vo.MachineLogDTO;
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

	@Test
	void testAroundAdvice() throws PulsarClientException {
		sender = Mockito.mock(MessageSender.class);
		aop.sender = sender;

		ProceedingJoinPoint jp = Mockito.mock(ProceedingJoinPoint.class);
		Signature sig = Mockito.mock(Signature.class);
		when(jp.getTarget()).thenReturn(new Object());
		when(jp.getSignature()).thenReturn(sig);
		assertDoesNotThrow(() -> aop.aroundAdvice(jp, new RequestDTO()));
	}

	@Test
	void testAroundAdviceWithTransactionId() throws PulsarClientException {
		sender = Mockito.mock(MessageSender.class);
		aop.sender = sender;

		ProceedingJoinPoint jp = Mockito.mock(ProceedingJoinPoint.class);
		Signature sig = Mockito.mock(Signature.class);
		when(jp.getTarget()).thenReturn(new Object());
		when(jp.getSignature()).thenReturn(sig);
		RequestDTO req = new RequestDTO();
		req.setUiTransactionId(UUID.randomUUID());
		assertDoesNotThrow(() -> aop.aroundAdvice(jp, req));
	}

	@Test
	void testGenerateMessage() {
		RequestDTO dto = new RequestDTO();
		dto.setSessionId(UUID.randomUUID());
		String eventSummary = "Event Summary" + Math.random();
		String payload = "{Hello World}";
		dto.setData(payload);
		MachineLogDTO msg = aop.generateMessage(dto, eventSummary);

		assertNotNull(msg);
		assertEquals(LogLevel.SYSTEM, msg.getLogLevel());
		assertEquals(eventSummary, msg.getEventSummary());
		assertEquals(payload, msg.getPayload());
		assertEquals(dto.getSessionId(), msg.getSessionId());
	}
}
