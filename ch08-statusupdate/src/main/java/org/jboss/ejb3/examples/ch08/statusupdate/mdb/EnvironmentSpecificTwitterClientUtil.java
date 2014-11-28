package org.jboss.ejb3.examples.ch08.statusupdate.mdb;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class EnvironmentSpecificTwitterClientUtil {
	
	private static final String ENV_VAR_NAME_TWITTER_CONSUMER_KEY = "CONSUMER_KEY";
	private static final String ENV_VAR_NAME_TWITTER_CONSUMER_SECRET = "CONSUMER_SECRET";
	private static final String ENV_VAR_NAME_TWITTER_ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String ENV_VAR_NAME_TWITTER_TOKEN_SECRET = "TOKEN_SECRET";
	
	static final String MSG_UNSUPPORTED_ENVIRONMENT = "The environment variables " + ENV_VAR_NAME_TWITTER_CONSUMER_KEY + ", " + 
	ENV_VAR_NAME_TWITTER_CONSUMER_SECRET + ", " + ENV_VAR_NAME_TWITTER_ACCESS_TOKEN + ", " + ENV_VAR_NAME_TWITTER_TOKEN_SECRET +
	"must be specified for this test to run";
	
	private EnvironmentSpecificTwitterClientUtil() {
		throw new UnsupportedOperationException("No instantiation allowed");
	}
	
	static boolean isSupportedEnviroment() {
		final OAuthCredentials creds = getCredentials();
		return creds.consumerKey != null && creds.consumerSecret != null && 
				creds.accessToken != null && creds.tokenSecret != null;
	}
	
	static Twitter getTwitterClient() throws IllegalStateException {
		if (!isSupportedEnviroment()) {
			throw new IllegalStateException(MSG_UNSUPPORTED_ENVIRONMENT);
		}
		
		OAuthCredentials creds = getCredentials();
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
			.setOAuthConsumerKey(creds.consumerKey)
			.setOAuthConsumerSecret(creds.consumerSecret)
			.setOAuthAccessToken(creds.accessToken)
			.setOAuthAccessTokenSecret(creds.tokenSecret);
		
		TwitterFactory tf = new TwitterFactory(cb.build());
		return tf.getInstance();
	}
	
	private static OAuthCredentials getCredentials() {
		OAuthCredentials creds = new OAuthCredentials();
		creds.consumerKey = SecurityActions.getEnvironmentVariable(ENV_VAR_NAME_TWITTER_CONSUMER_KEY);
		creds.consumerSecret = SecurityActions.getEnvironmentVariable(ENV_VAR_NAME_TWITTER_CONSUMER_SECRET);
		creds.accessToken = SecurityActions.getEnvironmentVariable(ENV_VAR_NAME_TWITTER_ACCESS_TOKEN);
		creds.tokenSecret = SecurityActions.getEnvironmentVariable(ENV_VAR_NAME_TWITTER_TOKEN_SECRET);
		return creds;
	}

	private static class OAuthCredentials {
		private String consumerKey;
		private String consumerSecret;
		private String accessToken;
		private String tokenSecret;
	}
	

}
