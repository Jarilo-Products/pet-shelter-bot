package pro.sky.petshelterbot.processor;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.model.LastCommand;
import pro.sky.petshelterbot.model.enums.Type;
import pro.sky.petshelterbot.service.LastCommandService;

import java.util.Optional;

import static pro.sky.petshelterbot.utility.TextUtils.ANSWERS;

@Component
@Slf4j
public class TextMessageProcessor {

  private final TelegramBot telegramBot;
  private final LastCommandService lastCommandService;

  @Autowired
  public TextMessageProcessor(TelegramBot telegramBot,
                              LastCommandService lastCommandService) {
    this.telegramBot = telegramBot;
    this.lastCommandService = lastCommandService;
  }

  public void processTextMessage(long chatId, String text) {
    Optional<LastCommand> optionalLastCommand = lastCommandService.getByChatId(chatId);
    // Пользователь в первый раз зашел в бота и ввел какую-то хрень
    if (optionalLastCommand.isEmpty() && !text.equalsIgnoreCase("/start")) {
      return;
    }
    // Пользователь в первый раз зашел в бота и ввел "/start"
    if (optionalLastCommand.isEmpty()) {
      processFirstStartCommand(chatId);
      return;
    }

    // Если дошли до сюда, то пользователь уже общался с ботом
    LastCommand lastCommand = optionalLastCommand.get();
    if (!lastCommand.getIsClosed()) { // Обработка последних команд пользователя со статусом is_closed = false
      switch (lastCommand.getLastCommand()) {
        case "/start" -> processChoosingShelter(lastCommand, text);

      }
    } else { // Обработка последних команд пользователя со статусом is_closed = true
      switch (text) {
        case "/start" -> processStartCommand(lastCommand);
      }
    }
    lastCommandService.save(lastCommand);
  }

  private void processFirstStartCommand(long chatId) {
    String message = ANSWERS.get("start");
    sendMessage(chatId, message);
    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(chatId);
    lastCommand.setLastCommand("/start");
    lastCommand.setIsClosed(false);
  }

  private void processStartCommand(LastCommand lastCommand) {
    String message = ANSWERS.get("start_registered");
    sendMessage(lastCommand.getChatId(), message);
    lastCommand.setLastCommand("/start");
    lastCommand.setIsClosed(false);
  }

  private void processChoosingShelter(LastCommand lastCommand, String text) {
    switch (text.trim()) {
      case "1" -> {
        sendMessage(lastCommand.getChatId(), ANSWERS.get("chosen_cat"));
        lastCommand.setIsClosed(true);
        lastCommand.setActiveType(Type.CAT);
      }
      case "2" -> {
        sendMessage(lastCommand.getChatId(), ANSWERS.get("chosen_dog"));
        lastCommand.setIsClosed(true);
        lastCommand.setActiveType(Type.DOG);
      }
      default -> processStartCommand(lastCommand);
    }
  }

  // YOUR METHODS

  private void sendMessage(long chatId, String message) {
    SendMessage sendMessage = new SendMessage(chatId, message);
    SendResponse sendResponse = telegramBot.execute(sendMessage);
    if (!sendResponse.isOk()) {
      log.error("Error during sending message: {}", sendResponse.description());
    }
  }

}
