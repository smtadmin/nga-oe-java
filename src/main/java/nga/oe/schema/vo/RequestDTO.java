package nga.oe.schema.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
@ToString
public class RequestDTO {
	String schema;

	String data;
}
