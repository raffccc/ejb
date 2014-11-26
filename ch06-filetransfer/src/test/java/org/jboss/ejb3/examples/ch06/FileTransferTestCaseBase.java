package org.jboss.ejb3.examples.ch06;

import java.io.File;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class FileTransferTestCaseBase {

	private static final Logger log = Logger.getLogger(FileTransferTestCaseBase.class.getName());

	/**
	 * The name of the directory under the writable temp filesystem which
	 * will act as the home for these tests
	 */
	private static final String RELATIVE_LOCATION_HOME = "ejb31_ch06-example-ftpHome";

	/**
	 * The name of the system property denoting the I/O temp directory
	 */
	private static final String SYS_PROP_NAME_IO_TMP_DIR = "java.io.tmpdir";

	/**
	 * File we'll use as the writeable home for FTP operations. Created and
	 * destroyed alongside test lifecycle.
	 */
	private static File ftpHome;

	@Before
	public void createFtpHome() throws Exception {
		final File ftpHome = getFtpHome();
		if (ftpHome.exists()) {
			throw new RuntimeException("Error in test setup; FTP Home shouldn't yet exist: " 
					+ ftpHome.getAbsolutePath());
		}
		
		final boolean created = ftpHome.mkdir();
		if (!created) {
			throw new RuntimeException("Request to create the FTP Home failed: " 
					+ ftpHome.getAbsolutePath());
		}
		log.info("Created FTP Home: " + ftpHome.getAbsolutePath());
	}
	
	@After
	public void deleteFtpHome() throws Exception {
		final File ftpHome = getFtpHome();
		if (!ftpHome.exists()) {
			throw new RuntimeException("Error in test setup; FTP Home shouldn exist: " 
					+ ftpHome.getAbsolutePath());
		}
		
		final boolean removed = this.deleteRecursive(ftpHome);
		if (!removed) {
			throw new RuntimeException("Request to remove the FTP Home failed: " 
					+ ftpHome.getAbsolutePath());
		}
		log.info("Removed FTP Home: " + ftpHome.getAbsolutePath());
	}
	
	@Test
	public void testMkdirCdAndPwd() throws Exception {
		log.info("testMkdirCdAndPwd");
		
		final FileTransferCommonBusiness client = this.getClient();
		
		final String home = getFtpHome().getAbsolutePath();
		client.cd(home);
		
		final String pwdBefore = client.pwd();
		TestCase.assertEquals("Present working directory should be our home", home, pwdBefore);
		
		final String newDir = "newDirectory";
		client.mkdir(newDir);
		client.cd(newDir);
		
		final String pwdAfter = client.pwd();
		TestCase.assertEquals("Present working directory should be our new directory", 
				home + File.separator + newDir, pwdAfter);
	}

	protected static File getFtpHome() throws Exception {
		if (ftpHome == null) {
			final String sysPropIoTempDir = SYS_PROP_NAME_IO_TMP_DIR;
			final String ioTempDir = System.getProperty(sysPropIoTempDir);
			if (ioTempDir == null) {
				throw new RuntimeException("I/O temp directory was not specified by system property " + sysPropIoTempDir);
			}

			final File ioTempDirFile = new File(ioTempDir);
			if (!ioTempDirFile.exists()) {
				throw new RuntimeException("I/O temp directory does not exist: " + ioTempDirFile.getAbsolutePath());
			}

			//Append the suffix for our home
			final File home = new File(ioTempDirFile, RELATIVE_LOCATION_HOME);
			ftpHome = home;
		}
		return ftpHome;
	}
	
	protected abstract FileTransferCommonBusiness getClient();
	
	protected boolean deleteRecursive(final File root) {
		if (!root.exists()) {
			return false;
		}
		
		final File[] children = root.listFiles();
		if (children != null) {
			for (File child : children) {
				this.deleteRecursive(child);
			}
		}
		
		final boolean success = root.delete();
		log.info("Deleted: " + root);
		return success;
	}
	
}
