package org.jboss.ejb3.examples.ch15.secureschool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.naming.Context;
import javax.naming.NamingException;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.ejb3.examples.ch15.secureschool.api.FireDepartmentLocalBusiness;
import org.jboss.ejb3.examples.ch15.secureschool.api.SecureSchoolLocalBusiness;
import org.jboss.ejb3.examples.ch15.secureschool.impl.SecureSchoolBean;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class SecureSchoolT {

	private static final Logger log = Logger.getLogger(SecureSchoolT.class.getName());

	private static String USER_NAME_ADMIN = "admin";
	private static String PASSWORD_ADMIN = "adminPassword";
	private static String USER_NAME_STUDENT = "student";
	private static String PASSWORD_STUDENT = "studentPassword";
	private static String USER_NAME_JANITOR = "janitor";
	private static String PASSWORD_JANITOR = "janitorPassword";

	private static final String JNDI_NAME_EJB = "SecureSchoolBeanLocal";

	/**
	 * Hook Arquillian so we can create new JNDI Contexts using supplied properties
	 */
//	@Inject

	/**
	 * EJB proxy injected without any explicit login or authentication/authorization
	 * Behind the scenes, Arquillian is using a default JNDI Context without any
	 * login properties to inject the proxy into this target.
	 */
	@EJB(mappedName="java:global/secureSchool/SecureSchoolBean!org.jboss.ejb3.examples.ch15.secureschool.api.SecureSchoolLocalBusiness")
	private SecureSchoolLocalBusiness unauthenticatedSchool;

	@EJB(mappedName="java:global/secureSchool/FireDepartmentBean!org.jboss.ejb3.examples.ch15.secureschool.api.FireDepartmentLocalBusiness")
	private FireDepartmentLocalBusiness fireDepartment;

	@Deployment
	public static JavaArchive getDeployment() {
		final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "secureSchool.jar")
				.addPackages(false,  SecureSchoolLocalBusiness.class.getPackage(), SecureSchoolBean.class.getPackage());
		log.info(archive.toString(true));
		return archive;
	}

	@Test(expected = EJBAccessException.class)
	public void unauthenticatedUserCannotOpenFrontDoor() {
		System.out.println(unauthenticatedSchool);
		System.out.println(fireDepartment);
		unauthenticatedSchool.openFrontDoor();
	}

//	@Test
	public void studentCanOpenFrontDoor() throws NamingException {
		//Log in via JNDI as "student" user
		final Context context = this.login(USER_NAME_STUDENT, PASSWORD_STUDENT);

		try {
			final SecureSchoolLocalBusiness school = this.getEjb(context);
			school.openFrontDoor();
		} finally {
			context.close();
		}
	}

//	@Test
	public void janitorCanOpenServiceDoor() throws NamingException {
		final Context context = this.login(USER_NAME_JANITOR, PASSWORD_JANITOR);

		try {
			final SecureSchoolLocalBusiness school = this.getEjb(context);
			school.openFrontDoor();
		} finally {
			context.close();
		}
	}

//	@Test(expected = EJBAccessException.class)
	public void studentCannotOpenServiceDoor() throws NamingException {
		final Context context = this.login(USER_NAME_STUDENT, PASSWORD_STUDENT);

		try {
			final SecureSchoolLocalBusiness school = this.getEjb(context);
			school.openServiceDoor();
		} finally {
			context.close();
		}
	}

//	@Test(expected = EJBAccessException.class)
	public void studentCannotCloseSchool() throws NamingException {
		final Context context = this.login(USER_NAME_STUDENT, PASSWORD_STUDENT);

		try {
			final SecureSchoolLocalBusiness school = this.getEjb(context);
			school.close();	
		} finally {
			context.close();
		}
	}

//	@Test
	public void adminCanCloseSchool() throws NamingException {
		final Context context = this.login(USER_NAME_ADMIN, PASSWORD_ADMIN);

		try {
			final SecureSchoolLocalBusiness school = this.getEjb(context);
			school.close();	
			Assert.assertFalse("School should now be closed", school.isOpen());
			school.open();
			Assert.assertTrue("School should now be open", school.isOpen());
		} finally {
			context.close();
		}
	}

//	@Test
	public void unauthenticatedUserCanCheckIfSchoolIsOpen() {
		Assert.assertTrue("Unauthenticated user should see that school is open", unauthenticatedSchool.isOpen());
	}

//	@Test(expected = SchoolClosedException.class)
	public void studentCannotOpenFrontDoorsWhenSchholIsClosed() throws Throwable {
		try {
			final Context context = this.login(USER_NAME_ADMIN, PASSWORD_ADMIN);
			final SecureSchoolLocalBusiness school = this.getEjb(context);
			school.close();	
			context.close();
			Assert.assertFalse("School should now be closed", school.isOpen());

			//Now try to open the front doors as a student, we do this in another Thread
			//because OpenEJB will associate the security context with this Thread to
			//"admin" (from above)
			final Callable<Void> stutentOpenDoorTask = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					final Context context = SecureSchoolT.this.login(USER_NAME_STUDENT, PASSWORD_STUDENT);
					try {
						final SecureSchoolLocalBusiness school = SecureSchoolT.this.getEjb(context);
						school.openFrontDoor();	
						return null;
					} finally {
						context.close();
					}
				}
			};

			final ExecutorService service = Executors.newSingleThreadExecutor();
			final Future<Void> future = service.submit(stutentOpenDoorTask);
			try {
				future.get(); //should fail here
			} catch (final ExecutionException ee) {
				throw ee.getCause();
			}
		} finally {
			final Context context = this.login(USER_NAME_ADMIN, PASSWORD_ADMIN);
			final SecureSchoolLocalBusiness school = this.getEjb(context);
			school.open();
			Assert.assertTrue("School should now be open", school.isOpen());
			context.close();
		}
	}
	
	/**
	 * Mechanism specific to the OpenEJB container
	 * @param username
	 * @param password
	 * @return
	 */
	private Context login(final String username, final String password) {
		final Map<String, Object> namingContextProps = new HashMap<String, Object>();
		namingContextProps.put(Context.SECURITY_PRINCIPAL, username);
		namingContextProps.put(Context.SECURITY_CREDENTIALS, password);
//		final Context context = arquillianContext.get(Context.class, namingContextProps);
//		return context;
		return null;
	}
	
	private SecureSchoolLocalBusiness getEjb(final Context context) throws NamingException {
		//Loop up in JNDI specific to OpenEJB
		return (SecureSchoolLocalBusiness)context.lookup(JNDI_NAME_EJB);
	}

}
