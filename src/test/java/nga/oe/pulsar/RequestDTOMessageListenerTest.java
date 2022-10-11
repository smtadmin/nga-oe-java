package nga.oe.pulsar;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import nga.oe.schema.exception.AppSchemaException;
import nga.oe.schema.vo.RequestDTO;
import nga.oe.service.RequestServiceImpl;

/**
 * <b>Title:</b> ResponseDTOMessageListenerTest.java <b>Project:</b>
 * Notifications MicroService <b>Description:</b> Unit Tests providing coverage
 * for the ResponseDTOMessageListener Class
 *
 * <b>Copyright:</b> 2022 <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author raptor
 * @version 1.0
 * @since Jul 14, 2022
 * @updates
 *
 */
@ExtendWith(SpringExtension.class)
class RequestDTOMessageListenerTest {

	String validSchema = "{\"$id\": \"https://nga.com/oe.human-feedback-core.schema.json\",\"$schema\": \"http://json-schema.org/draft-07/schema\","
			+ "\"title\": \"Core Human Feedback Schema\",\"description\": \"The General Human Feedback Schema for all ratings and feedback form the user on various services\","
			+ "\"createdBy\": \"Ray McManemin\",\"createdFor\": \"HumanFeedback\"," + "    \"version\": \".01\","
			+ "    \"type\": \"object\"," + "    \"properties\": {" + "      \"userId\": {"
			+ "        \"type\": \"string\"," + "        \"minLength\": 1,"
			+ "        \"description\": \"OE User Id for the requestor\"" + "      }," + "      \"serviceId\": {"
			+ "        \"type\": \"string\"," + "        \"minLength\": 1,"
			+ "        \"description\": \"Unique OE Id representng the service being rated\"" + "      },"
			+ "      \"transactionId\": {" + "        \"type\": \"string\"," + "        \"minLength\": 1,"
			+ "        \"description\": \"Unique Id from the UI that is assigned to all ajax requests\"" + "      },"
			+ "      \"servicePath\": {" + "        \"type\": \"string\"," + "        \"minLength\": 1,"
			+ "        \"description\": \"URI of the page being rated\"" + "      }," + "      \"submissionDate\": {"
			+ "        \"type\": \"string\"," + "        \"format\": \"date-time\","
			+ "        \"description\": \"Will capture date and time user submitted page\"" + "      },"
			+ "      \"questions\": {" + "        \"type\": \"object\","
			+ "        \"description\": \"OE Core questions that are part of each form displayed\","
			+ "        \"properties\": {" + "          \"oeGeneralRating\": {" + "            \"type\": \"number\","
			+ "            \"minimum\": 1," + "            \"maximum\": 5," + "            \"default\": 2,"
			+ "            \"description\": \"Rating from 1-5 on how the user would rate the service in question\""
			+ "          }," + "          \"favoriteFeature\": {" + "            \"type\": \"string\","
			+ "            \"description\": \"User selection to define which feature they most like about the service\","
			+ "            \"enum\": [" + "              \"Accuracy\"," + "              \"Performance\","
			+ "              \"Presentation\"," + "              \"Solves My Issue\"," + "              \"Usability\","
			+ "              \"None\"" + "            ]" + "          }," + "          \"leastFeature\": {"
			+ "            \"type\": \"string\","
			+ "            \"description\": \"User selection to define which feature they least like about the service\","
			+ "            \"enum\": [" + "              \"Accuracy\"," + "              \"Performance\","
			+ "              \"Presentation\"," + "              \"Solves My Issue\"," + "              \"Usability\","
			+ "              \"None\"" + "            ]" + "          }," + "          \"oeGeneralFeedback\": {"
			+ "            \"type\": \"string\","
			+ "            \"description\": \"OE General user feedback on the service\"" + "          }" + "        }, "
			+ "        \"required\": [" + "          \"oeGeneralRating\"," + "          \"favoriteFeature\","
			+ "          \"leastFeature\"" + "        ]" + "      }" + "    }," + "    \"required\": ["
			+ "      \"userId\"" + "    ]" + "  }";

	String validData = "{\"userId\": \"3d3531a6-0fcb-4c05-89a8-5d42b5ecc677\",\"serviceId\": \"c806de51-d4d2-45dc-bf31-82079e4ca53f\","
			+ "\"transactionId\": \"87cbcc8a-24f0-4949-8aeb-57c3a3f7a1c4\",\"servicePath\": \"/help\",\"submissionDate\": \"2022-07-15T19:38:24.228Z\","
			+ "\"questions\": {\"favoriteFeature\": \"Presentation\",\"leastFeature\": \"Usability\",\"oeGeneralRating\": 4,"
			+ "\"oeGeneralFeedback\": \"I really like the app.  I t looks great  Usability could be improved\"}}";
	@Mock
	RequestServiceImpl<Object> service;

	@Mock
	Consumer<byte[]> consumer;

	@Mock
	Message<byte[]> msg;

	@InjectMocks
	RequestDTOMessageListener<RequestServiceImpl<Object>> listener;

	RequestDTO dto;

	ObjectMapper mapper;

	String json;

	@BeforeEach
	void setup() throws IOException {
		json = String.format("{\"schema\": %s, \"data\": %s}", validSchema, validData);
	}

	@Test
	void messageInValidationPulsarCLientExceptionTest() throws PulsarClientException {
		json = json.substring(1);
		Mockito.when(msg.getData()).thenReturn(json.getBytes());
		assertDoesNotThrow(() -> listener.received(consumer, msg));
	}

	@Test
	void messageInValidationTest() {
		Mockito.when(msg.getData()).thenReturn(null);
		assertDoesNotThrow(() -> listener.received(consumer, msg));
	}

	@Test
	void messageValidationTest() {
		Mockito.when(msg.getData()).thenReturn(json.getBytes());
		assertDoesNotThrow(() -> listener.received(consumer, msg));
	}

	@Test
	void messageValidationTestWithProps() throws AppSchemaException {
		UUID sId = UUID.randomUUID();
		UUID tId = UUID.randomUUID();
		UUID uId = UUID.randomUUID();
		Map<String, String> properties = new HashMap<>();
		properties.put(RequestDTO.SESSION_ID, sId.toString());
		properties.put(RequestDTO.TRANSACTION_ID, tId.toString());
		properties.put(RequestDTO.USER_ID, uId.toString());
		Mockito.when(msg.getProperties()).thenReturn(properties);
		Mockito.when(msg.getData()).thenReturn(json.getBytes());
		Mockito.when(service.processRequest(dto)).thenReturn(Collections.singletonList(dto));
		assertDoesNotThrow(() -> listener.received(consumer, msg));
	}

	@Test
	void messageAckFailedTest() throws PulsarClientException {
		Mockito.when(msg.getData()).thenReturn(json.getBytes());
		doThrow(PulsarClientException.class).when(consumer).acknowledge(msg);
		assertDoesNotThrow(() -> listener.received(consumer, msg));
	}
}
