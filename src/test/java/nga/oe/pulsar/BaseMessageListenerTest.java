package nga.oe.pulsar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.junit.jupiter.api.Test;

import nga.oe.schema.vo.RequestDTO;

class BaseMessageListenerTest {

	private class SampleListener implements BaseMessageListener {

		private static final long serialVersionUID = -7356370481039510852L;

		@Override
		public void received(Consumer<byte[]> consumer, Message<byte[]> msg) {
		}

	}

	SampleListener listener = new SampleListener();

	@Test
	void convertToHashMapNull() {
		Map<String, String> reqProps = listener.convertToHashMap(null);
		assertNotNull(reqProps);
		assertEquals(0, reqProps.size());
	}

	@Test
	void convertToHashMapWithSessionId() {
		Map<String, String> props = new HashMap<>();
		props.put(RequestDTO.SESSION_ID, UUID.randomUUID().toString());
		props.put(RequestDTO.TRANSACTION_ID, UUID.randomUUID().toString());
		props.put(RequestDTO.USER_ID, UUID.randomUUID().toString());
		Map<String, String> reqProps = listener.convertToHashMap(props);
		assertNotNull(reqProps);
		assertEquals(2, reqProps.size());
		assertFalse(reqProps.containsKey(RequestDTO.SESSION_ID));
		assertEquals(props.get(RequestDTO.TRANSACTION_ID), reqProps.get(RequestDTO.TRANSACTION_ID));
		assertEquals(props.get(RequestDTO.USER_ID), reqProps.get(RequestDTO.USER_ID));
	}
}
