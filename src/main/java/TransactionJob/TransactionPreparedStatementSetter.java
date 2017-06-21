package TransactionJob;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

class TransactionPreparedStatementSetter implements ItemPreparedStatementSetter<TransactionDTO>
{
	@Override
	public void setValues(TransactionDTO transactionDTO, PreparedStatement preparedStatement) throws SQLException
	{
		// // Testing purposes
		// if (transactionDTO.ThrowsException())
		// throw new ExpectedException(transactionDTO.getOrderlineID(), "writer");
		preparedStatement.setInt(1, transactionDTO.getFundID());
		preparedStatement.setInt(2, transactionDTO.getOrderlineID());
		preparedStatement.setBigDecimal(3, transactionDTO.getTransactionSum());
		preparedStatement.setDate(4, transactionDTO.getTradeDate());
	}

}
