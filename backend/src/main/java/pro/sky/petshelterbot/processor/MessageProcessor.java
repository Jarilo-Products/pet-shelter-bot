package pro.sky.petshelterbot.processor;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.message.TelegramMessage;
import pro.sky.petshelterbot.model.LastCommand;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.model.enums.Type;
import pro.sky.petshelterbot.service.LastCommandService;
import pro.sky.petshelterbot.service.PersonService;
import pro.sky.petshelterbot.message.MessageSendingClass;

import java.util.List;
import java.util.Optional;

import static pro.sky.petshelterbot.processor.VolunteerProcessor.VOLUNTEER_ANSWERING_TO_USER_PATTERN;
import static pro.sky.petshelterbot.processor.VolunteerProcessor.VOLUNTEER_CHECKING_UP_THE_REPORT_PATTERN;
import static pro.sky.petshelterbot.processor.VolunteerProcessor.VOLUNTEER_PROBATION_DECISION_PATTERN;
import static pro.sky.petshelterbot.utility.TextUtils.*;

@Component
@Slf4j
public class MessageProcessor extends MessageSendingClass {

  private final LastCommandService lastCommandService;
  private final PersonService personService;
  private final ReportProcessor reportProcessor;
  private final VolunteerProcessor volunteerProcessor;
  private final ContactsProcessor contactsProcessor;

  public MessageProcessor(TelegramBot telegramBot,
                          LastCommandService lastCommandService,
                          PersonService personService,
                          ReportProcessor reportProcessor,
                          VolunteerProcessor volunteerProcessor,
                          ContactsProcessor contactsProcessor) {
    super(telegramBot);
    this.lastCommandService = lastCommandService;
    this.personService = personService;
    this.reportProcessor = reportProcessor;
    this.volunteerProcessor = volunteerProcessor;
    this.contactsProcessor = contactsProcessor;
  }

  /**
   * Обработка текстового сообщения от пользователя бота
   */
  public void processTextMessage(Message message) {
    long chatId = message.chat().id();
    TelegramMessage telegramMessage = new TelegramMessage(chatId);
    if (message.text() != null) {
      telegramMessage.setText((message.text().trim()));
    } else if (message.caption() != null) {
      telegramMessage.setText((message.caption().trim()));
    }
    if (message.photo() != null && message.photo().length > 0) {
      telegramMessage.setFileId(message.photo()[0].fileId());
    }
    Optional<LastCommand> optionalLastCommand = lastCommandService.getByChatId(chatId);
    // Пользователь в первый раз зашел в бота и ввел какую-то хрень
    if (optionalLastCommand.isEmpty() && !telegramMessage.getText().equalsIgnoreCase("/start")) {
      return;
    }
    // Пользователь в первый раз зашел в бота и ввел "/start"
    if (optionalLastCommand.isEmpty()) {
      processFirstStartCommand(chatId);
      return;
    }

    // Обработка ответа волонтера пользователю
    if (telegramMessage.getText() != null && !telegramMessage.getText().isBlank()
        && personService.isChatOfVolunteer(chatId)
        && (VOLUNTEER_ANSWERING_TO_USER_PATTERN.matcher(telegramMessage.getText()).matches()
        || VOLUNTEER_CHECKING_UP_THE_REPORT_PATTERN.matcher(telegramMessage.getText()).matches())
        || VOLUNTEER_PROBATION_DECISION_PATTERN.matcher(telegramMessage.getText()).matches()) {
      volunteerProcessor.processVolunteersMessage(telegramMessage);
      return;
    }

    // Если дошли до сюда, то пользователь уже общался с ботом
    LastCommand lastCommand = optionalLastCommand.get();
    if (!lastCommand.getIsClosed()) { // Обработка последних команд пользователя со статусом is_closed = false
      // Обработка сообщений пользователя, направленных волонтеру
      if (lastCommand.getLastCommand().startsWith(COMMAND_VOLUNTEER)) {
        processUserAsking(lastCommand, telegramMessage);
        return;
      }
      switch (lastCommand.getLastCommand()) {
        case COMMAND_START -> processChoosingShelter(lastCommand, telegramMessage);
        case COMMAND_SEND_CONTACTS -> contactsProcessor.processContacts(lastCommand, telegramMessage);
        case COMMAND_SEND_REPORT -> reportProcessor.processReportMessage(telegramMessage, lastCommand);
      }
    } else { // Обработка последних команд пользователя со статусом is_closed = true
      switch (telegramMessage.getText()) {
        case COMMAND_START -> processStartCommand(lastCommand);
        case COMMAND_SEND_CONTACTS -> processSendContactsCommand(lastCommand);
        case COMMAND_VOLUNTEER -> processVolunteerCommand(lastCommand);
        case COMMAND_SEND_REPORT -> processSendReportCommand(lastCommand);
        default -> processSingleAnswerCommand(lastCommand, telegramMessage);
      }
    }
    lastCommandService.save(lastCommand);
  }

