package com.pyjunkies.kemo.test.utils.js;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.junit.Assert;

/**
 * Provides shortcuts to junit asserts using JS engine.
 * 
 * @author krablak
 *
 */
public final class AssertJs {

	private AssertJs() {
	}

	public static final void assertTrue(ScriptEngine engine, String expression) throws ScriptException {
		Assert.assertTrue((Boolean) engine.eval(expression));
	}

}
