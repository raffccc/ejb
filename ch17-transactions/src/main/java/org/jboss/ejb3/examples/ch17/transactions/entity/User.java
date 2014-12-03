package org.jboss.ejb3.examples.ch17.transactions.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.jboss.ejb3.examples.testsupport.entity.IdentityBase;

@Entity
public class User extends IdentityBase {
	
	private String name;
	
	@OneToOne(cascade = CascadeType.PERSIST)
	private Account account;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
	@Override
	public String toString() {
		return User.class.getSimpleName() + " [id=" + this.getId() + ", name=" + name + 
				", account = " + account + "]";
	}

}
