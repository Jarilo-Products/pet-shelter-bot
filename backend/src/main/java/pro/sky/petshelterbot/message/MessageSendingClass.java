package pro.sky.petshelterbot.message;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class MessageSendingClass {

  protected final TelegramBot telegramBot;

  public MessageSendingClass(TelegramBot telegramBot) {
    this.telegramBot = telegramBot;
  }

  protected void sendMessage(TelegramMessage telegramMessage) {
    SendResponse sendResponse;
    if (telegramMessage.getFileId() != null) {
      SendPhoto sendPhoto = new SendPhoto(telegramMessage.getChatId(), telegramMessage.getFileId());
      sendPhoto.caption(telegramMessage.getText());
      sendResponse = telegramBot.execute(sendPhoto);
    } else {
      SendMessage sendMessage = new SendMessage(telegramMessage.getChatId(), telegramMessage.getText());
      sendResponse = telegramBot.execute(sendMessage);
    }
    if (!sendResponse.isOk()) {
      log.error("Error during sending message: {}", sendResponse.description());
    }
  }

}
