package com.pyjunkies.kemo.test.utils.js;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Provides simplified way for building {@link ScriptEngine} instance.
 * 
 * @author krablak
 *
 */
public final class ScriptEngineBuilder {

	/**
	 * Paths to scripts on classpath.
	 */
	private final List<String> scriptsOnClassPath = new LinkedList<>();

	private ScriptEngineBuilder() {
	}

	/**
	 * Creates new builder instance initialized with given paths of scripts on
	 * classpath.
	 * 
	 * @param scriptsOnClassPath
	 *            paths to scripts on classpath.
	 * @return new initialized builder instance.
	 */
	public static ScriptEngineBuilder withScriptsOnClassPath(String... scriptsOnClassPath) {
		ScriptEngineBuilder builder = new ScriptEngineBuilder();
		builder.addScriptsOnClassPath(scriptsOnClassPath);
		return builder;
	}

	/**
	 * Adds paths to scripts on classpath.
	 * 
	 * @param scriptsOnClassPath
	 *            paths to scripts on classpath.
	 * @return current builder instance.
	 */
	public ScriptEngineBuilder addScriptsOnClassPath(String... scriptsOnClassPath) {
		if (scriptsOnClassPath != null) {
			this.scriptsOnClassPath.addAll(asList(scriptsOnClassPath));
		}
		return this;
	}

	/**
	 * Evaluates script on given path on given engine.
	 * 
	 * @param engine
	 *            engine on which script will be evaluated.
	 * @param pathOnClassPath
	 *            path to script on classpath.
	 * @throws ScriptException
	 *             in case of evaluation error.
	 */
	private static void evalOn(ScriptEngine engine, String pathOnClassPath) throws ScriptException {
		try (InputStream scriptStream = ScriptEngineBuilder.class.getResourceAsStream(pathOnClassPath)) {
			engine.eval(new InputStreamReader(scriptStream));
		} catch (IOException e) {
			String errMsg = format("Unexpected error when reading file from classpath at '%s'", pathOnClassPath);
			new RuntimeException(errMsg, e);
		}
	}

	/**
	 * Builds new instance of {@link ScriptEngine} configured with "Nashorn"
	 * engine and builder provided scripts.
	 * 
	 * @return ready to use {@link ScriptEngine} instance.
	 * @throws ScriptException
	 *             in case of error when evaluating script.
	 */
	public ScriptEngine build() throws ScriptException {
		final ScriptEngine newEngine = new ScriptEngineManager().getEngineByName("nashorn");
		for (String curPathOnClassPath : this.scriptsOnClassPath) {
			evalOn(newEngine, curPathOnClassPath);
		}
		return newEngine;
	}

}
