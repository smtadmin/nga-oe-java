package nga.oe.pulsar;

import java.util.UUID;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;
import nga.oe.schema.SchemaUtil;
import nga.oe.schema.vo.RequestDTO;
import nga.oe.service.RequestServiceImpl;

/**
 * <b>Title:</b> ResponseDTOMessageListener.java <b>Project:</b> Notifications
 * MicroService <b>Description:</b> Pulsar Message Listener used for consuming
 * Messages from the Pulsar Consumer Topic.
 *
 * <b>Copyright:</b> 2022 <b>Company:</b> Silicon Mountain Technologies
 * 
 * <b>Example:</b> ./pulsar-client produce nga/hmf/hfdb -f
 * ~/nga/notification-system/src/main/resources/oe-human-feedback-data.sample.json
 * 
 * @author raptor
 * @version 1.0
 * @since Jul 13, 2022
 * @updates
 *
 */
@Component
@Log4j2
public class RequestDTOMessageListener<T extends RequestServiceImpl<?>> implements MessageListener<byte[]> {

	public static final String SESSION_ID = "sessionId";
	public static final String TRANSACTION_ID = "uiTransactionId";
	private static final long serialVersionUID = 2341934156383412579L;

	@Autowired
	transient T service;

	private ObjectMapper objectMapper;

	RequestDTOMessageListener() {
		objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
	}

	/**
	 * Handles async receipt of a Pulsar Message, converts to a RequestDTO and
	 * forwards to the linked Service.
	 */
	@Override
	public void received(Consumer<byte[]> consumer, Message<byte[]> msg) {
		try {
			JsonNode node = objectMapper.readTree(msg.getData());
			UUID transactionId = UUID.randomUUID();
			if (msg.hasProperty(TRANSACTION_ID)) {
				transactionId = UUID.fromString(msg.getProperty(TRANSACTION_ID));
			}

			RequestDTO req = new RequestDTO(SchemaUtil.extractData(node.get("schema")),
					SchemaUtil.extractData(node.get("data")), null, transactionId);

			service.processRequest(req);
			consumer.acknowledge(msg);
		} catch (Exception e) {
			log.error("Problem with inbound Message", e);
			try {
				consumer.acknowledge(msg);
			} catch (PulsarClientException e1) {
				log.error("We couldn't ack a failed message!", e1);
			}
		}
	}
}
