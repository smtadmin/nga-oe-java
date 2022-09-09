package nga.oe.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "conf") 
@ConfigurationPropertiesScan
@NoArgsConstructor
@Setter
@Getter
public class ApplicationConfig {

	private String serviceId;
	private String classificationLevel;
	private String microServiceId;
}
