package org.jboss.ejb3.examples.ch19.timer.api;

import java.util.Date;
import java.util.List;

import javax.ejb.ScheduleExpression;

public interface CreditCardTransactionProcessingLocalBusiness {
	
	List<CreditCardTransaction> getPendingTransactions();
	
	void process();
	
	void add(CreditCardTransaction transaction) throws IllegalArgumentException;
	
	Date scheduleProcessing(ScheduleExpression expression) throws IllegalArgumentException;

}
