package nga.oe.schema.vo.rtp;

import java.util.List;

import lombok.Data;

@Data
public class RTPSchemaSearchDTO {

	private int count;
	private List<RTPSchemaDTO> rows;
}
