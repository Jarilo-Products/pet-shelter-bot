package pro.sky.petshelterbot.processor;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.listener.TelegramBotUpdatesListener;
import pro.sky.petshelterbot.model.LastCommand;
import pro.sky.petshelterbot.model.enums.Type;
import pro.sky.petshelterbot.repository.LastCommandRepository;
import pro.sky.petshelterbot.service.LastCommandService;

import java.util.Optional;

import static pro.sky.petshelterbot.utility.TextUtils.ANSWERS;

@Component
public class TextMessageProcessor {

  private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

  private final TelegramBot telegramBot;

  private final LastCommandService lastCommandService;
  private final LastCommandRepository lastCommandRepository;

  @Autowired
  public TextMessageProcessor(TelegramBot telegramBot,
                              LastCommandService lastCommandService,
                              LastCommandRepository lastCommandRepository) {
    this.telegramBot = telegramBot;
    this.lastCommandService = lastCommandService;
    this.lastCommandRepository = lastCommandRepository;
  }

  public void processTextMessage(long chatId, String text) {
    Optional<LastCommand> optionalLastCommand = lastCommandService.getByChatId(chatId);
    if (optionalLastCommand.isEmpty() && !text.equalsIgnoreCase("/start")) {
      return;
    }
    if (optionalLastCommand.isEmpty()) {
      processFirstStartCommand(chatId);
      return;
    }

    LastCommand lastCommand = optionalLastCommand.get();
    if (!lastCommand.getIsClosed()) {
      switch (lastCommand.getLastCommand()) {
        case "/start" -> processChoosingShelter(lastCommand, text);

      }
    } else {
      switch (text) {
        case "/start" -> processStartCommand(lastCommand);
      }
    }
  }

  private void processFirstStartCommand(long chatId) {
    String message = ANSWERS.get("start");
    sendMessage(chatId, message);
    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(chatId);
    lastCommand.setLastCommand("/start");
    lastCommand.setIsClosed(false);
    lastCommandRepository.save(lastCommand);
  }

  private void processStartCommand(LastCommand lastCommand) {
    String message = ANSWERS.get("start_registered");
    sendMessage(lastCommand.getChatId(), message);
    lastCommand.setLastCommand("/start");
    lastCommand.setIsClosed(false);
    lastCommandRepository.save(lastCommand);
  }

  private void processChoosingShelter(LastCommand lastCommand, String text) {
    switch (text.trim()) {
      case "1" -> {
        sendMessage(lastCommand.getChatId(), ANSWERS.get("chosen_cat"));
        lastCommand.setIsClosed(true);
        lastCommand.setActiveType(Type.CAT);
        lastCommandRepository.save(lastCommand);
      }
      case "2" -> {
        sendMessage(lastCommand.getChatId(), ANSWERS.get("chosen_dog"));
        lastCommand.setIsClosed(true);
        lastCommand.setActiveType(Type.DOG);
        lastCommandRepository.save(lastCommand);
      }
      default -> processStartCommand(lastCommand);
    }
  }

  // YOUR METHODS

  private void sendMessage(long chatId, String message) {
    SendMessage sendMessage = new SendMessage(chatId, message);
    SendResponse sendResponse = telegramBot.execute(sendMessage);
    if (!sendResponse.isOk()) {
      logger.error("Error during sending message: {}", sendResponse.description());
    }
  }

}
