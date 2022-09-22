package nga.oe.pulsar;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.siliconmtn.data.text.StringUtil;
import com.siliconmtn.data.util.EnumUtil;
import com.siliconmtn.pulsar.PulsarConfig;
import com.siliconmtn.pulsar.TopicConfig;

import lombok.extern.log4j.Log4j2;
import nga.oe.config.ApplicationConfig;
import nga.oe.schema.vo.MachineLogDTO;
import nga.oe.schema.vo.MachineLogDTO.ClassificationLevel;
import nga.oe.schema.vo.MachineLogDTO.Environment;
import nga.oe.schema.vo.MachineLogDTO.EventTypeCd;
import nga.oe.schema.vo.MachineLogDTO.LogLevel;
import nga.oe.schema.vo.RequestDTO;

/**
 * <b>Title:</b> MessageSender.java <b>Project:</b> HFDB MicroService
 * <b>Description:</b> Helper Component for sending Async Messages out of the
 * System.
 *
 * <b>Copyright:</b> 2022 <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author raptor
 * @version 1.0
 * @since Jul 7, 2022
 * @updates
 *
 */
@ConfigurationPropertiesScan
@Configuration
@ConfigurationProperties(prefix = "conf")
@Component
@Log4j2
public class MessageSender {

	public static final String LOGGING_TOPIC = "loggingTopic";

	@Autowired
	ApplicationConfig appConfig;

	@Autowired
	PulsarClient client;

	@Autowired
	PulsarConfig config;

	@Autowired
	ObjectMapper mapper;

	protected String schema;

	/**
	 * Default constructor loads the schema for the machineLog.
	 * 
	 * @param mapper
	 */
	public MessageSender(ObjectMapper mapper) {
		this.mapper = mapper;
		mapper.findAndRegisterModules();
		Gson gson = new GsonBuilder().create();

		try {
			Resource resource = new ClassPathResource("machine_feedback_schema.json");
			JsonElement el = JsonParser.parseString(asString(resource));
			schema = gson.toJson(el);
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * Read a resource as a String.
	 * 
	 * @param resource
	 * @return
	 */
	public static String asString(Resource resource) {
		try (Reader reader = new InputStreamReader(resource.getInputStream())) {
			return FileCopyUtils.copyToString(reader).replace("\n", "").replace("\r", "");
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Drop a message into the Logging Topic
	 * 
	 * @param o
	 * @throws PulsarClientException
	 */
	public MessageId sendLog(MachineLogDTO mLog, Map<String, String> properties) throws PulsarClientException {
		TopicConfig lConfig = config.getTopics().get(LOGGING_TOPIC);
		MessageId mId = null;
		if (mLog.isValid()) {
			try (Producer<byte[]> p = buildProducer(lConfig, properties)) {
				String json = mapper.writeValueAsString(mLog);
				log.info(json);
				RequestDTO rdto = new RequestDTO(schema, json);
				mId = p.send(mapper.writeValueAsBytes(rdto));
			} catch (JsonProcessingException e) {
				log.error("TODO", e);
			}
		} else {
			log.error(mLog);
		}
		return mId;
	}

	/**
	 * Send an Exception on to the Pulsar Server.
	 * 
	 * @param e
	 * @return
	 * @throws PulsarClientException
	 */
	public MessageId sendErrorLog(Exception e, String eventName, Map<String, String> properties) {
		MachineLogDTO msg = generateBaseMachineLogWithStrings(properties.get(RequestDTOMessageListener.SESSION_ID),
				properties.get(RequestDTOMessageListener.TRANSACTION_ID));
		msg.setEventTypeCd(EventTypeCd.EVENT_IN_PROGRESS);
		msg.setLogLevel(LogLevel.SYSTEM);
		msg.setEventName(eventName);
		msg.setPayload(e.toString());
		msg.setEventSummary(e.getMessage());
		MessageId mId = null;
		try {
			mId = sendLog(msg, properties);
		} catch (PulsarClientException e1) {
			log.error(e1);
		}
		return mId;
	}

	/**
	 * Builds the core of a MachineLogDTO using String arguments for the session and transaction Ids.
	 * Populates all auto-config values
	 * @param sessionId
	 * @param transactionId
	 * @return
	 */
	public MachineLogDTO generateBaseMachineLogWithStrings(String sessionId, String transactionId) {
		UUID sessId = null;
		UUID transId = null;
		if (!StringUtil.isEmpty(sessionId)) {
			sessId = UUID.fromString(sessionId);
		}
		if (!StringUtil.isEmpty(transactionId)) {
			transId = UUID.fromString(transactionId);
		}
		return generateBaseMachineLog(sessId, transId);
	}

	/**
	 * Builds the core of a MachineLogDTO using UUID arguments for the session and transaction Ids.
	 * Populates all auto-config values
	 * @param sessionId
	 * @param transactionId
	 * @return
	 */
	public MachineLogDTO generateBaseMachineLog(UUID sessionId, UUID transactionId) {
		MachineLogDTO msg = new MachineLogDTO();
		msg.setServiceId(appConfig.getServiceId());
		msg.setEnvironment(EnumUtil.safeValueOf(Environment.class, appConfig.getEnvironmentCd()));
		msg.setClassificationLevel(EnumUtil.safeValueOf(ClassificationLevel.class, appConfig.getClassificationLevel()));
		msg.setMicroServiceId(appConfig.getMicroServiceId());
		msg.setExecutionDateTime(Instant.now());
		msg.setSessionId(sessionId);
		msg.setUiTransactionId(transactionId);
		return msg;
	}

	/**
	 * Drop a message to the supplied Topic
	 * 
	 * @param gdMsg
	 * @return
	 * @throws PulsarClientException
	 */
	public MessageId sendMessage(Object msg, String topic, Map<String, String> properties) throws PulsarClientException {
		TopicConfig tConfig = config.getTopics().get(topic);
		MessageId mId = null;
		
		// If an incorrect topic was supplied just return here
		if (tConfig == null) return mId;

		try (Producer<byte[]> p = buildProducer(tConfig, properties)) {
			String json;
			try {
				json = mapper.writeValueAsString(msg);
				log.info(json);
				mId = p.send(json.getBytes());
			} catch (JsonProcessingException e) {
				log.error("TODO", e);
			}
		}
		return mId;
	}

	/**
	 * Builds a Producer for given topicUri and name;
	 * 
	 * @param topicUri
	 * @param name
	 * @return
	 * @throws PulsarClientException
	 */
	public Producer<byte[]> buildProducer(TopicConfig topicConfig, Map<String, String> properties)
			throws PulsarClientException {
		return client.newProducer().topic(topicConfig.getTopicUri()).producerName(topicConfig.getName()).properties(properties).create();
	}

	/**
	 * Manage extracting shared Properties from the RequestDTO for passing around
	 * the async topics.
	 * @param req
	 * @return
	 */
	public static Map<String, String> extractProps(RequestDTO req) {
		Map<String, String> props = new HashMap<>();
		if (req.getSessionId() != null) {
			props.put(RequestDTOMessageListener.SESSION_ID, req.getSessionId().toString());
		}
		if (req.getUiTransactionId() != null) {
			props.put(RequestDTOMessageListener.TRANSACTION_ID, req.getUiTransactionId().toString());
		}
		return props;
	}
}
