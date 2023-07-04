package pro.sky.petshelterbot.scheduler;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.petshelterbot.model.LastCommand;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.model.Report;
import pro.sky.petshelterbot.service.LastCommandService;
import pro.sky.petshelterbot.service.PersonService;
import pro.sky.petshelterbot.service.ReportService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pro.sky.petshelterbot.utility.TextUtils.ANSWERS;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_REPORT_MEMO;

@ExtendWith(MockitoExtension.class)
class ReportSchedulerTest {

  @Mock
  private TelegramBot telegramBot;

  @Mock
  private ReportService reportService;

  @Mock
  private PersonService personService;

  @Mock
  private LastCommandService lastCommandService;

  @InjectMocks
  private ReportScheduler reportScheduler;

  private static SendResponse response;

  @BeforeAll
  public static void init() throws URISyntaxException, IOException {
    String json = Files.readString(
        Path.of(ReportScheduler.class.getResource("response.json").toURI()));
    response = BotUtils.fromJson(json, SendResponse.class);
  }

  @Test
  public void noReportSenderTest() {
    Person volunteer = new Person();
    volunteer.setChatId(2L);

    Report report = new Report();

    when(reportService.getReportsByDate(any())).thenReturn(Collections.singletonList(report));
    when(personService.getVolunteers()).thenReturn(Collections.singletonList(volunteer));

    reportScheduler.sendReportsToVolunteers();

    verify(telegramBot, times(0)).execute(any());
  }

  @Test
  public void noLastCommandTest() {
    Person user = new Person();
    user.setChatId(1L);
    Person volunteer = new Person();
    volunteer.setChatId(2L);

    Report report = new Report();
    report.setPerson(user);

    when(reportService.getReportsByDate(any())).thenReturn(Collections.singletonList(report));
    when(personService.getVolunteers()).thenReturn(Collections.singletonList(volunteer));
    when(lastCommandService.getByChatId(any())).thenReturn(Optional.empty());

    reportScheduler.sendReportsToVolunteers();

    verify(telegramBot, times(0)).execute(any());
  }

  @Test
  public void regularReportSendingTest() {
    checkReportSending(LocalDate.now().plusDays(1), "\\[REPORT-" + 1 + "]\n");
  }

  @Test
  public void ReportSendingWhenProbationFinishedTest() {
    checkReportSending(LocalDate.now(), "\\[REPORT-" + 1 + "] PROBATION END\n");

  }

  private void checkReportSending(LocalDate probationEnd, String expectedText) {
    Person user = new Person();
    user.setChatId(1L);
    user.setProbationEnd(probationEnd);
    Person volunteer = new Person();
    volunteer.setChatId(2L);

    Report report = new Report();
    report.setPerson(user);
    report.setText("smth.");
    report.setFileId("123");

    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(1L);

    when(reportService.getReportsByDate(any())).thenReturn(Collections.singletonList(report));
    when(personService.getVolunteers()).thenReturn(Collections.singletonList(volunteer));
    when(lastCommandService.getByChatId(any())).thenReturn(Optional.of(lastCommand));
    when(telegramBot.execute(any())).thenReturn(response);

    reportScheduler.sendReportsToVolunteers();

    ArgumentCaptor<AbstractSendRequest> argumentCaptor = ArgumentCaptor.forClass(AbstractSendRequest.class);
    verify(telegramBot, times(2)).execute(argumentCaptor.capture());
    List<AbstractSendRequest> actual = argumentCaptor.getAllValues();

    assertEquals(actual.get(0).getParameters().get("chat_id"), 2L);
    assertEquals(actual.get(0).getParameters().get("text"), ANSWERS.get(COMMAND_VOLUNTEER_REPORT_MEMO)
        .replaceAll("user_chat_id", "1"));

    assertEquals(actual.get(1).getParameters().get("chat_id"), 2L);
    assertEquals(actual.get(1).getParameters().get("caption"), expectedText + report.getText());
    assertEquals(actual.get(1).getParameters().get("photo"), report.getFileId());
  }

}
