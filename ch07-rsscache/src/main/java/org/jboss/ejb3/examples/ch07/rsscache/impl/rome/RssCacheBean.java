package org.jboss.ejb3.examples.ch07.rsscache.impl.rome;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.fetcher.impl.HttpClientFeedFetcher;
import com.sun.syndication.io.FeedException;

@Singleton
@Startup
@Local(RssCacheCommonBusiness.class)
//This annotation isn't necessary since Container Concurrency Management is the default
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class RssCacheBean implements RssCacheCommonBusiness {
	
	private static final Logger log = Logger.getLogger(RssCacheBean.class.getName());
	private URL url;
	private List<RssEntry> entries;
	
	@Lock(LockType.READ)
	@Override
	public List<RssEntry> getEntries() {
		return this.entries;
	}
	
	@Lock(LockType.READ)
	@Override
	public URL getUrl() {
		return ProtectExportUtil.copyURL(this.url);
	}
	
	void setUrl(final URL url) throws IllegalArgumentException {
		this.url  = url;
		this.refresh();
	}
	
	@PostConstruct
	@Lock(LockType.WRITE)
	@Override
	public void refresh() {
		final URL url = this.url;
		if (url == null) {
			throw new IllegalStateException("The feed URL has not been set");
		}
		log.info("Requested: " + url);
		
		final FeedFetcher feedFetcher = new HttpClientFeedFetcher();
		SyndFeed feed = null;
		try {
			feed = feedFetcher.retrieveFeed(this.url);
		} catch (IllegalArgumentException | IOException | FeedException	| FetcherException e) {
			throw new RuntimeException(e);
		}
		
		final List<RssEntry> rssEntries = new ArrayList<RssEntry>();
		
		@SuppressWarnings("unchecked")
		final List<SyndEntry> list = feed.getEntries();
		for (SyndEntry entry : list) {
			final RssEntry rssEntry = new RomeRssEntry(entry);
			rssEntries.add(rssEntry);
			log.fine("Found new RSS Entry: " + rssEntry);
		}
		
		final List<RssEntry> protectedEntries = Collections.unmodifiableList(rssEntries);
		this.entries = protectedEntries;
	}

}
