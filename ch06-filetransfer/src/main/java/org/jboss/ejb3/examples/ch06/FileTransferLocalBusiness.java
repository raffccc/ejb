package org.jboss.ejb3.examples.ch06;

public interface FileTransferLocalBusiness extends FileTransferCommonBusiness {
	
	/**
	 * Ends the current session; will result in a SFSB @Remove call as the
	 * bean implementation class will annotate this with {@link javax.ejb.Remove}
	 */
	void endSession();

}
