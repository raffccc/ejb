package org.jboss.ejb3.examples.ch17.transactions.impl;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.ejb3.examples.ch17.transactions.api.BankLocalBusiness;
import org.jboss.ejb3.examples.ch17.transactions.api.BlackjackGameLocalBusiness;
import org.jboss.ejb3.examples.ch17.transactions.api.InsufficientBalanceException;
import org.jboss.ejb3.examples.ch17.transactions.entity.Account;
import org.jboss.ejb3.examples.ch17.transactions.entity.User;

@Stateless
@Local(BlackjackGameLocalBusiness.class)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class BlackjackGameBean implements BlackjackGameLocalBusiness {

	@PersistenceContext
	private EntityManager em;
	
	@EJB
	private BankLocalBusiness bank;
	
	/*
	 * (non-Javadoc)
	 * @see org.jboss.ejb3.examples.ch17.transactions.api.BlackjackGameLocalBusiness#bet(long, java.math.BigDecimal)
	 */
	@Override
	public boolean bet(long userId, BigDecimal amount) throws IllegalArgumentException, InsufficientBalanceException {
		if (userId < 0) {
			throw new IllegalArgumentException("userId must be valid (>0)");
		}
		
		if (amount == null) {
			throw new IllegalArgumentException("amount must be specified");
		}
		
		if (BigDecimal.ZERO.compareTo(amount) > 0) {
			throw new IllegalArgumentException("amount must be greater than 0");
		}
		
		final Account userAccount = em.find(User.class, new Long(userId)).getAccount();
		final BigDecimal currentBalanceUserAccount = userAccount.getBalance();
		if (amount.compareTo(currentBalanceUserAccount) > 0) {
			throw new InsufficientBalanceException("Cannot place bet of " + amount + " when the user account has only " + currentBalanceUserAccount);
		}
		
		final boolean win = Math.random() > .5;
		final Account blackJackServiceAccount = em.find(Account.class, BlackjackServiceConstants.ACCOUNT_BLACKJACKGAME_ID);
		if (win) {
			bank.transfer(blackJackServiceAccount, userAccount, amount);
		} else {
			bank.transfer(userAccount, blackJackServiceAccount, amount);
		}
		
		return win;
	}

}
