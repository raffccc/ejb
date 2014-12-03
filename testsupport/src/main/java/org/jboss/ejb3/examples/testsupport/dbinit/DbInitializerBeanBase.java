package org.jboss.ejb3.examples.testsupport.dbinit;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

public abstract class DbInitializerBeanBase implements DbInitializerLocalBusiness {

	protected static final Logger log = Logger.getLogger(DbInitializerBeanBase.class.getName());

	@PersistenceContext
	protected EntityManager em;

	@Resource(mappedName="java:jboss/TransactionManager")
	private TransactionManager txManager;

	public abstract void populateDefaultData() throws Exception;

	public abstract void cleanup() throws Exception;

	@PostConstruct
	public void populateDatabase() throws Exception {
		final Transaction tx = txManager.getTransaction();
		final boolean startOurOwnTx = tx == null;
		if (startOurOwnTx) {
			txManager.begin();
		}

		try {
			this.populateDefaultData();
		} catch (final Throwable t) {
			txManager.setRollbackOnly();
		} finally {
			if (startOurOwnTx) {
				txManager.commit();
			}
		}
	}

	@Override
	public void refreshWithDefaultData() throws Exception {
		txManager.begin();
		try {
			this.cleanup();
			this.populateDatabase();
		} finally {
			txManager.commit();
		}
	}

}
