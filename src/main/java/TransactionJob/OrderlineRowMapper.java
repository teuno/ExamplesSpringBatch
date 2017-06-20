package TransactionJob;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

class OrderlineRowMapper implements RowMapper<OrderlineDTO>
{

	@Override
	public OrderlineDTO mapRow(ResultSet rs, int rowNum) throws SQLException
	{
        System.out.println("TEuno" + rowNum);
        // Testing purposes
		if (rs.getBoolean("ThrowsException"))
			throw new ExpectedException(rs.getInt("OrderlineID"), "Reader");

		OrderlineDTO orderlineDTO = new OrderlineDTO();
		orderlineDTO.setOrderlineID(rs.getInt("OrderlineID"));
		orderlineDTO.setFundID(rs.getInt("FundID"));
		orderlineDTO.setOrderSum(rs.getBigDecimal("OrderSum_"));
		orderlineDTO.setPercentage(rs.getDouble("Percentage"));
		orderlineDTO.setThrowsException(rs.getBoolean("ThrowsException"));
		return orderlineDTO;

	}
}
