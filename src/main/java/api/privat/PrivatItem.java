package api.privat;

import api.Currency;

public class PrivatItem {
   private Currency ccy;
   private Currency base_ccy;
   private double buy;
   private double sale;

    public Currency getCcy() {
        return ccy;
    }
    public Currency getBase_ccy() {
        return base_ccy;
    }
    public double getBuy() {
        return buy;
    }
    public double getSale() {
        return sale;
    }
}