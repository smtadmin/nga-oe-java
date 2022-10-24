package nga.oe.schema.vo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
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

	@Schema(description = "String representation of the JSON Schema that describes the accompanying data field.")
	String schema;

	@Schema(description = "String representation of the Data.  Must validate against the accompanying schema field to be considered for processing.")
	String data;

	@Schema(description = "Contains meta fields for processing a request such as sessionId (UUID), uiTransactionId (UUID), userId (UUID)", 
			type = "object",
			example = "{'userId': '3fa85f64-5717-4562-b3fc-2c963f66afa6', 'uiTransactionId': '3fa85f64-5717-4562-b3fc-2c963f66afa6', 'sessionId': '3fa85f64-5717-4562-b3fc-2c963f66afa6'}")
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
