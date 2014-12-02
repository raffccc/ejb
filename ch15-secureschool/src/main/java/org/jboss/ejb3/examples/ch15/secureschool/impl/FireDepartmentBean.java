package org.jboss.ejb3.examples.ch15.secureschool.impl;

import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.jboss.ejb3.examples.ch15.secureschool.api.FireDepartmentLocalBusiness;
import org.jboss.ejb3.examples.ch15.secureschool.api.SecureSchoolLocalBusiness;

@Singleton
@RunAs(Roles.ADMIN)
@PermitAll //implicit
public class FireDepartmentBean implements FireDepartmentLocalBusiness {
	
	private static final Logger log = Logger.getLogger(FireDepartmentBean.class.getName());
	
	@EJB
	private SecureSchoolLocalBusiness school;

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ejb3.examples.ch15.secureschool.api.FireDepartmentLocalBusiness#declareEmergency()
	 */
	@Override
	public void declareEmergency() {
		log.info("Dispatching emergency support from the Fire Department, closing local school");
		school.close();
	}

}
