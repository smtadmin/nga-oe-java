package nga.oe.documentation;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

public class OpenAPIDocUploader {

	@EventListener(ApplicationReadyEvent.class)
	public void uploadDocumentsToApiRegistry() {
	    System.out.println("hello world, I have just started up");
	}
}
