package com.pyjunkies.kemo.server.servlet;

import static com.pyjunkies.kemo.server.util.TemplateParameters.params;
import static com.pyjunkies.kemo.templates.RenderUtil.render;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.pyjunkies.kemo.server.GlobalSettings;

/**
 * Provides common helper for servlets.
 * 
 * @author krablak
 *
 */
public final class ServletUtils {

	private ServletUtils() {
	}

	private static Map<String, Object> cachedDefaultParams;

	/**
	 * Returns new instance of parameters map based on default parameters common
	 * for most pages.
	 * 
	 * @return new parameters map.
	 */
	public static Map<String, Object> defaultParams() {
		if (cachedDefaultParams == null) {
			cachedDefaultParams = params().add(coreParams()).add(headerAndFooter()).get();
		}
		return new HashMap<>(cachedDefaultParams);
	}

	/**
	 * Provides function adding core parameters required by every page.
	 * 
	 * @return ready to use function.
	 */
	public static Function<Map<String, Object>, Map<String, Object>> coreParams() {
		return params -> {
			params = params != null ? params : new HashMap<>();
			params.put("prod_mode", GlobalSettings.prodMode);
			params.put("res_version", GlobalSettings.resourcesVersion);
			return params;
		};
	}

	/**
	 * Provides function adding header and footer html parts.
	 * 
	 * @return ready to use function.
	 */
	public static Function<Map<String, Object>, Map<String, Object>> headerAndFooter() {
		return params -> {
			params = params != null ? params : new HashMap<>();
			params.put("header_html", render("web/templates/parts/header.mustache", params, GlobalSettings.prodMode));
			params.put("footer_html", render("web/templates/parts/footer.mustache", params, GlobalSettings.prodMode));
			return params;
		};
	}

}
