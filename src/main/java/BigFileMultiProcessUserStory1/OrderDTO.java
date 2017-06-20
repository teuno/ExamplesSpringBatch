package BigFileMultiProcessUserStory1;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderDTO implements Serializable {
    private String rekeningNummer;

    private BigDecimal bedrag;

    private Date datum;

    String getRekeningNummer() {
        return rekeningNummer;
    }

    void setRekeningNummer(String rekeningNummer) {
        this.rekeningNummer = rekeningNummer;
    }

    BigDecimal getBedrag() {
        return bedrag;
    }

    void setBedrag(BigDecimal bedrag) {
        this.bedrag = bedrag;
    }

    Date getDatum() {
        return datum;
    }

    void setDatum(Date datum) {
        this.datum = datum;
    }


    @Override
    public String toString() {
        return "OrderDTO{" + "rekeningNummer=" + rekeningNummer + ", bedrag=" + bedrag + ", datum=" + datum + '}';
    }
}
