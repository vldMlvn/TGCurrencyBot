package api.privat;

import api.Currency;
import api.CurrencyService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;


public class PrivatService implements CurrencyService {
    private static final String URL = "https://api.privatbank.ua/p24api/pubinfo?exchange&coursid=5";

    @Override
    public List<PrivatItem> getItemList(Currency currency) {
        String json;
        try {
            json = Jsoup
                    .connect(URL)
                    .ignoreContentType(true)
                    .get()
                    .body()
                    .text();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Type typeToken = TypeToken
                .getParameterized(List.class, PrivatItem.class)
                .getType();
        List<PrivatItem> privatItem = new Gson().fromJson(json, typeToken);
        return privatItem;
    }

    @Override
    public String getCurrencyRate(Currency currency) {
        List<PrivatItem> privatList = getItemList(currency);
        double buy = privatList.stream()
                .filter(s -> s.getCcy() == currency)
                .map(s -> s.getBuy())
                .findFirst()
                .orElseThrow();
        buy = Math.round(buy * 100.0) / 100.0;
        double sale = privatList.stream()
                .filter(s -> s.getCcy() == currency)
                .filter(s -> s.getBase_ccy() == Currency.UAH)
                .map(s -> s.getSale())
                .findFirst()
                .orElseThrow();
        sale = Math.round(sale * 100.0) / 100.0;
        return "Курс " + currency + " в ПиватБанк\uD83C\uDDFA\uD83C\uDDE6:" +
                "\nКупівля: " + buy + " грн." +
                "\nПродаж: " + sale + " грн";
    }

    @Override
    public double convertorToUAH(Currency currency, double value) {
        List<PrivatItem> privatList = getItemList(currency);
        double buy = privatList.stream()
                .filter(s -> s.getCcy() == currency)
                .map(s -> s.getBuy())
                .findFirst()
                .orElseThrow();
        double rez = Math.round((buy * value) * 100.0) / 100.0;
        return rez;
    }

    @Override
    public double convertorToCurrency(Currency currency, double value) {
        List<PrivatItem> privatList = getItemList(currency);
        double sell = privatList.stream()
                .filter(s -> s.getCcy() == currency)
                .map(s -> s.getSale())
                .findFirst()
                .orElseThrow();
        return Math.round((value / sell) * 100.0) / 100.0;
    }
}