package org.jboss.ejb3.examples.ch17.transactions.ejb;

import java.util.Collection;

import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.jboss.ejb3.examples.ch17.transactions.entity.Account;
import org.jboss.ejb3.examples.ch17.transactions.entity.User;
import org.jboss.ejb3.examples.ch17.transactions.impl.BlackjackServiceConstants;
import org.jboss.ejb3.examples.testsupport.dbinit.DbInitializerBeanBase;
import org.jboss.ejb3.examples.testsupport.dbinit.DbInitializerLocalBusiness;

@Singleton
@Startup
@Local(DbInitializerLocalBusiness.class)
@TransactionManagement(TransactionManagementType.BEAN)
public class DbInitializerBean extends DbInitializerBeanBase {

	@Override
	public void cleanup() throws Exception {
		final Collection<Account> accounts = em.createQuery("SELECT o FROM " + Account.class.getSimpleName() + " o", Account.class).getResultList();
		final Collection<User> users = em.createQuery("SELECT o FROM " + User.class.getSimpleName() + " o", User.class).getResultList();
		
		for (Account account : accounts) {
			em.remove(account);
		}
		
		for (User user : users) {
			em.remove(user);
		}
	}

	@Override
	public void populateDefaultData() throws Exception {
		//ALR
		final User alrubinger = new User();
		alrubinger.setId(ExampleUserData.USER_ALRUBINGER_ID);
		alrubinger.setName(ExampleUserData.USER_ALRUBINGER_NAME);
		
		final Account alrubingerAccount = new Account();
		alrubingerAccount.deposit(ExampleUserData.INITIAL_ACCOUNT_BALANCE_ALR);
		alrubingerAccount.setOwner(alrubinger);
		alrubingerAccount.setId(ExampleUserData.ACCOUNT_ALRUBINGER_ID);
		alrubinger.setAccount(alrubingerAccount);
		
		//Poker Game Service
		final User blackjackGameService = new User();
		blackjackGameService.setId(BlackjackServiceConstants.USER_BLACKJACKGAME_ID);
		blackjackGameService.setName(BlackjackServiceConstants.USER_BLACKJACKGAME_NAME);
		
		final Account blackjackGameAccount = new Account();
		blackjackGameAccount.deposit(BlackjackServiceConstants.INITIAL_ACCOUNT_BALANCE_BLACKJACKGAME);
		blackjackGameAccount.setOwner(blackjackGameService);
		blackjackGameAccount.setId(BlackjackServiceConstants.ACCOUNT_BLACKJACKGAME_ID);
		blackjackGameService.setAccount(blackjackGameAccount);
		
		//Persist
		em.persist(alrubinger);
		log.info("Created: " + alrubinger);
		em.persist(blackjackGameAccount);
		log.info("Created: " + blackjackGameAccount);
	}
	
}
