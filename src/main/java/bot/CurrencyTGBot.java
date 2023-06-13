package bot;

import api.Currency;
import api.CurrencyService;
import api.mono.MonoService;
import api.privat.PrivatService;
import bot.commands.StartCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;

public class CurrencyTGBot extends TelegramLongPollingCommandBot {
    private CurrencyService monoService;
    private CurrencyService privatService;
    private static final String CURRENCY_SELECTION_CALLBACK = "currency_selection";
    private static final String BACK_TO_MENU_CALLBACK = "back_to_menu";
    private static final String CONVERTOR_MENU_CALLBACK = "convertor_menu";
    private double convertAmount;
    public CurrencyTGBot() {
        monoService = new MonoService();
        privatService = new PrivatService();

        register(new StartCommand());
    }
    @Override
    public String getBotUsername() {
        return BotConstans.BOT_NAME;
    }
    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            String callbackQuery = update.getCallbackQuery().getData();
            switch (callbackQuery) {
                case CURRENCY_SELECTION_CALLBACK ->
                        showCurrencySelection(update.getCallbackQuery().getMessage().getChat());
                case BACK_TO_MENU_CALLBACK -> showMainMenu(update.getCallbackQuery().getMessage().getChat());
                case "USD", "EUR" -> {
                    Currency currency = Currency.valueOf(callbackQuery);
                    showCurrencyRate(update.getCallbackQuery().getMessage().getChat(), currency);
                }
                case CONVERTOR_MENU_CALLBACK -> getAmount(update.getCallbackQuery().getMessage().getChat());
                case "uah-to-usd" ->
                    getConvertRateToCurrency
                            (update.getCallbackQuery().getMessage().getChat(), Currency.USD, convertAmount);
                case "uah-to-eur" ->
                        getConvertRateToCurrency
                                (update.getCallbackQuery().getMessage().getChat(), Currency.EUR, convertAmount);
                case "usd-to-uah" ->
                        getConvertRateToUAH
                                (update.getCallbackQuery().getMessage().getChat(), Currency.USD, convertAmount);
                case "eur-to-uah" ->
                        getConvertRateToUAH
                                (update.getCallbackQuery().getMessage().getChat(), Currency.EUR, convertAmount);
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            if (convertAmount == 0 && messageText.matches("-?\\d+(\\.\\d+)?")) {
                convertAmount = Double.parseDouble(messageText);
                showConvertorMenu(update.getMessage().getChat());
            }
        }
    }
    @Override
    public String getBotToken() {
        return BotConstans.BOT_TOKEN;
    }
    public void showCurrencyMenu(Chat chat) {
        SendMessage message = createMessage(chat.getId().toString(), "Виберіть валюту:");

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(Collections.singletonList(createInlineKeyboardButton("USD\uD83D\uDCB5", Currency.USD.name())))
                .keyboardRow(Collections.singletonList(createInlineKeyboardButton("EUR\uD83D\uDCB6", Currency.EUR.name())))
                .keyboardRow(Collections.singletonList(createInlineKeyboardButton
                        ("Попереднє меню", BACK_TO_MENU_CALLBACK)))
                .build();

        message.setReplyMarkup(keyboard);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void showCurrencySelection(Chat chat) {
        showCurrencyMenu(chat);
    }
    public void showMainMenu(Chat chat) {
        SendMessage message = createMessage(chat.getId().toString(), "Виберіть функцію:");

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(Collections.singletonList(createInlineKeyboardButton
                        ("Курси валют \uD83D\uDCB5\uD83D\uDCB4", CURRENCY_SELECTION_CALLBACK)))
                .keyboardRow(Collections.singletonList(createInlineKeyboardButton
                        ("Конвертор \uD83D\uDCB5\uD83D\uDCB4", CONVERTOR_MENU_CALLBACK)))
                .build();

        message.setReplyMarkup(keyboard);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void showCurrencyRate(Chat chat, Currency currency) {
        String rate = getCurrencyRate(currency);

        SendMessage message = createMessage(chat.getId().toString(), rate);

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(Collections.singletonList(createInlineKeyboardButton("USD\uD83D\uDCB5", Currency.USD.name())))
                .keyboardRow(Collections.singletonList(createInlineKeyboardButton("EUR\uD83D\uDCB6", Currency.EUR.name())))
                .keyboardRow(Collections.singletonList(createInlineKeyboardButton
                        ("Попереднє меню", BACK_TO_MENU_CALLBACK)))
                .build();

        message.setReplyMarkup(keyboard);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void getAmount(Chat chat) {
        SendMessage message = createMessage(chat.getId().toString(), "Введіть суму:\n\uD83D\uDCB5\uD83D\uDCB6");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void showConvertorMenu(Chat chat) {
        SendMessage message = createMessage
                (chat.getId().toString(), "Виберіть тип конвертування:\n\uD83D\uDCB5\uD83D\uDCB6");

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(Collections.singletonList(createInlineKeyboardButton
                        ("UAH->USD","uah-to-usd")))
                .keyboardRow(Collections.singletonList(createInlineKeyboardButton
                        ("UAH->EUR","uah-to-eur")))
                .keyboardRow(Collections.singletonList(createInlineKeyboardButton
                        ("USD->UAH","usd-to-uah")))
                .keyboardRow(Collections.singletonList(createInlineKeyboardButton
                        ("EUR->UAH","eur-to-uah")))
                .keyboardRow(Collections.singletonList(createInlineKeyboardButton
                        ("Попереднє меню", BACK_TO_MENU_CALLBACK)))
                .build();

        message.setReplyMarkup(keyboard);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private String getCurrencyRate(Currency currency) {
        switch (currency) {
            case USD:
            case EUR:
                return monoService.getCurrencyRate(currency) + "\n" + privatService.getCurrencyRate(currency);
            default:
                return "";
        }
    }
    private void getConvertRateToCurrency(Chat chat,Currency currency,double value){
        double mono = monoService.convertorToCurrency(currency, value);
        double privat=privatService.convertorToCurrency(currency,value);
        String result="Моно Банк: \uD83C\uDDFA\uD83C\uDDE6 "+mono+" "+currency+"\n"+
                "Приват Банк: \uD83C\uDDFA\uD83C\uDDE6 "+privat+" "+ currency;
        SendMessage message = createMessage
                (chat.getId().toString(),result);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void getConvertRateToUAH(Chat chat,Currency currency,double value){
        double mono = monoService.convertorToUAH(currency, value);
        double privat=privatService.convertorToUAH(currency,value);
        String result="Моно Банк: \uD83C\uDDFA\uD83C\uDDE6 "+mono+" "+Currency.UAH+"\n"+
                "Приват Банк: \uD83C\uDDFA\uD83C\uDDE6 "+privat+" "+ Currency.UAH;
        SendMessage message = createMessage
                (chat.getId().toString(),result);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private SendMessage createMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        return message;
    }
    private InlineKeyboardButton createInlineKeyboardButton(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }
}