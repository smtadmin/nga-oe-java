package nga.oe.schema.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * <b>Title:</b> Violation.java
 * <b>Project:</b> Notifications MicroService
 * <b>Description:</b> Container for holding specific validation violations.
 *
 * <b>Copyright:</b> 2022
 * <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author raptor
 * @version 1.0
 * @since Jul 14, 2022
 * @updates
 *
 */
@AllArgsConstructor
@Setter
@Getter
public class Violation {

	private final String fieldName;
	private final String message;
}