  /**
   * Обработка команды <code>/start</code> при первом использованием бота
   *
   * @param chatId id чата
   */
  private void processFirstStartCommand(long chatId) {
    TelegramMessage message = new TelegramMessage(chatId, ANSWERS.get(COMMAND_START));
    sendMessage(message);
    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(chatId);
    lastCommand.setLastCommand(COMMAND_START);
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
  private void processStartCommand(LastCommand lastCommand) {
    TelegramMessage message = new TelegramMessage(lastCommand.getChatId(),
        ANSWERS.get(COMMAND_START + "_registered"));
    sendMessage(message);
    lastCommand.setLastCommand(COMMAND_START);
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
   * @param userMessage сообщение пользователя (включает chat_id, текст и file_id)
   */
  private void processChoosingShelter(LastCommand lastCommand, TelegramMessage userMessage) {
    TelegramMessage message = new TelegramMessage(lastCommand.getChatId());
    switch (userMessage.getText()) {
      case "1" -> {
        message.setText(ANSWERS.get("chosen_CAT"));
        sendMessage(message);
        lastCommand.setIsClosed(true);
        lastCommand.setActiveType(Type.CAT);
      }
      case "2" -> {
        message.setText(ANSWERS.get("chosen_DOG"));
        sendMessage(message);
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
   * @param lastCommand сущность, содержащая информацию о последней команде пользователя
   *                    и о последнем выбранном приюте. Также содержит флаг <code>isClosed</code>,
   *                    сигнализирующий о том, что пользователь может использовать другие команды.
   *                    Если <code>isClosed == false</code>, то пользователь должен завершить
   *                    использование последней команды.
   * @param userMessage сообщение пользователя (включает chat_id, текст и file_id)
   */
  private void processSingleAnswerCommand(LastCommand lastCommand, TelegramMessage userMessage) {
    TelegramMessage message = new TelegramMessage(lastCommand.getChatId(),
        ANSWERS.get(userMessage.getText() + "_" + lastCommand.getActiveType().name()));
    if (message.getText() == null) {
      message.setText(UNKNOWN_COMMAND);
      sendMessage(message);
      return;
    }
    sendMessage(message);
    lastCommand.setLastCommand(userMessage.getText());
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
  private void processVolunteerCommand(LastCommand lastCommand) {
    List<Person> volunteers = personService.getVolunteers();
    TelegramMessage messageToUser = new TelegramMessage(lastCommand.getChatId());
    if (volunteers.isEmpty()) {
      messageToUser.setText(ANSWERS.get(COMMAND_VOLUNTEER + "_empty"));
      sendMessage(messageToUser);
      lastCommand.setIsClosed(true);
      lastCommand.setLastCommand(COMMAND_VOLUNTEER);
      return;
    }

    int randomIndex = (int) (Math.random() * volunteers.size());
    Person randomVolunteer = volunteers.get(randomIndex);
    messageToUser.setText(ANSWERS.get(COMMAND_VOLUNTEER).replace("%NAME%", randomVolunteer.getFirstName()));
    sendMessage(messageToUser);
    lastCommand.setLastCommand(COMMAND_VOLUNTEER + " " + randomVolunteer.getChatId());
    lastCommand.setIsClosed(false);

    TelegramMessage messageToVolunteer = new TelegramMessage(randomVolunteer.getChatId(),
        ANSWERS.get(COMMAND_VOLUNTEER + "_user_memo")
            .replace("user_chat_id", lastCommand.getChatId().toString()));
    sendMessage(messageToVolunteer);
  }

  /**
   * Обработка сообщения пользователя, пересылаемого волонтеру
   *
   * @param lastCommand сущность, содержащая информацию о последней команде пользователя
   *                    и о последнем выбранном приюте. Также содержит флаг <code>isClosed</code>,
   *                    сигнализирующий о том, что пользователь может использовать другие команды.
   *                    Если <code>isClosed == false</code>, то пользователь должен завершить
   *                    использование последней команды.
   * @param userMessage сообщение пользователя (включает chat_id, текст и file_id)
   */
  private void processUserAsking(LastCommand lastCommand, TelegramMessage userMessage) {
    long volunteerChatId = Long.parseLong(lastCommand.getLastCommand().split(" ")[1]);
    TelegramMessage messageToVolunteer = new TelegramMessage(volunteerChatId,
        "[USER-" + lastCommand.getChatId().toString() + "] " + userMessage.getText(),
        userMessage.getFileId());
    sendMessage(messageToVolunteer);
  }

  /**
   * Обработка команды <code>/sendcontacts</code>
   *
   * @param lastCommand сущность, содержащая информацию о последней команде пользователя
   *                    и о последнем выбранном приюте. Также содержит флаг <code>isClosed</code>,
   *                    сигнализирующий о том, что пользователь может использовать другие команды.
   *                    Если <code>isClosed == false</code>, то пользователь должен завершить
   *                    использование последней команды.
   */
  private void processSendContactsCommand(LastCommand lastCommand) {
    TelegramMessage message = new TelegramMessage(lastCommand.getChatId(), ANSWERS.get(COMMAND_SEND_CONTACTS));
    sendMessage(message);
    lastCommand.setLastCommand(COMMAND_SEND_CONTACTS);
    lastCommand.setIsClosed(false);
  }

  /**
   * Обработка команды <code>/sendreport</code>
   *
   * @param lastCommand сущность, содержащая информацию о последней команде пользователя
   *                    и о последнем выбранном приюте. Также содержит флаг <code>isClosed</code>,
   *                    сигнализирующий о том, что пользователь может использовать другие команды.
   *                    Если <code>isClosed == false</code>, то пользователь должен завершить
   *                    использование последней команды.
   */
  private void processSendReportCommand(LastCommand lastCommand) {
    Optional<Person> optionalPerson = personService.getPersonByChatId(lastCommand.getChatId());
    TelegramMessage message = new TelegramMessage(lastCommand.getChatId());
    if (optionalPerson.isPresent() && optionalPerson.get().getPet() != null) {
      message.setText(ANSWERS.get(COMMAND_SEND_REPORT));
      sendMessage(message);
      lastCommand.setLastCommand(COMMAND_SEND_REPORT);
      lastCommand.setIsClosed(false);
    } else {
      message.setText(ANSWERS.get(COMMAND_NO_PET_REPORT));
      sendMessage(message);
    }
  }
}
