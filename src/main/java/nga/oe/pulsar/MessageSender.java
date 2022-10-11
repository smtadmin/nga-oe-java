package nga.oe.pulsar;

import java.time.Instant;
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
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siliconmtn.data.text.StringUtil;
import com.siliconmtn.data.util.EnumUtil;
import com.siliconmtn.pulsar.PulsarConfig;
import com.siliconmtn.pulsar.TopicConfig;

import lombok.extern.log4j.Log4j2;
import nga.oe.config.ApplicationConfig;
import nga.oe.schema.SchemaLoader;
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
		try {
			schema = SchemaLoader.getResourceAsJSON("machine_feedback_schema.json");
		} catch(Exception e) {
			log.error("Unable to load Machine Schema", e);
		}
	}

	/**
	 * Drop a message into the Logging Topic
	 * 
	 * @param o
	 * @throws PulsarClientException
	 */
	public MessageId sendLog(MachineLogDTO mLog, Map<String, String> properties) throws PulsarClientException {
		MessageId mId = null;
		if (mLog.isValid()) {
			return sendRequestDTOMessage(mLog, schema, LOGGING_TOPIC, properties);
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
		MachineLogDTO msg = generateBaseMachineLogWithStrings(properties.get(RequestDTO.SESSION_ID),
				properties.get(RequestDTO.TRANSACTION_ID), properties.get(RequestDTO.USER_ID));
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
	public MachineLogDTO generateBaseMachineLogWithStrings(String sessionId, String transactionId, String userId) {
		UUID sessId = null;
		UUID transId = null;
		UUID uId = null;
		if (!StringUtil.isEmpty(sessionId)) {
			sessId = UUID.fromString(sessionId);
		}
		if (!StringUtil.isEmpty(transactionId)) {
			transId = UUID.fromString(transactionId);
		}
		if (!StringUtil.isEmpty(userId)) {
			uId = UUID.fromString(userId);
		}
		return generateBaseMachineLog(sessId, transId, uId);
	}

	/**
	 * Builds the core of a MachineLogDTO using UUID arguments for the session and transaction Ids.
	 * Populates all auto-config values
	 * @param sessionId
	 * @param transactionId
	 * @return
	 */
	public MachineLogDTO generateBaseMachineLog(UUID sessionId, UUID transactionId, UUID userId) {
		MachineLogDTO msg = new MachineLogDTO();
		msg.setServiceId(appConfig.getServiceId());
		msg.setEnvironment(EnumUtil.safeValueOf(Environment.class, appConfig.getEnvironmentCd()));
		msg.setClassificationLevel(EnumUtil.safeValueOf(ClassificationLevel.class, appConfig.getClassificationLevel()));
		msg.setMicroServiceId(appConfig.getMicroServiceId());
		msg.setExecutionDateTime(Instant.now());
		msg.setSessionId(sessionId);
		msg.setUiTransactionId(transactionId);
		msg.setUserId(userId);
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

		try (Producer<byte[]> p = buildProducer(tConfig)) {
			String json;
			try {
				json = mapper.writeValueAsString(msg);
				log.info(json);
				mId = p
						.newMessage()
						.value(json.getBytes())
						.properties(properties)
						.send();
			} catch (JsonProcessingException e) {
				log.error("TODO", e);
			}
		}
		return mId;
	}
	


	/**
	 * Create a request DTO and drop it into the queue
	 * @param notification
	 * @throws PulsarClientException
	 */
	public MessageId sendRequestDTOMessage(Object msg, String reqSchema, String topic, Map<String, String> properties) throws PulsarClientException {
		TopicConfig tConfig = config.getTopics().get(topic);
		MessageId mId = null;
		
		// If an incorrect topic was supplied just return here
		if (tConfig == null) return mId;

		try(Producer<byte[]> p = buildProducer(tConfig)) {
			String json = mapper.writeValueAsString(msg);
			RequestDTO rdto = new RequestDTO(reqSchema, json);
			mId = p
				.newMessage()
				.value(mapper.writeValueAsBytes(rdto))
				.properties(properties)
				.send();
		} catch (JsonProcessingException e) {
			log.error("TODO", e);
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
	public Producer<byte[]> buildProducer(TopicConfig topicConfig)
			throws PulsarClientException {
		return client.newProducer().topic(topicConfig.getTopicUri()).producerName(topicConfig.getName()).create();
	}

	/**
	 * Manage extracting shared Properties from the RequestDTO for passing around
	 * the async topics.
	 * @param req
	 * @return
	 */
	public static Map<String, String> extractProps(RequestDTO req) {
		return req.getProperties();
	}
}
