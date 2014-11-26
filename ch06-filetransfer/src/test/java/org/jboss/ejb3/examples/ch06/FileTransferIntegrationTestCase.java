package org.jboss.ejb3.examples.ch06;

import java.io.File;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.NoSuchEJBException;

import junit.framework.TestCase;

import org.apache.commons.net.SocketClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class FileTransferIntegrationTestCase extends FileTransferTestCaseBase {

	private static final Logger log = Logger.getLogger(FileTransferIntegrationTestCase.class.getName());
	private static final String FTP_SERVER_USERS_CONFIG_FILENAME = "ftpusers.properties";
	private static final int FTP_SERVER_BIND_PORT = 12345;
	private static FtpServerPojo ftpServer;

	/**
	 * Our view of the EJB, remote business interface type of the Proxy
	 */
	@EJB(mappedName="java:module/FileTransferEJB!org.jboss.ejb3.examples.ch06.FileTransferLocalBusiness")
	private FileTransferLocalBusiness client1;

	/**
	 * Another FTP Client Session
	 */
	@EJB(mappedName="java:module/FileTransferEJB!org.jboss.ejb3.examples.ch06.FileTransferLocalBusiness")
	private FileTransferLocalBusiness client2;

	protected FileTransferLocalBusiness getClient() {
		return this.client1;
	}
	
	@Deployment
	public static JavaArchive createDeployment() {
		final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "ftpclient.jar")
				.addPackages(true, FileTransferBean.class.getPackage(), SocketClient.class.getPackage());
		log.info(archive.toString(true));
		return archive;
	}

	/*
	 * These @BeforeClass and @AfterClass methods simulate the state
	 * of @PostConstruction and @PreDestroy 
	 */

	@BeforeClass
	public static void startFtpServer() throws Exception {
		final FtpServerPojo server = new FtpServerPojo();
		server.setUsersConfigFileName(FTP_SERVER_USERS_CONFIG_FILENAME);
		server.setBindPort(FTP_SERVER_BIND_PORT);

		server.initializeServer();
		server.startServer();
		ftpServer = server;
	}

	@AfterClass
	public static void stopFtpServer() throws Exception {
		ftpServer.stopServer();
	}

	/*
	 * This simulates 
	 */
	@After
	public void endClientSessions() throws Exception {
		try {
			client1.endSession();
			client2.endSession();
		} catch (NoSuchEJBException nsee) {
			//If we've already been ended ignore
		}
	}
	
	@Test
	public void testSessionIsolation() throws Exception {
		log.info("testSessionIsolation");
		
		final FileTransferLocalBusiness session1 = this.client1;
		final FileTransferLocalBusiness session2 = this.client2;
		
		final String ftpHome = getFtpHome().getAbsolutePath();
		session1.cd(ftpHome);
		session2.cd(ftpHome);
		
		final String newDirSession1 = "newDirSession1";
		final String newDirSession2 = "newDirSession2";
		session1.mkdir(newDirSession1);
		session1.cd(newDirSession1);
		session2.mkdir(newDirSession2);
		session2.cd(newDirSession2);
		
		TestCase.assertEquals("Session 1 is in unexpected pwd", ftpHome + File.separator + newDirSession1, session1.pwd());
		TestCase.assertEquals("Session 2 is in unexpected pwd", ftpHome + File.separator + newDirSession2, session2.pwd());
		
		//End the session manyally for session2 (session1 will be ended by test lifecycle)
		session2.endSession();
	}
	
	@Test
	public void testSfsbRemoval() throws Exception {
		log.info("testSfsbRemoval");
		
		final FileTransferLocalBusiness sfsb = this.client1;
		
		final String ftpHome = getFtpHome().getAbsolutePath();
		sfsb.cd(ftpHome);
		
		final String pwdBefore = sfsb.pwd();
		TestCase.assertEquals("Session should be in the FTP Home Directory", ftpHome, pwdBefore);
		
		sfsb.endSession();
		
		boolean gotExpectedException = false;
		try {
			//This should not succeed, because we've called a method marked as @Remove
			sfsb.pwd();
		} catch (final NoSuchEJBException nsee) {
			gotExpectedException = true;
		}
		TestCase.assertTrue("Call to end the session did not result in underlying removal of "
				+ "the SFSB bean instance", gotExpectedException);
	}
	
}