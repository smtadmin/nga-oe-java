package nga.oe.pulsar;

// Junit 5
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;

import java.time.Instant;
import java.util.HashMap;
// JDK 11
import java.util.UUID;

// Apache Pulsar
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.ProducerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
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
import nga.oe.schema.vo.BannerMessageDTO;
import nga.oe.schema.vo.GumdropMessageDTO;
import nga.oe.schema.vo.MachineLogDTO;
import nga.oe.schema.vo.MachineLogDTO.ClassificationLevel;
import nga.oe.schema.vo.MachineLogDTO.LogLevel;

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
		Mockito.when(client.newProducer()).thenReturn(pb);
		Mockito.when(pb.topic(any())).thenReturn(pb);
		Mockito.when(pb.producerName(any())).thenReturn(pb);
		Mockito.when(pb.create()).thenReturn(producer);
		assertNotNull(sender.sendLog(msg));
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
		sender.appConfig.setClassificationLevel(ClassificationLevel.UNCLASSIFIED.name());
		sender.appConfig.setMicroServiceId("notification:system");
		sender.appConfig.setServiceId("HMF");
		sender.config = new PulsarConfig();
		sender.config.setTopics(new HashMap<>());
		sender.config.getTopics().put(MessageSender.LOGGING_TOPIC, new TopicConfig());
		sender.client = client;
		ProducerBuilder<byte[]> pb = Mockito.mock(ProducerBuilder.class);
		Mockito.when(client.newProducer()).thenReturn(pb);
		Mockito.when(pb.topic(any())).thenReturn(pb);
		Mockito.when(pb.producerName(any())).thenReturn(pb);
		Mockito.when(pb.create()).thenReturn(producer);
		assertNotNull(sender.sendErrorLog(e));
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
		Mockito.when(pb.producerName(any())).thenReturn(pb);
		Mockito.when(pb.create()).thenReturn(producer);
		assertNull(sender.sendLog(msg));
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
		sender.config.getTopics().put(MessageSender.GUMDROP_TOPIC, new TopicConfig());
		sender.client = client;
		ProducerBuilder<byte[]> pb = Mockito.mock(ProducerBuilder.class);
		Mockito.when(client.newProducer()).thenReturn(pb);
		Mockito.when(pb.topic(any())).thenReturn(pb);
		Mockito.when(pb.producerName(any())).thenReturn(pb);
		Mockito.when(pb.create()).thenReturn(producer);

		assertNotNull(sender.sendGumdrop(msg));
	}

	/**
	 * Tests the notifications
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	void testSendNotificationBanner() throws Exception {
		BannerMessageDTO msg = new BannerMessageDTO();
		msg.setMsg("Hello World");
		msg.setState("success");

		producer = Mockito.mock(Producer.class);
		client = Mockito.mock(PulsarClient.class);
		Mockito.when(producer.send(any())).thenReturn(MessageId.latest);

		sender = new MessageSender(mapper);
		sender.config = new PulsarConfig();
		sender.config.setTopics(new HashMap<>());
		sender.config.getTopics().put(MessageSender.BANNER_TOPIC, new TopicConfig());
		sender.client = client;
		ProducerBuilder<byte[]> pb = Mockito.mock(ProducerBuilder.class);
		Mockito.when(client.newProducer()).thenReturn(pb);
		Mockito.when(pb.topic(any())).thenReturn(pb);
		Mockito.when(pb.producerName(any())).thenReturn(pb);
		Mockito.when(pb.create()).thenReturn(producer);

		assertNotNull(sender.sendBanner(msg));
	}
}