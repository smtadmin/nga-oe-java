package nga.oe.schema.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BannerMessageDTOTest {

	@Test
	void constructorTest() {
		BannerMessageDTO dto = new BannerMessageDTO("success", "Test");
		assertEquals("success", dto.getState());
		assertEquals("Test", dto.getMsg());
	}
}
