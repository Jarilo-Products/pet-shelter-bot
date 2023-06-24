package pro.sky.petshelterbot.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ReportScheduler extends MessageSendingClass {

  private static LocalDate lastReportSendingDay = LocalDate.now().minusDays(1);

  private final ReportService reportService;
  private final PersonService personService;
  private final LastCommandService lastCommandService;

  public ReportScheduler(TelegramBot telegramBot,
                         ReportService reportService,
                         PersonService personService,
                         LastCommandService lastCommandService) {
    super(telegramBot);
    this.reportService = reportService;
    this.personService = personService;
    this.lastCommandService = lastCommandService;
  }

  @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
  private void sendReportsToVolunteers() {
    LocalDateTime now = LocalDateTime.now();
    LocalDate today = now.toLocalDate();
    if (now.getHour() > 21 && lastReportSendingDay.isBefore(today)) {
      List<Report> reports = reportService.getReportsByDate(today);
      List<Person> volunteers = personService.getVolunteers();
      for (Report report : reports) {
        int randomIndex = (int) (Math.random() * volunteers.size());
        Person randomVolunteer = volunteers.get(randomIndex);
        Person reportSender = report.getPerson();
        if (reportSender == null) {
          log.error("Unexpected error during sending report to volunteer: " +
              "there is no linked person to the report " + report.getId());
          return;
        }
        Optional<LastCommand> reportSendersLastCommandOptional
            = lastCommandService.getByChatId(reportSender.getChatId());
        if (reportSendersLastCommandOptional.isEmpty()) {
          log.error("Unexpected error during sending report to volunteer: " +
              "there is no LastCommand for chat_id " + reportSender.getChatId());
          return;
        }
        LastCommand reportSendersLastCommand = reportSendersLastCommandOptional.get();
        String message = "[REPORT-" + reportSender.getChatId() + "]\n" + report.getText();
        sendMessage(randomVolunteer.getChatId(), message, report.getFileId());

        reportSendersLastCommand.setIsClosed(true);
        lastCommandService.save(reportSendersLastCommand);
        reportSender.setLastReportDate(today);
        personService.save(reportSender);
      }
      lastReportSendingDay = today;
    }
  }

}
