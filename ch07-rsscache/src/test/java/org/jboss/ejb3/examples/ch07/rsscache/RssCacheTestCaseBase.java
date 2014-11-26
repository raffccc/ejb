package org.jboss.ejb3.examples.ch07.rsscache;

import java.util.logging.Logger;

public abstract class RssCacheTestCaseBase {
	
	private static final Logger log = Logger.getLogger(RssCacheTestCaseBase.class.getName());
	
	/**
	 * Number of expected RSS entries from the default RSS Feed
	 */
	private static final int EXPECTED_15_RSS_ENTRIES = 15;
	private static final int EXPECTED_5_RSS_ENTRIES = 5;

	static final String FILENAME_RSS_MOCK_FEED_15_ENTRIES = "15_entries.rss";
	static final String FILENAME_RSS_MOCK_FEED_5_ENTRIES = "5_entries.rss";
	static final String FILENAME_RSS_FEED = "feed.rss";
	
	static final int HTTP_TEST_BIND_PORT = 12345;
	
	private static final String CONTENT_TYPE_RSS = "text/rss";
	
	/**
	 * HTTP server used to serve out the mock RSS file
	 */
	static Server httpServer;
	
	private static final char NEWLINE = '\n';

}
