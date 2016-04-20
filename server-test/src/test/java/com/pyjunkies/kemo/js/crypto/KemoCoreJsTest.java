package com.pyjunkies.kemo.js.crypto;

import static com.pyjunkies.kemo.test.utils.js.AssertJs.assertTrue;
import static com.pyjunkies.kemo.test.utils.js.ScriptEngineBuilder.withScriptsOnClassPath;

import javax.script.ScriptEngine;

import org.junit.Test;

public class KemoCoreJsTest {

	@Test
	public void kemoCoreModuleSmokeTest() throws Exception {
		ScriptEngine engine = withScriptsOnClassPath("/web/static/dev/js/kemo.core.js").build();
		assertTrue(engine, "kemo.core !== undefined");
		assertTrue(engine, "kemo.core.ready !== undefined");
	}

}
