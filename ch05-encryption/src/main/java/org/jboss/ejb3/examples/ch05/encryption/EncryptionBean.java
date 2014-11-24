package org.jboss.ejb3.examples.ch05.encryption;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.apache.commons.codec.binary.Base64;

/**
 * Bean implementation class of the EncryptionEJB. Shows
 * how lifecycle callbacks are implemented (@PostConstruct),
 * and two ways of obtaining externalized environment entries. 
 */
/*
 * The name() attribute identifies the EJB name of the session bean, it defaults to the 
 * unqualified(simple) name of the bean class if you initialize this attribute; in this case, 
 * the EJB name would default to EncryptionBean
 */
@Stateless(name="EncryptionEJB")
@Local(EncryptionLocalBusiness.class)
@Remote(EncryptionRemoteBusiness.class)
public class EncryptionBean implements EncryptionLocalBusiness,	EncryptionRemoteBusiness {

	private Logger log = Logger.getLogger(EncryptionBean.class.getName());

	/**
	 * Name of the environment entry representing the ciphers' passphrase supplied
	 * in ejb-jar.xml
	 */
	private static final String ENV_ENTRY_NAME_CIPHERS_PASSPHRASE = "ciphersPassphrase";

	private static final String DEFAULT_ALGORITHM_MESSAGE_DIGEST = "MD5";

	/**
	 * Charset used for encoding/decoding Strings to/from byte representation
	 */
	private static final String CHARSET = "UTF-8";

	/**
	 * Algorithm used by the Cipher Key for symmetric encryption
	 */
	private static final String DEFAULT_ALGORITHM_CIPHER = "PBEWithMD5AndDES";

	private static final String DEFAULT_PASPHRASE = "LocalTestingPassphrase";

	private static final byte[] DEFAULT_SALT_CIPHERS = { (byte) 0xB4, (byte) 0xA2, (byte) 0x43, (byte) 0x89, (byte) 0x3E, (byte) 0xC5, 
		(byte) 0x78, (byte) 0x53};

	private static final int DEFAULT_ITERATION_COUNT_CIPHERS = 20;

	@Resource
	private SessionContext context;

	/**
	 * Passphrase to use for the key in cipher operations; lazily
	 * initialized and loaded via SessionContext.lookup() since it
	 * doesn't have the @Resource annotation
	 */
	private String ciphersPassphrase;

	/**
	 * Algotirhm to use in message digest (hash) operations, injected
	 * via @Resource annotation, it automatically injects the value
	 * from the ejb-jar.xml
	 */
	@Resource
	private String messageDigestAlgorithm;

	/**
	 * Digest used for one-way hasing
	 */
	private MessageDigest messageDigest;

	private Cipher encryptionCipher;

	private Cipher decryptionCipher;

	/**
	 * Initializes this service before it may handle requests
	 */
	@PostConstruct
	public void initialize() throws Exception {
		//Log that we're here
		log.info("Initializing, part of " + PostConstruct.class.getName() + " lifecycle");

		initSymmetricEncryption();
		initOneWayHashing();
	}

	private void initSymmetricEncryption() throws Exception {
		//Obtain parameters used in initializing the ciphers
		final String cipherAlgorithm = DEFAULT_ALGORITHM_CIPHER;
		final byte[] ciphersSalt = DEFAULT_SALT_CIPHERS;
		final int ciphersIterationCount = DEFAULT_ITERATION_COUNT_CIPHERS;
		final String ciphersPassphrase = this.getCiphersPassphrase();

		//Obtain key and param spec for the ciphers
		final KeySpec ciphersKeySpec = new PBEKeySpec(ciphersPassphrase.toCharArray(), ciphersSalt, ciphersIterationCount);
		final SecretKey ciphersKey = SecretKeyFactory.getInstance(cipherAlgorithm).generateSecret(ciphersKeySpec);
		final AlgorithmParameterSpec paramSpec = new PBEParameterSpec(ciphersSalt, ciphersIterationCount);

		//Create and init the ciphers
		this.encryptionCipher = Cipher.getInstance(ciphersKey.getAlgorithm());
		this.decryptionCipher = Cipher.getInstance(ciphersKey.getAlgorithm());
		encryptionCipher.init(Cipher.ENCRYPT_MODE, ciphersKey, paramSpec);
		encryptionCipher.init(Cipher.DECRYPT_MODE, ciphersKey, paramSpec);

		log.info("Initialized encryption cipher: " + this.encryptionCipher);
		log.info("Initialized decryption cipher: " + this.decryptionCipher);
	}

	private void initOneWayHashing() {
		//Get the algorithm for the MessageDigest
		final String messageDigestAlgorithm = this.getMessageDigestAlgorithm();

		//Create the MessageDigest
		try {
			this.messageDigest = MessageDigest.getInstance(messageDigestAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Could not obtain the " + MessageDigest.class.getSimpleName() + 
					" for algorithm: " + messageDigestAlgorithm, e);
		}

		log.info("Initialized MessageDigest for one-way hashing: " + this.messageDigest);
	}

