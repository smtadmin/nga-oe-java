package nga.oe.schema.exception;

import java.util.Set;

import com.networknt.schema.ValidationMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
/**
 * <b>Title:</b> AppSchemaException.java
 * <b>Project:</b> mf-db-mgmt
 * <b>Description:</b> Custom app exception used when a RequestDTO fails to convert.
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
@AllArgsConstructor
@Setter
@Getter
public class AppSchemaException extends Exception {

	private static final long serialVersionUID = 7921961456775367221L;
	private final Set<ValidationMessage> issues;

	public AppSchemaException(String message, Set<ValidationMessage> issues) {
		super(message);
		this.issues = issues;
	}
}
