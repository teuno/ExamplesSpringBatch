package TransactionJob;

import java.math.BigDecimal;
import java.sql.Date;

class TransactionDTO
{
	private int orderlineID;

	private int fundID;

	private Date tradeDate;

	private BigDecimal transactionSum;

	private boolean ThrowsException;

	Date getTradeDate()
	{
		return tradeDate;
	}

	void setTradeDate(Date tradeDate)
	{
		this.tradeDate = tradeDate;
	}

	BigDecimal getTransactionSum()
	{
		return transactionSum;
	}

	void setTransactionSum(BigDecimal transactionSum)
	{
		this.transactionSum = transactionSum;
	}

	int getFundID()
	{
		return fundID;
	}

	void setFundID(int fundID)
	{
		this.fundID = fundID;
	}

	int getOrderlineID()
	{
		return orderlineID;
	}

	void setOrderlineID(int orderlineID)
	{
		this.orderlineID = orderlineID;
	}

	void setThrowsException(boolean throwsException)
	{
		ThrowsException = throwsException;
	}

	boolean ThrowsException()
	{
		return ThrowsException;
	}
}
