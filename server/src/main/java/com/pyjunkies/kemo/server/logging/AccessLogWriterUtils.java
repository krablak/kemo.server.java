package com.pyjunkies.kemo.server.logging;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;

import org.jboss.logging.Logger;

import io.undertow.server.HttpServerExchange;

/**
 * Provides utility functions related to {@link AccessLogWriter}
 * 
 * @author krablak
 *
 */
public final class AccessLogWriterUtils {

	public static final Logger log = Logger.getLogger(AccessLogWriterUtils.class);

	interface Constants {
		String LOG_FILE_NAME_FORMAT = "access_%1$tY_%1$tm_%1$te.log";
	}

	private AccessLogWriterUtils() {
	}

	/**
	 * Creates log file name based on current day date.
	 * 
	 * @return log file as string.
	 */
	static String createLogFileName() {
		return format(Constants.LOG_FILE_NAME_FORMAT, new Date());
	}

	/**
	 * Get or creates log file in given log directory.
	 * 
	 * @param logDirPath
	 *            path to logging directory.
	 * @param fileNameSupp
	 *            log file name supplier.
	 * @return optional with ready to use log file.
	 */
	public static Optional<File> getOrCreateLogFile(String logDirPath, Supplier<String> fileNameSupp) {
		Optional<File> logFileOpt = Optional.empty();
		if (logDirPath != null && fileNameSupp != null) {
			File logDir = new File(logDirPath);
			if (!logDir.exists()) {
				logDir.mkdirs();
			}
			File logFile = Paths.get(logDir.getAbsolutePath(), fileNameSupp.get()).toFile();
			if (!logFile.exists()) {
				try {
					logFile.createNewFile();
					logFileOpt = Optional.of(logFile);
				} catch (IOException e) {
					log.errorf("Unexpected error when creating new log file: '%s'", e);
				}
			} else {
				logFileOpt = Optional.of(logFile);
			}
		} else {
			log.errorf("Cannot create logfile for log directory path '%s' and file name supplier '%'.", logDirPath, fileNameSupp);
		}
		return logFileOpt;
	}

	/**
	 * Creates formated access log message from given exchange.
	 * 
	 * @param exchange
	 *            processed exchange.
	 * @param duration
	 *            duration of request processing in business logic.
	 * @return formated log message.
	 */
	public static String toMessage(HttpServerExchange exchange, long duration) {
		StringBuilder strBlrd = new StringBuilder();
		if (exchange != null) {
			Date curDate = new Date();
			// Date time
			strBlrd.append(format("%1$tY-%1$tm-%1$td\t%1$tH:%1$tM:%1$tS:%1$tL", curDate));
			strBlrd.append("\t");
			// HTTP method GET/POST etc.
			strBlrd.append(exchange.getRequestMethod().toString());
			strBlrd.append("\t");
			// Accessed Relative path
			strBlrd.append(exchange.getRelativePath());
			strBlrd.append("\t");
			// Query string
			strBlrd.append(exchange.getQueryString());
			strBlrd.append("\t");
			// Measured request duration in ms
			strBlrd.append(duration);
			strBlrd.append("\t");
			// Source IP
			strBlrd.append(exchange.getSourceAddress().toString());
			strBlrd.append("\t");
			// Language
			String language = exchange.getRequestHeaders().getFirst("Accept-Language");
			strBlrd.append(language != null ? language : "null");
			strBlrd.append("\t");
			// Client user agent
			String userAgent = exchange.getRequestHeaders().getFirst("User-Agent");
			strBlrd.append(userAgent != null ? userAgent : "null");
			strBlrd.append("\t");

		}
		return strBlrd.toString();
	}

}
