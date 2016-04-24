package com.pyjunkies.kemo.js.crypto;

import static com.pyjunkies.kemo.test.utils.js.AssertJs.assertTrue;
import static com.pyjunkies.kemo.test.utils.js.ScriptEngineBuilder.withScriptsOnClassPath;

import javax.script.ScriptEngine;

import org.junit.Test;

public class KemoEncryptionJsTest {

	@Test
	public void simpleEncryptionDecryption() throws Exception {
		ScriptEngine engine = withScriptsOnClassPath(
				"/web/static/dev/js/thirdparty/forge/forge.min.js",
				"/web/static/dev/js/kemo.client.encryption.js").build();
		assertTrue(engine, "kemo !== undefined");
		assertTrue(engine, "kemo.encryption !== undefined");
		assertTrue(engine, "kemo.encryption.encrypt('key','some message to be encrypted') !== undefined");
	}

}
