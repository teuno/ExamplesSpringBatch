package OrdersEnRegelsAanmakenVanStortingsbestand;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

class OrderRegelPreparedStatementSetter implements ItemPreparedStatementSetter<OrderLineDTO>
{
	@Override
    public void setValues(OrderLineDTO orderRegelDTO, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, orderRegelDTO.getOrderId());
        preparedStatement.setInt(2, orderRegelDTO.getFondsId());
    }
}
