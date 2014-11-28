package org.jboss.ejb3.examples.ch08.statusupdate.mdb;

import java.util.logging.Logger;

import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import junit.framework.TestCase;

import org.jboss.ejb3.examples.ch08.statusupdate.api.StatusUpdate;
import org.junit.Test;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class StatusUpdateUnitTestCase extends StatusUpdateTestBase {

	private static final Logger log = Logger.getLogger(StatusUpdateUnitTestCase.class.getName());
	
	@Test
	public void testUpdateStatusBase() {
		final StatusCachingMessageListener listener = new StatusCachingMessageListener();
		
		final StatusUpdate newStatus = this.getUniqueStatusUpdate();
		this.sendMessage(newStatus, listener);
		
		//Extract out the status sent
		final StatusUpdate roundtrip = listener.getLastStatus();
		TestCase.assertEquals("Status sent was not dispatched and received as expected", newStatus, roundtrip);
	}
	
	@Test
	public void testTwitterUpdateMdb() throws IllegalArgumentException, TwitterException {
		if (!EnvironmentSpecificTwitterClientUtil.isSupportedEnviroment()) {
			log.warning(EnvironmentSpecificTwitterClientUtil.MSG_UNSUPPORTED_ENVIRONMENT);
			return;
		}
		
		//Make a listener (the MDB bean impl class as a POJO
		final TwitterUpdateMdb listener = new TwitterUpdateMdb();
		
		//Manually invoke @PostConstruct
		listener.createTwitterClient();
		
		//Make status update
		final StatusUpdate newStatus = this.getUniqueStatusUpdate();
		
		this.sendMessage(newStatus, listener);
		
		final Twitter twitterClient = EnvironmentSpecificTwitterClientUtil.getTwitterClient();
		this.assertLastUpdateSentToTwitter(twitterClient, newStatus);
	}
	
	private void sendMessage(final StatusUpdate newStatus, final MessageListener listener) {
		final ObjectMessage message = new MockObjectMessage(newStatus);
		
		//Send manually
		listener.onMessage(message);
	}
	
	/**
	 * {@link MessageListener} to be invoked in a POJO environment, where the last incoming status update via
	 * {@link MessageListener#onMessage(javax.jms.Message)} is cached and available for retrieval. Not thread-safe
	 * as this is intended to be used in a single-threaded environment 
	 */
	private static class StatusCachingMessageListener extends StatusUpdateBeanBase {

		private StatusUpdate lastStatus = null;
		
		@Override
		public void updateStatus(StatusUpdate newStatus) throws IllegalArgumentException {
			this.lastStatus = newStatus;
		}
		
		StatusUpdate getLastStatus() {
			return lastStatus;
		}
		
	}
	
}
