package org.jboss.ejb3.examples.ch07.rsscache.impl.rome;

import java.net.MalformedURLException;
import java.net.URL;

import com.sun.syndication.feed.synd.SyndEntry;

public class RomeRssEntry implements RssEntry {

	private String author;
	private String description;
	private String title;
	private URL url;
	
	RomeRssEntry(final SyndEntry entry) throws IllegalArgumentException {
		this.author = entry.getAuthor();
		this.description = entry.getDescription().getValue();
		this.title = entry.getTitle();
		
		final String urlString = entry.getLink();
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Obtained invalid URL from Rome RSS entry: " + entry, e);
		}
		this.url = url;
	}
	
	@Override
	public String getAuthor() {
		return this.author;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public URL getUrl() {
		return ProtectExportUtil.copyURL(this.url);
	}

	@Override
	public String getDescription() {
		return this.description;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.getTitle());
		sb.append(" - ");
		sb.append(this.url.toExternalForm());
		return sb.toString();
	}

}