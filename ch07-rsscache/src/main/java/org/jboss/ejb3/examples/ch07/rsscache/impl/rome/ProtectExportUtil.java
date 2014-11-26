package org.jboss.ejb3.examples.ch07.rsscache.impl.rome;

import java.net.MalformedURLException;
import java.net.URL;

class ProtectExportUtil {
	
	private ProtectExportUtil() {}
	
	static URL copyURL(final URL url) {
		if (url == null) {
			return url;
		}
		
		try {
			return new URL(url.toExternalForm());
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error in copying URL", e);
		}
	}

}
