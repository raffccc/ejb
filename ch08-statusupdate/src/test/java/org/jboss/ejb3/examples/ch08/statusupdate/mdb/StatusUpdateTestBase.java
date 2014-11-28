package org.jboss.ejb3.examples.ch08.statusupdate.mdb;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.jboss.ejb3.examples.ch08.statusupdate.api.StatusUpdate;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class StatusUpdateTestBase {

	private static final Logger log = Logger.getLogger(StatusUpdateTestBase.class.getName());

	private static final String STATUS_UPDATE_PREFIX_TWITTER = 
			"I'm testing Message-Driven EJBs using JBoss EJB 3.x by @ALRubinger/@OReillyMedia!";
	
	void assertLastUpdateSentToTwitter(final Twitter twitterClient, final StatusUpdate sent) 
	throws TwitterException, IllegalArgumentException {
		if (twitterClient == null) {
			throw new IllegalArgumentException("Twitter client must be specified");
		}
		
		if (sent == null) {
			throw new IllegalArgumentException("Last sent must be specified");
		}
		
		final List<Status> statuses = twitterClient.getUserTimeline(new Paging(1, 1));
		
		TestCase.assertEquals("Should have obtained one status (the most recent) back from request", 1, statuses.size());
		
		final String roundtrip = statuses.get(0).getText();
		final String expected = sent.getText();
		log.info("Sent status update to Twitter: " + expected);
		log.info("Got last status update from Twitter: " + roundtrip);
		
		TestCase.assertEquals("Twitter API did not update with last sent status", expected, roundtrip);
	}
	
	StatusUpdate getUniqueStatusUpdate() {
		return new StatusUpdate(STATUS_UPDATE_PREFIX_TWITTER + UUID.randomUUID().toString());
	}
}
