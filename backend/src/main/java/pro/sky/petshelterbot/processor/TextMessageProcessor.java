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

  /**
   * Обработка текстового сообщения от пользователя бота
   * @param chatId id чата
   * @param text текст сообщения
   */
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
        case "/info" -> processInfoCommand(lastCommand);
        case "/howtopet" -> processHowToPetCommand(lastCommand);
        case "/sendreport" -> processSendReportCommand(lastCommand);
      }
    }
    lastCommandService.save(lastCommand);
  }

  /**
   * Обработка команды <code>/start</code> при первом использованием бота
   * @param chatId id чата
   */
  private void processFirstStartCommand(long chatId) {
    String message = ANSWERS.get("start");
    sendMessage(chatId, message);
    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(chatId);
    lastCommand.setLastCommand("/start");
    lastCommand.setIsClosed(false);
    lastCommandService.save(lastCommand);
  }

  /**
   * Обработка команды <code>/start</code> если пользователь до этого уже запускал эту команду
   * @param lastCommand сущность, содержащая информацию о последней команде пользователя
   *                    и о последнем выбранном приюте. Также содержит флаг <code>isClosed</code>,
   *                    сигнализирующий о том, что пользователь может использовать другие команды.
   *                    Если <code>isClosed == false</code>, то пользователь должен завершить
   *                    использование последней команды.
   */
  private void processStartCommand(LastCommand lastCommand) {
    String message = ANSWERS.get("start_registered");
    sendMessage(lastCommand.getChatId(), message);
    lastCommand.setLastCommand("/start");
    lastCommand.setIsClosed(false);
  }

  /**
   * Метод определяет какой приют был выбран в зависимости от значения <code>text</code>
   * @param lastCommand сущность, содержащая информацию о последней команде пользователя
   *                    и о последнем выбранном приюте. Также содержит флаг <code>isClosed</code>,
   *                    сигнализирующий о том, что пользователь может использовать другие команды.
   *                    Если <code>isClosed == false</code>, то пользователь должен завершить
   *                    использование последней команды.
   * @param text "1" – приют для кошек, "2" – приют для собак. При любом другом значении предлагается
   *             повторно выбрать приют (вызывается метод {@link TextMessageProcessor#processStartCommand})
   */
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

  // TODO: написать методы (3 шт.) обработки команд /info, /howtopet и /sendreport
  // на каждую из команд пользователю выдается сообщение из мапы ANSWERS
  // в соответствии с выбранным приютом (из таблицы last_commands)
  // + написать на свой метод javadoc

  /**
   * Отправляет информацию по выбранному пункту меню "Узнать информацию о приюте"
   * @param lastCommand последняя команда пользователя
   */
  private void processInfoCommand(LastCommand lastCommand) {
    if (lastCommand.getActiveType() == Type.CAT) {
      String message = ANSWERS.get("info_cat");
      sendMessage(lastCommand.getChatId(), message);
    } else {
      String message = ANSWERS.get("info_dog");
      sendMessage(lastCommand.getChatId(), message);
    }
  }

  /**
   * Отправляет информацию по выбранному пункту из меню "Как взять животное из приюта"
   * @param lastCommand последняя команда пользователя
   */
  private void processHowToPetCommand(LastCommand lastCommand) {
    // Пользователь может выбрать необходимую информацию по уходу и оформлению
      String message;
    if (lastCommand.getActiveType() == Type.CAT) {
      message = ANSWERS.get("howtopet_cat");
      sendMessage(lastCommand.getChatId(), message);
    } else {
      message = ANSWERS.get("howtopet_dog");
      sendMessage(lastCommand.getChatId(), message);
    }
  }

  /**
   * Отправляет информацию по выбранному пункту из меню "Прислать отчет о питомце"
   * @param lastCommand последняя команда пользователя
   */
  private void processSendReportCommand(LastCommand lastCommand) {
    String message;
    if (lastCommand.getActiveType() == Type.CAT) {
      message = ANSWERS.get("sendreport_cat");
    } else {
      message = ANSWERS.get("sendreport_dog");
    }
    sendMessage(lastCommand.getChatId(), message);
  }

  private void sendMessage(long chatId, String message) {
    SendMessage sendMessage = new SendMessage(chatId, message);
    SendResponse sendResponse = telegramBot.execute(sendMessage);
    if (!sendResponse.isOk()) {
      log.error("Error during sending message: {}", sendResponse.description());
    }
  }

}
