package com.pyjunkies.kemo.server.util;

import static com.pyjunkies.kemo.server.util.TemplateParameters.params;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

public class TemplateParametersTest {

	@Test
	public void noFunctionCreation() {
		Map<String, ?> params = params().get();
		assertNotNull(params);
		assertTrue(params.isEmpty());
	}

	@Test
	public void emptyFunctionCreation() {
		Map<String, ?> params = params().add(map -> map).get();
		assertNotNull(params);
		assertTrue(params.isEmpty());
	}

	@Test
	public void multipleAdds() {
		Map<String, ?> params = params()
				.add(map -> {
					map.put("test_0", "test_value_0");
					return map;
				}).add(map -> {
					map.put("test_1", "test_value_1");
					return map;
				}).get();
		assertNotNull(params);
		assertEquals("test_value_0", params.get("test_0"));
		assertEquals("test_value_1", params.get("test_1"));
	}

	@Test(expected = RuntimeException.class)
	public void addFunctionWithException() {
		params().add(map -> {
					map.put("test_0", "test_value_0");
					return map;
				}).add(map -> {
					map.put("test_1", "test_value_1");
					return map;
				}).add(map -> {
					throw new RuntimeException();
				}).get();
	}

}
