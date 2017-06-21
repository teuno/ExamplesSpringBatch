package CourseJob;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

class TransactionShareRateMapper implements RowMapper<TransactionShareRateDTO>
{
	@Override
	public TransactionShareRateDTO mapRow(ResultSet rs, int rowNum) throws SQLException
	{

		TransactionShareRateDTO tsrDTO = new TransactionShareRateDTO();
		tsrDTO.setAccountNumber(rs.getString("Accountnumber"));
		tsrDTO.setFundID(rs.getLong("FundID"));
		tsrDTO.setSum(rs.getBigDecimal("TransactionSum"));
		tsrDTO.setShareRate(rs.getBigDecimal("ShareRate"));
		tsrDTO.setSharesException(rs.getBoolean("SharesException"));
		tsrDTO.setTransactionException(rs.getBoolean("TransactionException"));
		return tsrDTO;
	}
}
