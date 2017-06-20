package TransactionJob;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

class StatusPreparedStatementSetter implements ItemPreparedStatementSetter<TransactionDTO>
{

	@Override
	public void setValues(TransactionDTO transactionDTO, PreparedStatement preparedStatement) throws SQLException
	{

		// Testing purposes
		if (transactionDTO.ThrowsException())
			throw new ExpectedException(transactionDTO.getOrderlineID(), "writer");
		preparedStatement.setInt(1, transactionDTO.getOrderlineID());

	}
}
