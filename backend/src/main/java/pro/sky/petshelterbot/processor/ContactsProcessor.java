package pro.sky.petshelterbot.processor;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.message.MessageSendingClass;
import pro.sky.petshelterbot.message.TelegramMessage;
import pro.sky.petshelterbot.model.LastCommand;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.service.PersonService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static pro.sky.petshelterbot.utility.TextUtils.ANSWERS;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_SEND_CONTACTS;

@Component
public class ContactsProcessor extends MessageSendingClass {

  private final PersonService personService;

  public ContactsProcessor(TelegramBot telegramBot,
                           PersonService personService) {
    super(telegramBot);
    this.personService = personService;
  }

  /**
   * Обрабатывает информацию, введённую пользователем, и сохраняется контактные данные в базу.
   *
   * @param lastCommand сущность, содержащая информацию о последней команде пользователя
   *                    и о последнем выбранном приюте. Также содержит флаг <code>isClosed</code>,
   *                    сигнализирующий о том, что пользователь может использовать другие команды.
   *                    Если <code>isClosed == false</code>, то пользователь должен завершить
   *                    использование последней команды.
   * @param userMessage сообщение пользователя (включает chat_id, текст и file_id)
   */
  public void processContacts(LastCommand lastCommand, TelegramMessage userMessage) {
    TelegramMessage message = new TelegramMessage(lastCommand.getChatId());
    String[] lines = userMessage.getText().split("\n");
    if (lines.length < 5) {
      message.setText(ANSWERS.get(COMMAND_SEND_CONTACTS + "_notenough"));
      sendMessage(message);
      return;
    }

    String fullName = lines[0];
    String birthdate = lines[1];
    String phone = lines[2];
    String email = lines[3];
    String address = lines[4];

    boolean isValid = true;
    StringBuilder error = new StringBuilder();
    if (!fullName.matches("^[а-яёА-ЯЁ-]+\s[а-яёА-ЯЁ-]+\s[а-яёА-ЯЁ-]+$")) {
      error.append(ANSWERS.get(COMMAND_SEND_CONTACTS + "_invalid_name")).append('\n');
      isValid = false;
    }
    if (!birthdate.matches("^\\d{2}[.]\\d{2}[.]\\d{4}$")) {
      error.append(ANSWERS.get(COMMAND_SEND_CONTACTS + "_invalid_bithdate")).append('\n');
      isValid = false;
    }
    if (!email.matches("^[\\w.]+@[\\w.]+\\.[A-Za-z]{2,6}$")) {
      error.append(ANSWERS.get(COMMAND_SEND_CONTACTS + "_invalid_email")).append('\n');
      isValid = false;
    }

    if (!isValid) {
      error.append(ANSWERS.get(COMMAND_SEND_CONTACTS + "_invalid"));
      message.setText(error.toString());
      sendMessage(message);
    } else {
      String[] nameParts = fullName.split(" ");
      String firstName = nameParts[1];
      String lastName = nameParts[0];
      String middleName = nameParts[2];

      Person person = new Person(
          lastCommand.getChatId(),
          firstName,
          lastName,
          middleName,
          parseDate(birthdate),
          phone,
          email,
          address);
      personService.save(person);

      lastCommand.setIsClosed(true);
      message.setText(ANSWERS.get("acceptedcontacts"));
      sendMessage(message);
    }
  }

  /**
   * Метод для парсинга строки в формате даты dd.MM.yyyy в объект LocalDate.
   *
   * @param dateString строка с датой в формате dd.MM.yyyy
   * @return объект LocalDate
   */
  private LocalDate parseDate(String dateString) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    return LocalDate.parse(dateString, formatter);
  }
}
