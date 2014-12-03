package org.jboss.ejb3.examples.testsupport.dbquery;

import javax.persistence.EntityManager;

/**
 * Exposes generic database operations directly via the {@link EntityManager}
 */
public interface EntityManagerExposingLocalBusiness {
	
	EntityManager getEntityManager();

}
