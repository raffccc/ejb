package org.jboss.ejb3.examples.testsupport.dbquery;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
@Local(EntityManagerExposingLocalBusiness.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class EntityManagerExposingBean implements EntityManagerExposingLocalBusiness {

	@PersistenceContext
	private EntityManager em;
	
	/*
	 * (non-Javadoc)
	 * @see org.jboss.ejb3.examples.testsupport.dbquery.EntityManagerExposingLocalBusiness#getEntityManager()
	 */
	@Override
	public EntityManager getEntityManager() {
		if (em == null) {
			throw new IllegalStateException(EntityManagerExposingBean.class.getSimpleName() + " was not injected.");
		}
		return em;
	}

}
