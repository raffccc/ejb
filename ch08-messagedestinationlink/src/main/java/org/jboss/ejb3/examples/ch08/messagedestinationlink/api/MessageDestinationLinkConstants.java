package org.jboss.ejb3.examples.ch08.messagedestinationlink.api;

public interface MessageDestinationLinkConstants {
	
	String JNDI_NAME_CONNECTION_FACTORY = "ConnectionFactory";
	
	//Matches XML message-destination-ref-name
	String NAME_MESSAGE_DESTINATION_LINK_REF ="queue/MessageDestinationLinkQueue";
	
	String TYPE_DESTINATION ="javax.jms.Queue";

}
