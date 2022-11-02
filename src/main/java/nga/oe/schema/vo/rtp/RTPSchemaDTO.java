package nga.oe.schema.vo.rtp;

import java.util.Date;
import java.util.UUID;

import lombok.Data;

@Data
public class RTPSchemaDTO {

	private UUID id;
	private String name;
	private String version;
	private Object schema;
	private Date created;
	private Date updated;
}
