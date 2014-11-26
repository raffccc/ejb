package org.jboss.ejb3.examples.ch06;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

/**
 * Responsible for starting/stopping the Embedded FTP Server
 */
public final class FtpServerPojo {

	private static final Logger log = Logger.getLogger(FtpServerPojo.class.getName());
	private static final String LISTENER_NAME_DEFAULT = "default";
	private int bindPort;
	private FtpServer server;
	private String usersConfigFileName;
	
	public void initializeServer() throws IllegalStateException {
		final int bindPort = this.getBindPort();
		
		if (bindPort <= 0 ) {
			throw new IllegalStateException("Property for bind port has not been set to a valid value above 0");
		}
		
		final FtpServerFactory serverFactory = new FtpServerFactory();
		final ListenerFactory factory = new ListenerFactory();
		
		log.fine("Using FTP bind port: " + bindPort);
		factory.setPort(bindPort);
		
		serverFactory.addListener(LISTENER_NAME_DEFAULT, factory.createListener());
		
		//Get current classloader
		final ClassLoader tccl = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
			@Override
			public ClassLoader run() {
				return Thread.currentThread().getContextClassLoader();
			}
		});
		
		//Load the properties file to get its URI
		final String usersConfigFileName = this.getUsersConfigFileName();
		log.info("Using users configuration file: " + usersConfigFileName);
		
		final URL usersConfigUrl = tccl.getResource(usersConfigFileName);
		if (usersConfigUrl == null) {
			throw new RuntimeException("Could not find specified users configuration file upon the classpath: " + usersConfigFileName);
		}
		
		//Configure the user auth mechanism
		final PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
		userManagerFactory.setUrl(usersConfigUrl);
		userManagerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());
		final UserManager userManager = userManagerFactory.createUserManager();
		serverFactory.setUserManager(userManager);
		
		//Create the server
		final FtpServer server = serverFactory.createServer();
		this.setServer(server);
		log.info("Created FTP Server: " + server);
	}
	
	public void startServer() throws IllegalStateException, FtpException {
		final FtpServer server = this.getServer();
		
		if (server == null) {
			throw new IllegalStateException("The server has not yet been initialized");
		}
		
		if (!server.isStopped()) {
			throw new IllegalStateException("Server cannot be started if it is not currently stopped");
		}
		
		log.fine("Starting the FTP Server: " + server);
		server.start();
		log.info("FTP Server Started: " + server);
	}
	
	public void stopServer() throws IllegalStateException {
		final FtpServer server = this.getServer();
		
		if (server == null) {
			throw new IllegalStateException("The server has not yet been initialized");
		}
		
		if (server.isStopped()) {
			throw new IllegalStateException("Server cannot be stopped if it is already stopped");
		}
		
		log.fine("Stopping the FTP Server: " + server);
		server.stop();
		log.info("FTP Server stopped: " + server);
	}

	public int getBindPort() {
		return bindPort;
	}

	public void setBindPort(int bindPort) {
		this.bindPort = bindPort;
	}

	public FtpServer getServer() {
		return server;
	}

	public void setServer(FtpServer server) {
		this.server = server;
	}

	public String getUsersConfigFileName() {
		return usersConfigFileName;
	}

	public void setUsersConfigFileName(String usersConfigFileName) {
		this.usersConfigFileName = usersConfigFileName;
	}

}
