package org.jboss.ejb3.examples.ch05.encryption;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.ejb.EJB;

import junit.framework.TestCase;

import org.apache.commons.codec.BinaryEncoder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

public class EncryptionIntegrationTestCase extends EncryptionTestCaseSupport {
	
	private static final Logger log = Logger.getLogger(EncryptionIntegrationTestCase.class.getName());
	
	/**
	 * The EJB 3.x local business view of the EncryptionEJB
	 */
	@EJB
	private static EncryptionLocalBusiness encryptionLocalBusiness;
	
	/**
	 * Correlates to the envy-entry within ejb-jar.xml, to be used as an override from the default
	 */
	private static final String EXPECTED_CIPHERS_PASSPHRASE = "OverriddenPassword";
	private static final String EXPECTED_ALGORITHM_MESSAGE_DIGEST = "SHA";
	
	@Deployment
	public static JavaArchive createDeployment() throws MalformedURLException {
		final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "slsb.jar")
				.addPackage(EncryptionBean.class.getPackage())
				.addAsManifestResource(new URL(EncryptionIntegrationTestCase.class.getProtectionDomain().getCodeSource().getLocation(),
						"../classes/META-INF/ejb-jar.xml"), "ejb-jar.xml")
				.addPackages(true, BinaryEncoder.class.getPackage());
		
		log.info(archive.toString(true));
		return archive;
	}
	
	@Test
	public void testHashing() throws Throwable {
		log.info("testHashing");
		this.assertHashing(encryptionLocalBusiness);
	}
	
	@Test
	public void testEncryption() throws Throwable {
		log.info("testEncryption");
		this.assertEncryption(encryptionLocalBusiness);
	}
	
	/**
	 * Ensures that the hashing algorithm was overridden from the environment entry declared in ejb-jar.xml
	 */
	@Test
	public void testMessageDigestAlgorithmOverride() throws Throwable {
		log.info("testMesageDigestAlgorithmOverride");
		
		//Get the algorithm used
		final String algorithm = encryptionLocalBusiness.getMessageDigestAlgorithm();
		log.info("Using MessageDigest algorithm: " + algorithm);
		
		TestCase.assertEquals("MessageDigest algorithm should have been overridden from the environment entry", 
				EXPECTED_ALGORITHM_MESSAGE_DIGEST, algorithm);
	}
	
	@Test
	public void testCiphersPassphraseOverride() throws Throwable {
		log.info("testCiphersPassphraseOverride");
		
		//Get the algorithm used
		final String passphrase = encryptionLocalBusiness.getCiphersPassphrase();
		log.info("Using Encryption passphrase: " + passphrase);
		
		TestCase.assertEquals("Encryption passphrase should have been overridden from the environment entry", 
				EXPECTED_CIPHERS_PASSPHRASE, passphrase);
	}

}