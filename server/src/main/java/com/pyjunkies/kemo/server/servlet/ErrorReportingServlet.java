package com.pyjunkies.kemo.server.servlet;

import static com.pyjunkies.kemo.server.logging.AccessLogWriterUtils.getOrCreateLogFile;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

import com.pyjunkies.kemo.server.GlobalSettings;

/**
 * Servlet for logging error reports sent by clients.
 * 
 * @author krablak
 *
 */
public class ErrorReportingServlet extends HttpServlet implements Servlet {

	private static final long serialVersionUID = 1L;

	public static final Logger log = Logger.getLogger(ErrorReportingServlet.class);

	@SuppressWarnings("unused")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.debug("Received error report in request: '%s'");
		if (GlobalSettings.accessLogDir.isPresent()) {
			try (InputStream reqStreamData = req.getInputStream()) {
				getOrCreateLogFile(GlobalSettings.accessLogDir.get(), () -> "error-report.log").ifPresent(file -> {
					try (Writer fileWriter = new FileWriter(file, true)) {
						byte[] contents = new byte[1024];
						int bytesRead = 0;
						while ((bytesRead = req.getInputStream().read(contents)) != -1) {
							fileWriter.write(new String(contents));
							fileWriter.write("\n");
						}
					} catch (IOException e) {
						log.error("Unexpected error when writing error report.", e);
					}
				});
			}
		} else {
			log.error("Log directory is not set and error report cannot be stored.");
		}
	}

}
