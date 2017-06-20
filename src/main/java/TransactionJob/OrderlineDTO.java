package TransactionJob;

import java.math.BigDecimal;

class OrderlineDTO
{

	private int orderlineID;

	private double percentage;

	private int fundID;

	private BigDecimal orderSum;

	private boolean throwsException;
	boolean DoesThrowException()
	{
		return throwsException;
	}

	int getOrderlineID()
	{
		return orderlineID;
	}

	void setOrderlineID(int orderlineID)
	{
		this.orderlineID = orderlineID;
	}

	int getFundID()
	{
		return fundID;
	}

	void setFundID(int fundID)
	{
		this.fundID = fundID;
	}

	BigDecimal getOrderSum()
	{
		return orderSum;
	}

	void setOrderSum(BigDecimal orderSum)
	{
		this.orderSum = orderSum;
	}

	double getPercentage()
	{
		return percentage;
	}

	void setPercentage(double percentage)
	{
		this.percentage = percentage;
	}

	void setThrowsException(boolean throwsException)
	{
		this.throwsException = throwsException;
	}
}
