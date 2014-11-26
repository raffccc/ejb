package org.jboss.ejb3.examples.ch07.rsscache.impl.rome;

import java.net.URL;
import java.util.List;

public interface RssCacheCommonBusiness {
	
	List<RssEntry> getEntries();
	
	URL getUrl();
	
	void refresh();

}
