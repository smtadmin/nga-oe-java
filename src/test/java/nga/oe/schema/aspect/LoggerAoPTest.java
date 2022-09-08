package nga.oe.schema.aspect;

// Junit 5
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// Pulsar Libsa
import org.apache.pulsar.client.api.PulsarClientException;
// Join point for the aspects
import org.aspectj.lang.JoinPoint;
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

/****************************************************************************
 * <b>Title</b>: LoggerAoPTest.java
 * <b>Project</b>: notification-system
 * <b>Description: </b> CHANGE ME!
 * <b>Copyright:</b> Copyright (c) 2022
 * <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author James Camire
 * @version 3.0
 * @since Jul 28, 2022
 * @updates:
 ****************************************************************************/
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfig.class})
class LoggerAoPTest {
	
    @Mock
	private MessageSender sender;
    
    @Mock
    ApplicationConfig config;

    @InjectMocks
    private LoggerAoP aop = new LoggerAoP();

	@Test
	void testBeforeAdvice() throws PulsarClientException {
		sender = Mockito.mock(MessageSender.class);
		aop.sender = sender;

		JoinPoint jp = Mockito.mock(JoinPoint.class);
		assertDoesNotThrow(() -> aop.beforeAdvice(jp, ""));
	}

	@Test
	void testAfterAdvice() {
		sender = Mockito.mock(MessageSender.class);
		aop.sender = sender;
		assertDoesNotThrow(() -> aop.afterAdvice("Hello World"));
	}

	@Test
	void testGenerateMessage() {
		String eventSummary = "Event Summary" + Math.random();
		String payload = "{Hello World}";
		MachineLogDTO msg = aop.generateMessage(payload, eventSummary);

		assertNotNull(msg);
		assertEquals(LogLevel.SYSTEM, msg.getLogLevel());
		assertEquals(eventSummary, msg.getEventSummary());
		assertEquals(payload, msg.getPayload());
	}
}
