package api;

import java.util.List;

public interface CurrencyService {
    <E> List<E> getItemList(Currency currency);
    String getCurrencyRate(Currency currency);
    double convertorToUAH(Currency currency, double value);
    double convertorToCurrency(Currency currency,double value);
}
