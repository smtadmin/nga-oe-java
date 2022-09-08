package nga.oe.schema.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GumdropMessageDTOTest {

	@Test
	void allConstructorTest() {
		GumdropMessageDTO dto = new GumdropMessageDTO(5, "success");
		assertEquals("success", dto.getState());
		assertEquals(5, dto.getCount());
	}
}
