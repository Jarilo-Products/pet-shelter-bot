package pro.sky.petshelterbot.utility;

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

  protected void sendMessage(long chatId, String message) {
    SendMessage sendMessage = new SendMessage(chatId, message);
    SendResponse sendResponse = telegramBot.execute(sendMessage);
    if (!sendResponse.isOk()) {
      log.error("Error during sending message: {}", sendResponse.description());
    }
  }

  protected void sendMessage(long chatId, String message, String fileId) {
    SendPhoto sendPhoto = new SendPhoto(chatId, fileId);
    sendPhoto.caption(message);
    SendResponse sendResponse = telegramBot.execute(sendPhoto);
    if (!sendResponse.isOk()) {
      log.error("Error during sending message: {}", sendResponse.description());
    }
  }

}
