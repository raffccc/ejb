package org.jboss.ejb3.examples.ch07.rsscache.impl.rome;

import java.net.URL;

import org.jboss.ejb3.examples.ch07.rsscache.impl.rome.RssCacheBean;

public class TestRssCacheBean extends RssCacheBean {
	
	@Override
	public void setUrl(final URL url) throws IllegalArgumentException {
		super.setUrl(url);
	}

}
