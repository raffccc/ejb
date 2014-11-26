package org.jboss.ejb3.examples.ch06;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileTransferUnitTestCase extends FileTransferTestCaseBase {

	private static final Logger log = Logger.getLogger(FileTransferUnitTestCase.class.getName());
	private static FtpServerPojo ftpService;
	private static final int FTP_SERVICE_BIND_PORT = 12345;
	private static final String FILE_NAME_USERS_CONFIG = "ftpusers.properties";
	
	private FileTransferBean ftpClient;
	
	@BeforeClass
	public static void createFtpService() throws Exception {
		final FtpServerPojo service = new FtpServerPojo();
		
		service.setBindPort(FTP_SERVICE_BIND_PORT);
		service.setUsersConfigFileName(FILE_NAME_USERS_CONFIG);
		
		service.initializeServer();
		service.startServer();
		
		log.info("Started up test FTP service: " + service);
		ftpService = service;
	}
	
	@AfterClass
	public static void destroyFtpService() throws Exception {
		if (ftpService == null) {
			return;
		}
		ftpService.stopServer();
		ftpService = null;
		log.info("Brought down test FTP Service");
	}
	
	@Before
	public void createFtpClient() throws Exception {
		final FileTransferBean ftpClient = new FileTransferBean();
		ftpClient.connect();
		this.ftpClient = ftpClient;
		log.info("Set FTP Client: " + ftpClient);
	}
	
	@After
	public void cleanup() throws Exception {
		final FileTransferBean ftpClient = this.ftpClient;
		if (ftpClient != null) {
			ftpClient.disconnect();
			this.ftpClient = null;
		}
	}
	
	@Test
	public void testPassivationAndActivation() throws Exception {
		log.info("testPassivationAndActivation");
		
		final FileTransferCommonBusiness client = this.getClient();
		final String home = getFtpHome().getAbsolutePath();
		client.cd(home);
		TestCase.assertEquals("Present working directry should be set to home", home, client.pwd());
		
		log.info("Mock @" + PrePassivate.class.getName());
		client.disconnect();
		
		log.info("Mock passivation");
		final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		final ObjectOutput objectOut = new ObjectOutputStream(outStream);
		objectOut.writeObject(client);
		objectOut.close();
		
		log.info("Mock Activation");
		final InputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
		final ObjectInput objectIn = new ObjectInputStream(inStream);
		
		//Get a new client from passivation/activation roundtrip
		final FileTransferCommonBusiness serializedClient = (FileTransferCommonBusiness)objectIn.readObject();
		objectIn.close();
		
		log.info("Mock @" + PostActivate.class.getName());
		serializedClient.connect();
		
		TestCase.assertEquals("Present working directry should be the same as before passivation/activation", home, serializedClient.pwd());
	}
	
	@Override
	protected FileTransferCommonBusiness getClient() {
		return this.ftpClient;
	}

}
