package org.jboss.ejb3.examples.ch17.transactions.api;

import java.math.BigDecimal;

import org.jboss.ejb3.examples.ch17.transactions.entity.Account;

public interface BankLocalBusiness {
	
	String JNDI_NAME = "BankLocalBusiness";
	
	BigDecimal withdraw(long accountId, BigDecimal amount) throws IllegalArgumentException, InsufficientBalanceException;
	
	BigDecimal deposit(long accountId, BigDecimal amount) throws IllegalArgumentException;	
	
	BigDecimal getBalance(long accountId) throws IllegalArgumentException;
	
	void transfer(long accountIdFrom, long accountIdTo, BigDecimal amount) throws IllegalArgumentException, InsufficientBalanceException;
	
	void transfer(Account accountFrom, Account accountTo, BigDecimal amount) throws IllegalArgumentException, InsufficientBalanceException;

}
