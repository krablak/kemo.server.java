package com.pyjunkies.kemo.templates;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;

/**
 * Provides helper methods related to templates rendering.
 * 
 * @author krablak
 *
 */
public final class RenderUtil {

	private static MustacheFactory mf = new DefaultMustacheFactory();

	private RenderUtil() {
	}

	/**
	 * Renders given template with passed parameters into provided output.
	 * 
	 * @param templatePath
	 *            path to template on classpath.
	 * @param params
	 *            map with template parameters.
	 * @param out
	 *            output where to write template rendering result.
	 */
	public static void render(String templatePath, Map<String, Object> params, Writer out) {
		render(templatePath, params, out, false);
	}

	/**
	 * Renders given template with passed parameters into provided output.
	 * 
	 * @param templatePath
	 *            path to template on classpath.
	 * @param params
	 *            map with template parameters.
	 * @param out
	 *            output where to write template rendering result.
	 * @param devel
	 *            in case of <code>true</code> value templates caching will be
	 *            disabled.
	 */
	public static void render(String templatePath, Map<String, Object> params, Writer out, boolean devel) {
		try {
			if (devel) {
				new DefaultMustacheFactory().compile(templatePath).execute(out, params != null ? params : new HashMap<String, Object>()).flush();
			} else {
				mf.compile(templatePath).execute(out, params != null ? params : new HashMap<String, Object>()).flush();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Renders given template with passed parameters and returns result as
	 * string.
	 * 
	 * @param templatePath
	 *            path to template on classpath.
	 * @param params
	 *            map with template parameters.
	 * @param devel
	 *            in case of <code>true</code> value templates caching will be
	 *            disabled.
	 * @return result of template rendering as string.
	 */
	public static String render(String templatePath, Map<String, Object> params, boolean devel) {
		Writer outWriter = new StringWriter();
		render(templatePath, params, outWriter, devel);
		return outWriter.toString();
	}

}
