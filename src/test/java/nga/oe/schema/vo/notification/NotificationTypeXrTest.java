package nga.oe.schema.vo.notification;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

/**
 * <b>Title:</b> NotificationTest.java
 * <b>Project:</b> Feeback Subscription
 * <b>Description:</b> Unit Tests providing coverage for the Notification Class
 *
 * <b>Copyright:</b> 2022
 * <b>Company:</b> Silicon Mountain Technologies
 * 
 * @author Eric Damschroder
 * @version 1.0
 * @since Aug 27, 2022
 * @updates
 *
 */
class NotificationTypeXrTest {

	// Members
	NotificationTypeXr n = new NotificationTypeXr();

	/**
	 * Test method for {@link com.smt.ezform.question.Question#prePersist()}.
	 */
	@Test
	void testPrePersist() throws Exception {
		assertDoesNotThrow(() -> {n.prePersist(); });
	}
}
