package nga.oe.schema.vo;

import com.siliconmtn.io.api.base.BaseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class GumdropMessageDTO implements BaseDTO {

	private static final long serialVersionUID = -214440097137901619L;
	int count;
	String state;
}
