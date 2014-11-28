package org.jboss.ejb3.examples.ch08.statusupdate.mdb;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.MessageListener;

import org.jboss.ejb3.examples.ch08.statusupdate.api.StatusUpdate;
import org.jboss.ejb3.examples.ch08.statusupdate.api.StatusUpdateConstants;

@MessageDriven(name = TwitterUpdateMdb.NAME, activationConfig = {
		@ActivationConfigProperty(propertyName="destinationType", propertyValue=StatusUpdateConstants.TYPE_DESTINATION_STATUSUPDATE),
		@ActivationConfigProperty(propertyName="destination", propertyValue=StatusUpdateConstants.JNDI_NAME_TOPIC_STATUSUPDATE),
})
public class TwitterUpdateBlockingTestMdb extends TwitterUpdateMdb implements MessageListener {

	private static final Logger log = Logger.getLogger(TwitterUpdateBlockingTestMdb.class.getName());

	public static CountDownLatch LATCH = new CountDownLatch(1);

	public void updateStatus(final StatusUpdate newStatus) throws IllegalArgumentException, Exception {
		try {
			super.updateStatus(newStatus);
		} finally {
			log.info("Couting down the latch...");
			LATCH.countDown();
		}
	}


}
