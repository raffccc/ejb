package org.jboss.ejb3.examples.ch08.statusupdate.api;


public interface StatusUpdateConstants {
	
	/**
	 * JNDI Name of the pub/sub Topic for status updates
	 */
	String JNDI_NAME_TOPIC_STATUSUPDATE = "topic/StatusUpdate";
	
	/**
	 * The type of destination used by StatusUpdate MDB implementations
	 */
	String TYPE_DESTINATION_STATUSUPDATE = "javax.jms.Topic";
	
	/**
	 * The JMX {@link ObjectName} which will be used as a dependency name for the topic
	 */
	String OBJECT_NAME_TOPIC_STATUSUPDATE = "jboss.messaging.destination:service=Topic,name=StatusUpdate";

}
