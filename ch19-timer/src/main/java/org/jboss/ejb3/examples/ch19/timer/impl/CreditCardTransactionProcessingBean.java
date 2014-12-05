package org.jboss.ejb3.examples.ch19.timer.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.ScheduleExpression;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.jboss.ejb3.examples.ch19.timer.api.CreditCardTransaction;
import org.jboss.ejb3.examples.ch19.timer.api.CreditCardTransactionProcessingLocalBusiness;

@Singleton
@Local(CreditCardTransactionProcessingLocalBusiness.class)
public class CreditCardTransactionProcessingBean implements	CreditCardTransactionProcessingLocalBusiness {

	private static final Logger log = Logger.getLogger(CreditCardTransactionProcessingBean.class.getName());
	
	private static final String EVERY = "*";
	
	private static final String ZERO = "0";
	
	@Resource
	private SessionContext context;
	
	@Resource
	private TimerService timerService;
	
	private final List<CreditCardTransaction> pendingTransactions = new ArrayList<CreditCardTransaction>();
	
	@Timeout
	@Schedule(dayOfMonth=EVERY, month = EVERY, year=EVERY, second = ZERO, minute = ZERO, hour=EVERY)
	@Lock(LockType.WRITE)
	public void processViaTimeour(final Timer timer) {
		this.process();
	}
	
	@Lock(LockType.WRITE)
	@Override
	public void add(CreditCardTransaction transaction) throws IllegalArgumentException {
		if (transaction == null) {
			throw new IllegalArgumentException("Transaction must be specified");
		}
		
		this.pendingTransactions.add(transaction);
		log.info("Added transaction pending to be processed: " + transaction);
	}
	
	@Lock(LockType.READ)
	@Override
	public List<CreditCardTransaction> getPendingTransactions() {
		return Collections.unmodifiableList(pendingTransactions);
	}

	@Lock(LockType.WRITE)
	@Override
	public void process() {
		for (CreditCardTransaction transaction : pendingTransactions) {
			log.info("Processed transaction: " + transaction);
		}
		pendingTransactions.clear();
	}

	@Override
	public Date scheduleProcessing(ScheduleExpression expression) throws IllegalArgumentException {
		if (expression == null) {
			throw new IllegalArgumentException("Timer expression must be specified");
		}
		
		final TimerService timerService = context.getTimerService();
		final Timer timer = timerService.createCalendarTimer(expression);
		final Date next = timer.getNextTimeout();
		log.info("Created " + timer + " to process transactions; next fire is at: " + next);
		return next;
	}

}
