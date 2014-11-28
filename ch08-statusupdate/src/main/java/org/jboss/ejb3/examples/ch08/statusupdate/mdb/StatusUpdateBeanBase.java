package org.jboss.ejb3.examples.ch08.statusupdate.mdb;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.jboss.ejb3.examples.ch08.statusupdate.api.StatusUpdate;

public abstract class StatusUpdateBeanBase implements MessageListener {

	public abstract void updateStatus(StatusUpdate newStatus) throws IllegalArgumentException, Exception;
	
	@Override
	public void onMessage(final Message message) {
		if (message == null) {
			throw new IllegalArgumentException("Message must be specified");
		}
		
		final ObjectMessage objMessage;
		if (message instanceof ObjectMessage) {
			objMessage = (ObjectMessage)message;
		} else {
			throw new IllegalArgumentException("Specified message must be of type" + ObjectMessage.class.getName());
		}
		
		final Serializable obj;
		try {
			obj = objMessage.getObject();
		} catch (JMSException e) {
			throw new IllegalArgumentException("Could not obtain contents of message " + objMessage);
		}
		
		final StatusUpdate status;
		if (obj instanceof StatusUpdate) {
			status = (StatusUpdate)obj;
		} else {
			throw new IllegalArgumentException("Contents of message should be of type " + StatusUpdate.class.getName() +
					"; was instead " + obj);
		}
		
		try {
			this.updateStatus(status);
		} catch (final Exception e) {
			throw new RuntimeException("Encountered problem with processing status update " + status, e);
		}
	}
	
}
