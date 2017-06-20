package Utils;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrderDTO {
    private String accountNumber;

    private String name;

    private BigDecimal money;

    private LocalDate date;

    public OrderDTO() {
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "accountNumber='" + accountNumber + '\'' +
                ", name='" + name + '\'' +
                ", money=" + money +
                ", date=" + date +
                '}';
    }
}