package pro.sky.petshelterbot.processor;

import com.pengrad.telegrambot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.message.TelegramMessage;
import pro.sky.petshelterbot.model.LastCommand;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.model.Report;
import pro.sky.petshelterbot.service.PersonService;
import pro.sky.petshelterbot.service.ReportService;
import pro.sky.petshelterbot.message.MessageSendingClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static pro.sky.petshelterbot.utility.CallbackUtils.BUTTONS;
import static pro.sky.petshelterbot.utility.TextUtils.*;

@Component
@Slf4j
public class ReportProcessor extends MessageSendingClass {

  private final ReportService reportService;
  private final PersonService personService;

  public ReportProcessor(TelegramBot telegramBot,
                         ReportService reportService,
                         PersonService personService) {
    super(telegramBot);
    this.reportService = reportService;
    this.personService = personService;
  }

  public void processReportMessage(TelegramMessage message, LastCommand lastCommand) {
    LocalDate now = LocalDateTime.now().getHour() > 21
        ? LocalDateTime.now().plusDays(1).toLocalDate()
        : LocalDateTime.now().toLocalDate();
    Optional<Person> personOptional = personService.getPersonByChatId(lastCommand.getChatId());
    if (personOptional.isEmpty()) {
      log.error("Unexpected error during sending report to volunteer: " +
          "there is no person with such chat_id:  " + lastCommand.getChatId());
      return;
    }
    Person person = personOptional.get();
    Optional<Report> reportOptional = reportService.getReportByPersonAndDate(person, now);
    Report report;
    if (reportOptional.isEmpty()){
      report = new Report();
      report.setDate(now);
      report.setPerson(person);
      report.setPet(person.getPet());
    } else {
      report = reportOptional.get();
    }

    Integer messageId = null;
    if (message.getText() != null && message.getFileId() != null) {
      report.setText(message.getText());
      report.setFileId(message.getFileId());
    } else if (message.getText() != null) {
      report.setText(message.getText());
      if (report.getFileId() == null) {
        messageId = sendMessage(new TelegramMessage(
                lastCommand.getChatId(),
                ANSWERS.get(COMMAND_NO_PHOTO_REPORT)
            ),
            BUTTONS.get(COMMAND_SEND_REPORT));
      }
    }

    if (report.getText() != null && report.getFileId() != null) {
      lastCommand.setIsClosed(true);
      TelegramMessage messageToUser = new TelegramMessage(lastCommand.getChatId());
      if (report.getDate().isAfter(LocalDate.now())) {
        messageToUser.setText(ANSWERS.get(COMMAND_LOSING_TIME_REPORT));
      } else {
        messageToUser.setText(ANSWERS.get(COMMAND_SUCCESSFUL_REPORT));
      }
      messageId = sendMessage(messageToUser, BUTTONS.get(COMMAND_MAIN));
    } else if (message.getFileId() != null) {
      report.setFileId(message.getFileId());
      if (report.getText() == null) {
        messageId = sendMessage(new TelegramMessage(
                lastCommand.getChatId(),
                ANSWERS.get(COMMAND_NO_TEXT_REPORT)
            ),
            BUTTONS.get(COMMAND_SEND_REPORT));
      }
    }
    lastCommand.setLastMessageId(messageId);
    reportService.save(report);
  }

}
