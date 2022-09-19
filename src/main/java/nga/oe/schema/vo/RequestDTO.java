package nga.oe.schema.vo;

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
	public RequestDTO(String schema, String data) {
		this.schema = schema;
		this.data = data;
	}

	String schema;
	String data;

	UUID sessionId;
	UUID uiTransactionId;
}
