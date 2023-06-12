package api.mono;

import api.Currency;
import api.CurrencyService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class MonoService implements CurrencyService {
    private static final String URL = "https://api.monobank.ua/bank/currency";
    private List<MonoItem> monoItems;

    public MonoService() {
        try {
            String json = Jsoup.connect(URL)
                    .ignoreContentType(true)
                    .get()
                    .body()
                    .text();
            Type typeToken = TypeToken.getParameterized(List.class, MonoItem.class).getType();
            monoItems = new Gson().fromJson(json, typeToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MonoItem> getItemList(Currency currency) {
        int code = getCurrencyCode(currency);
        return monoItems.stream()
                .filter(s -> s.getCurrencyCodeA() == code)
                .collect(Collectors.toList());
    }

    @Override
    public String getCurrencyRate(Currency currency) {
        int code = getCurrencyCode(currency);
        List<MonoItem> filteredItems = getItemList(currency);
        double buy = filteredItems.stream()
                .mapToDouble(MonoItem::getRateBuy)
                .findFirst()
                .orElseThrow();
        double sell = filteredItems.stream()
                .mapToDouble(MonoItem::getRateSell)
                .findFirst()
                .orElseThrow();
        return "Курс " + currency + " в МоноБанк\uD83C\uDDFA\uD83C\uDDE6:" +
                "\nКупівля: " + buy + " грн." +
                "\nПродаж: " + Math.round(sell*100.0)/100.0 + " грн";
    }

    @Override
    public double convertorToUAH(Currency currency, double value) {
        int code = getCurrencyCode(currency);
        List<MonoItem> filteredItems = getItemList(currency);
        double buy = filteredItems.stream()
                .mapToDouble(MonoItem::getRateBuy)
                .findFirst()
                .orElseThrow();
        return buy * value;
    }

    @Override
    public double convertorToCurrency(Currency currency, double value) {
        int code = getCurrencyCode(currency);
        List<MonoItem> filteredItems = getItemList(currency);
        double sell = filteredItems.stream()
                .mapToDouble(MonoItem::getRateSell)
                .findFirst()
                .orElseThrow();
        return Math.round((value/sell) * 100.0) / 100.0;
    }

    private int getCurrencyCode(Currency currency) {
        if (currency.equals(Currency.USD)) {
            return 840;
        } else if (currency.equals(Currency.EUR)) {
            return 978;
        } else {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
    }
}
