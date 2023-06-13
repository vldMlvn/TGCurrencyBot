package bot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TGBotService {
    private CurrencyTGBot currencyTGBot;
    public TGBotService() {
        currencyTGBot =new CurrencyTGBot();
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(currencyTGBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}