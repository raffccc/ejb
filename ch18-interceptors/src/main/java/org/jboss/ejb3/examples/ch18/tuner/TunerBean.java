package org.jboss.ejb3.examples.ch18.tuner;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

@Stateless
@Local(TunerLocalBusiness.class)
@Interceptors(CachingAuditor.class)
public class TunerBean implements TunerLocalBusiness {

	private static final Logger log = Logger.getLogger(TunerBean.class.getName());

	@Override
	public InputStream getChannel(final int channel) throws IllegalArgumentException {
		final InputStream stream;
		switch (channel) {
		case 1:
			stream = new InputStream() {
				@Override
				public int read() throws IOException {
					return 1;
				}
			};
			break;
		case 2:
			stream = new InputStream() {
				@Override
				public int read() throws IOException {
					return 2;
				}
			};
			break;
		default: throw new IllegalArgumentException("Not a valid channel: " + channel); 
		}
		
		log.info("Returning stream for Channel " + channel + ": " + stream);
		return stream;
	}

}
