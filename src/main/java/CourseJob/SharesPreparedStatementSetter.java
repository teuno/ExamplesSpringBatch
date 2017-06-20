package CourseJob;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

public class SharesPreparedStatementSetter implements ItemPreparedStatementSetter<SharesDTO>
{
	@Override
	public void setValues(SharesDTO sharesDTO, PreparedStatement preparedStatement) throws SQLException
	{
		preparedStatement.setLong(1, sharesDTO.getFundID());
		preparedStatement.setString(2, sharesDTO.getAccountNumber());
		preparedStatement.setBigDecimal(3, sharesDTO.getAmount());
		preparedStatement.setBigDecimal(4, sharesDTO.getAmount());
	}
}
