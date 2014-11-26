package org.jboss.ejb3.examples.ch07.rsscache.impl.rome;

import java.net.URL;

public interface RssEntry {
	
	String getAuthor();
	
	String getTitle();
	
	URL getUrl();
	
	String getDescription();

}
