package org.jboss.ejb3.examples.ch08.statusupdate.mdb;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.MessageListener;

import org.jboss.ejb3.examples.ch08.statusupdate.api.StatusUpdate;
import org.jboss.ejb3.examples.ch08.statusupdate.api.StatusUpdateConstants;

import twitter4j.Twitter;

@MessageDriven(activationConfig={
		@ActivationConfigProperty(propertyName="destinationType", propertyValue=StatusUpdateConstants.TYPE_DESTINATION_STATUSUPDATE),
		@ActivationConfigProperty(propertyName="destination", propertyValue=StatusUpdateConstants.JNDI_NAME_TOPIC_STATUSUPDATE)
	})
public class TwitterUpdateMdb extends StatusUpdateBeanBase implements MessageListener {

	private static final Logger log = Logger.getLogger(TwitterUpdateMdb.class.getName());
	static final String NAME = "TwitterUpdateMdb";
	private Twitter client;
	
	public TwitterUpdateMdb() {}
	
	@PostConstruct
	void createTwitterClient() {
		if (!EnvironmentSpecificTwitterClientUtil.isSupportedEnviroment()) {
			log.warning(EnvironmentSpecificTwitterClientUtil.MSG_UNSUPPORTED_ENVIRONMENT);
			return;
		}
		
		client = EnvironmentSpecificTwitterClientUtil.getTwitterClient();
		log.info("Created Twitter Client " + client);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jboss.ejb3.examples.ch08.statusupdate.api.StatusUpdateBeanBase#updateStatus(org.jboss.ejb3.examples.ch08.statusupdate.api.StatusUpdate)
	 */
	@Override
	public void updateStatus(StatusUpdate newStatus) throws IllegalArgumentException, Exception {
		if (!EnvironmentSpecificTwitterClientUtil.isSupportedEnviroment()) {
			return;
		}
		
		if (client == null) {
			throw new IllegalStateException("Twitter client has not been initialized");
		}
		
		final String status = newStatus.getText();
		client.updateStatus(status);
	}

}
