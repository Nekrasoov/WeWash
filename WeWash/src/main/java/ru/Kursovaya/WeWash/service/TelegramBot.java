package ru.Kursovaya.WeWash.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

        @Value("${bot.name}")
        String botName;
        @Value("${bot.token}")
        String token;



    static final String HELP_TEXT = "Чтобы получить помощь по решению проблем вам нужно выбрать: \n\n" +
        "Университет /university \n\n" +
        "Адрес общежития, если указан  \n\n" +
            "Номер машинки, по которой у вас вопрос  \n\n" +
            "И описать проблему, с которой вы столкнулись.";


    public TelegramBot() {
        List <BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начать общение"));
        listOfCommands.add(new BotCommand("/university", "Выбрать университет"));
        listOfCommands.add(new BotCommand("/help", "Информация как пользоваться"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Ошибка настройки команд бота: " + e.getMessage());
        }
    }




    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }



    String universityText = " ";
    String adresText = "";
    String machineText = "";


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String userProblem = update.getMessage().getText();
            String userName =  update.getMessage().getChat().getFirstName();
            String messageId = String.valueOf(update.getMessage().getMessageId());
            Integer postId = update.getMessage().getMessageId();


            long chatId = update.getMessage().getChatId();


            switch (messageText) {
                case "/start":
                    if (chatId != groupChatId) {
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    }
                    break;
                case "/admin":
                    if (chatId == groupChatId) {
                    }
                    break;
                case "/id":
                    if (chatId == groupChatId) {
                    }
                    break;
                case "/text":
                    if (chatId == groupChatId) {
                    }
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                case "/university":
                    university(chatId);
                    break;
                case "/urfu":
                    urfu(chatId);
                    break;
                case "/uyrgu":
                    uyrgu(chatId);
                    break;
                case "/machine":
                    machine(chatId);
                    break;

                default:
                    if (chatId != groupChatId) {
                    if (universityText.isEmpty()) {
                        sendMessage(chatId, "Выберите нужный пункт из представленного списка.");
                        university(chatId);
                    } else if (universityText.equals("УРФУ") && adresText.isEmpty()) {
                        sendMessage(chatId, "Выберите нужный пункт из представленного списка.");
                        urfu(chatId);
                    } else if (universityText.equals("ЮУрГУ") && adresText.isEmpty()) {
                        sendMessage(chatId, "Выберите нужный пункт из представленного списка.");
                        uyrgu(chatId);

                    } else if (machineText.isEmpty()) {
                        sendMessage(chatId, "Выберите нужный пункт из представленного списка.");
                        machine(chatId);
                    }
                        String combinedMessage = universityText + " " + adresText + " " + machineText;
                        String message = "id: " + chatId + " msg: " + messageId + " " + userName + ": " + "\n" + userProblem;
                        String fullMessage = combinedMessage + "\n" + message;
//                        sendMessageToGroup(fullMessage);
                        sendMessageToChannel(fullMessage);
                        sendMessageFromChannelToUserChat(Long.valueOf(postId),channelChatId, 388783190L);
                }
            }



        } else if (update.hasCallbackQuery()){

            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals("URFU_BUTTON") || callbackData.equals("REURFU_ADRES")) {
                universityText = "УРФУ";
                adresText = "";
                machineText = "";
                urfu(chatId);

            } else if (callbackData.equals("K1A_BUTTON")) {
                adresText = "Комитерна 1a";
                machine(chatId);
            } else if (callbackData.equals("K3_BUTTON")) {
                adresText = "Комитерна 3";
                machine(chatId);
            } else if (callbackData.equals("K11_BUTTON")) {
                adresText = "Комитерна 11";
                machine(chatId);
            } else if (callbackData.equals("F4_BUTTON")) {
                adresText = "Фонвизина 4";
                machine(chatId);
            } else if (callbackData.equals("LEN66_BUTTON")) {
                adresText = "Ленина 66";
                machine(chatId);
            } else if (callbackData.equals("CH16A_BUTTON")) {
                adresText = "Чапаева 16а";
                machine(chatId);
            } else if (callbackData.equals("M140_BUTTON")) {
                adresText = "Малышева 140";
                machine(chatId);
            } else if (callbackData.equals("M144_BUTTON")) {
                adresText = "Малышева 144";
                machine(chatId);
            } else if (callbackData.equals("KOM70_BUTTON")) {
                adresText = "Комсомольская 70";
                machine(chatId);
            } else if (callbackData.equals("B71_BUTTON")) {
                adresText = "Большакова 71";
                machine(chatId);
            } else if (callbackData.equals("B77_BUTTON")) {
                adresText = "Большакова 77";
                machine(chatId);
            } else if (callbackData.equals("B79_BUTTON")) {
                adresText = "Большакова 79";
                machine(chatId);


            } else if (callbackData.equals("UGLTU_BUTTON")) {
                universityText = "УГЛТУ";
                adresText = "";
                machineText = "";
                machine(chatId);

            } else if (callbackData.equals("UYRGU_BUTTON") | callbackData.equals("REUYRGU_ADRES")) {
                universityText = "ЮУрГУ";
                adresText = "";
                machineText = "";
                uyrgu(chatId);
            } else if (callbackData.equals("LEN78_BUTTON")) {
                adresText = "Ленина 78";
                machine(chatId);
            } else if (callbackData.equals("LEN80_BUTTON")) {
                adresText = "Ленина 80";
                machine(chatId);
            } else if (callbackData.equals("LEN80A_BUTTON")) {
                adresText = "Ленина 80a";
                machine(chatId);

            }else if (callbackData.equals("OMGPU_BUTTON")) {
                universityText = "ОмГПУ";
                adresText = "";
                machineText = "";
                machine(chatId);

            }else if (callbackData.equals("UGGU_BUTTON")) {
                universityText = "УГГУ";
                adresText = "";
                machineText = "";
                machine(chatId);

            } else if (callbackData.equals("ONE_BUTTON")) {
                machineText = "1";
                fullMessage(chatId, universityText, adresText, machineText);
            } else if (callbackData.equals("TWO_BUTTON")) {
                machineText = "2";
                fullMessage(chatId, universityText, adresText, machineText);
            } else if (callbackData.equals("THREE_BUTTON")) {
                machineText = "3";
                fullMessage(chatId, universityText, adresText, machineText);
            } else if (callbackData.equals("FOUR_BUTTON")) {
                machineText = "4";
                fullMessage(chatId, universityText, adresText, machineText);
            } else if (callbackData.equals("FIVE_BUTTON")) {
                machineText = "5";
                fullMessage(chatId, universityText, adresText, machineText);

            }  else if (callbackData.equals("RE_UNIVERSITY")) {
                university(chatId);
            }

        }

    }




    private void startCommandReceived(long chatId, String name) {
        String answer = "Привет, " + name + ", это поддержка прачечных WeWash, для того, чтобы вам помочь нам нужно знать твой вуз:";
        log.info("Replied to user " + name);
        sendMessage(chatId, answer);
        university(chatId);

        universityText = "";
        adresText = "";
        machineText = "";

    }

    private void university(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите университет");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var urfuButton = new InlineKeyboardButton();

        urfuButton.setText("УрФУ");
        urfuButton.setCallbackData("URFU_BUTTON");

        var ugltuButton = new InlineKeyboardButton();

        ugltuButton.setText("УГЛТУ");
        ugltuButton.setCallbackData("UGLTU_BUTTON");

        var uyrguButton = new InlineKeyboardButton();

        uyrguButton.setText("ЮУрГУ");
        uyrguButton.setCallbackData("UYRGU_BUTTON");

        rowInline.add(urfuButton);
        rowInline.add(ugltuButton);
        rowInline.add(uyrguButton);

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        List<InlineKeyboardButton> row2Inline = new ArrayList<>();

        var omgpuButton = new InlineKeyboardButton();

        omgpuButton.setText("ОмГПУ");
        omgpuButton.setCallbackData("OMGPU_BUTTON");

        var ugguButton = new InlineKeyboardButton();

        ugguButton.setText("УГГУ");
        ugguButton.setCallbackData("UGGU_BUTTON");


        row2Inline.add(omgpuButton);
        row2Inline.add(ugguButton);

        rowsInline.add(row2Inline);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }

    }

    private void urfu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите адрес общежития УрФУ");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        List<InlineKeyboardButton> row2Inline = new ArrayList<>();
        List<InlineKeyboardButton> row3Inline = new ArrayList<>();
        List<InlineKeyboardButton> row4Inline = new ArrayList<>();
        List<InlineKeyboardButton> row5Inline = new ArrayList<>();
        List<InlineKeyboardButton> row6Inline = new ArrayList<>();


        var k1aButton = new InlineKeyboardButton();

        k1aButton.setText("Комитерна 1a");
        k1aButton.setCallbackData("K1A_BUTTON");

        var k3Button = new InlineKeyboardButton();

        k3Button.setText("Комитерна 3");
        k3Button.setCallbackData("K3_BUTTON");

        var k11Button = new InlineKeyboardButton();

        k11Button.setText("Комитерна 11");
        k11Button.setCallbackData("K11_BUTTON");

        rowInline.add(k1aButton);
        rowInline.add(k3Button);
        rowInline.add(k11Button);

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        var f4Button = new InlineKeyboardButton();

        f4Button.setText("Фонвизина 4 ");
        f4Button.setCallbackData("F4_BUTTON");

        var len66Button = new InlineKeyboardButton();

        len66Button.setText("Ленина 66");
        len66Button.setCallbackData("LEN66_BUTTON");

        var ch16aButton = new InlineKeyboardButton();

        ch16aButton.setText("Чапаева 16а");
        ch16aButton.setCallbackData("CH16A_BUTTON");

        row3Inline.add(f4Button);
        row3Inline.add(len66Button);
        row3Inline.add(ch16aButton);

        rowsInline.add(row3Inline);
        markupInline.setKeyboard(rowsInline);

        var m140Button = new InlineKeyboardButton();

        m140Button.setText("Малышева 140");
        m140Button.setCallbackData("M140_BUTTON");

        var m144Button = new InlineKeyboardButton();

        m144Button.setText("Малышева 144");
        m144Button.setCallbackData("M144_BUTTON");



        row2Inline.add(m140Button);
        row2Inline.add(m144Button);

        rowsInline.add(row2Inline);
        markupInline.setKeyboard(rowsInline);

        var kom70Button = new InlineKeyboardButton();

        kom70Button.setText("Комсомольская 70");
        kom70Button.setCallbackData("KOM70_BUTTON");

        var b71Button = new InlineKeyboardButton();

        b71Button.setText("Большакова 71");
        b71Button.setCallbackData("B71_BUTTON");

        row4Inline.add(kom70Button);
        row4Inline.add(b71Button);

        rowsInline.add(row4Inline);
        markupInline.setKeyboard(rowsInline);

        var b79Button = new InlineKeyboardButton();

        b79Button.setText("Большакова 79");
        b79Button.setCallbackData("B79_BUTTON");

        var b77Button = new InlineKeyboardButton();

        b77Button.setText("Большакова 77");
        b77Button.setCallbackData("B77_BUTTON");

        row5Inline.add(b79Button);
        row5Inline.add(b77Button);

        rowsInline.add(row5Inline);
        markupInline.setKeyboard(rowsInline);

        var reuButton = new InlineKeyboardButton();

        reuButton.setText("Выбрать другой университет");
        reuButton.setCallbackData("RE_UNIVERSITY");

        row6Inline.add(reuButton);

        rowsInline.add(row6Inline);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }

    }


    private void uyrgu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите адрес общежития УЮрГУ");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        List<InlineKeyboardButton> row2Inline = new ArrayList<>();

        var len78Button = new InlineKeyboardButton();

        len78Button.setText("Ленина 78");
        len78Button.setCallbackData("LEN78_BUTTON");

        var len80Button = new InlineKeyboardButton();

        len80Button.setText("Ленина 80");
        len80Button.setCallbackData("LEN80_BUTTON");

        var len80aButton = new InlineKeyboardButton();

        len80aButton.setText("Ленина 80а");
        len80aButton.setCallbackData("LEN80A_BUTTON");

        rowInline.add(len78Button);
        rowInline.add(len80Button);
        rowInline.add(len80aButton);

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        var reuButton = new InlineKeyboardButton();

        reuButton.setText("Выбрать другой университет");
        reuButton.setCallbackData("RE_UNIVERSITY");

        row2Inline.add(reuButton);

        rowsInline.add(row2Inline);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }

    }



    private void machine(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        if (adresText.isEmpty()) {
            message.setText("Выберите номер машинки в университете " + universityText);
        } else  message.setText("Выберите номер машинки в университете " + universityText + " по адресу  " + adresText);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        List<InlineKeyboardButton> row2Inline = new ArrayList<>();
        List<InlineKeyboardButton> row3Inline = new ArrayList<>();
        List<InlineKeyboardButton> row4Inline = new ArrayList<>();


        var oneButton = new InlineKeyboardButton();

        oneButton.setText("1");
        oneButton.setCallbackData("ONE_BUTTON");


        var twoButton = new InlineKeyboardButton();

        twoButton.setText("2");
        twoButton.setCallbackData("TWO_BUTTON");

        var threeButton = new InlineKeyboardButton();

        threeButton.setText("3");
        threeButton.setCallbackData("THREE_BUTTON");

        var fourButton = new InlineKeyboardButton();

        fourButton.setText("4");
        fourButton.setCallbackData("FOUR_BUTTON");

        var fiveButton = new InlineKeyboardButton();

        fiveButton.setText("5");
        fiveButton.setCallbackData("FIVE_BUTTON");

        rowInline.add(oneButton);
        rowInline.add(twoButton);
        rowInline.add(threeButton);
        rowInline.add(fourButton);
        rowInline.add(fiveButton);

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        if (universityText.equals("ЮУрГУ")) {
            var reuyrguButton = new InlineKeyboardButton();

            reuyrguButton.setText("Выбрать другой адрес");
            reuyrguButton.setCallbackData("REUYRGU_ADRES");

            row3Inline.add(reuyrguButton);


            rowsInline.add(row3Inline);
            markupInline.setKeyboard(rowsInline);
        }

        if (universityText.equals("УРФУ")) {
            var reurfuButton = new InlineKeyboardButton();

            reurfuButton.setText("Выбрать другой адрес");
            reurfuButton.setCallbackData("REURFU_ADRES");

            row4Inline.add(reurfuButton);


            rowsInline.add(row4Inline);
            markupInline.setKeyboard(rowsInline);
        }

        if (adresText.isEmpty()) {
            var reuButton = new InlineKeyboardButton();

            reuButton.setText("Выбрать другой университет");
            reuButton.setCallbackData("RE_UNIVERSITY");

            row2Inline.add(reuButton);

            rowsInline.add(row2Inline);

            markupInline.setKeyboard(rowsInline);
        }
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    public
    void fullMessage(long chatId, String universityText, String adresText, String machineText) {
        String systemMessageText;
        String messageText;


        if (adresText.isEmpty()) {

            systemMessageText = "Университет: " + universityText + ", машинка номер " + machineText + "." ;
            messageText =  "\n" + "Опишите проблему, которая возникла. Так же, по возможности, прикрепите" +
                    "фотографию машинки, терминала и скриншот оплаты (если есть). Оператор ответит вам в ближайшее время!";
        } else {
            systemMessageText ="Университет: " + universityText + ". Адрес: " + adresText + ", машинка номер " + machineText + ".";
            messageText = "\n" + "Опишите проблему, которая возникла. Так же, по возможности, прикрепите" +
                    "фотографию машинки, терминала и скриншот оплаты (если есть). Оператор ответит вам в ближайшее время!";
        }

        sendMessage(chatId, systemMessageText);
        sendMessage(chatId, messageText);

    }





    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);


        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }



    private static final long groupChatId = -1002125625596L;
    private static final long channelChatId = -1002115699702L;
    public String sendMessageToGroup(String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(groupChatId));
        message.setText(messageText);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            execute(message);
        } catch (TelegramApiRequestException e) {
            System.out.println("Ошибка при регистрации бота: " + e.getMessage());
        } catch (TelegramApiException e) {
            System.out.println("Ошибка при отправке сообщения: " + e.getMessage());
        }
        return messageText;
    }


    public Integer sendMessageToChannel(String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(channelChatId));
        message.setText(messageText);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            Message sentMessage = execute(message);
            Integer postId = sentMessage.getMessageId();
            System.out.println(postId);
            return postId;
        } catch (TelegramApiRequestException e) {
            System.out.println("Ошибка при регистрации бота: " + e.getMessage());
        } catch (TelegramApiException e) {
            System.out.println("Ошибка при отправке сообщения: " + e.getMessage());
        }
        return null;
    }

    public void sendMessageFromChannelToUserChat(Long messageThreadId, Long fromChatId, Long toChatId) {
        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(fromChatId);
        forwardMessage.setChatId(toChatId);
        forwardMessage.setMessageId(messageThreadId.intValue());

        try {
            Message sentMessage = execute(forwardMessage);
            System.out.println("Message successfully sent: " + sentMessage.getText());
        } catch (TelegramApiException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }
    public void forwardCommentToUserChat(Long commentId, Long fromChatId, Long toChatId) {
        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setFromChatId(fromChatId);
        forwardMessage.setChatId(toChatId);
        forwardMessage.setMessageId(commentId.intValue());

        try {
            Message forwardedMessage = execute(forwardMessage);
            System.out.println("Comment successfully forwarded to user chat: " + forwardedMessage.getText());
        } catch (TelegramApiException e) {
            System.out.println("Error forwarding comment to user chat: " + e.getMessage());
        }
    }
}

