package org.jboss.ejb3.examples.ch17.transactions.ejb;

import java.math.BigDecimal;

public interface ExampleUserData {
	
	long USER_ALRUBINGER_ID = 2L;
	
	String USER_ALRUBINGER_NAME = "Andrew Lee Rubinger";
	
	long ACCOUNT_ALRUBINGER_ID = 2L;
	
	BigDecimal INITIAL_ACCOUNT_BALANCE_ALR = new BigDecimal(500);

}
