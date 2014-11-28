package org.jboss.ejb3.examples.ch08.messagedestinationlink.slsb;

import javax.jms.TextMessage;

public interface MessageSendingBusiness {

	String NAME_EJB = "MessageSendingEJB";
	
	String NAME_JNDI = NAME_EJB + "/local";
	
	/**
	 * Sends a {@link TextMessage} with the specified contents to the
	 * message destination link as configured by ejb-jar.xml
	 */
	void sendMessage(String contents) throws IllegalArgumentException;
}
