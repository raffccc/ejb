package org.jboss.ejb3.examples.ch06;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Local;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

@Stateful(name="FileTransferEJB")
@Local(FileTransferLocalBusiness.class)
public class FileTransferBean implements FileTransferLocalBusiness, Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(FileTransferBean.class.getName());

	private static String CONNECT_HOST = "localhost";
	private static int CONNECT_PORT = 12345;

	/*
	 * Instance members that will comprise our SFSB's internal state
	 */

	/*
	 * We don't want the FTP Client's state getting Serialized during passivation.
	 * We'll reinitialize this client and its connections upon activation.
	 */
	private FTPClient client;

	/*
	 * In cases where we're passivated, if this is specified we'll change
	 * into this directory upon activation.
	 */
	private String presentWorkingDirectory;

	private FTPClient getClient() {
		return this.client;
	}

	private void setClient(FTPClient client) {
		this.client = client;
	}

	private String getConnectHost() {
		return CONNECT_HOST;
	}

	private int getConnectPort() {
		return CONNECT_PORT;
	}

	private String getPresentWorkingDirectory() {
		return this.presentWorkingDirectory;
	}

	private void setPresentWorkingDirectory(final String presentWorkingDirectory) {
		this.presentWorkingDirectory = presentWorkingDirectory;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ejb3.examples.ch06.FileTransferCommonBusiness#disconnect()
	 */
	@Override
	@PrePassivate
	@PreDestroy
	public void disconnect() {
		if (client != null) {
			if (client.isConnected()) {
				logoutOnFTPClient();
				disconnectOnFTPClient();
			}

			//Null out so we don't serialize this field
			this.client = null;
		}
	}

	private void logoutOnFTPClient() {
		final FTPClient client = this.getClient();
		try {
			client.logout();
			log.info("Logged out of: " + client);
		} catch (final IOException ioe) {
			log.warning("Exception encountered in logging out of the FTP client " + ioe.getMessage());
		}
	}

	private void disconnectOnFTPClient() {
		final FTPClient client = this.getClient();
		try {
			log.fine("Disconnecting: " + client);
			client.disconnect();
			log.info("Disconnected: " + client);
		} catch (final IOException ioe) {
			log.warning("Exception encountered in disconnecting the FTP client " + ioe.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ejb3.examples.ch06.FileTransferCommonBusiness#connect()
	 */
	@Override
	@PostConstruct
	@PostActivate
	public void connect() throws IllegalStateException {
		/*
		 * Precondition checks
		 */
		final FTPClient clientBefore = this.getClient();
		if (clientBefore != null && clientBefore.isConnected()) {
			throw new IllegalStateException("FTP Client is already initialized");
		}

		//Get the connection properties
		final String connectHost = this.getConnectHost();
		final int connectPort = this.getConnectPort();

		//Create the client
		final FTPClient client = new FTPClient();
		final String canonicalServerName = connectHost + ":" + connectPort;
		log.fine("Connecting to FTP server at " + canonicalServerName);
		try {
			client.connect(connectHost, connectPort);
		} catch (final IOException ioe) {
			throw new FileTransferException("Error in connecting to " + canonicalServerName, ioe);
		}

		//Set
		log.info("Connected fo FTP Server at: " + canonicalServerName);
		this.setClient(client);

		//Check that the last operation succeeded
		this.checkLastOperation();

		try {
			client.login("user", "password");
			this.checkLastOperation();
		} catch (final Exception e) {
			throw new FileTransferException("Could not log in", e);
		}

		//If there's a pwd defined, cd into it
		final String pwd = this.getPresentWorkingDirectory();
		if (pwd != null) {
			this.cd(pwd);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ejb3.examples.ch06.FileTransferCommonBusiness#cd(java.lang.String)
	 */
	@Override
	public void cd(String directory) throws IllegalStateException {
		final FTPClient client = this.getClient();

		try {
			client.changeWorkingDirectory(directory);
			this.checkLastOperation();
		} catch (final Exception e) {
			throw new FileTransferException("Could not change working directory to \"" + directory + "\"", e);
		}

		log.info("cd > " + directory);
		this.setPresentWorkingDirectory(directory);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ejb3.examples.ch06.FileTransferCommonBusiness#mkdir(java.lang.String)
	 */
	@Override
	public void mkdir(String directory) throws IllegalStateException {
		final FTPClient client = this.getClient();

		try {
			client.makeDirectory(directory);
			this.checkLastOperation();
		} catch (final Exception e) {
			throw new FileTransferException("Could not make directory \"" + directory + "\"", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ejb3.examples.ch06.FileTransferCommonBusiness#pwd()
	 */
	@Override
	public String pwd() throws IllegalStateException {
		final FTPClient client = this.getClient();

		try {
			final FTPFile[] files = client.listFiles();
			for (FTPFile file : files) {
				log.info(file.toString());
			}
			return client.printWorkingDirectory().replaceFirst("/", "").replace("/", File.separator);
		} catch (final IOException ioe) {
			throw new FileTransferException("Could not print working directory", ioe);
		}
	}
	
	/**
	 * Ensures that the last operation succeeded with a positive reply code.
	 * Otherwise a {@link FileTransferException} is raised, noting the reply
	 * code denoting the error
	 */
	private void checkLastOperation() throws FileTransferException {
		final FTPClient client = this.getClient();
		
		final int connectReply = client.getReplyCode();
		if (!FTPReply.isPositiveCompletion(connectReply)) {
			throw new FileTransferException("Did not receive a positive completion code from server, instead code was: " + connectReply);
		}
	}

	@Remove
	@Override
	public void endSession() {
		log.info("Session ending...");
	}

}
