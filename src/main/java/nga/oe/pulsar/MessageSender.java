package nga.oe.pulsar;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.time.Instant;
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
import com.siliconmtn.data.util.EnumUtil;
import com.siliconmtn.pulsar.PulsarConfig;
import com.siliconmtn.pulsar.TopicConfig;

import lombok.extern.log4j.Log4j2;
import nga.oe.config.ApplicationConfig;
import nga.oe.schema.vo.BannerMessageDTO;
import nga.oe.schema.vo.GumdropMessageDTO;
import nga.oe.schema.vo.MachineLogDTO;
import nga.oe.schema.vo.MachineLogDTO.ClassificationLevel;
import nga.oe.schema.vo.MachineLogDTO.EventTypeCd;
import nga.oe.schema.vo.MachineLogDTO.LogLevel;
import nga.oe.schema.vo.RequestDTO;

/**
 * <b>Title:</b> MessageSender.java
 * <b>Project:</b> HFDB MicroService
 * <b>Description:</b> Helper Component for sending Async Messages out of the
 * System.
 *
 * <b>Copyright:</b> 2022
 * <b>Company:</b> Silicon Mountain Technologies
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
	public static final String BANNER_TOPIC = "bannerTopic";
	public static final String GUMDROP_TOPIC = "gumdropTopic";

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
	 * @param mapper
	 */
	public MessageSender(ObjectMapper mapper) {
		this.mapper = mapper;
		mapper.findAndRegisterModules();
		try {
			Resource resource = new ClassPathResource("machine_feedback_schema.json");
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.findAndRegisterModules();
			schema = asString(resource);
		} catch(Exception e) {
			log.error(e);
		}
	}

	/**
	 * Read a resource as a String.
	 * @param resource
	 * @return
	 */
	public static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

	/**
	 * Drop a message into the Logging Topic
	 * @param o
	 * @throws PulsarClientException
	 */
	public MessageId sendLog(MachineLogDTO mLog) throws PulsarClientException {
		TopicConfig lConfig = config.getTopics().get(LOGGING_TOPIC);
		MessageId mId = null;
		if(mLog.isValid()) {
			try(Producer<byte[]> p = buildProducer(lConfig.getTopicUri(), lConfig.getName())) {
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
	 * @param e
	 * @return
	 * @throws PulsarClientException
	 */
	public MessageId sendErrorLog(Exception e) {
		MachineLogDTO msg = new MachineLogDTO();
		msg.setEventTypeCd(EventTypeCd.EVENT_IN_PROGRESS);
		msg.setLogLevel(LogLevel.SYSTEM);
		msg.setServiceId(appConfig.getServiceId());
		msg.setClassificationLevel(EnumUtil.safeValueOf(ClassificationLevel.class, appConfig.getClassificationLevel()));
		msg.setEventName("Notification System Error");
		msg.setPayload(e.toString());
		msg.setExecutionDateTime(Instant.now());
		msg.setEventSummary(e.getMessage());
		msg.setMicroServiceId(appConfig.getMicroServiceId());
		msg.setSessionId(UUID.randomUUID());
		MessageId mId = null;
		try {
			mId = sendLog(msg);
		} catch (PulsarClientException e1) {
			log.error(e1);
		}
		return mId;
	}

	/**
	 * Drop a message into the Banner Topic
	 * @param notification
	 * @throws PulsarClientException
	 */
	public MessageId sendBanner(BannerMessageDTO bmMsg) throws PulsarClientException {
		MessageId mId = null;
		TopicConfig bConfig = config.getTopics().get(BANNER_TOPIC);

		try(Producer<byte[]> p = buildProducer(bConfig.getTopicUri(), bConfig.getName())) {
			String json;
			try {
				json = mapper.writeValueAsString(bmMsg);
				log.info(json);
				mId = p.send(json.getBytes());
			} catch (JsonProcessingException e) {
				log.error("TODO", e);
			}
		}
		return mId;
	}
	
	/**
	 * Drop a message to the Gumdrop Topic
	 * @param gdMsg
	 * @return
	 * @throws PulsarClientException
	 */
	public MessageId sendGumdrop(GumdropMessageDTO gdMsg) throws PulsarClientException {
		TopicConfig gConfig = config.getTopics().get(GUMDROP_TOPIC);
		MessageId mId = null;

		try(Producer<byte[]> p = buildProducer(gConfig.getTopicUri(), gConfig.getName())) {
			String json;
			try {
				json = mapper.writeValueAsString(gdMsg);
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
	 * @param topicUri
	 * @param name
	 * @return
	 * @throws PulsarClientException
	 */
	public Producer<byte[]> buildProducer(String topicUri, String topicName) throws PulsarClientException {
		return client.newProducer()
		.topic(topicUri)
		.producerName(topicName)
		.create();
	}
}
