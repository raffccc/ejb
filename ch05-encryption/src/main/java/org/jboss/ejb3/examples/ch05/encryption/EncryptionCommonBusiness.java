package org.jboss.ejb3.examples.ch05.encryption;

import java.util.concurrent.Future;

public interface EncryptionCommonBusiness {

	/**
	 * Encrypts the specified String, returning the result
	 */
	String encrypt(String input) throws EncryptionException;
	
	/**
	 * Decrypts the specified String, returning the result.
	 * The general contract is that the result of decrypting a String
	 * encrypted with {@link EncryptionCommonBusiness#encrypt(String)}
	 * will be equal by value to the original input (round trip).
	 */
	String decrypt(String input) throws EncryptionException;
	
	/**
	 * Returns a one-way hash of the specified argument.
	 * Useful for sately storing passwords.
	 */
	String hash(String input);
	
	/**
	 * Returns a one-way hash of the specified argument, calculated asynchronously.
	 * Useful for safely storing passwords
	 */
	Future<String> hashAsync(String input) throws EncryptionException;
	
	/**
	 * Returns whether or not the specified input matches the
	 * specified hash. Useful for validating passwords against
	 * a securely stored hash.
	 */
	boolean compare(String hash, String input);
	
	/**
	 * A real system won't expose this method in the public API, ever. We
	 * do it here for testing and to illustrate the example.
	 */
	String getCiphersPassphrase();
	
	String getMessageDigestAlgorithm();
	
}
