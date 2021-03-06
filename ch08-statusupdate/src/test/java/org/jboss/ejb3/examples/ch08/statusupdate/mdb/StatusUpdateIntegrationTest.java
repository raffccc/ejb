package org.jboss.ejb3.examples.ch08.statusupdate.mdb;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.ejb3.examples.ch08.statusupdate.api.StatusUpdate;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import twitter4j.Twitter;
import twitter4j.auth.OAuthSupport;

@RunWith(Arquillian.class)
@RunAsClient
public class StatusUpdateIntegrationTest extends StatusUpdateTestBase {

	private static final Logger log = Logger.getLogger(StatusUpdateIntegrationTest.class.getName());

	private static final String NAME_MDB_ARCHIVE = "statusUpdateEjb.jar";

	/**
	 * Name of the ClassLoader resource for the deployment descriptor making a new StatusUpdate JMS Topic
	 */
	private static final String NAME_RESOURCE_TOPIC_DEPLOYMENT = "hornetq-jms.xml";

	private static Context NAMING_CONTEXT;
	
	@Before
	public void setNamingContext() throws NamingException {
		NAMING_CONTEXT = new InitialContext();
	}

	//Testable = false means that the test will run in client mode
	@Deployment(testable = false)
	public static JavaArchive getDeployment() {
		final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, NAME_MDB_ARCHIVE)
				.addPackage(StatusUpdate.class.getPackage())
				.addClasses(TwitterUpdateBlockingTestMdb.class, EnvironmentSpecificTwitterClientUtil.class, SecurityActions.class, StatusUpdateBeanBase.class, TwitterUpdateMdb.class, StatusUpdateTestBase.class)
				.addPackages(true, Twitter.class.getPackage(), OAuthSupport.class.getPackage())
				.addAsResource(NAME_RESOURCE_TOPIC_DEPLOYMENT);
		log.info(archive.toString(true));
		return archive;
	}

	@Test
	public void testTwitterUpdateMdb() throws Exception {
		final Twitter twitterClient;
		try {
			twitterClient = EnvironmentSpecificTwitterClientUtil.getTwitterClient();
		} catch (final IllegalStateException ise) {
			log.warning(ise.getMessage() + "skipping...");
			return;
		}

		final StatusUpdate newStatus = this.getUniqueStatusUpdate();

		//Publish the update to a JSM Topic(where it should be consumed by the MDB subscriber)
		this.publishStatusUpdateToTopic(newStatus);

		/*
		 * Wait for the MDB to process, as it's doing so in another Thread.
		 * This is only possible when we test MDBs in the same JVM as the test
		 */
		try {
			log.info("Waiting on the MDB...");
			TwitterUpdateBlockingTestMdb.LATCH.await(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			//Clear the flag and rethrow; some error in setup is in play
			Thread.interrupted();
			throw new RuntimeException("Thread was interrupted while waiting for MDB processing; should not happen in this test");
		}

		log.info("MDB signaled it's done processing, so we can resume");
		this.assertLastUpdateSentToTwitter(twitterClient, newStatus);
	}

	private void publishStatusUpdateToTopic(StatusUpdate status) throws Exception, IllegalArgumentException {
		if (status == null) {
			throw new IllegalArgumentException("status must be provided");
		}

		final TopicConnectionFactory factory = (TopicConnectionFactory) NAMING_CONTEXT.lookup("/ConnectionFactory");
		final Topic topic = (Topic) NAMING_CONTEXT.lookup("/topic/StatusUpdate");
		
		final TopicConnection connection = factory.createTopicConnection();
		final TopicSession sendSession = connection.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
		final TopicPublisher publisher = sendSession.createPublisher(topic);

		final Message message = sendSession.createObjectMessage(status);
		publisher.publish(message);
		log.info("Published message " + message + " with contents: " + status);

		sendSession.close();
		connection.close();
	}

}
