package bot.commands;

import bot.CurrencyTGBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;



public class StartCommand extends BotCommand {
    public StartCommand() {
        super("start", "Start command");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        String text="Привіт!\uD83D\uDC4B" +
                "\nЯ тестовий проект студента GoIT. Я знаю курси $ i € в різних банках України, " +
                "Я оновлюю інформацю про курс після команди /start " +
                "а також вмію конвертувати валюту\uD83D\uDE0A" +
                "\nВиберіть функцію :";
        SendMessage message= new SendMessage();
        message.setText(text);
        message.setChatId(Long.toString(chat.getId()));
        InlineKeyboardButton currencyButton =
                InlineKeyboardButton.builder().text("Курси валют \uD83D\uDCB5\uD83D\uDCB4").callbackData("currency_selection").build();
        InlineKeyboardButton convertorButton =
                InlineKeyboardButton.builder().text("Конвертор \uD83D\uDCB5\uD83D\uDCB4").callbackData("convertor_menu").build();

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup
                .builder()
                .keyboard(Collections.singleton(Collections.singletonList(currencyButton)))
                .keyboard(Collections.singleton(Collections.singletonList(convertorButton)))
                .build();
        message.setReplyMarkup(keyboard);

        try {
            absSender.execute(message);
        }
        catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
