package org.jboss.ejb3.examples.ch07.rsscache.impl.rome;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.junit.BeforeClass;

public class RssCacheUnitTestCase extends RssCacheTestCaseBase {

	private static final Logger log = Logger.getLogger(RssCacheUnitTestCase.class.getName());

	/**
	 * The POJO instance to test, mocking a @Singleton EJB
	 */
	private static RssCacheCommonBusiness bean;

	@BeforeClass
	public static void createPojo() {
		final TestRssCacheBean bean = new TestRssCacheBean();
		RssCacheUnitTestCase.bean = bean;
		log.info("Created POJO instance: " + bean);

		URL url = null;
		try {
			url = new URL(getBaseConnectUrl(), FILENAME_RSS_FEED);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error in test setup while constructiong the mock RSS feed URL", e);
		}
		
		bean.setUrl(url);
		
		//Mock container initialization upon the bean
		bean.refresh();
	}

	private static URL getBaseConnectUrl() {
		try {
			return new URL("http://localhost:" + HTTP_TEST_BIND_PORT);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error in creating the base URL during setup", e);
		}
	}

	@Override
	protected RssCacheCommonBusiness getRssCacheBean() {
		return bean;
	}

}
