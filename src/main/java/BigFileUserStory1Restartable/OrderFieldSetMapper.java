package BigFileUserStory1Restartable;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

class OrderFieldSetMapper implements FieldSetMapper<OrderDTO> {

    @Override
    public OrderDTO mapFieldSet(FieldSet fieldSet) throws BindException {
        OrderDTO order = new OrderDTO();
        order.setRekeningNummer(fieldSet.readString(0));
        order.setBedrag(fieldSet.readBigDecimal(2));
        order.setDatum(fieldSet.readDate(3));

        return order;
    }
}
