package com.pyjunkies.kemo.server.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;

import com.pyjunkies.kemo.server.Constants;

/**
 * Provides common helper for servlets.
 * 
 * @author krablak
 *
 */
public final class ServletUtils {

	private ServletUtils() {
	}

	/**
	 * Returns new map with parameters.
	 * 
	 * @param servletConfig
	 *            servlet configuration.
	 * @return map with basic common parameters.
	 */
	static Map<String, Object> newParams(ServletConfig servletConfig) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (servletConfig != null) {
			params.put("prod_mode", Boolean.valueOf(servletConfig.getInitParameter(Constants.Params.MODE_PROD)));
			params.put("res_version", servletConfig.getInitParameter(Constants.Params.MODE_RES_VERSION));
		}
		return params;
	}

}
