package org.jboss.ejb3.examples.ch07.rsscache.impl.rome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.jboss.ejb3.examples.ch07.rsscache.impl.rome.RssCacheCommonBusiness;
import org.jboss.ejb3.examples.ch07.rsscache.impl.rome.RssEntry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

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

	@BeforeClass
	public static void startHttpServer() {
		final Handler handler = new StaticFileHandler();
		final Server httpServer = new Server(HTTP_TEST_BIND_PORT);
		httpServer.setHandler(handler);
		try {
			httpServer.start();
		} catch (Exception e) {
			throw new RuntimeException("Coult not start server");
		}
		log.info("HTTP Server Started: " + httpServer);
		RssCacheUnitTestCase.httpServer = httpServer;
	}
	
	@BeforeClass
	public static void createRssFeedFile() throws Exception {
		writeToRssFeedFile(getMock15EntriesRssFile());
	}
	
	@AfterClass
	public static void shutdownHttpServer() {
		if (httpServer != null) {
			try {
				httpServer.stop();
			} catch (Exception e) {
				throw new RuntimeException("Coult not stop HTTP Server cleanly", e);
			}
			log.info("HTTP Server Stopped: " + httpServer);
			httpServer = null;
		}
	}

	@AfterClass
	public static void deleteRssFeedFile() throws Exception {
		final File rssFile = getRssFeedFile();
		boolean deleted = rssFile.delete();
		if (!deleted) {
			log.warning("RSS Feed File was not cleaned up properly: " + rssFile);
		}
	}
	
	@Test
	public void testRssEntries() throws Exception {
		log.info("testRssEntries");
		
		final RssCacheCommonBusiness rssCache = this.getRssCacheBean();
		final List<RssEntry> rssEntries = rssCache.getEntries();
		log.info("Got entries: " + rssEntries);
		
		//Ensure they've been specified/initialized, and parsed out in proper size
		this.ensureExpectedEntries(rssEntries, EXPECTED_15_RSS_ENTRIES);
		
		//Swap out the contents of the RSS Feed File, so a refresh will pull in the new contents
		writeToRssFeedFile(getMock5EntriesRssFile());
		rssCache.refresh();
		
		final List<RssEntry> rssEntriesAfterRefresh = rssCache.getEntries();
		log.info("Got entries after refresh: " + rssEntriesAfterRefresh);
		this.ensureExpectedEntries(rssEntriesAfterRefresh, EXPECTED_5_RSS_ENTRIES);
		
		writeToRssFeedFile(getMock15EntriesRssFile());
		rssCache.refresh();
		
		final List<RssEntry> rssEntriesAfterRestored = rssCache.getEntries();
		log.info("Got entries: " + rssEntriesAfterRestored);
		this.ensureExpectedEntries(rssEntriesAfterRestored, EXPECTED_15_RSS_ENTRIES);
	}
	
	private static File getMock15EntriesRssFile() throws Exception {
		return getFileFromBase(FILENAME_RSS_MOCK_FEED_15_ENTRIES);
	}
	
	private static File getMock5EntriesRssFile() throws Exception {
		return getFileFromBase(FILENAME_RSS_MOCK_FEED_5_ENTRIES);
	}

	protected abstract RssCacheCommonBusiness getRssCacheBean();
	
	private void ensureExpectedEntries(final List<RssEntry> entries, final int expectedSize) {
		Assert.assertNotNull("RSS Entries was either not initialized or is returning null", entries);
		final int actualSize = entries.size();
		Assert.assertEquals("Wrong number of RSS entries parsed out from feed", expectedSize, actualSize);
		log.info("Got expected " + expectedSize + " RSS entries");
	}
	
	private static File getFileFromBase(String filename) throws Exception {
		return new File(getBaseDirectory(), filename);
	}
	
	private static URL getCodebaseLocation() {
		return RssCacheUnitTestCase.class.getProtectionDomain().getCodeSource().getLocation();
	}
	
	private static void writeToRssFeedFile(final File templateFile) throws Exception {
		final File rssFile = getRssFeedFile();
		
		final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(rssFile)));
		final BufferedReader reader = new BufferedReader(new FileReader(templateFile));
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			writer.write(line);
			writer.write(NEWLINE);
		}
		
		writer.flush();
		writer.close();
		reader.close();
	}
	
	private static File getRssFeedFile() throws Exception {
		final File baseFile = getBaseDirectory();
		final File rssFile = new File(baseFile, FILENAME_RSS_FEED);
		return rssFile;
	}
	
	private static File getBaseDirectory() throws Exception {
		final URL baseLocation = getCodebaseLocation();
		final URI baseUri = baseLocation.toURI();
		return new File(baseUri);
	}

	private static class StaticFileHandler extends AbstractHandler implements Handler {

		/*
		 * (non-Javadoc)
		 * @see org.mortbay.jetty.Handler#handle(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, int)
		 */
		@Override
		public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response,
				final int dispatch) throws IOException,	ServletException {
			response.setContentType(CONTENT_TYPE_RSS);
			response.setStatus(HttpServletResponse.SC_OK);

			final URL root = getCodebaseLocation();
			final URL fileUrl = new URL(root.toExternalForm() + target);
			
			URI uri = null;
			try {
				uri = fileUrl.toURI();
			} catch (final URISyntaxException urise) {
				throw new RuntimeException(urise);
			}
			
			final File file = new File(uri);
			
			if (!file.exists()) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				log.warning("Requested file is not found: " + file);
				return;
			}
			
			final BufferedReader reader = new BufferedReader(new FileReader(file));
			final PrintWriter writer = response.getWriter();
			String line = null;
			while ((line = reader.readLine()) != null) {
				writer.println(line);
			}
			writer.flush();
			writer.close();
			reader.close();
		}

	}

}
