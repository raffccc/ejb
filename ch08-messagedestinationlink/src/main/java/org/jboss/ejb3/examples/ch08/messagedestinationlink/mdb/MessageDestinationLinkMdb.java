package org.jboss.ejb3.examples.ch08.messagedestinationlink.mdb;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jboss.ejb3.examples.ch08.messagedestinationlink.api.MessageDestinationLinkConstants;

@MessageDriven(name=MessageDestinationLinkMdb.NAME_EJB, activationConfig = {
		@ActivationConfigProperty(propertyName = "destination", propertyValue = MessageDestinationLinkConstants.NAME_MESSAGE_DESTINATION_LINK_REF),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = MessageDestinationLinkConstants.TYPE_DESTINATION)
})
public class MessageDestinationLinkMdb implements MessageListener {

	private static final Logger log = Logger.getLogger(MessageDestinationLinkMdb.class.getName());
	
	/**
	 * Shared latch, so tests can wait until the MDB is processed. In POJO testing
	 * this is wholly unnecessary as we've got a single-threaded environment, but
	 * when testing in an EJB Container running in the *same* JVM as the test,
	 * the test can use this to wait until the MDB has been invoked, strengthening
	 * the integrity of the test. It's not recommended to put this piece into a production EJB;
	 * instead test an extension of your EJB which adds this (and only this) support.
	 */
	public static CountDownLatch LATCH = new CountDownLatch(1);
	
	static final String NAME_EJB = "MessageDestinationLinkMdb";

	/**
	 * Last message received; never use this in production (as many Threads/instances may concurrently have access)
	 */
	public static String LAST_MESSAGE;
	
	/*
	 * (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message message) {
		if (!(message instanceof TextMessage)) {
			throw new IllegalArgumentException("Expecting message of type " + TextMessage.class.getName() + "; got: " + message);
		}
		
		final TextMessage txtMessage = (TextMessage) message;
		final String contents;
		try {
			contents = txtMessage.getText();
		} catch (final JMSException e ) {
			throw new RuntimeException("Could not get contents of message: " + txtMessage, e);
		}
		log.info("Received message with contents: " + contents);
		LAST_MESSAGE = contents;
		
		log.info("Counting down the latch...");
		LATCH.countDown();
	}

}
