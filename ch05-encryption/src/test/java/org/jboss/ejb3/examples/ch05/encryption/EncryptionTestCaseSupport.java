package org.jboss.ejb3.examples.ch05.encryption;

import java.util.logging.Logger;

import junit.framework.TestCase;

/**
 * Common base for centralizing test logic used
 * for the Encryption POJO and EncryptionEJB
 */
public class EncryptionTestCaseSupport {
	
	private static final Logger log = Logger.getLogger(EncryptionTestCaseSupport.class.getName());
	
	private static final String TEST_STRING = "EJB 3.1 Examples Test String";
	
	protected void assertHashing(final EncryptionCommonBusiness service) throws Throwable {
		log.info("assertHashing");
		
		final String input = TEST_STRING;
		final String hash = service.hash(input);
		log.info("Hash of \"" + input + "\": " + hash);
		
		TestCase.assertNotSame("The hash function had no effect upon the supplied input", input, hash);
		TestCase.assertTrue("The comparison of the input to its hashed result failed", service.compare(hash, input));
	}
	
	protected void assertEncryption(final EncryptionCommonBusiness service) throws Throwable {
		log.info("assertEncryption");
		
		final String input = TEST_STRING;
		
		final String encrypted = service.encrypt(input);
		log.info("Encrypted result of \"" + input + "\": " + encrypted);
		
		TestCase.assertNotSame("The encryption function had no effect upon the supplied input", input, encrypted);
		
		//Get the round-trip result
		final String roundTrip = service.decrypt(encrypted);
		
		TestCase.assertEquals("The comparison of the input to its encrypted result failed", input, roundTrip);
	}

}
