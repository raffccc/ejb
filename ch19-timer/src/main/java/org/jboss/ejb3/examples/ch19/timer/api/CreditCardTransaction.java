package org.jboss.ejb3.examples.ch19.timer.api;

import java.math.BigDecimal;

public class CreditCardTransaction {
	
	private final String cardNumber;
	
	private final BigDecimal amount;
	
	public CreditCardTransaction(final String cardNumber, final BigDecimal amount) {
		this.cardNumber = cardNumber;
		this.amount = amount;
	}
	
	@Override
	public String toString() {
		return "CreditCardTransaction [amount=" + amount + ", cardNumber=" + cardNumber + "]";
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

}
