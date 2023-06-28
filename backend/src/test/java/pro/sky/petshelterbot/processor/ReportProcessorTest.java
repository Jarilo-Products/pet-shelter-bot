package pro.sky.petshelterbot.processor;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.petshelterbot.message.TelegramMessage;
import pro.sky.petshelterbot.model.LastCommand;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.model.Report;
import pro.sky.petshelterbot.service.PersonService;
import pro.sky.petshelterbot.service.ReportService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pro.sky.petshelterbot.utility.TextUtils.ANSWERS;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_LOSING_TIME_REPORT;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_NO_PHOTO_REPORT;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_NO_TEXT_REPORT;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_SEND_REPORT;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_SUCCESSFUL_REPORT;

@ExtendWith(MockitoExtension.class)
class ReportProcessorTest {

  @Mock
  private TelegramBot telegramBot;

  @Mock
  private ReportService reportService;

  @Mock
  private PersonService personService;

  @Mock
  private Clock clock;

  @InjectMocks
  private ReportProcessor reportProcessor;

  private static LastCommand lastCommand;

  private static Person person;

  private static SendResponse response;

  @BeforeAll
  public static void init() throws URISyntaxException, IOException {
    lastCommand = new LastCommand();
    lastCommand.setChatId(1L);
    lastCommand.setLastCommand(COMMAND_SEND_REPORT);
    lastCommand.setIsClosed(false);
    lastCommand.setLastMessageId(1);

    person = new Person();
    person.setChatId(1L);

    String json = Files.readString(
        Path.of(ReportProcessorTest.class.getResource("response.json").toURI()));
    response = BotUtils.fromJson(json, SendResponse.class);
  }

  @Test
  public void processReportWhenThereIsNoPersonCorrespondingToLastCommand() throws URISyntaxException, IOException {
    TelegramMessage telegramMessage = new TelegramMessage();

    mockClock();

    when(personService.getPersonByChatId(1L)).thenReturn(Optional.empty());

    reportProcessor.processReportMessage(telegramMessage, lastCommand);
  }

  @Test
  public void processFullyFilledReportWhenNoReportInDbONThatDay() {
    checkMessageWhenThereIsNoReportInDbOnThatDay(
        new TelegramMessage(1, "smth.", "1"),
        ANSWERS.get(COMMAND_SUCCESSFUL_REPORT));
  }

  @Test
  public void processReportWithOnlyTextWhenNoReportInDbONThatDay() {
    checkMessageWhenThereIsNoReportInDbOnThatDay(
        new TelegramMessage(1, "smth."),
        ANSWERS.get(COMMAND_NO_PHOTO_REPORT));
  }

  @Test
  public void processReportWithOnlyPhotoWhenNoReportInDbONThatDay() {
    checkMessageWhenThereIsNoReportInDbOnThatDay(
        new TelegramMessage(1, null, "1"),
        ANSWERS.get(COMMAND_NO_TEXT_REPORT));
  }

  @Test
  public void processFullyFilledReportWhenNoReportInDbOnThatDayButAfter9pm() {
    TelegramMessage telegramMessage = new TelegramMessage(1, "smth.", "1");

    Clock fixedClock = Clock.fixed(
        LocalDateTime.of(LocalDate.now(), LocalTime.of(22, 0)).atZone(ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault());
    doReturn(fixedClock.instant()).when(clock).instant();
    doReturn(fixedClock.getZone()).when(clock).getZone();

    when(personService.getPersonByChatId(1L)).thenReturn(Optional.of(person));
    when(reportService.getReportByPersonAndDate(person, LocalDate.now().plusDays(1))).thenReturn(Optional.empty());
    when(telegramBot.execute(any(SendMessage.class))).thenReturn(response);

    reportProcessor.processReportMessage(telegramMessage, lastCommand);

    ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
    verify(telegramBot).execute(argumentCaptor.capture());
    SendMessage actual = argumentCaptor.getValue();

    Assertions.assertEquals(actual.getParameters().get("chat_id"), 1L);
    Assertions.assertEquals(actual.getParameters().get("text"), ANSWERS.get(COMMAND_LOSING_TIME_REPORT));
  }

  @Test
  public void processFullyFilledReportWhenExistingReportInDbONThatDay() {
    TelegramMessage telegramMessage = new TelegramMessage(1, "smth.", "1");

    mockClock();

    Report report = new Report();
    report.setDate(LocalDate.now());

    when(personService.getPersonByChatId(1L)).thenReturn(Optional.of(person));
    when(reportService.getReportByPersonAndDate(person, LocalDate.now())).thenReturn(Optional.of(report));
    when(telegramBot.execute(any(SendMessage.class))).thenReturn(response);

    reportProcessor.processReportMessage(telegramMessage, lastCommand);

    ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
    verify(telegramBot).execute(argumentCaptor.capture());
    SendMessage actual = argumentCaptor.getValue();

    Assertions.assertEquals(actual.getParameters().get("chat_id"), 1L);
    Assertions.assertEquals(actual.getParameters().get("text"), ANSWERS.get(COMMAND_SUCCESSFUL_REPORT));
  }

  private void checkMessageWhenThereIsNoReportInDbOnThatDay(TelegramMessage telegramMessage, String expectedAnswer) {
    mockClock();

    when(personService.getPersonByChatId(1L)).thenReturn(Optional.of(person));
    when(reportService.getReportByPersonAndDate(person, LocalDate.now())).thenReturn(Optional.empty());
    when(telegramBot.execute(any(SendMessage.class))).thenReturn(response);

    reportProcessor.processReportMessage(telegramMessage, lastCommand);

    ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
    verify(telegramBot).execute(argumentCaptor.capture());
    SendMessage actual = argumentCaptor.getValue();

    Assertions.assertEquals(actual.getParameters().get("chat_id"), 1L);
    Assertions.assertEquals(actual.getParameters().get("text"), expectedAnswer);
  }

  private void mockClock() {
    Clock fixedClock = Clock.fixed(
        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault());
    doReturn(fixedClock.instant()).when(clock).instant();
    doReturn(fixedClock.getZone()).when(clock).getZone();
  }

}
