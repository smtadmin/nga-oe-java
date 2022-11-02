package nga.oe.schema;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;

import javax.net.ssl.SSLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.siliconmtn.data.text.StringUtil;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.log4j.Log4j2;
import nga.oe.schema.exception.AppSchemaException;
import nga.oe.schema.exception.UnexpectedException;
import nga.oe.schema.vo.rtp.RTPSchemaDTO;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Component
@Log4j2
public class SchemaLoader {

	@Value("${rtp-schema.path}")
	protected String rtpSchemaPath;

	protected WebClient client;

	protected SchemaUtil<?> util;

	public SchemaLoader(@Value("${rtp-schema.url}") String rtpSchemaUrl, SchemaUtil<?> util)
			throws UnexpectedException {
		if (StringUtil.isEmpty(rtpSchemaUrl)) {
			throw new UnexpectedException("Missing Expected rtpSchemaUrl.");
		}
		this.util = util;

		// TODO - This is a hack to allow access to RTP on the unrecognized gov cert.
		try {
			SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
					.build();
			HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

			client = WebClient.builder().filters(exchangeFilterFunctions -> {
				exchangeFilterFunctions.add(logRequest());
				exchangeFilterFunctions.add(logResponse());
			}).clientConnector(new ReactorClientHttpConnector(httpClient)).baseUrl(rtpSchemaUrl).build();
		} catch (SSLException e) {
			throw new UnexpectedException("Problem creating RTP Schema Webclient", e);
		}
	}

	ExchangeFilterFunction logRequest() {
		return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
			StringBuilder sb = new StringBuilder("RTP Schema Request: \n");
			sb.append("URL: ").append(clientRequest.url().toString()).append("\n");
			sb.append("Headers: \n");
			// append clientRequest method and url
			clientRequest.headers().forEach(
					(name, values) -> values.forEach(value -> sb.append(String.format("%s: %s\n", name, value))));
			log.info(sb.toString());
			return Mono.just(clientRequest);
		});
	}

	ExchangeFilterFunction logResponse() {
		return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
			StringBuilder sb = new StringBuilder("RTP Schema Response: \n");
			// append clientRequest method and url
			sb.append(new Gson().toJson(clientResponse.bodyToMono(Object.class)));
			log.info(sb.toString());
			return Mono.just(clientResponse);
		});
	}

	/**
	 * Helper method to retrieve a Schema file from Resource Bundle.
	 * 
	 * @param resPath
	 * @return
	 * @throws JsonParseException
	 */
	public static String getResourceAsJSON(String resPath) throws JsonParseException {
		if (StringUtil.isEmpty(resPath)) {
			throw new IllegalArgumentException("resPath can not be null or empty.");
		}
		Gson gson = new GsonBuilder().create();
		Resource resource = new ClassPathResource(resPath);
		if (resource.exists()) {
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

	/**
	 * Connects out to RTP and attempts to retrieve a schema by ID.
	 * 
	 * @param schemaId
	 * @return the Schema if found retrieved and valid, otherwise null.
	 * @throws AppSchemaException
	 */
	public String retrieveSchemaRemotely(String schemaId) throws AppSchemaException {
		if (StringUtil.isEmpty(schemaId)) {
			throw new AppSchemaException(String.format("Unable to retrieve schema of id: %s", schemaId));
		}
		if (schemaId.equals("goodId")) {
			log.debug("Testing");
		}
		try {
			RTPSchemaDTO data = client.get().uri(uriBuilder -> uriBuilder.path(rtpSchemaPath).build(schemaId))
					.retrieve().bodyToMono(RTPSchemaDTO.class).block();

			// Validate that the data retrieved is correct and parse it to a valid schema
			if (data != null && data.getSchema() != null) {
				if (util.isValidJSON(data.getSchema().toString())) {
					return data.getSchema().toString();
				} else {
					return new Gson().toJson(data.getSchema());
				}
			} else {
				throw new AppSchemaException(String.format("Unable to retrieve schema of id: %s", schemaId));
			}
		} catch (Exception e) {
			throw new AppSchemaException(String.format("Unable to retrieve schema of id: %s", e));
		}
	}
}
