package org.jboss.ejb3.examples.ch17.transactions.api;

import java.math.BigDecimal;

public interface BlackjackGameLocalBusiness {
	
	String JNDI_NAME = "PokerGameLocal";
	
	boolean bet(long userId, BigDecimal amount) throws IllegalArgumentException, InsufficientBalanceException;

}
