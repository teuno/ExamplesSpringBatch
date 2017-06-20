package BigFileMultiProcessUserStory1;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

class OrderPreparedStatementSetter implements ItemPreparedStatementSetter<OrderDTO> {
    @Override
    public void setValues(OrderDTO orderDTO, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setBigDecimal(1, orderDTO.getBedrag());
        preparedStatement.setObject(2, orderDTO.getDatum());
        preparedStatement.setString(3, orderDTO.getRekeningNummer());
    }
}
