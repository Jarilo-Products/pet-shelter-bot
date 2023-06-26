package pro.sky.petshelterbot.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.message.TelegramMessage;
import pro.sky.petshelterbot.model.LastCommand;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.model.Report;
import pro.sky.petshelterbot.service.LastCommandService;
import pro.sky.petshelterbot.service.PersonService;
import pro.sky.petshelterbot.service.ReportService;
import pro.sky.petshelterbot.message.MessageSendingClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static pro.sky.petshelterbot.utility.TextUtils.ANSWERS;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_REPORT_MEMO;

@Component
@Slf4j
public class ReportScheduler extends MessageSendingClass {

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

  /**
   * Метод, рассылающий отчеты по волонтерам в 21:00.
   */
  @Scheduled(cron = "0 2 20 * * ?", zone = "Europe/Moscow")
  private void sendReportsToVolunteers() {
    LocalDateTime now = LocalDateTime.now();
    LocalDate today = now.toLocalDate();
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

      TelegramMessage memo = new TelegramMessage(randomVolunteer.getChatId(),
          ANSWERS.get(COMMAND_VOLUNTEER_REPORT_MEMO)
              .replaceAll("user_chat_id", reportSender.getChatId().toString()));
      sendMessage(memo);

      String message;
      InlineKeyboardMarkup buttons;
      if (reportSender.getProbationEnd().compareTo(today) <= 0) {
        message = "\\[REPORT-" + reportSender.getChatId() + "] PROBATION END\n" + report.getText();
        buttons = new InlineKeyboardMarkup(
            new InlineKeyboardButton[]{
                new InlineKeyboardButton("Плохой отчет. Отправить предупреждение")
                    .callbackData("[REPORT-" + reportSender.getChatId() + "]")
            },
            new InlineKeyboardButton[]{
                new InlineKeyboardButton("Закончить тестовый период")
                    .callbackData("[PROBATION-" + reportSender.getChatId() + "] end")
            },
            new InlineKeyboardButton[]{
                new InlineKeyboardButton("+14 дней")
                    .callbackData("[PROBATION-" + reportSender.getChatId() + "] 14"),
                new InlineKeyboardButton("+30 дней")
                    .callbackData("[PROBATION-" + reportSender.getChatId() + "] 30")
            }
        );
      } else {
        message = "\\[REPORT-" + reportSender.getChatId() + "]\n" + report.getText();
        buttons = new InlineKeyboardMarkup(
            new InlineKeyboardButton("Плохой отчет. Отправить предупреждение")
                .callbackData("[REPORT-" + reportSender.getChatId() + "]"));
      }
      sendMessage(new TelegramMessage(randomVolunteer.getChatId(), message, report.getFileId()), buttons);

      reportSendersLastCommand.setIsClosed(true);
      lastCommandService.save(reportSendersLastCommand);
      reportSender.setLastReportDate(today);
      personService.save(reportSender);
    }
  }

}
