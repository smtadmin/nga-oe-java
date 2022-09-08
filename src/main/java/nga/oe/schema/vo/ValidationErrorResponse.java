package nga.oe.schema.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <b>Title:</b> ValidationErrorResponse.java
 * <b>Project:</b> Notifications MicroService
 * <b>Description:</b> Wrapper for managing violations in data back to the user.
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
@NoArgsConstructor
@Getter
public class ValidationErrorResponse {

	private List<Violation> violations = new ArrayList<>();
}
