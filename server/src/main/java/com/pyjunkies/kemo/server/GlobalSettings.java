package com.pyjunkies.kemo.server;

/**
 * Provides global application settings.
 * 
 * @author krablak
 *
 */
public class GlobalSettings {

	/**
	 * Flag says if application is in production mode.
	 */
	public static boolean prodMode = false;

	/**
	 * Static resources version.
	 */
	public static String resourcesVersion = String.valueOf(System.currentTimeMillis());

}
