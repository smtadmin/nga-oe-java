package nga.oe.schema;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;

@Getter
public abstract class Parseable {

	@JsonIgnore
    public Map<String, Object> unMappedData = new HashMap<>();
}
