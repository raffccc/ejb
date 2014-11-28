package org.jboss.ejb3.examples.ch08.statusupdate.mdb;

import java.io.Serializable;
import java.util.Enumeration;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

public class MockObjectMessage implements ObjectMessage {

	private static final String MESSAGE_UNSUPPORTED = "This mock implementation does not support this operation";
	
	private final Serializable object;
	
	public MockObjectMessage(final Serializable object) {
		this.object = object;
	}
	
	@Override
	public Serializable getObject() throws JMSException {
		return this.object;
	}
	
	@Override
	public void setObject(Serializable object) throws JMSException {
		callUnsupportedOperationException();
		
	}
	
	@Override
	public String getJMSMessageID() throws JMSException {
		callUnsupportedOperationException();
		return null;
	}

	@Override
	public void setJMSMessageID(String id) throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public long getJMSTimestamp() throws JMSException {
		callUnsupportedOperationException();
		return 0;
	}

	@Override
	public void setJMSTimestamp(long timestamp) throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
		callUnsupportedOperationException();
		return null;
	}

	@Override
	public void setJMSCorrelationIDAsBytes(byte[] correlationID)
			throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public void setJMSCorrelationID(String correlationID) throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public String getJMSCorrelationID() throws JMSException {
		callUnsupportedOperationException();
		return null;
	}

	@Override
	public Destination getJMSReplyTo() throws JMSException {
		callUnsupportedOperationException();
		return null;
	}

	@Override
	public void setJMSReplyTo(Destination replyTo) throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public Destination getJMSDestination() throws JMSException {
		callUnsupportedOperationException();
		return null;
	}

	@Override
	public void setJMSDestination(Destination destination) throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public int getJMSDeliveryMode() throws JMSException {
		callUnsupportedOperationException();
		return 0;
	}

	@Override
	public void setJMSDeliveryMode(int deliveryMode) throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public boolean getJMSRedelivered() throws JMSException {
		callUnsupportedOperationException();
		return false;
	}

	@Override
	public void setJMSRedelivered(boolean redelivered) throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public String getJMSType() throws JMSException {
		callUnsupportedOperationException();
		return null;
	}

	@Override
	public void setJMSType(String type) throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public long getJMSExpiration() throws JMSException {
		callUnsupportedOperationException();
		return 0;
	}

	@Override
	public void setJMSExpiration(long expiration) throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public int getJMSPriority() throws JMSException {
		callUnsupportedOperationException();
		return 0;
	}

	@Override
	public void setJMSPriority(int priority) throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public void clearProperties() throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public boolean propertyExists(String name) throws JMSException {
		callUnsupportedOperationException();
		return false;
	}

	@Override
	public boolean getBooleanProperty(String name) throws JMSException {
		callUnsupportedOperationException();
		return false;
	}

	@Override
	public byte getByteProperty(String name) throws JMSException {
		callUnsupportedOperationException();
		return 0;
	}

	@Override
	public short getShortProperty(String name) throws JMSException {
		callUnsupportedOperationException();
		return 0;
	}

	@Override
	public int getIntProperty(String name) throws JMSException {
		callUnsupportedOperationException();
		return 0;
	}

	@Override
	public long getLongProperty(String name) throws JMSException {
		callUnsupportedOperationException();
		return 0;
	}

	@Override
	public float getFloatProperty(String name) throws JMSException {
		callUnsupportedOperationException();
		return 0;
	}

	@Override
	public double getDoubleProperty(String name) throws JMSException {
		callUnsupportedOperationException();
		return 0;
	}

	@Override
	public String getStringProperty(String name) throws JMSException {
		callUnsupportedOperationException();
		return null;
	}

	@Override
	public Object getObjectProperty(String name) throws JMSException {
		callUnsupportedOperationException();
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enumeration getPropertyNames() throws JMSException {
		callUnsupportedOperationException();
		return null;
	}

	@Override
	public void setBooleanProperty(String name, boolean value)
			throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public void setByteProperty(String name, byte value) throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public void setShortProperty(String name, short value) throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public void setIntProperty(String name, int value) throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public void setLongProperty(String name, long value) throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public void setFloatProperty(String name, float value) throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public void setDoubleProperty(String name, double value) throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public void setStringProperty(String name, String value) throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public void setObjectProperty(String name, Object value)
			throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public void acknowledge() throws JMSException {
		callUnsupportedOperationException();
		
	}

	@Override
	public void clearBody() throws JMSException {
		callUnsupportedOperationException();
		
	}
	
	private void callUnsupportedOperationException() {
		throw new UnsupportedOperationException(MESSAGE_UNSUPPORTED);
	}

}
