package org.jboss.ejb3.examples.ch08.statusupdate.api;

import java.io.Serializable;

public class StatusUpdate implements Serializable {

	private static final long serialVersionUID = -7147325870172448336L;
	
	private final String status;
	
	public StatusUpdate(final String status) throws IllegalArgumentException {
		if (status == null || status.length() == 0) {
			throw new IllegalArgumentException("Status must be specified");
		}
		
		this.status = status;
	}
	
	public String getText() {
		return status;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [status=" + status + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatusUpdate other = (StatusUpdate) obj;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}
	
	

}
