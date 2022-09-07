package nga.oe.schema;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;

import lombok.extern.log4j.Log4j2;
import nga.oe.schema.exception.AppSchemaException;
import nga.oe.schema.vo.RequestDTO;

/****************************************************************************
 * <b>Title</b>: SchemaUtil.java
 * <b>Project</b>: nga-oe
 * <b>Description: </b> CHANGE ME!
 * <b>Copyright:</b> Copyright (c) 2022
 * <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author James Camire
 * @version 3.0
 * @since Sep 6, 2022
 * @updates:
 ****************************************************************************/
@Log4j2
public class SchemaUtil<T> {

	/**
	 * Defines the available error messages for this utility
	 */
	public enum ErrorType {
		INVALID_DATA_PATH("data"),
		INVALID_SCHEMA_PATH("schema"),
		INVALID_DATA_MSG("Data could not be parsed into JsonNode"),
		INVALID_SCHEMA_MSG("Provided Schema was invalid."),
		BASE_SCHEMA_KEY("baseSchema"),
		INVALID_SCHEMA_ERR_MSG("Invalid Schema Data"),
		INVALID_DATA_ERR_MSG("Invalid Payload Data");
		
		String message;
		ErrorType(String message) { this.message = message; }
		public String getMessage() { return message; }
	}
	
	// Members
	ObjectMapper mapper;
	
	/**
	 * Attempts to convert the data portion of a given RequestDTO into a JsonNode Object
	 * @param req
	 * @return
	 * @throws AppSchemaException
	 */
	public JsonNode convertNode(RequestDTO req) throws AppSchemaException {
		try {
			return mapper.readTree(req.getData());
		} catch (Exception e) {
			log.error("Could not convert the Data on the request to JsonNode");
			Set<ValidationMessage> issues = new HashSet<>();
			issues.add(new ValidationMessage.Builder().path(ErrorType.INVALID_DATA_PATH.getMessage())
					.customMessage(ErrorType.INVALID_DATA_MSG.getMessage())
					.format(new MessageFormat("")).build());
			throw new AppSchemaException(ErrorType.INVALID_DATA_ERR_MSG.getMessage(), issues);
		}
	}
	
	/**
	 * In cases that we're using a customized Variant, look for the BaseSchema, convert that to an EventLogDTO
	 * then grab all extra fields as extras.
	 * @param node
	 * @return
	 * @throws JsonProcessingException
	 * @throws IllegalArgumentException
	 */
	public T parseCustom(JsonNode node, Class<T> bean) throws JsonProcessingException, IllegalArgumentException {
		T dto = mapper.treeToValue(node.get(ErrorType.BASE_SCHEMA_KEY.getMessage()), bean);
		extractExtras(node, dto, "");
		return dto;
	}
	
	/**
	 * Recursively iterate down the object storing keys as it goes on the given EventLogDTO's extendedData Map.
	 * @param node
	 * @param dto
	 * @param parentKey
	 */
	public void extractExtras(JsonNode node, T dto, String parentKey) {
		Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
		while(fields.hasNext()) {
			Map.Entry<String, JsonNode> field = fields.next();
			if(!ErrorType.BASE_SCHEMA_KEY.getMessage().equals(field.getKey())) {
				if(field.getValue().isValueNode()) {
					EventLogDataDTO eld = new EventLogDataDTO();
					eld.setEventLog(dto.getEventLogId());
					eld.setValueTxt(field.getValue().asText());
					eld.setExtendedDataKeyCd(buildKey(parentKey, field.getKey()));

					dto.getExtendedData().put(buildKey(parentKey, field.getKey()), eld);
				} else {
					extractExtras(field.getValue(), dto, buildKey(parentKey, field.getKey()));
				}
			}
		}
	}

	/**
	 * Attempts to convert the given RequestDTO into an EventLogDTO
	 * @param req
	 * @return
	 */
	public T convertRequest(RequestDTO req, Class<T> bean) {
		T dto = null;
		try {
			JsonNode node = convertNode(req);
			if(node.has(ErrorType.BASE_SCHEMA_KEY.getMessage())) {
				dto = parseCustom(node, bean);
			} else {
				dto = mapper.treeToValue(node, bean);
			}
		} catch (AppSchemaException | JsonProcessingException | IllegalArgumentException e) {
			log.error("Error Processing Request", e);
		}
		
		return dto;
	}
}
