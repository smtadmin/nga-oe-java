package nga.oe.service;

import java.util.List;

import nga.oe.schema.exception.AppSchemaException;
import nga.oe.schema.vo.RequestDTO;

public interface RequestServiceImpl<T> {

	public List<T> processRequest(RequestDTO req) throws AppSchemaException;
}
