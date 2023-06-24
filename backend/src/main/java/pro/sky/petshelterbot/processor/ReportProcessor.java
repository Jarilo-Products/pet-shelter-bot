package pro.sky.petshelterbot.processor;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.model.LastCommand;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.model.Report;
import pro.sky.petshelterbot.service.LastCommandService;
import pro.sky.petshelterbot.service.PersonService;
import pro.sky.petshelterbot.service.ReportService;
import pro.sky.petshelterbot.utility.MessageSendingClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static pro.sky.petshelterbot.utility.TextUtils.*;

@Component
@Slf4j
public class ReportProcessor extends MessageSendingClass {

  private final ReportService reportService;
  private final LastCommandService lastCommandService;
  private final PersonService personService;

  public ReportProcessor(TelegramBot telegramBot,
                         ReportService reportService,
                         LastCommandService lastCommandService,
                         PersonService personService) {
    super(telegramBot);
    this.reportService = reportService;
    this.lastCommandService = lastCommandService;
    this.personService = personService;
  }

  public void processorReport(Message message, LastCommand lastCommand) {
    String text = message.text();
    PhotoSize photo;
    try {
      photo = message.photo()[0];
    } catch (NullPointerException e) {
      photo = null;
    }
    String caption = message.caption();
    LocalDate now = LocalDateTime.now().getHour() > 21
        ? LocalDateTime.now().plusDays(1).toLocalDate()
        : LocalDateTime.now().toLocalDate();
    Optional<Report> optionalReport = reportService.getReportByPersonAndData(personService.getPersonByChatId(lastCommand.getChatId()).get(),  now);
    Report report;
    if (optionalReport.isEmpty()){
      report = new Report();
      report.setDate(now);
      Person person = personService.getPersonByChatId(lastCommand.getChatId()).get();
      report.setPerson(person);
      report.setPet(person.getPet());
    } else {
      report = optionalReport.get();
    }
    if (caption != null && photo != null) {
      processTextPhotoReport(caption, photo, report, lastCommand);
    } else if (photo != null){
      processPhotoReport(photo, report, lastCommand);
    } else if (text != null) {
      processTextReport(text, report, lastCommand);
    }
    reportService.save(report);
    if (report.getText() != null && report.getFileId() != null) {
      lastCommand.setIsClosed(true);
      if (report.getDate().isAfter(LocalDate.now())) {
        sendMessage(lastCommand.getChatId(), ANSWERS.get(COMMAND_LOSING_TIME_REPORT));
      } else {
        sendMessage(lastCommand.getChatId(), ANSWERS.get(COMMAND_SUCCESSFUL_REPORT));
      }
    }
    lastCommandService.save(lastCommand);
  }
  public void processTextPhotoReport(String caption, PhotoSize photo, Report report, LastCommand lastCommand) {
    report.setText(caption);
    report.setFileId(photo.fileId());
  }

  public void processTextReport(String text, Report report, LastCommand lastCommand) {
    report.setText(text);
    if (report.getFileId() == null) {
      sendMessage(lastCommand.getChatId(), ANSWERS.get(COMMAND_NO_PHOTO_REPORT));
    }
  }

  public void processPhotoReport(PhotoSize photo, Report report, LastCommand lastCommand) {
    report.setFileId(photo.fileId());
    if (report.getText() == null) {
      sendMessage(lastCommand.getChatId(), ANSWERS.get(COMMAND_NO_TEXT_REPORT));
    }
  }

}
