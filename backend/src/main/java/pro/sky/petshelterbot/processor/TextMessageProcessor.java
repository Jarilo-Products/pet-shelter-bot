package pro.sky.petshelterbot.processor;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.model.LastCommand;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.model.enums.Type;
import pro.sky.petshelterbot.service.LastCommandService;
import pro.sky.petshelterbot.service.PersonService;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

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
  private final Pattern VOLUNTEER_ANSWERING_TO_USER_PATTERN = Pattern.compile(
      "^ *[\\[][0-9]+[\\]].+$");

  private final TelegramBot telegramBot;
  private final LastCommandService lastCommandService;
  private final PersonService personService;

  @Autowired
  public TextMessageProcessor(TelegramBot telegramBot,
                              LastCommandService lastCommandService,
                              PersonService personService) {
    this.telegramBot = telegramBot;
    this.lastCommandService = lastCommandService;
    this.personService = personService;
  }

  /**
   * Обработка текстового сообщения от пользователя бота
   *
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

    // Обработка ответа волонтера пользователю
    if (personService.isChatOfVolunteer(chatId)
        && VOLUNTEER_ANSWERING_TO_USER_PATTERN.matcher(text).matches()) {
      processVolunteerAnswering(text, chatId);
      return;
    }

    // Если дошли до сюда, то пользователь уже общался с ботом
    LastCommand lastCommand = optionalLastCommand.get();
    if (!lastCommand.getIsClosed()) { // Обработка последних команд пользователя со статусом is_closed = false
      // Обработка сообщений пользователя, направленных волонтеру
      if (lastCommand.getLastCommand().startsWith("/volunteer")
          && !lastCommand.getIsClosed()) {
        processUserAsking(lastCommand, text);
        return;
      }
      switch (lastCommand.getLastCommand()) {
        case "/start" -> processChoosingShelter(lastCommand, text);
      }
    } else { // Обработка последних команд пользователя со статусом is_closed = true
      switch (text) {
        case "/start" -> processStartCommand(lastCommand);
        case "/volunteer" -> processVolunteerCommand(lastCommand);
        default -> processSingleAnswerCommand(text, lastCommand);
      }
    }
    lastCommandService.save(lastCommand);
  }

  /**
   * Обработка команды <code>/start</code> при первом использованием бота
   *
   * @param chatId id чата
   */
  void processFirstStartCommand(long chatId) {
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
   *
   * @param lastCommand сущность, содержащая информацию о последней команде пользователя
   *                    и о последнем выбранном приюте. Также содержит флаг <code>isClosed</code>,
   *                    сигнализирующий о том, что пользователь может использовать другие команды.
   *                    Если <code>isClosed == false</code>, то пользователь должен завершить
   *                    использование последней команды.
   */
  void processStartCommand(LastCommand lastCommand) {
    String message = ANSWERS.get("/start_registered");
    sendMessage(lastCommand.getChatId(), message);
    lastCommand.setLastCommand("/start");
    lastCommand.setIsClosed(false);
  }

  /**
   * Метод определяет какой приют был выбран в зависимости от значения <code>text</code>
   *
   * @param lastCommand сущность, содержащая информацию о последней команде пользователя
   *                    и о последнем выбранном приюте. Также содержит флаг <code>isClosed</code>,
   *                    сигнализирующий о том, что пользователь может использовать другие команды.
   *                    Если <code>isClosed == false</code>, то пользователь должен завершить
   *                    использование последней команды.
   * @param text "1" – приют для кошек, "2" – приют для собак. При любом другом значении предлагается
   *             повторно выбрать приют (вызывается метод {@link TextMessageProcessor#processStartCommand})
   */
  void processChoosingShelter(LastCommand lastCommand, String text) {
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
   *
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
  void processSingleAnswerCommand(String command, LastCommand lastCommand) {
    String message = ANSWERS.get(command + "_" + lastCommand.getActiveType().name());
    if (message == null) {
      sendMessage(lastCommand.getChatId(), UNKNOWN_COMMAND);
      return;
    }
    sendMessage(lastCommand.getChatId(), message);
    lastCommand.setLastCommand(command);
    lastCommand.setIsClosed(true);
  }

  /**
   * Обработка команды <code>/volunteer</code>. При наличии волонтеров в базе данных
   * пользователю будет назначен один из них случайным образом (ему отправится уведомление и памятка
   * по работе с пользователями). В противном случае пользователю будет отправлено сообщение о том,
   * что волонтеры ещё не набраны.
   *
   * @param lastCommand сущность, содержащая информацию о последней команде пользователя
   *                    и о последнем выбранном приюте. Также содержит флаг <code>isClosed</code>,
   *                    сигнализирующий о том, что пользователь может использовать другие команды.
   *                    Если <code>isClosed == false</code>, то пользователь должен завершить
   *                    использование последней команды.
   */
  void processVolunteerCommand(LastCommand lastCommand) {
    List<Person> volunteers = personService.getVolunteers();
    if (volunteers.isEmpty()) {
      sendMessage(lastCommand.getChatId(), ANSWERS.get("/volunteer_empty"));
      lastCommand.setIsClosed(true);
      lastCommand.setLastCommand("/volunteer");
      return;
    }
    int randomIndex = (int) (Math.random() * volunteers.size());
    Person randomVolunteer = volunteers.get(randomIndex);
    sendMessage(lastCommand.getChatId(),
        ANSWERS.get("/volunteer").replace("[name]", randomVolunteer.getFirstName()));
    lastCommand.setLastCommand("/volunteer " + randomVolunteer.getChatId());
    lastCommand.setIsClosed(false);
    sendMessage(randomVolunteer.getChatId(),
        ANSWERS.get("/volunteer_memo").replace("user_chat_id",
            lastCommand.getChatId().toString()));
  }

  /**
   * Обработка ответов волонтера на сообщения пользователей. Если волонтер пытается ответить в несуществующий чат
   * или в чат, в котором не требуется его помощь, то он получит соответствующее сообщение. Также волонтер может
   * "закрыть" чат для общения с пользователем когда все вопросы пользователя были обсуждены – волонтер должен
   * отправить сообщение "[user_chat_id] end"
   *
   * @param text            текст ответного сообщения волонтера. Включает в себя номер чата, куда направлено сообщение в
   *                        формате "[user_chat_id] Текст ответного сообщения"
   * @param volunteerChatId id чата волонтера
   */
  void processVolunteerAnswering(String text, Long volunteerChatId) {
    text = text.trim();
    String[] arr = text.split("[\\[|\\]]");
    Long userChatId = Long.parseLong(arr[1]);
    Optional<LastCommand> userLastCommandOptional = lastCommandService.getByChatId(userChatId);
    if (userLastCommandOptional.isEmpty()) {
      sendMessage(volunteerChatId, ANSWERS.get("/volunteer_no_chat"));
      return;
    }
    LastCommand userLastCommand = userLastCommandOptional.get();
    if (!userLastCommand.getLastCommand().startsWith("/volunteer")
        || userLastCommand.getIsClosed()) {
      sendMessage(volunteerChatId, ANSWERS.get("/volunteer_chat_not_opened"));
      return;
    }
    String answer = text.substring(text.indexOf(']') + 1).trim();
    if (answer.equalsIgnoreCase("end")) {
      sendMessage(volunteerChatId,
          ANSWERS.get("/volunteer_end").replace("user_chat_id", userChatId.toString()));
      sendMessage(userChatId, ANSWERS.get("/volunteer_end_user"));
      userLastCommand.setLastCommand("/volunteer");
      userLastCommand.setIsClosed(true);
      lastCommandService.save(userLastCommand);
    } else if (!answer.isBlank()) {
      sendMessage(userChatId, answer);
    }
  }

  /**
   * Обработка сообщения пользователя, пересылаемого волонтеру
   *
   * @param lastCommand сущность, содержащая информацию о последней команде пользователя
   *                    и о последнем выбранном приюте. Также содержит флаг <code>isClosed</code>,
   *                    сигнализирующий о том, что пользователь может использовать другие команды.
   *                    Если <code>isClosed == false</code>, то пользователь должен завершить
   *                    использование последней команды.
   * @param text        текст сообщения, который пересылается волонтеру в формате
   *                    "[user_chat_id] Текст сообщения пользователя"
   */
  void processUserAsking(LastCommand lastCommand, String text) {
    Long volunteerChatId = Long.parseLong(lastCommand.getLastCommand().split(" ")[1]);
    sendMessage(volunteerChatId,
        '[' + lastCommand.getChatId().toString() + "] " + text);
  }

  void sendMessage(long chatId, String message) {
    SendMessage sendMessage = new SendMessage(chatId, message);
    SendResponse sendResponse = telegramBot.execute(sendMessage);
    if (!sendResponse.isOk()) {
      log.error("Error during sending message: {}", sendResponse.description());
    }
  }

}
