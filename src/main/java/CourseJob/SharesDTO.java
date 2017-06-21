package CourseJob;

import java.math.BigDecimal;

class SharesDTO
{
	private String accountNumber;

	private long fundID;

	private BigDecimal amount;

	private boolean ThrowsException;

	String getAccountNumber()
	{
		return accountNumber;
	}

	void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	long getFundID()
	{
		return fundID;
	}

	void setFundID(long fundID)
	{
		this.fundID = fundID;
	}

	BigDecimal getAmount()
	{
		return amount;
	}

	void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}

	boolean isThrowsException()
	{
		return ThrowsException;
	}

	void setThrowsException(boolean throwsException)
	{
		ThrowsException = throwsException;
	}
}
