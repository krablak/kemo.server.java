package com.pyjunkies.kemo.server.util;

import static com.pyjunkies.kemo.server.util.CommandLineParams.read;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class CommandLineParamsTest {

	@Test
	public void readWithNullArgs() throws Exception {
		assertFalse(read(null).get("test", "test").isPresent());
		assertFalse(read(null).getBool("test", "test").isPresent());
	}

	@Test
	public void readWithEmptyArgs() throws Exception {
		assertEquals("default_test", read(new String[] {}).get("test").orElse("default_test"));
		assertEquals(Boolean.FALSE, read(new String[] {}).getBool("test").orElse(Boolean.FALSE));
	}

	@Test
	public void readWithSingleArgs() throws Exception {
		assertEquals("value_0", read(new String[] { "test_0", "value_0" }).get("test_0").get());
		assertEquals(Boolean.TRUE, read(new String[] { "test_0", "true" }).getBool("test_0", "test").get());
	}

	@Test
	public void readWithMultipleComplexArgs() throws Exception {
		CommandLineParams cmdParams = read(new String[] {
				"-b", "127.0.0.1",
				"--bind", "127.0.0.1",
				"-m", "prod",
				"--mode", "prod" });

		// Existing args
		assertEquals("127.0.0.1", cmdParams.get("-b", "--bind").orElse("localhost"));
		assertEquals("127.0.0.1", cmdParams.get("-b").orElse("localhost"));
		assertEquals("127.0.0.1", cmdParams.get("--bind").orElse("localhost"));
		assertEquals("prod", cmdParams.get("-m", "--mode").orElse("devel"));
		assertEquals("prod", cmdParams.get("-m").orElse("devel"));
		assertEquals("prod", cmdParams.get("--mode").orElse("devel"));

		// Missing args
		assertFalse(cmdParams.get("notPresentStr").isPresent());
		assertFalse(cmdParams.getBool("notPresentBool").isPresent());
	}

}
