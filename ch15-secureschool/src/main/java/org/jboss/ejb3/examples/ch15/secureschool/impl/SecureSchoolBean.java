package org.jboss.ejb3.examples.ch15.secureschool.impl;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.jboss.ejb3.examples.ch15.secureschool.api.SchoolClosedException;
import org.jboss.ejb3.examples.ch15.secureschool.api.SecureSchoolLocalBusiness;

@Stateless
@Local(SecureSchoolLocalBusiness.class)
@DeclareRoles({Roles.ADMIN, Roles.STUDENT, Roles.JANITOR})
@RolesAllowed({})
public class SecureSchoolBean implements SecureSchoolLocalBusiness {

	private static final Logger log = Logger.getLogger(SecureSchoolBean.class.getName());

	private boolean open;
	
	@Resource
	private SessionContext context;

	@RolesAllowed({Roles.ADMIN, Roles.STUDENT, Roles.JANITOR})
	@Override
	public void openFrontDoor() throws SchoolClosedException {
		final String callerName = context.getCallerPrincipal().getName();
		if (!open && !context.isCallerInRole(Roles.ADMIN)) {
			throw SchoolClosedException.newInstance("Attempt to open the front door after hours "
					+ "is prohibited to all but admins, denied to " + callerName);
		}
		log.info("Opening front door for: " + callerName);
	}

	@RolesAllowed({Roles.ADMIN, Roles.JANITOR})
	@Override
	public void openServiceDoor() {
		log.info("Opening service door for: " + context.getCallerPrincipal().getName());
	}
	
	@RolesAllowed({Roles.ADMIN})
	@Override
	public void close() {
		this.open = false;
	}
	
	@PostConstruct
	@RolesAllowed({Roles.ADMIN})
	@Override
	public void open() {
		this.open = true;
	}

	@Override
	@PermitAll
	public boolean isOpen() {
		return this.open;
	}

}
