package com.pyjunkies.kemo.server.servlet;

import static com.pyjunkies.kemo.server.servlet.ServletUtils.defaultParams;
import static com.pyjunkies.kemo.templates.RenderUtil.render;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyjunkies.kemo.server.GlobalSettings;

/**
 * Handles requests on welcome page.
 * 
 * @author krablak
 *
 */
public class WelcomeServlet extends HttpServlet implements Servlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try (PrintWriter out = resp.getWriter()) {
			resp.setContentType("text/html");
			Map<String, Object> params = defaultParams();
			if (GlobalSettings.prodMode) {
				params.put("chat_frame_url", "https://kemoundertow-krablak.rhcloud.com/embedded");
			}
			render("web/templates/index.mustache", params, resp.getWriter(), !GlobalSettings.prodMode);
		}
	}

}
