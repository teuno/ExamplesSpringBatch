package CourseJob;

import java.math.BigDecimal;

public class TransactionShareRateDTO
{
	private String accountNumber;

	private BigDecimal sum;

	private BigDecimal shareRate;

	private boolean transactionException;

	private boolean sharesException;

	private long fundID;

	String getAccountNumber()
	{
		return accountNumber;
	}

	void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	BigDecimal getSum()
	{
		return sum;
	}

	void setSum(BigDecimal sum)
	{
		this.sum = sum;
	}

	BigDecimal getShareRate()
	{
		return shareRate;
	}

	void setShareRate(BigDecimal shareRate)
	{
		this.shareRate = shareRate;
	}

	boolean isTransactionException()
	{
		return transactionException;
	}

	void setTransactionException(boolean transactionException)
	{
		this.transactionException = transactionException;
	}

	boolean isSharesException()
	{
		return sharesException;
	}

	void setSharesException(boolean sharesException)
	{
		this.sharesException = sharesException;
	}

	long getFundID()
	{
		return fundID;
	}

	void setFundID(long fundID)
	{
		this.fundID = fundID;
	}
}
