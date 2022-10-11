package nga.oe.pulsar;

import static org.junit.jupiter.api.Assertions.assertEquals;
// Junit 5
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
// JDK 11
import java.util.UUID;

// Apache Pulsar
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.ProducerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.TypedMessageBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siliconmtn.pulsar.PulsarConfig;
import com.siliconmtn.pulsar.TopicConfig;

import nga.oe.config.ApplicationConfig;
import nga.oe.schema.vo.GumdropMessageDTO;
import nga.oe.schema.vo.MachineLogDTO;
import nga.oe.schema.vo.MachineLogDTO.ClassificationLevel;
import nga.oe.schema.vo.MachineLogDTO.Environment;
import nga.oe.schema.vo.MachineLogDTO.LogLevel;
import nga.oe.schema.vo.RequestDTO;

/**
 * <b>Title:</b> MessageSenderTest.java <b>Project:</b> HFDB MicroService
 * <b>Description:</b> Unit Tests providing coverage for the MessageSender Class
 *
 * <b>Copyright:</b> 2022 <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author raptor
 * @version 1.0
 * @since Jul 14, 2022
 * @updates
 *
 */
@ActiveProfiles("MessageSenderTest-test")
class MessageSenderTest {

	@MockBean
	private Producer<byte[]> producer;

	@Mock
	PulsarClient client;

	@Mock
	private MessageSender sender;

	@Mock
	private ObjectMapper mapper;

	@BeforeEach
	void setup() {
		mapper = new ObjectMapper();
	}

	@Test
	void verifySchema() {
		MessageSender sender = new MessageSender(mapper);
		assertNotNull(sender.schema);
	}

