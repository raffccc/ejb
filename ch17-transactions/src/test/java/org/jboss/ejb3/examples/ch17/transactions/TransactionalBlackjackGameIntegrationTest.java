package org.jboss.ejb3.examples.ch17.transactions;

import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.ejb3.examples.ch17.transactions.api.BankLocalBusiness;
import org.jboss.ejb3.examples.ch17.transactions.api.BlackjackGameLocalBusiness;
import org.jboss.ejb3.examples.ch17.transactions.ejb.DbInitializerBean;
import org.jboss.ejb3.examples.ch17.transactions.ejb.ExampleUserData;
import org.jboss.ejb3.examples.ch17.transactions.entity.Account;
import org.jboss.ejb3.examples.ch17.transactions.entity.User;
import org.jboss.ejb3.examples.ch17.transactions.impl.BankBean;
import org.jboss.ejb3.examples.ch17.transactions.impl.BlackjackServiceConstants;
import org.jboss.ejb3.examples.testsupport.dbinit.DbInitializerLocalBusiness;
import org.jboss.ejb3.examples.testsupport.dbquery.EntityManagerExposingBean;
import org.jboss.ejb3.examples.testsupport.dbquery.EntityManagerExposingLocalBusiness;
import org.jboss.ejb3.examples.testsupport.entity.IdentityBase;
import org.jboss.ejb3.examples.testsupport.txwrap.ForcedTestException;
import org.jboss.ejb3.examples.testsupport.txwrap.TaskExecutionException;
import org.jboss.ejb3.examples.testsupport.txwrap.TxWrappingLocalBusiness;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class TransactionalBlackjackGameIntegrationTest {

	private static final Logger log = Logger.getLogger(TransactionalBlackjackGameIntegrationTest.class.getName());

	@EJB(mappedName="java:global/test/DbInitializerBean!org.jboss.ejb3.examples.testsupport.dbinit.DbInitializerLocalBusiness")
	private DbInitializerLocalBusiness dbInitializer;

	@EJB(mappedName="java:global/test/TxWrappingBean!org.jboss.ejb3.examples.testsupport.txwrap.TxWrappingLocalBusiness")
	private TxWrappingLocalBusiness txWrapper;

	@EJB(mappedName="java:global/test/EntityManagerExposingBean!org.jboss.ejb3.examples.testsupport.dbquery.EntityManagerExposingLocalBusiness")
	private EntityManagerExposingLocalBusiness emHook;

	@EJB(mappedName="java:global/test/BankBean!org.jboss.ejb3.examples.ch17.transactions.api.BankLocalBusiness")
	private BankLocalBusiness bank;

	@EJB(mappedName="java:global/test/BlackjackGameBean!org.jboss.ejb3.examples.ch17.transactions.api.BlackjackGameLocalBusiness")
	private BlackjackGameLocalBusiness blackjackGame;

	@Deployment
	public static JavaArchive getDeployment() {
		final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addPackages(false, BankLocalBusiness.class.getPackage(), User.class.getPackage(), 
						DbInitializerBean.class.getPackage(), TxWrappingLocalBusiness.class.getPackage(),
						BankBean.class.getPackage(), DbInitializerLocalBusiness.class.getPackage(),
						EntityManagerExposingBean.class.getPackage(), IdentityBase.class.getPackage())
						.addAsManifestResource("persistence.xml");

		log.info(archive.toString(true));
		return archive;
	}
	
	@After
	public void refreshWithDefaultData() throws Exception {
		dbInitializer.refreshWithDefaultData();
	}
	
	@Test
	public void transferRetainsIntegrity() throws Throwable {
		final long alrubingerAccountId = ExampleUserData.ACCOUNT_ALRUBINGER_ID;
		final long blackjackAccountId = BlackjackServiceConstants.ACCOUNT_BLACKJACKGAME_ID;
		
		final BigDecimal expectedinitialALR = ExampleUserData.INITIAL_ACCOUNT_BALANCE_ALR;
		final BigDecimal expectedinitialBlackjack = BlackjackServiceConstants.INITIAL_ACCOUNT_BALANCE_BLACKJACKGAME;
		
		this.executeInTx(new CheckBalanceOfAccountTask(alrubingerAccountId, expectedinitialALR),
				new CheckBalanceOfAccountTask(blackjackAccountId, expectedinitialBlackjack));
		
		final BigDecimal oneHundred = new BigDecimal(100);
		bank.transfer(alrubingerAccountId, blackjackAccountId, oneHundred);
		
		this.executeInTx(new CheckBalanceOfAccountTask(alrubingerAccountId, expectedinitialALR.subtract(oneHundred)),
				new CheckBalanceOfAccountTask(blackjackAccountId, expectedinitialBlackjack.add(oneHundred)));
		
		boolean gotExpectedException = false;
		final Callable<Void> transferTask = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				bank.transfer(alrubingerAccountId, blackjackAccountId, oneHundred);
				return null;
			}
		};
		
		try {
		this.executeInTx(
				transferTask, 
				new CheckBalanceOfAccountTask(alrubingerAccountId, expectedinitialALR.subtract(oneHundred).subtract(oneHundred)),
				new CheckBalanceOfAccountTask(blackjackAccountId, expectedinitialBlackjack.add(oneHundred).add(oneHundred)),
				ForcedTestExceptionTask.INSTANCE);
		} catch (final ForcedTestException fte) {
			gotExpectedException = true;
		}
		
		Assert.assertTrue("Didn't receive expected exception as signaled from the test; wasn't rolled back", gotExpectedException);
		
		this.executeInTx(new CheckBalanceOfAccountTask(alrubingerAccountId, expectedinitialALR.subtract(oneHundred)),
				new CheckBalanceOfAccountTask(blackjackAccountId, expectedinitialBlackjack.add(oneHundred)));
	}
	
	@Test
	public void sequenceOfBetsDoesntRollBackAll() {
		final BigDecimal originalBalance = ExampleUserData.INITIAL_ACCOUNT_BALANCE_ALR;
		log.info("Starting balance before playing blackjack: " + originalBalance);
		
		final BigDecimal betAmount = new BigDecimal(20);
		final Place11BetsThenForceExceptionTask task = new Place11BetsThenForceExceptionTask(betAmount);
		boolean gotForcedException = false;
		try {
			this.executeInTx(task);
		} catch (Throwable e) {
			gotForcedException = true;
		}
		
		Assert.assertTrue("Did not obtain the test exception as expected", gotForcedException);
		
		final BigDecimal afterBetsBalance = bank.getBalance(ExampleUserData.ACCOUNT_ALRUBINGER_ID);
		final int gameOutcomeCount = task.gameOutcomeCount;
		new AssertGameOutcome(originalBalance, afterBetsBalance, gameOutcomeCount, betAmount).call();
	}
	
	/*
	 * Helpers
	 */
	
	private static final class AssertGameOutcome implements Callable<Void> {

		private final BigDecimal originalBalance;
		
		private final int gameOutcomeCount;
		
		private final BigDecimal betAmount;
		
		private final BigDecimal afterBetsBalance;
		
		AssertGameOutcome(final BigDecimal originalBalance, final BigDecimal afterBetsBalance,
				final int gameOutcomeCount, final BigDecimal betAmount) {
			this.originalBalance = originalBalance;
			this.afterBetsBalance = afterBetsBalance;
			this.gameOutcomeCount = gameOutcomeCount;
			this.betAmount = betAmount;
		}
		
		
		/*
		 * (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public Void call() {
			final BigDecimal expectedGains = betAmount.multiply(new BigDecimal(gameOutcomeCount));
			final BigDecimal expectedBalance = originalBalance.add(expectedGains);
			Assert.assertTrue("Balance after all bets was not as expected " + expectedBalance + " but was " + afterBetsBalance, expectedBalance.compareTo(afterBetsBalance) == 0);
			return null;
		}
	}
	
	private final class Place11BetsThenForceExceptionTask implements Callable<Void> {
		
		private int gameOutcomeCount = 0;
		
		private final BigDecimal betAmount;
			
		Place11BetsThenForceExceptionTask(final BigDecimal betAmount) {
			this.betAmount = betAmount;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public Void call() throws Exception {
			final long alrubingerAccountId = ExampleUserData.ACCOUNT_ALRUBINGER_ID;
			final BigDecimal startingBalance = bank.getBalance(alrubingerAccountId);
			
			for (int i = 0; i < 11; i++) {
				final boolean win = blackjackGame.bet(ExampleUserData.ACCOUNT_ALRUBINGER_ID, betAmount);
				gameOutcomeCount += win ? 1 : -1;
			}
			
			log.info("Won " + gameOutcomeCount + " games at " + betAmount + "/game");
			
			final BigDecimal afterBetsBalance = bank.getBalance(alrubingerAccountId);
			
			//Ensure that money's been allocated properly
			new AssertGameOutcome(startingBalance, afterBetsBalance, gameOutcomeCount, betAmount).call();
			
			//Force an exception to get a Tx rollback. This should not affect the
			//money already transferred during the bets, as they should have taken place
			//in nested Txs and already committed
			throw new ForcedTestException();
		}
		
	}
	
	private final class CheckBalanceOfAccountTask implements Callable<Void> {
		private long accountId;
		
		private BigDecimal expectedBalance;
		
		CheckBalanceOfAccountTask(final long accountId, final BigDecimal expectedBalance) {
			assert accountId > 0;
			assert expectedBalance != null;
			this.accountId = accountId;
			this.expectedBalance = expectedBalance;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public Void call() throws Exception {
			final Account account = emHook.getEntityManager().find(Account.class, accountId);
			Assert.assertTrue("Balance was not as expected", expectedBalance.compareTo(account.getBalance()) == 0);
			return null;
		}
		
	}
	
	/*
	 * Task which throws a exception for use in testing
	 * for instance to force a tx rollback.
	 */
	private enum ForcedTestExceptionTask implements Callable<Void> {
		INSTANCE;

		@Override
		public Void call() throws Exception {
			throw new ForcedTestException();
		}
		
	}
	
	private void executeInTx(final Callable<?>... tasks) throws Throwable {
		assert tasks != null : "Tasks must be specified";
		
		try {
			txWrapper.wrapInTx(tasks);
		} catch (final TaskExecutionException tee) {
			throw tee.getCause();
		}
	}
	
	
}
