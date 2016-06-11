package com.pyjunkies.kemo.server.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

/**
 * Builds and provides template parameters.
 * 
 * @author krablak
 *
 */
public class TemplateParameters {

	/**
	 * List of function which will built template parameters.
	 */
	private final LinkedList<Function<Map<String, Object>, Map<String, Object>>> paramsFunctions = new LinkedList<>();

	private TemplateParameters() {
	}

	/**
	 * Creates new {@link TemplateParameters} instance.
	 * 
	 * @return new ready to use instance of {@link TemplateParameters#}
	 */
	public static TemplateParameters params() {
		return new TemplateParameters();
	}

	/**
	 * Adds new parameters providing function.
	 * 
	 * @param addParamsFunction
	 *            function which gets map with parameters and returns it's
	 *            extended instance.
	 * @return reference to current instance.
	 */
	public TemplateParameters add(Function<Map<String, Object>, Map<String, Object>> addParamsFunction) {
		if (addParamsFunction != null) {
			paramsFunctions.add(addParamsFunction);
		}
		return this;
	}

	/**
	 * Returns new map create by applying all available functions.
	 * 
	 * @return parameters map or empty map.
	 */
	public Map<String, Object> get() {
		Map<String, Object> params = new HashMap<>();
		for (Function<Map<String, Object>, Map<String, Object>> curFunc : this.paramsFunctions) {
			params = curFunc.apply(params);
		}
		return params;
	}

}