	/**
	 * Tests the sending of the logs
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	void testSendLog() throws Exception {
		MachineLogDTO msg = new MachineLogDTO();
		msg.setLogLevel(LogLevel.SYSTEM);
		msg.setServiceId("HMF");
		msg.setClassificationLevel(ClassificationLevel.UNCLASSIFIED);
		msg.setEventName("Human Feedback DB Log");
		msg.setExecutionDateTime(Instant.now());
		msg.setSessionId(UUID.randomUUID());
		msg.setMicroServiceId("TestService");
		msg.setEventSummary("START JOB + " + System.currentTimeMillis());

		producer = Mockito.mock(Producer.class);
		client = Mockito.mock(PulsarClient.class);
		Mockito.when(producer.send(any())).thenReturn(MessageId.latest);

		sender = new MessageSender(mapper);
		sender.config = new PulsarConfig();
		sender.config.setTopics(new HashMap<>());
		sender.config.getTopics().put(MessageSender.LOGGING_TOPIC, new TopicConfig());
		sender.client = client;
		ProducerBuilder<byte[]> pb = Mockito.mock(ProducerBuilder.class);
		TypedMessageBuilder<byte[]> tmb = Mockito.mock(TypedMessageBuilder.class);

		Mockito.when(client.newProducer()).thenReturn(pb);
		Mockito.when(pb.topic(any())).thenReturn(pb);
		Mockito.when(pb.properties(anyMap())).thenReturn(pb);
		Mockito.when(pb.producerName(any())).thenReturn(pb);
		Mockito.when(pb.create()).thenReturn(producer);
		Mockito.when(tmb.value(any())).thenReturn(tmb);
		Mockito.when(tmb.properties(any())).thenReturn(tmb);
		Mockito.when(tmb.send()).thenReturn(MessageId.latest);
		Mockito.when(producer.newMessage()).thenReturn(tmb);
		Map<String, String> props = new HashMap<>();
		assertNotNull(sender.sendLog(msg, props));
	}

	/**
	 * Tests the sending of the logs
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	void testSendErrorLog() throws Exception {
		String errMsg = "There was an error";
		Exception e = new Exception(errMsg);

		producer = Mockito.mock(Producer.class);
		client = Mockito.mock(PulsarClient.class);
		Mockito.when(producer.send(any())).thenReturn(MessageId.latest);

		sender = new MessageSender(mapper);
		sender.appConfig = new ApplicationConfig();
		sender.appConfig.setEnvironmentCd(Environment.XC.name());
		sender.appConfig.setClassificationLevel(ClassificationLevel.UNCLASSIFIED.name());
		sender.appConfig.setMicroServiceId("notification:system");
		sender.appConfig.setServiceId("HMF");
		sender.config = new PulsarConfig();
		sender.config.setTopics(new HashMap<>());
		sender.config.getTopics().put(MessageSender.LOGGING_TOPIC, new TopicConfig());
		sender.client = client;
		ProducerBuilder<byte[]> pb = Mockito.mock(ProducerBuilder.class);
		TypedMessageBuilder<byte[]> tmb = Mockito.mock(TypedMessageBuilder.class);
		Mockito.when(client.newProducer()).thenReturn(pb);
		Mockito.when(pb.topic(any())).thenReturn(pb);
		Mockito.when(pb.properties(anyMap())).thenReturn(pb);
		Mockito.when(pb.producerName(any())).thenReturn(pb);
		Mockito.when(pb.create()).thenReturn(producer);
		Mockito.when(tmb.value(any())).thenReturn(tmb);
		Mockito.when(tmb.properties(any())).thenReturn(tmb);
		Mockito.when(tmb.send()).thenReturn(MessageId.latest);
		Mockito.when(producer.newMessage()).thenReturn(tmb);

		Map<String, String> props = new HashMap<>();
		props.put(RequestDTO.SESSION_ID, UUID.randomUUID().toString());
		assertNotNull(sender.sendErrorLog(e, "SampleEvent", props));
	}

	@Test
	void testBaseMessageGenerationEmptyArgs() {
		sender = new MessageSender(mapper);
		sender.appConfig = new ApplicationConfig();
		sender.appConfig.setEnvironmentCd(Environment.XC.name());
		sender.appConfig.setClassificationLevel(ClassificationLevel.UNCLASSIFIED.name());
		sender.appConfig.setMicroServiceId("notification:system");
		sender.appConfig.setServiceId("HMF");
		MachineLogDTO msg = sender.generateBaseMachineLogWithStrings(null, "", "");

		assertNotNull(msg);
		assertNotNull(msg.getExecutionDateTime());
		assertEquals(sender.appConfig.getClassificationLevel(), msg.getClassificationLevel().name());
		assertEquals(sender.appConfig.getEnvironmentCd(), msg.getEnvironment().name());
		assertEquals(sender.appConfig.getServiceId(), msg.getServiceId());
		assertEquals(sender.appConfig.getMicroServiceId(), msg.getMicroServiceId());
		assertNull(msg.getSessionId());
		assertNull(msg.getUiTransactionId());
		assertNull(msg.getUserId());
	}

	@Test
	void testBaseMessageGenerationStringArgs() {
		sender = new MessageSender(mapper);
		sender.appConfig = new ApplicationConfig();
		sender.appConfig.setClassificationLevel(ClassificationLevel.UNCLASSIFIED.name());
		sender.appConfig.setEnvironmentCd(Environment.XC.name());
		sender.appConfig.setMicroServiceId("notification:system");
		sender.appConfig.setServiceId("HMF");
		UUID sessId = UUID.randomUUID();
		UUID transId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		MachineLogDTO msg = sender.generateBaseMachineLogWithStrings(sessId.toString(), transId.toString(), userId.toString());

		assertNotNull(msg);
		assertNotNull(msg.getExecutionDateTime());
		assertEquals(sender.appConfig.getClassificationLevel(), msg.getClassificationLevel().name());
		assertEquals(sender.appConfig.getEnvironmentCd(), msg.getEnvironment().name());
		assertEquals(sender.appConfig.getServiceId(), msg.getServiceId());
		assertEquals(sender.appConfig.getMicroServiceId(), msg.getMicroServiceId());
		assertEquals(sessId, msg.getSessionId());
		assertEquals(transId, msg.getUiTransactionId());
		assertEquals(userId, msg.getUserId());
	}

	@Test
	void testBaseMessageGenerationUUIDArgs() {
		sender = new MessageSender(mapper);
		sender.appConfig = new ApplicationConfig();
		sender.appConfig.setClassificationLevel(ClassificationLevel.UNCLASSIFIED.name());
		sender.appConfig.setEnvironmentCd(Environment.XC.name());
		sender.appConfig.setMicroServiceId("notification:system");
		sender.appConfig.setServiceId("HMF");
		UUID sessId = UUID.randomUUID();
		UUID transId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		MachineLogDTO msg = sender.generateBaseMachineLog(sessId, transId, userId);

		assertNotNull(msg);
		assertNotNull(msg.getExecutionDateTime());
		assertEquals(sender.appConfig.getClassificationLevel(), msg.getClassificationLevel().name());
		assertEquals(sender.appConfig.getEnvironmentCd(), msg.getEnvironment().name());
		assertEquals(sender.appConfig.getServiceId(), msg.getServiceId());
		assertEquals(sender.appConfig.getMicroServiceId(), msg.getMicroServiceId());
		assertEquals(sessId, msg.getSessionId());
		assertEquals(transId, msg.getUiTransactionId());
		assertEquals(userId, msg.getUserId());
	}

	@SuppressWarnings("unchecked")
	@Test
	void testSendLogInvalid() throws Exception {
		MachineLogDTO msg = new MachineLogDTO();
		msg.setServiceId("HMF");
		msg.setClassificationLevel(ClassificationLevel.UNCLASSIFIED);
		msg.setEventName("Human Feedback DB Log");
		msg.setExecutionDateTime(Instant.now());
		msg.setMicroServiceId("HFDB");
		msg.setSessionId(UUID.randomUUID());
		msg.setUserId(UUID.randomUUID());
		msg.setEventSummary("START JOB + " + System.currentTimeMillis());

		producer = Mockito.mock(Producer.class);
		client = Mockito.mock(PulsarClient.class);
		Mockito.when(producer.send(msg.toString().getBytes())).thenReturn(MessageId.latest);

		sender = new MessageSender(mapper);
		sender.config = new PulsarConfig();
		sender.config.setTopics(new HashMap<>());
		sender.config.getTopics().put(MessageSender.LOGGING_TOPIC, new TopicConfig());
		sender.client = client;
		ProducerBuilder<byte[]> pb = Mockito.mock(ProducerBuilder.class);
		Mockito.when(client.newProducer()).thenReturn(pb);
		Mockito.when(pb.topic(any())).thenReturn(pb);
		Mockito.when(pb.properties(anyMap())).thenReturn(pb);
		Mockito.when(pb.producerName(any())).thenReturn(pb);
		Mockito.when(pb.create()).thenReturn(producer);
		Map<String, String> props = new HashMap<>();
		assertNull(sender.sendLog(msg, props));
	}

	/**
	 * Tests the notifications
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	void testSendGumdrop() throws Exception {
		GumdropMessageDTO msg = new GumdropMessageDTO();
		msg.setCount(5);
		msg.setState("success");

		producer = Mockito.mock(Producer.class);
		client = Mockito.mock(PulsarClient.class);
		Mockito.when(producer.send(any())).thenReturn(MessageId.latest);

		sender = new MessageSender(mapper);
		sender.config = new PulsarConfig();
		sender.config.setTopics(new HashMap<>());
		sender.config.getTopics().put("gumdrop", new TopicConfig());
		sender.client = client;
		ProducerBuilder<byte[]> pb = Mockito.mock(ProducerBuilder.class);
		TypedMessageBuilder<byte[]> tmb = Mockito.mock(TypedMessageBuilder.class);

		Mockito.when(client.newProducer()).thenReturn(pb);
		Mockito.when(pb.topic(any())).thenReturn(pb);
		Mockito.when(pb.properties(anyMap())).thenReturn(pb);
		Mockito.when(pb.producerName(any())).thenReturn(pb);
		Mockito.when(pb.create()).thenReturn(producer);
		Mockito.when(tmb.value(any())).thenReturn(tmb);
		Mockito.when(tmb.properties(any())).thenReturn(tmb);
		Mockito.when(tmb.send()).thenReturn(MessageId.latest);
		Mockito.when(producer.newMessage()).thenReturn(tmb);
		Map<String, String> props = new HashMap<>();
		assertNotNull(sender.sendMessage(msg, "gumdrop", props));
	}

	@Test
	void testExtractPropsValid() {
		RequestDTO dto = new RequestDTO();
		dto.setProperty(RequestDTO.SESSION_ID,UUID.randomUUID().toString());
		dto.setProperty(RequestDTO.TRANSACTION_ID,UUID.randomUUID().toString());
		dto.setProperty(RequestDTO.USER_ID,UUID.randomUUID().toString());
		Map<String, String> props = MessageSender.extractProps(dto);
		assertEquals(dto.getProperty(RequestDTO.SESSION_ID), props.get(RequestDTO.SESSION_ID));
		assertEquals(dto.getProperty(RequestDTO.TRANSACTION_ID), props.get(RequestDTO.TRANSACTION_ID));
		assertEquals(dto.getProperty(RequestDTO.USER_ID), props.get(RequestDTO.USER_ID));
	}

	@Test
	void testNoTopic() throws PulsarClientException {
		sender = new MessageSender(mapper);
		sender.config = new PulsarConfig();
		sender.config.setTopics(new HashMap<>());
		sender.config.getTopics().put("gumdrop", new TopicConfig());
		Map<String, String> props = new HashMap<>();
		assertNull(sender.sendMessage(null, "", props));
	}
}
