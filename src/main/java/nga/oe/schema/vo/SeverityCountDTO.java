package nga.oe.schema.vo;

import com.siliconmtn.io.api.base.BaseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <b>Title:</b> SeverityCountDTO.java
 * <b>Project:</b> Notification Management
 * <b>Description:</b> DTO that holds severity count information
 * 
 * <b>Copyright:</b> 2022
 * <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author Eric Damschroder
 * @version 1.0
 * @since Aug 30, 2022
 * @updates
 *
 */

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class SeverityCountDTO implements BaseDTO {
	
	private static final long serialVersionUID = 5170332500980880321L;

	private int count;
	
	private String severityCd;

}
