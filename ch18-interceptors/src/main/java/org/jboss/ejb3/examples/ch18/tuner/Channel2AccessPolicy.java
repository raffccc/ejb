package org.jboss.ejb3.examples.ch18.tuner;

public class Channel2AccessPolicy {
	
	private static boolean channel2Permitted = false;
	
	private Channel2AccessPolicy() {
		throw new UnsupportedOperationException("No instances permitted");
	}

	public static boolean isChannel2Permitted() {
		return channel2Permitted;
	}

	public static void setChannel2Permitted(boolean channel2Permitted) {
		Channel2AccessPolicy.channel2Permitted = channel2Permitted;
	}
	

}
