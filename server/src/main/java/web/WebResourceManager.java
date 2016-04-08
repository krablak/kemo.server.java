package web;

import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceManager;

public class WebResourceManager extends ClassPathResourceManager implements ResourceManager {

	public WebResourceManager() {
		super(WebResourceManager.class.getClassLoader(), WebResourceManager.class.getPackage());
	}
}
