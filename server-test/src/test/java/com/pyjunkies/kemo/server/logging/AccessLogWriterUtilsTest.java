package com.pyjunkies.kemo.server.logging;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

import org.junit.Test;

public class AccessLogWriterUtilsTest {

	@Test
	public void createLogFileName() {
		String logFileName = AccessLogWriterUtils.createLogFileName();
		assertNotNull(logFileName);
		assertTrue(logFileName.endsWith(".log"));
		assertTrue(logFileName.startsWith("access_"));
	}
	
	@Test
	public void getOrCreateLogFileWithNull() {
		assertFalse(AccessLogWriterUtils.getOrCreateLogFile(null, null).isPresent());
		assertFalse(AccessLogWriterUtils.getOrCreateLogFile(null, AccessLogWriterUtils::createLogFileName).isPresent());
	}

	@Test
	public void getOrCreateLogFile() {
		String logDirPath = Paths.get(System.getProperty("java.io.tmpdir"), String.valueOf(new Date().getTime())).toAbsolutePath().toString();
		Optional<File> fileOpt = AccessLogWriterUtils.getOrCreateLogFile(logDirPath, AccessLogWriterUtils::createLogFileName);
		assertTrue(fileOpt.isPresent());
		assertTrue(fileOpt.get().exists());
		fileOpt.get().delete();
	}

}
