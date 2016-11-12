package com.pyjunkies.kemo.server.logging;

import static com.pyjunkies.kemo.server.logging.AccessLogWriterUtils.getOrCreateLogFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.logging.Logger;

/**
 * Provides simple way of logging performed requests into file.
 * 
 * @author krablak
 *
 */
public class AccessLogWriter {

	public static final Logger log = Logger.getLogger(AccessLogWriter.class);

	interface Constants {
		/**
		 * Default interval of writing into log file in ms.
		 */
		Integer DEFAULT_WRITE_INTERVAL = 10000;
	}

	/**
	 * Log file.
	 */
	private final Optional<File> logFileOpt;

	/**
	 * Queue with message waiting to be stored in file.
	 */
	private final Queue<String> inputQueue = new ConcurrentLinkedDeque<>();

	/**
	 * Close operation flag.
	 */
	private boolean markAsClosed = false;

	/**
	 * Single thread service for non-blocking log writes.
	 */
	private static ExecutorService execService = Executors.newFixedThreadPool(1);

	/**
	 * Creates and prepares log writer.
	 * 
	 * @param pathToLogDir
	 *            path to log directory.
	 */
	public AccessLogWriter(String pathToLogDir) {
		log.debugf("Creating acces log on in directory path: '%s'", pathToLogDir);
		// Get access to log file
		this.logFileOpt = getOrCreateLogFile(pathToLogDir, AccessLogWriterUtils::createLogFileName);
		// When file is available run endless log writing loop
		if (this.logFileOpt.isPresent()) {
			if (execService.isShutdown() || execService.isTerminated()) {
				execService = Executors.newFixedThreadPool(1);
			}
			execService.submit(loggingRunnable());
		}
	}

	/**
	 * Prepares runnable with endless writing into log.
	 */
	private Runnable loggingRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				try (Writer fileWriter = new FileWriter(logFileOpt.get(), true)) {
					while (true) {
						log.trace("Started logging thread.");
						while (true) {
							String item = AccessLogWriter.this.inputQueue.poll();
							while (item != null) {
								fileWriter.write(item + "\n");
								item = AccessLogWriter.this.inputQueue.poll();
							}
							fileWriter.flush();
							try {
								Thread.sleep(Constants.DEFAULT_WRITE_INTERVAL);
							} catch (InterruptedException e) {
								log.error("Interrupted when waiting for next log writting loop.", e);
							}
						}
					}
				} catch (IOException ioex) {
					log.error("Unexpected error when writing into access log.", ioex);
				}
			}
		};
	}

	/**
	 * Writes message into log.
	 * 
	 * @param message
	 *            message to be written. In case of <code>null</code> message or
	 *            closed writer i writing skipped.
	 */
	public void write(String message) {
		if (message != null && !this.markAsClosed) {
			this.inputQueue.add(message);
		}
	}

	/**
	 * Returns reference to log file if present.
	 * 
	 * @return reference to log file.
	 */
	public Optional<File> getLogFile() {
		return logFileOpt;
	}

	/**
	 * Closes log writer and waits until waiting messages are processed..
	 */
	public void close() {
		log.debug("Closing access log writer.");
		// Mark as closed
		this.markAsClosed = true;
		// Wait until waiting messages are processed
		while (!this.inputQueue.isEmpty()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.error("Interrupted when waiting for next log writting loop.", e);
			}
		}
		// Shutdown thread
		execService.shutdown();
		log.debug("Access log writer closed.");
	}

}
