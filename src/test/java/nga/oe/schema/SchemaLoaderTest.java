package nga.oe.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParseException;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import nga.oe.schema.exception.AppSchemaException;
import nga.oe.schema.exception.UnexpectedException;
import nga.oe.schema.vo.rtp.RTPSchemaDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@ExtendWith(SpringExtension.class)
class SchemaLoaderTest {

	public MockWebServer mockBackEnd;

	SchemaLoader loader;

	static ObjectMapper mapper;

	@BeforeAll
	static void setup() {
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
	}

	@AfterEach
	void tearDown() throws IOException {
		mockBackEnd.shutdown();
	}

	@BeforeEach
	void initialize() throws UnexpectedException, IOException {
		mockBackEnd = new MockWebServer();
		mockBackEnd.start();

		String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
		loader = new SchemaLoader(baseUrl, new SchemaUtil<Parseable>());
		loader.rtpSchemaPath = "/api/v1/data/{schemaId}";
	}

	@Test
	void verifySSLErrorCatch() throws SSLException {
		SslContextBuilder sslContextBuilder = mock(SslContextBuilder.class, RETURNS_DEEP_STUBS);

		Mockito.mockStatic(SslContextBuilder.class);
		Mockito.when(SslContextBuilder.forClient()).thenReturn(sslContextBuilder);
		Mockito.when(sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE).build())
				.thenThrow(new SSLException("Error"));

		assertThrows(UnexpectedException.class, () -> new SchemaLoader("hello", new SchemaUtil<Parseable>()));

		Mockito.clearAllCaches();
	}

	@Test
	void noUrl() {
		assertThrows(UnexpectedException.class, () -> new SchemaLoader(null, null));
	}

	@Test
	void emptyUrl() {
		assertThrows(UnexpectedException.class, () -> new SchemaLoader("", null));
	}

	@Test
	void getResourceAsJSONNull() {
		assertThrows(IllegalArgumentException.class, () -> SchemaLoader.getResourceAsJSON(null));
	}

	@Test
	void getResourceAsJSONEmpty() {
		assertThrows(IllegalArgumentException.class, () -> SchemaLoader.getResourceAsJSON(""));
	}

	@Test
	void getResourceAsJSONValid() {
		assertNotNull(SchemaLoader.getResourceAsJSON("machine_feedback_schema.json"));
	}

	@Test
	void getResourceAsJSONNotExists() {
		assertNull(SchemaLoader.getResourceAsJSON("machine_feedback_schema_missing.json"));
	}

	@Test
	void getResourceAsJSONInvalidJson() {
		assertThrows(JsonParseException.class,
				() -> SchemaLoader.getResourceAsJSON("machine_feedback_schema_invalid.json"));
	}

	@Test
	void asStringIoException() throws IOException {
		Resource mockRes = Mockito.mock(Resource.class);
		when(mockRes.getInputStream()).thenThrow(new IOException());
		assertThrows(UncheckedIOException.class, () -> SchemaLoader.asString(mockRes));
	}

	@Test
	void testConstructor() throws SSLException {
		assertNotNull(loader.client);
	}

	@Test
	void testGoodRetrieve() throws JsonProcessingException, AppSchemaException {
		RTPSchemaDTO res = new RTPSchemaDTO();
		String schema = SchemaLoader.getResourceAsJSON("machine_feedback_schema.json");
		res.setSchema(schema);

		mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(res)).addHeader("Content-Type",
				"application/json"));
		String actual = loader.retrieveSchemaRemotely("goodId");
		assertNotNull(actual);
		assertEquals(actual, schema);
	}

	@Test
	void testGoodRetrieveObject() throws JsonProcessingException, AppSchemaException {
		Map<String, String> json = new HashMap<>();
		json.put("Hello", "world");
		RTPSchemaDTO res = new RTPSchemaDTO();
		res.setSchema(json);

		mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(res)).addHeader("Content-Type",
				"application/json"));

		assertNotNull(loader.retrieveSchemaRemotely("goodId"));
	}

	@Test
	void testBadRetrieveNull() throws JsonProcessingException {
		RTPSchemaDTO res = null;

		mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(res)).addHeader("Content-Type",
				"application/json"));

		assertThrows(AppSchemaException.class, () -> loader.retrieveSchemaRemotely("badId"));
	}

	@Test
	void testBadRetrieveNullSchema() throws JsonProcessingException {
		RTPSchemaDTO res = new RTPSchemaDTO();
		res.setSchema(null);

		mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(res)).addHeader("Content-Type",
				"application/json"));

		assertThrows(AppSchemaException.class, () -> loader.retrieveSchemaRemotely("badId"));
	}

	@Test
	void testBadRetrieveEmptySchemaId() throws JsonProcessingException {
		RTPSchemaDTO res = new RTPSchemaDTO();

		mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(res)).addHeader("Content-Type",
				"application/json"));

		assertThrows(AppSchemaException.class, () -> loader.retrieveSchemaRemotely(""));
	}

	@Test
	void testBadRetrieveNullSchemaId() throws JsonProcessingException {
		RTPSchemaDTO res = new RTPSchemaDTO();

		mockBackEnd.enqueue(new MockResponse().setBody(mapper.writeValueAsString(res)).addHeader("Content-Type",
				"application/json"));

		assertThrows(AppSchemaException.class, () -> loader.retrieveSchemaRemotely(null));
	}
}