	@Override
	public String encrypt(String input) throws IllegalArgumentException, EncryptionException {
		final Cipher cipher = this.encryptionCipher;
		if (cipher == null) {
			throw new IllegalStateException("Encryption cipher not available, has this service been initialized?");
		}

		final byte[] inputBytes = this.stringToByteArray(input);

		//Run the cipher
		byte[] resultBytes = null;
		try {
			resultBytes = Base64.encodeBase64(cipher.doFinal(inputBytes));
		} catch (final Throwable t) {
			throw new EncryptionException("Error in encryption of: " + input, t);
		}

		final String result = this.byteArrayToString(resultBytes);

		log.info("Encryption on \"" + input + "\": " + result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ejb3.examples.ch05.encryption.EncryptionCommonBusiness#decrypt(java.lang.String)
	 */
	@Override
	public String decrypt(String input) throws EncryptionException {
		final Cipher cipher = this.decryptionCipher;
		if (cipher == null) {
			throw new IllegalStateException("Decryption cipher not available, has this service been initialized?");
		}

		//Run the cipher
		byte[] resultBytes = null;

		try {
			final byte[] inputBytes = this.stringToByteArray(input);
			resultBytes = cipher.doFinal(Base64.decodeBase64(inputBytes));
		} catch (final Throwable t) {
			throw new EncryptionException("Error in decryption", t);
		}

		final String result = this.byteArrayToString(resultBytes);
		log.info("Decryption on \"" + input + "\": " + result);
		return result;
	}

	@Override
	public String hash(String input) throws IllegalArgumentException {
		if (input == null) {
			throw new IllegalArgumentException("Input is required.");
		}

		final byte[] inputBytes = this.stringToByteArray(input);

		final MessageDigest digest = this.messageDigest;

		//Update with our input, and obtain the hash, resetting the messageDigest
		digest.update(inputBytes, 0, inputBytes.length);
		final byte[] hashBytes = digest.digest();
		final byte[] encodedBytes = Base64.encodeBase64(hashBytes);

		//Get the input back in some readable format
		final String hash = this.byteArrayToString(encodedBytes);
		log.info("One-way hash of \"" + input + "\": " + hash);
		return hash;
	}

	//@Asynchronous can be annotated here or in the interface
	@Asynchronous
	@Override
	public Future<String> hashAsync(String input) throws IllegalArgumentException, EncryptionException {
		//Get the real hash
		final String hash = this.hash(input);

		//Wrap and return
		return new AsyncResult<String>(hash);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ejb3.examples.ch05.encryption.EncryptionCommonBusiness#compare(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean compare(String hash, String input) throws IllegalArgumentException {
		if (hash == null) {
			throw new IllegalArgumentException("Hash is required.");
		}
		if (input == null) {
			throw new IllegalArgumentException("Input is required.");
		}

		return hash.equals(this.hash(input));
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ejb3.examples.ch05.encryption.EncryptionCommonBusiness#getCiphersPassphrase()
	 */
	@Override
	public String getCiphersPassphrase() {
		//Obtain current
		String passphrase = this.ciphersPassphrase;

		if (passphrase == null) {
			//Do a lookup via SessionContext, this is the manual way
			passphrase = this.getEnvironmentEntryAsString(ENV_ENTRY_NAME_CIPHERS_PASSPHRASE);

			//See if provided
			if (passphrase == null) {
				log.warning("No encryption passphrase has been supplied explicitly via an env-entry, falling back on the default...");
				passphrase = DEFAULT_PASPHRASE;
			}

			//Set the passphrase to be used so we don't have to do this lazy init again
			this.ciphersPassphrase = passphrase;
		}

		return passphrase;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ejb3.examples.ch05.encryption.EncryptionCommonBusiness#getMessageDigestAlgorithm()
	 */
	@Override
	public String getMessageDigestAlgorithm() {
		if (this.messageDigestAlgorithm == null) {
			log.warning("No message digest algorithm has been supplied explicitly via an env-entry, falling back on the default...");
			this.messageDigestAlgorithm = DEFAULT_ALGORITHM_MESSAGE_DIGEST;
		}
		
		log.info("Configured MessageDigest one-way hash algorithm is: " + this.messageDigestAlgorithm);
		return this.messageDigestAlgorithm;
	}
	
	private String getEnvironmentEntryAsString(final String envEntryName) {
		//See if we hava a SessionContext
		final SessionContext context = this.context;
		if (context == null) {
			log.warning("No SessionContext, bypassing request to obtain environment entry: " + envEntryName);
			return null;
		}
		
		//Lookup in the private JNDI ENC via the injected SessionContext
		Object lookupValue = null;
		try {
			lookupValue = context.lookup(envEntryName);
			log.fine("Obtained environment entry \"" + envEntryName + "\": " + lookupValue);
		} catch (final IllegalArgumentException iae) {
			//Not found defined within this EJB's Component Environment, so return null and 
			//let the caller handle it
			log.warning("Could not find environment entry with name: " + envEntryName);
			return null;
		}
		
		//Cast
		String returnValue = null;
		try {
			returnValue = (String)returnValue;
		} catch (ClassCastException cce) {
			throw new IllegalStateException("The specified environment entry, " + lookupValue + 
					", was not able to be represented as a " + String.class.getName(), cce);
		}
		return returnValue;
	}
	
	private String byteArrayToString(final byte[] bytes) {
		if (bytes == null) {
			throw new IllegalArgumentException("Byte array is required.");
		}
		
		//Represent as a String
		String result = null;
		try {
			result = new String(bytes, CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Specified charset is invalid: " + CHARSET, e);
		}
		
		return result;
	}
	
	private byte[] stringToByteArray(final String input) {
		if (input == null) {
			throw new IllegalArgumentException("Input is required.");
		}
		
		byte[] result = null;
		try {
			result = input.getBytes(CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Specified charset is invalid: " + CHARSET, e);
		}
		return result;
	}

	private void useHashAsync() throws Exception {
		final String input = "Teste";

		//Hash
		final Future<String> hashFuture = this.hashAsync(input);

		//Work while the hash invocation runs concurrently
		//... loads of work

		//Get the result and block for up to 10 seconds to get it
		final String hash = hashFuture.get(10, TimeUnit.SECONDS);
		log.info("Hash of \"" + input + "\": " + hash);
	}
}
