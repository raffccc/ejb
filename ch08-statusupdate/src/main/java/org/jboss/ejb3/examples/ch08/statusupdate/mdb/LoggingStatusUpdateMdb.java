package org.jboss.ejb3.examples.ch08.statusupdate.mdb;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.jboss.ejb3.examples.ch08.statusupdate.api.StatusUpdate;
import org.jboss.ejb3.examples.ch08.statusupdate.api.StatusUpdateConstants;

/**
 * An MDB which, {@link MessageListener#onMessage(Message)} will
 * log out the status update at INFO-level
 */
@MessageDriven(activationConfig={
	@ActivationConfigProperty(propertyName="destinationType", propertyValue=StatusUpdateConstants.TYPE_DESTINATION_STATUSUPDATE),
	@ActivationConfigProperty(propertyName="destination", propertyValue=StatusUpdateConstants.JNDI_NAME_TOPIC_STATUSUPDATE)
})
public class LoggingStatusUpdateMdb extends StatusUpdateBeanBase implements	MessageListener {

	private static final Logger log = Logger.getLogger(LoggingStatusUpdateMdb.class.getName());
	
	@Override
	public void updateStatus(final StatusUpdate newStatus) throws IllegalArgumentException {
		if (newStatus == null) {
			throw new IllegalArgumentException("status must be specified");
		}
		
		final String status = newStatus.getText();
		
		log.info("New status received: \"" + status + "\"");
	}

}
