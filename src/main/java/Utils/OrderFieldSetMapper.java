package Utils;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

class OrderFieldSetMapper implements FieldSetMapper<OrderDTO> {

    @Override
    public OrderDTO mapFieldSet(FieldSet fieldSet) throws BindException {
        OrderDTO order = new OrderDTO();
        order.setAccountNumber(fieldSet.readString("accountNumber"));
        order.setName(fieldSet.readString("name"));
        order.setMoney(fieldSet.readBigDecimal("money"));
        order.setDate(LocalDate.parse(fieldSet.readString("date"), ISO_LOCAL_DATE));
        return order;
    }
}
