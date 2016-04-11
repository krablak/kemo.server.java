package com.pyjunkies.kemo.server.util;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * Provides access to command line args.
 * 
 * @author krablak
 *
 */
public class CommandLineParams {

	/**
	 * Parsed parameters as map.
	 */
	private final Map<String, String> params;

	private CommandLineParams(String[] args) {
		this.params = parse(args);
	}

	/**
	 * Reads arguments as map.
	 */
	private Map<String, String> parse(String[] args) {
		Map<String, String> parsedMap = new HashMap<>();
		if (args != null) {
			Iterator<String> argsIter = asList(args).iterator();
			while (argsIter.hasNext()) {
				String key = argsIter.next();
				if (argsIter.hasNext()) {
					String value = argsIter.next();
					parsedMap.put(key, value);
				}
			}
		}
		return parsedMap;
	}

	/**
	 * Reads command line from given arguments array.
	 * 
	 * @param args
	 *            command line arguments.
	 * @return new instance with parsed values.
	 */
	public static CommandLineParams read(String[] args) {
		return new CommandLineParams(args);
	}

	public Optional<String> get(String... nameVariants) {
		return findFirst(nameVariants);
	}

	public Optional<Boolean> getBool(String... nameVariants) {
		Optional<Boolean> found = Optional.empty();
		Optional<String> foundStrOpt = findFirst(nameVariants);
		if (foundStrOpt.isPresent()) {
			found = Optional.ofNullable(Boolean.valueOf(foundStrOpt.get()));
		}
		return found;
	}

	/**
	 * Finds first value for given parameter names variants.
	 */
	private Optional<String> findFirst(String... nameVariants) {
		Optional<String> foundRes = Optional.empty();
		if (nameVariants != null) {
			for (String curNameVariant : nameVariants) {
				String curVarValue = this.params.get(curNameVariant);
				if (curVarValue != null) {
					foundRes = Optional.of(curVarValue);
					break;
				}
			}
		}
		return foundRes;
	}

}
