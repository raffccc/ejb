package org.jboss.ejb3.examples.ch05.encryption;

import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

public class EncryptionUnitTestCase extends EncryptionTestCaseSupport {

	private static final Logger log = Logger.getLogger(EncryptionUnitTestCase.class.getName());
	
	private static EncryptionBean encryptionService;
	
	@BeforeClass
	public static void initialize() throws Throwable {
		encryptionService = new EncryptionBean();
		encryptionService.initialize();
	}
	
	@Test
	public void testHashing() throws Throwable {
		log.info("testHashing");
		this.assertHashing(encryptionService);
	}
	
	@Test
	public void testEncryption() throws Throwable {
		log.info("testEncryption");
		this.assertEncryption(encryptionService);
	}
	
}
