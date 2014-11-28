package org.jboss.ejb3.examples.ch08.messagedestinationlink.slsb;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;

import org.jboss.ejb3.examples.ch08.messagedestinationlink.api.MessageDestinationLinkConstants;

@Stateless(name=MessageSendingBusiness.NAME_EJB)
@Local(MessageSendingBean.class)
public class MessageSendingBean implements MessageSendingBusiness {

	private static final Logger log = Logger.getLogger(MessageSendingBean.class.getName());

	/**
	 * Logical name as wired from the message-destination-link
	 */
	@Resource(name = MessageDestinationLinkConstants.NAME_MESSAGE_DESTINATION_LINK_REF, mappedName=MessageDestinationLinkConstants.NAME_MESSAGE_DESTINATION_LINK_REF)
	private Queue queue;
	
	@Resource(name = MessageDestinationLinkConstants.JNDI_NAME_CONNECTION_FACTORY, mappedName=MessageDestinationLinkConstants.JNDI_NAME_CONNECTION_FACTORY)
	private QueueConnectionFactory connectionFactory;
	
	/*
	 * (non-Javadoc)
	 * @see org.jboss.ejb3.examples.ch08.messagedestinationlink.slsb.MessageSendingBusiness#sendMessage(java.lang.String)
	 */
	@Override
	public void sendMessage(String contents) throws IllegalArgumentException {
		if (contents == null || contents.length() == 0) {
			throw new IllegalArgumentException("contents must be specified");
		}
		
		final QueueConnection connection;
		try {
			connection = connectionFactory.createQueueConnection();
		} catch (JMSException e) {
			throw new RuntimeException("Could not create new connection", e);
		}
		
		final QueueSession session;
		try {
			session = connection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			throw new RuntimeException("Could not create new session", e);
		}
		
		final QueueSender sender;
		try {
			sender = session.createSender(queue);
		} catch (JMSException e) {
			throw new RuntimeException("Could not create new sender", e);
		}
		
		final TextMessage message;
		try {
			message = session.createTextMessage(contents);
		} catch (JMSException e) {
			throw new RuntimeException("Could not create new message", e);
		}
		
		try {
			sender.send(message);
			log.info("Sent to MDB: " + message);
		} catch (JMSException e) {
			throw new RuntimeException("Could not send message", e);
		}
		
		try {
			session.close();
			connection.close();
		} catch (JMSException e) {
			log.log(Level.SEVERE, null, e);
		}
	}

}
