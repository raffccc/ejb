package org.jboss.ejb3.examples.ch08.messagedestinationlink;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ejb.EJB;

import junit.framework.TestCase;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.ejb3.examples.ch08.messagedestinationlink.api.MessageDestinationLinkConstants;
import org.jboss.ejb3.examples.ch08.messagedestinationlink.mdb.MessageDestinationLinkMdb;
import org.jboss.ejb3.examples.ch08.messagedestinationlink.slsb.MessageSendingBean;
import org.jboss.ejb3.examples.ch08.messagedestinationlink.slsb.MessageSendingBusiness;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MessageDestinationLinkIntegrationTest {

	private static final Logger log = Logger.getLogger(MessageDestinationLinkIntegrationTest.class.getName());

	private static final String NAME_MDB_ARCHIVE = "messageDestinationLink.jar";
	private static final String QUEUE_DEPLOYMENT_NAME = "hornetq-jms.xml";

	@EJB(mappedName="java:module/MessageSendingEJB!org.jboss.ejb3.examples.ch08.messagedestinationlink.slsb.MessageSendingBusiness")
	private MessageSendingBusiness bean;

	@Deployment
	public static JavaArchive createDeployment() {
		final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, NAME_MDB_ARCHIVE)
				.addClasses(MessageDestinationLinkConstants.class, MessageDestinationLinkMdb.class, MessageSendingBusiness.class, MessageSendingBean.class)
				.addAsResource(QUEUE_DEPLOYMENT_NAME, "queues/" + QUEUE_DEPLOYMENT_NAME);

		log.info("Deploying archive: " + archive.toString(true));
		return archive;
	}

	@Test
	public void testMessageLinkFromSlsbToMdb() throws Exception {
		final String message = "Testing Message Linking";
		bean.sendMessage(message);

		//Wait for the MDB to process, as it's doing so in another Thread
		final boolean processed;
		try {
			log.info("Waiting on the MDB...");
			processed = MessageDestinationLinkMdb.LATCH.await(10, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			// Clear the flag and rethrow; some error in setup is in play
			Thread.interrupted();
			throw new RuntimeException("Thread was interrupted while waiting for MDB processing; should not happen in this test");
		}
		
		if (!processed) {
			TestCase.fail("The MDB did not process the message in the allotted time.");
		}
		
		log.info("MDB sinaled it's done processing, so we can resume");
		
		final String roundtrip = MessageDestinationLinkMdb.LAST_MESSAGE;
		TestCase.assertEquals("Last message sent was not as expected", message, roundtrip);
	}

}