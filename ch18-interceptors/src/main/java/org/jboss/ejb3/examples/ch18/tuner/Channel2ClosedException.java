package org.jboss.ejb3.examples.ch18.tuner;

import javax.ejb.ApplicationException;

@ApplicationException
public class Channel2ClosedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public static final Channel2ClosedException INSTANCE = new Channel2ClosedException();
	
	private static final String MSG = "Channel 2 is not currently available for viewing";
	
	private Channel2ClosedException() {
		super(MSG);
	}

}
