package nga.oe.schema.vo;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import nga.oe.schema.vo.MachineLogDTO.ClassificationLevel;
import nga.oe.schema.vo.MachineLogDTO.Environment;
import nga.oe.schema.vo.MachineLogDTO.LogLevel;

class MachineLogDTOTest {

	@Test
	void testValid() {
		MachineLogDTO msg = new MachineLogDTO();
		msg.setServiceId("serviceId");
		msg.setLogLevel(LogLevel.SYSTEM);
		msg.setExecutionDateTime(Instant.now());
		msg.setClassificationLevel(ClassificationLevel.UNCLASSIFIED);
		msg.setEventName("eventName");
		msg.setEnvironment(Environment.UC);
		msg.setMicroServiceId("Test");
		msg.setSessionId(UUID.randomUUID());
		assertTrue(msg.isValid());
	}
	
	@Test
	void testInValidServiceId() {
		MachineLogDTO msg = new MachineLogDTO();
		msg.setLogLevel(LogLevel.SYSTEM);
		msg.setExecutionDateTime(Instant.now());
		msg.setClassificationLevel(ClassificationLevel.UNCLASSIFIED);
		msg.setEventName("eventName");
		msg.setEnvironment(Environment.UC);
		msg.setMicroServiceId("Test");
		msg.setSessionId(UUID.randomUUID());
		assertFalse(msg.isValid());
	}
	
	@Test
	void testInValidLogLevel() {
		MachineLogDTO msg = new MachineLogDTO();
		msg.setServiceId("serviceId");
		msg.setExecutionDateTime(Instant.now());
		msg.setClassificationLevel(ClassificationLevel.UNCLASSIFIED);
		msg.setEventName("eventName");
		msg.setEnvironment(Environment.UC);
		msg.setMicroServiceId("Test");
		msg.setSessionId(UUID.randomUUID());
		assertFalse(msg.isValid());
	}
	
	@Test
	void testInValidExecutionDateTime() {
		MachineLogDTO msg = new MachineLogDTO();
		msg.setServiceId("serviceId");
		msg.setLogLevel(LogLevel.SYSTEM);
		msg.setClassificationLevel(ClassificationLevel.UNCLASSIFIED);
		msg.setEventName("eventName");
		msg.setEnvironment(Environment.UC);
		msg.setMicroServiceId("Test");
		msg.setSessionId(UUID.randomUUID());
		assertFalse(msg.isValid());
	}
	
	@Test
	void testInValidClassificationLevel() {
		MachineLogDTO msg = new MachineLogDTO();
		msg.setServiceId("serviceId");
		msg.setLogLevel(LogLevel.SYSTEM);
		msg.setExecutionDateTime(Instant.now());
		msg.setEventName("eventName");
		msg.setEnvironment(Environment.UC);
		msg.setMicroServiceId("Test");
		msg.setSessionId(UUID.randomUUID());
		assertFalse(msg.isValid());
	}
	
	@Test
	void testInValidEventName() {
		MachineLogDTO msg = new MachineLogDTO();
		msg.setServiceId("serviceId");
		msg.setLogLevel(LogLevel.SYSTEM);
		msg.setExecutionDateTime(Instant.now());
		msg.setClassificationLevel(ClassificationLevel.UNCLASSIFIED);
		msg.setEnvironment(Environment.UC);
		msg.setMicroServiceId("Test");
		msg.setSessionId(UUID.randomUUID());
		assertFalse(msg.isValid());
	}

	@Test
	void testInValidContext() {
		MachineLogDTO msg = new MachineLogDTO();
		msg.setServiceId("serviceId");
		msg.setLogLevel(LogLevel.SYSTEM);
		msg.setExecutionDateTime(Instant.now());
		msg.setClassificationLevel(ClassificationLevel.UNCLASSIFIED);
		msg.setEnvironment(Environment.UC);
		assertFalse(msg.isValid());
	}

	@Test
	void testInValidMicroServiceId() {
		MachineLogDTO msg = new MachineLogDTO();
		msg.setServiceId("serviceId");
		msg.setLogLevel(LogLevel.SYSTEM);
		msg.setExecutionDateTime(Instant.now());
		msg.setClassificationLevel(ClassificationLevel.UNCLASSIFIED);
		msg.setEventName("eventName");
		msg.setEnvironment(Environment.UC);
		msg.setSessionId(UUID.randomUUID());
		assertFalse(msg.isValid());
	}
	
	@Test
	void testInValidSessionId() {
		MachineLogDTO msg = new MachineLogDTO();
		msg.setServiceId("serviceId");
		msg.setLogLevel(LogLevel.SYSTEM);
		msg.setExecutionDateTime(Instant.now());
		msg.setClassificationLevel(ClassificationLevel.UNCLASSIFIED);
		msg.setEventName("eventName");
		msg.setEnvironment(Environment.UC);
		msg.setMicroServiceId("Test");
		assertFalse(msg.isValid());
	}
}
