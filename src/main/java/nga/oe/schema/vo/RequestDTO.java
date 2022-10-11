package nga.oe.schema.vo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <b>Title:</b> RequestDTO.java
 * <b>Project:</b> mf-db-mgmt
 * <b>Description:</b> Intermediate DTO for passing JSON Data and related Schema to the SchemaService.
 *
 * <b>Copyright:</b> 2022
 * <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author raptor
 * @version 1.0
 * @since Aug 18, 2022
 * @updates
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RequestDTO {

	public static final String SESSION_ID = "sessionId";
	public static final String TRANSACTION_ID = "uiTransactionId";
	public static final String USER_ID = "userId";

	public RequestDTO(String schema, String data) {
		this.schema = schema;
		this.data = data;
		this.properties = new HashMap<>();
	}

	String schema;
	String data;

	Map<String, String> properties = new HashMap<>();

	public String getProperty(String key) {
		return properties.get(key);
	}

	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	public boolean hasProperty(String key) {
		return properties.containsKey(key);
	}

	public UUID getUUIDValue(String key) {
		if(properties.containsKey(key)) {
			return UUID.fromString(properties.get(key));
		} else {
			return null;
		}
	}
}
