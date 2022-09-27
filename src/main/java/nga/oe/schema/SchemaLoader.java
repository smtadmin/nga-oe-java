package nga.oe.schema;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.siliconmtn.data.text.StringUtil;

public class SchemaLoader {
	
	private SchemaLoader() {}

	public static String getResourceAsJSON(String resPath) throws JsonParseException {
		if(StringUtil.isEmpty(resPath)) {
			throw new IllegalArgumentException("resPath can not be null or empty.");
		}
		Gson gson = new GsonBuilder().create();
		Resource resource = new ClassPathResource(resPath);
		if(resource.exists()) {
			return gson.toJson(JsonParser.parseString(asString(resource)));
		}
		return null;
	}
	
	/**
	 * Read a resource as a String.
	 * 
	 * @param resource
	 * @return
	 */
	public static String asString(Resource resource) {
		try (Reader reader = new InputStreamReader(resource.getInputStream())) {
			return FileCopyUtils.copyToString(reader).replace("\n", "").replace("\r", "");
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
