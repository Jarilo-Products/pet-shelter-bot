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

  /**
   * Сообщение о том, что пользователь ввел необрабатываемую команду.
   */
  private final String UNKNOWN_COMMAND = """
      К сожалению, бот не знает что ответить :(
      Используйте команду /volunteer для вызова волонтера
      """;

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
        default -> processSingleAnswerCommand(text, lastCommand);
      }
    }
    lastCommandService.save(lastCommand);
  }

  /**
   * Обработка команды <code>/start</code> при первом использованием бота
   * @param chatId id чата
   */
  private void processFirstStartCommand(long chatId) {
    String message = ANSWERS.get("/start");
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
    String message = ANSWERS.get("/start_registered");
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
        sendMessage(lastCommand.getChatId(), ANSWERS.get("/chosen_CAT"));
        lastCommand.setIsClosed(true);
        lastCommand.setActiveType(Type.CAT);
      }
      case "2" -> {
        sendMessage(lastCommand.getChatId(), ANSWERS.get("/chosen_DOG"));
        lastCommand.setIsClosed(true);
        lastCommand.setActiveType(Type.DOG);
      }
      default -> processStartCommand(lastCommand);
    }
  }

  /**
   * Обработка команд, требующих лишь отправки определенного текста. Если команда найдена
   * в {@link pro.sky.petshelterbot.utility.TextUtils#ANSWERS ANSWERS}, то она будет записана в <code>lastCommand</code>
   * (<code>isClosed == true</code>)
   * @param command команда, вызванная пользователем <i>(e.g. "/info")</i>.
   *                Если в {@link pro.sky.petshelterbot.utility.TextUtils#ANSWERS ANSWERS} не найдется
   *                подходящего ответа, то пользователю будет отправлено сообщение
   *                {@link TextMessageProcessor#UNKNOWN_COMMAND}
   * @param lastCommand сущность, содержащая информацию о последней команде пользователя
   *                    и о последнем выбранном приюте. Также содержит флаг <code>isClosed</code>,
   *                    сигнализирующий о том, что пользователь может использовать другие команды.
   *                    Если <code>isClosed == false</code>, то пользователь должен завершить
   *                    использование последней команды.
   */
  private void processSingleAnswerCommand(String command, LastCommand lastCommand) {
    String message = ANSWERS.get(command + "_" + lastCommand.getActiveType().name());
    if (message == null) {
      sendMessage(lastCommand.getChatId(), UNKNOWN_COMMAND);
      return;
    }
    sendMessage(lastCommand.getChatId(), message);
    lastCommand.setLastCommand(command);
    lastCommand.setIsClosed(true);
  }

  private void sendMessage(long chatId, String message) {
    SendMessage sendMessage = new SendMessage(chatId, message);
    SendResponse sendResponse = telegramBot.execute(sendMessage);
    if (!sendResponse.isOk()) {
      log.error("Error during sending message: {}", sendResponse.description());
    }
  }

}
