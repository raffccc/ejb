package org.jboss.ejb3.examples.ch08.statusupdate.mdb;

import java.security.AccessController;
import java.security.PrivilegedAction;

class SecurityActions {
	
	private SecurityActions() {}
	
	static ClassLoader getThreadContextClassLoader() {
		return AccessController.doPrivileged(GetTcclAction.INSTANCE);
	}
	
	static void setThreadContextClassLoader(final ClassLoader cl) throws IllegalArgumentException {
		if (cl == null) {
			throw new IllegalArgumentException("ClassLoader was null");
		}
		
		AccessController.doPrivileged(new PrivilegedAction<Void>() {

			@Override
			public Void run() {
				Thread.currentThread().setContextClassLoader(cl);
				return null;
			}
			
		});
	}
	
	static String getEnvironmentVariable(final String envVarName) throws IllegalArgumentException {
		if (envVarName == null || envVarName.length() == 0) {
			throw new IllegalArgumentException("Environment variable name was not specified");
		}
		return AccessController.doPrivileged(new GetEnvironmentVariableAction(envVarName));
	}
	
	static String getSystemProperty(final String key) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException("key was null");
		}
		return AccessController.doPrivileged(new GetSystemPropertyAction(key));
	}
	
	/**
	 * {@link PrivilegedAction} to obtain the TCCL 
	 */
	private enum GetTcclAction implements PrivilegedAction<ClassLoader> {
		INSTANCE;
		
		@Override
		public ClassLoader run() {
			return Thread.currentThread().getContextClassLoader();
		}
		
	}
	
	/**
	 * {@link PrivilegedAction} to access an environment variable 
	 */
	private static class GetEnvironmentVariableAction implements PrivilegedAction<String> {
		
		private String envVarName;
		
		public GetEnvironmentVariableAction(final String envVarName) {
			this.envVarName = envVarName;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.security.PrivilegedAction#run()
		 */
		@Override
		public String run() {
			return System.getenv(envVarName);
		}
		
	}
	
	/**
	 * {@link PrivilegedAction} to access a system property
	 */
	private static class GetSystemPropertyAction implements PrivilegedAction<String> {
		
		private String sysPropName;
		
		public GetSystemPropertyAction(final String sysPropName) {
			this.sysPropName = sysPropName;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.security.PrivilegedAction#run()
		 */
		@Override
		public String run() {
			return System.getProperty(sysPropName);
		}
		
	}

}
