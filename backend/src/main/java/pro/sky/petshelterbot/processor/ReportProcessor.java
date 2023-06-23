package pro.sky.petshelterbot.processor;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.model.LastCommand;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.model.Report;
import pro.sky.petshelterbot.service.LastCommandService;
import pro.sky.petshelterbot.service.PersonService;
import pro.sky.petshelterbot.service.ReportService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static pro.sky.petshelterbot.utility.TextUtils.*;

@Component
@Slf4j
public class ReportProcessor{

  private final ReportService reportService;
  protected final TelegramBot telegramBot;
  protected final LastCommandService lastCommandService;
  protected final PersonService personService;

  public ReportProcessor(ReportService reportService,
                         TelegramBot telegramBot,
                         LastCommandService lastCommandService,
                         PersonService personService) {
    this.reportService = reportService;
    this.telegramBot = telegramBot;
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
    if (report.getText() != null && report.getFile_id() != null) {
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
    report.setFile_id(photo.fileId());
  }

  public void processTextReport(String text, Report report, LastCommand lastCommand) {
    report.setText(text);
    if (report.getFile_id() == null) {
      sendMessage(lastCommand.getChatId(), ANSWERS.get(COMMAND_NO_PHOTO_REPORT));
    }
  }

  public void processPhotoReport(PhotoSize photo, Report report, LastCommand lastCommand) {
    report.setFile_id(photo.fileId());
    if (report.getText() == null) {
      sendMessage(lastCommand.getChatId(), ANSWERS.get(COMMAND_NO_TEXT_REPORT));
    }
  }

  protected void sendMessage(long chatId, String message) {
    SendMessage sendMessage = new SendMessage(chatId, message);
    SendResponse sendResponse = telegramBot.execute(sendMessage);
    if (!sendResponse.isOk()) {
      log.error("Error during sending message: {}", sendResponse.description());
    }
  }

}
