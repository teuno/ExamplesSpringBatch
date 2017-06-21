package TransactionJob;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;

import org.springframework.batch.item.ItemProcessor;

class OrderlinetoTransactionItemProcessor implements ItemProcessor<OrderlineDTO, TransactionDTO>
{

	public TransactionDTO process(OrderlineDTO orderlineSource) throws ExpectedException
	{
		// // Testing purposes
		// if (orderlineSource.DoesThrowException())
		// throw new ExpectedException(orderlineSource.getOrderlineID(), "processor");
		TransactionDTO transactionDTO = new TransactionDTO();
		transactionDTO.setOrderlineID(orderlineSource.getOrderlineID());
		transactionDTO.setFundID(orderlineSource.getFundID());
		transactionDTO.setThrowsException(orderlineSource.DoesThrowException());
		transactionDTO.setTradeDate(findTradeDate());
		transactionDTO.setTransactionSum(calculateTRXSum(orderlineSource));
		return transactionDTO;
	}

	private BigDecimal calculateTRXSum(OrderlineDTO orderlineDTO)
	{
		return orderlineDTO.getOrderSum().multiply(BigDecimal.valueOf(orderlineDTO.getPercentage()))
			.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
	}

	private Date findTradeDate()
	{
		// Business logic.
		return Date.valueOf(LocalDate.now().plusDays(1));
	}
}
