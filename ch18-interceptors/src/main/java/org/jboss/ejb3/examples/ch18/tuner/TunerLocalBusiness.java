package org.jboss.ejb3.examples.ch18.tuner;

import java.io.InputStream;

public interface TunerLocalBusiness {
	
	InputStream getChannel(int channel) throws IllegalArgumentException;

}
