package com.pyjunkies.kemo.server.logging;

import org.junit.Test;

public class LoggerInitiliazerTest {

	@Test
	public void justDoNoFailOnInit() {
		LoggerInitiliazer.configureJavaLogging(null);
		LoggerInitiliazer.configureJavaLogging("not url");
		LoggerInitiliazer.configureJavaLogging("notexisting.file.properties");
		LoggerInitiliazer.configureJavaLogging("LoggerInitiliazerTest/existing.properties");
	}

}
