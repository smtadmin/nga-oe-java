package nga.oe.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;

import nga.oe.schema.SchemaUtil.ErrorType;
import nga.oe.schema.exception.AppSchemaException;
import nga.oe.schema.vo.MachineLogDTO;
import nga.oe.schema.vo.RequestDTO;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SchemaUtil.class })
class SchemaUtilTest {

	@Autowired
	SchemaUtil<MachineLogDTO> service;

	ObjectMapper mapper;

	public SchemaUtilTest() {
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
	}

	Resource resource = new ClassPathResource("oe-machine-feedback-data.sample.json");
	Resource customResource = new ClassPathResource("oe-machine-feedback-data.sample.custom.json");

	RequestDTO dto;

	@Test
	void extractDataNull() {
		assertNull(SchemaUtil.extractData(null));
	}

	@Test
	void extractDataValueNode() {
		JsonNode n = mock(JsonNode.class);
		when(n.isValueNode()).thenReturn(true);
		when(n.asText()).thenReturn("Hello World");
		assertEquals("Hello World", SchemaUtil.extractData(n));
	}

	@Test
	void extractDataNonValueNode() {
		JsonNode n = mock(JsonNode.class);
		when(n.isValueNode()).thenReturn(false);
		when(n.toString()).thenReturn("Hello World");
		assertEquals("Hello World", SchemaUtil.extractData(n));
	}

	@Test
	void schemaServiceHappyPathTest() throws AppSchemaException, IOException {
		File file = resource.getFile();
		JsonNode node = mapper.readTree(file);
		dto = new RequestDTO();
		dto.setData(node.get("data").toString());
		dto.setSchema(node.get("schema").toString());
		assertNotNull(service.convertSchema(dto));
		assertNotNull(service.convertNode(dto));
		assertFalse(service.convertNode(dto).isNull());
		Set<ValidationMessage> validations = service.validate(dto);
		assertTrue(validations.isEmpty());
	}

	@Test
	void convertSchemaMissingSchemaTest() throws AppSchemaException, IOException {
		File file = resource.getFile();
		ValidationMessage m = new ValidationMessage.Builder().path(ErrorType.INVALID_SCHEMA_PATH.getMessage())
				.customMessage(ErrorType.INVALID_SCHEMA_MSG.getMessage()).format(new MessageFormat("")).build();
		JsonNode node = mapper.readTree(file);
		dto = new RequestDTO();
		dto.setData(node.get("data").toString());

		AppSchemaException ex = assertThrows(AppSchemaException.class, () -> service.convertSchema(dto));
		assertEquals(ErrorType.INVALID_SCHEMA_ERR_MSG.getMessage(), ex.getMessage());
		assertEquals(1, ex.getIssues().size());
		assertEquals(m.getPath(), ex.getIssues().iterator().next().getPath());

	}

	@Test
	void convertNodeMissingDataTest() throws AppSchemaException, IOException {
		File file = resource.getFile();
		ValidationMessage m = new ValidationMessage.Builder().path(ErrorType.INVALID_DATA_PATH.getMessage())
				.customMessage(ErrorType.INVALID_SCHEMA_MSG.getMessage()).format(new MessageFormat("")).build();
		JsonNode node = mapper.readTree(file);
		dto = new RequestDTO();
		dto.setSchema(node.get("schema").toString());
		AppSchemaException ex = assertThrows(AppSchemaException.class, () -> service.convertNode(dto));
		assertEquals(ErrorType.INVALID_DATA_ERR_MSG.getMessage(), ex.getMessage());
		assertEquals(1, ex.getIssues().size());
		assertEquals(m.getPath(), ex.getIssues().iterator().next().getPath());
	}

	@Test
	void validateBadSchemaSyntaxTest() throws AppSchemaException, IOException {
		File file = resource.getFile();
		JsonNode node = mapper.readTree(file);
		dto = new RequestDTO();
		dto.setData(node.get("data").toString());
		dto.setSchema(node.get("schema").toString().substring(1));
		assertEquals(1, service.validate(dto).size());
	}

	@Test
	void validateBadDataSyntaxTest() throws AppSchemaException, IOException {
		File file = resource.getFile();
		JsonNode node = mapper.readTree(file);
		dto = new RequestDTO();
		dto.setData(node.get("data").toString().substring(1));
		dto.setSchema(node.get("schema").toString());
		assertEquals(1, service.validate(dto).size());
	}

	@Test
	void convertBaseRequestTest() throws IOException, AppSchemaException {
		File file = resource.getFile();
		JsonNode node = mapper.readTree(file);
		dto = new RequestDTO();
		dto.setData(node.get("data").toString());
		dto.setSchema(node.get("schema").toString());

		MachineLogDTO logDto = service.convertRequest(dto, MachineLogDTO.class);
		assertNotNull(logDto);
		assertEquals(logDto.getUserId().toString(), node.get("data").get("userId").asText());
	}

	@Test
	void convertCustomRequestTest() throws IOException, AppSchemaException {
		File file = customResource.getFile();
		JsonNode node = mapper.readTree(file);
		dto = new RequestDTO();
		dto.setData(node.get("data").toString());
		dto.setSchema(node.get("schema").toString());
		Set<ValidationMessage> validations = service.validate(dto);
		assertTrue(validations.isEmpty());
		MachineLogDTO logDto = service.convertRequest(dto, MachineLogDTO.class);
		assertNotNull(logDto);
		assertEquals(logDto.getUserId().toString(),
				node.get("data").get(SchemaUtil.BASE_SCHEMA_KEY).get("userId").asText());
	}

	@Test
	void convertCustomRequestNestedTest() throws IOException, AppSchemaException {
		File file = customResource.getFile();
		JsonNode node = mapper.readTree(file);
		dto = new RequestDTO();
		dto.setData(node.get("data").toString());
		dto.setSchema(node.get("schema").toString());

		MachineLogDTO logDto = service.convertRequest(dto, MachineLogDTO.class);
		assertNotNull(logDto);
		assertEquals(logDto.getUserId().toString(),
				node.get("data").get(SchemaUtil.BASE_SCHEMA_KEY).get("userId").asText());
		assertFalse(logDto.getUnMappedData().isEmpty());
		assertEquals(3, logDto.getUnMappedData().size());
	}

	@Test
	void convertBadDataSyntaxTest() throws IOException, AppSchemaException {
		File file = resource.getFile();
		JsonNode node = mapper.readTree(file);
		dto = new RequestDTO();
		dto.setData(node.get("data").toString().substring(1));
		dto.setSchema(node.get("schema").toString());

		assertThrows(AppSchemaException.class, () -> service.convertRequest(dto, MachineLogDTO.class));
	}

	@Test
	void buildKeyNull() {
		assertEquals("", service.buildKey(null, null));
	}

	@Test
	void buildKeyParentOnly() {
		assertEquals("Hello.", service.buildKey("Hello", null));
	}

	@Test
	void buildKeyKeyOnly() {
		assertEquals("world", service.buildKey(null, "world"));
	}

	@Test
	void buildKeyParentAndKey() {
		assertEquals("Hello.world", service.buildKey("Hello", "world"));
	}
}
