package TransactiesAggregerenEnStatussenUpdaten;

import java.math.BigDecimal;
import java.sql.Date;

class TempDataTableDTO {
    private String fundName;
    private Date tradeDate;
    private BigDecimal sum;
    private Long orderID;
    private Long orderLineID;

    public TempDataTableDTO() {
    }

    TempDataTableDTO(String fundName, Date tradeDate, BigDecimal sum) {
        this.fundName = fundName;
        this.tradeDate = tradeDate;
        this.sum = sum;
    }

    TempDataTableDTO(String fundName, Date tradeDate, BigDecimal sum, Long orderID, Long orderLineID) {
        this.fundName = fundName;
        this.tradeDate = tradeDate;
        this.sum = sum;
        this.orderID = orderID;
        this.orderLineID = orderLineID;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }

    public Long getOrderLineID() {
        return orderLineID;
    }

    public void setOrderLineID(Long orderLineID) {
        this.orderLineID = orderLineID;
    }
}
