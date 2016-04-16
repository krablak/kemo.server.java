package com.pyjunkies.kemo.server.logging;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.LogManager;

/**
 * Provides way of creation and initialization of logging before first use.
 * 
 * @author krablak
 *
 */
public class LoggerInitiliazer {

	/**
	 * Configures java logging using passed path to configuration file.
	 * 
	 * @param pathToConfiguration
	 *            url to configuration file. In case of <code>null</code> value
	 *            performs default java logging configuration.
	 */
	public static void configureJavaLogging(String pathToConfiguration) {
		if (pathToConfiguration != null && !pathToConfiguration.trim().isEmpty()) {
			URL url = null;
			try {
				// Get URL to given logging file
				url = LoggerInitiliazer.class.getClassLoader().getResource(pathToConfiguration);
				if (url == null) {
					System.err.println(format("Cannot find logging configuration '%s' on classpath.", pathToConfiguration));
				} else {
					// Read configuration file and coofigure logger.
					try (InputStream cfgInpStream = url.openStream()) {
						LogManager.getLogManager().readConfiguration(cfgInpStream);
					}
				}
			} catch (Exception e) {
				System.err.println(format("Cannot configure logger using passed configuration path to '%s'", url));
				e.printStackTrace();
			}
		} else {
			// Perform default logging configuration
			try {
				LogManager.getLogManager().readConfiguration();
			} catch (SecurityException | IOException e) {
				System.err.println("Unexpected error when runnging default logging configuration.");
				e.printStackTrace();
			}
		}
	}

}
