package com.pyjunkies.kemo.server.logging;

import static java.lang.String.format;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.Date;

import org.junit.Test;

public class AccessLogWriterTest {

	@Test
	public void simpleLogging() throws Exception {
		String logDirPath = Paths.get(System.getProperty("java.io.tmpdir"), String.valueOf(new Date().getTime())).toAbsolutePath().toString();
		AccessLogWriter writer = new AccessLogWriter(logDirPath);
		writer.write("Simple log message");
		writer.close();
		assertTrue("Test should just passed with non empty log file.",  writer.getLogFile().get().length() > 0);
	}

	@Test
	public void simpleLoggingWithNull() throws Exception {
		String logDirPath = Paths.get(System.getProperty("java.io.tmpdir"), String.valueOf(new Date().getTime())).toAbsolutePath().toString();

		AccessLogWriter writer = new AccessLogWriter(logDirPath);
		writer.write(null);
		writer.close();
		assertTrue("Test should just passed.", true);
	}

	@Test
	public void simpleLoggingWithLargeData() throws Exception {
		String logDirPath = Paths.get(System.getProperty("java.io.tmpdir"), String.valueOf(new Date().getTime())).toAbsolutePath().toString();

		long startTime = new Date().getTime();
		AccessLogWriter writer = new AccessLogWriter(logDirPath);
		for (int i = 0; i < 1000; i++) {
			writer.write("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		}
		long endTime = new Date().getTime();
		System.out.println(format("Writing duration: %s ms", endTime - startTime));

		long startCloseTime = new Date().getTime();
		writer.close();
		long endCloseTime = new Date().getTime();
		System.out.println(format("Writing duration: %s ms", endCloseTime - startCloseTime));

		assertTrue("Test should just passed with non empty log file.",  writer.getLogFile().get().length() > 0);
	}

}
