package nga.oe.pulsar;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.pulsar.client.api.MessageListener;

import nga.oe.schema.vo.RequestDTO;

public interface BaseMessageListener extends MessageListener<byte[]>{

	public default Map<String, String> convertToHashMap(Map<String, String> messageProps) {
		Map<String, String > reqProps = new HashMap<>();
		if(messageProps != null && !messageProps.isEmpty()) {
			for(Entry<String, String> e : messageProps.entrySet()) {
				if(!RequestDTO.SESSION_ID.equals(e.getKey())) {
					reqProps.put(e.getKey(), e.getValue());
				}
			}
		}
		return reqProps;
	}
}
