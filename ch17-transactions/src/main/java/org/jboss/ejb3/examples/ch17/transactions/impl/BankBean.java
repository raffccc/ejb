package org.jboss.ejb3.examples.ch17.transactions.impl;

import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.ejb3.examples.ch17.transactions.api.BankLocalBusiness;
import org.jboss.ejb3.examples.ch17.transactions.api.InsufficientBalanceException;
import org.jboss.ejb3.examples.ch17.transactions.entity.Account;

@Stateless
@Local(BankLocalBusiness.class)
public class BankBean implements BankLocalBusiness {

	private static final Logger log = Logger.getLogger(BankBean.class.getName());

	@PersistenceContext
	private EntityManager em;

	@Override
	public BigDecimal deposit(long accountId, BigDecimal amount) throws IllegalArgumentException {
		final Account account = this.getAccount(accountId);
		return account.deposit(amount);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public BigDecimal getBalance(long accountId) throws IllegalArgumentException {
		final Account account = this.getAccount(accountId);

		//We don't expose this account object to callers at all; its changes
		//elsewhere in the (optional) Tx should not be synchronized with the
		//DB in case of a write
		em.detach(account);
		return account.getBalance();
	}

	@Override
	public void transfer(long accountIdFrom, long accountIdTo, BigDecimal amount) 
			throws IllegalArgumentException, InsufficientBalanceException {

		final Account accountFrom = this.getAccount(accountIdFrom);
		final Account accountTo = this.getAccount(accountIdTo);
		this.transfer(accountFrom, accountTo, amount);
	}

	@Override
	public void transfer(Account accountFrom, Account accountTo, BigDecimal amount) 
			throws IllegalArgumentException, InsufficientBalanceException {
		if (accountFrom == null) {
			throw new IllegalArgumentException("accountFrom must be specified");
		}

		if (accountTo == null) {
			throw new IllegalArgumentException("accountTo must be specified");
		}

		accountFrom.withdraw(amount);
		accountTo.deposit(amount);
		log.info("Deposited " + amount + "to " + accountTo + " from " + accountFrom);
	}

	@Override
	public BigDecimal withdraw(long accountId, BigDecimal amount)
			throws IllegalArgumentException, InsufficientBalanceException {
		final Account account = this.getAccount(accountId);
		return account.withdraw(amount);
	}

	private Account getAccount(final long accountId) throws IllegalArgumentException {
		final Account account;
		account = em.find(Account.class, new Long(accountId));
		if (account == null) {
			throw new IllegalArgumentException("Could not find account with ID " + accountId);
		}
		return account;
	}

}