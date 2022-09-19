package nga.oe.schema.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b>Title:</b> SendLogs.java <b>Project:</b> notification-system
 * <b>Description:</b> Annotation to be applied to controller that needs
 * logging.
 *
 * <b>Copyright:</b> 2022 <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author raptor
 * @version 1.0
 * @since Jul 19, 2022
 * @updates
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SendAroundLogs {

}
