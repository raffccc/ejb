package org.jboss.ejb3.examples.ch17.transactions.entity;

import java.math.BigDecimal;
import java.math.MathContext;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.jboss.ejb3.examples.ch17.transactions.api.InsufficientBalanceException;
import org.jboss.ejb3.examples.testsupport.entity.IdentityBase;

@Entity
public class Account extends IdentityBase {
	
	@OneToOne(cascade=CascadeType.PERSIST)
	private User owner;
	
	private BigDecimal balance = new BigDecimal(0, new MathContext(2));

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	@Transient
	public BigDecimal withdraw(final BigDecimal amount) throws IllegalArgumentException, InsufficientBalanceException {
		if (amount == null) {
			throw new IllegalArgumentException("amount must be specified");
		}
		
		final BigDecimal current = this.getBalance();
		if (amount.compareTo(current) > 0) {
			throw new InsufficientBalanceException("Cannot withdraw " + amount + " from account with " + current);
		}
		
		final BigDecimal newBalanceShoes = balance.subtract(amount);
		this.setBalance(newBalanceShoes);
		return newBalanceShoes;
	}
	
	@Transient
	public BigDecimal deposit(final BigDecimal amount) throws IllegalArgumentException {
		if (amount == null) {
			throw new IllegalArgumentException("amount must be specified");
		}
		
		final BigDecimal newBalanceShoes = balance.add(amount);
		this.setBalance(newBalanceShoes);
		return newBalanceShoes;
	}
	
	@Override
	public String toString() {
		final User owner = this.getOwner();
		return "Account [id=" + this.getId() + ", balance=" + balance + 
				", owner = " + (owner == null ? "No Owner" : owner.getId()) + "]";
	}

}
