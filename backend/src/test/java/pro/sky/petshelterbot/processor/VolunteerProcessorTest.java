package pro.sky.petshelterbot.processor;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.SendResponse;
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
import pro.sky.petshelterbot.model.Pet;
import pro.sky.petshelterbot.model.Report;
import pro.sky.petshelterbot.model.enums.Status;
import pro.sky.petshelterbot.service.LastCommandService;
import pro.sky.petshelterbot.service.PersonService;
import pro.sky.petshelterbot.service.PetService;
import pro.sky.petshelterbot.service.ReportService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pro.sky.petshelterbot.utility.TextUtils.ANSWERS;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_BAD_REPORT;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_NO_CHAT;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_NO_PERSON;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_NO_REPORT;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_PROBATION_DECISION_WRONG_FORMAT;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_PROBATION_END_USER;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_PROBATION_END_VOLUNTEER;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_PROBATION_EXTENDED_USER;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_PROBATION_EXTENDED_VOLUNTEER;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_WARNING_SENT;

@ExtendWith(MockitoExtension.class)
class VolunteerProcessorTest {

  @Mock
  private TelegramBot telegramBot;

  @Mock
  private LastCommandService lastCommandService;

  @Mock
  private PersonService personService;

  @Mock
  private ReportService reportService;

  @Mock
  private PetService petService;

  @InjectMocks
  private VolunteerProcessor volunteerProcessor;

  private static SendResponse response;

  @BeforeAll
  public static void init() throws URISyntaxException, IOException {
    String json = Files.readString(
        Path.of(ReportProcessorTest.class.getResource("response.json").toURI()));
    response = BotUtils.fromJson(json, SendResponse.class);
  }

  @Test
  public void volunteerSendingPhotoMessageTest() {
    String text = "smth.";
    String photo = "123";
    TelegramMessage telegramMessage = new TelegramMessage(1, "[USER-2] " + text, photo);

    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(2L);
    lastCommand.setLastCommand(COMMAND_VOLUNTEER + " 1");
    lastCommand.setIsClosed(false);

    when(lastCommandService.getByChatId(2L)).thenReturn(Optional.of(lastCommand));
    when(telegramBot.execute(any(SendPhoto.class))).thenReturn(response);

    volunteerProcessor.processVolunteersMessage(telegramMessage);

    ArgumentCaptor<SendPhoto> argumentCaptor = ArgumentCaptor.forClass(SendPhoto.class);
    verify(telegramBot).execute(argumentCaptor.capture());
    SendPhoto actual = argumentCaptor.getValue();

    assertEquals(actual.getParameters().get("chat_id"), 2L);
    assertEquals(actual.getParameters().get("caption"), text);
    assertEquals(actual.getParameters().get("photo"), photo);
  }

  @Test
  public void volunteerSendingWarningToUserBecauseOfBadReportTest() {
    TelegramMessage telegramMessage = new TelegramMessage(1, "[REPORT-2]");

    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(2L);

    Person person = new Person();
    person.setChatId(2L);

    Report report = new Report();
    report.setPerson(person);

    when(lastCommandService.getByChatId(2L)).thenReturn(Optional.of(lastCommand));
    when(personService.getPersonByChatId(2L)).thenReturn(Optional.of(person));
    when(reportService.getReportByPersonAndDate(person, LocalDate.now())).thenReturn(Optional.of(report));
    when(telegramBot.execute(any(SendMessage.class))).thenReturn(response);

    volunteerProcessor.processVolunteersMessage(telegramMessage);

    ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
    verify(telegramBot, times(2)).execute(argumentCaptor.capture());
    List<SendMessage> actual = argumentCaptor.getAllValues();

    assertEquals(actual.get(0).getParameters().get("chat_id"), 2L);
    assertEquals(actual.get(0).getParameters().get("text"), ANSWERS.get(COMMAND_VOLUNTEER_BAD_REPORT));

    assertEquals(actual.get(1).getParameters().get("chat_id"), 1L);
    assertEquals(actual.get(1).getParameters().get("text"), ANSWERS.get(COMMAND_VOLUNTEER_WARNING_SENT));
  }

  @Test
  public void volunteerTryingToSendWarningToUserThatHaveNotSentReportTodayTest() {
    TelegramMessage telegramMessage = new TelegramMessage(1, "[REPORT-2]");

    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(2L);

    Person person = new Person();
    person.setChatId(2L);

    when(lastCommandService.getByChatId(2L)).thenReturn(Optional.of(lastCommand));
    when(personService.getPersonByChatId(2L)).thenReturn(Optional.of(person));
    when(reportService.getReportByPersonAndDate(person, LocalDate.now())).thenReturn(Optional.empty());
    when(telegramBot.execute(any(SendMessage.class))).thenReturn(response);

    volunteerProcessor.processVolunteersMessage(telegramMessage);

    ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
    verify(telegramBot).execute(argumentCaptor.capture());
    SendMessage actual = argumentCaptor.getValue();

    assertEquals(actual.getParameters().get("chat_id"), 1L);
    assertEquals(actual.getParameters().get("text"), ANSWERS.get(COMMAND_VOLUNTEER_NO_REPORT));
  }

  @Test
  public void volunteerTryingToSendWarningToUserThatIsNotRegisteredAsAPersonTest() {
    TelegramMessage telegramMessage = new TelegramMessage(1, "[REPORT-2]");

    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(2L);

    when(lastCommandService.getByChatId(2L)).thenReturn(Optional.of(lastCommand));
    when(personService.getPersonByChatId(2L)).thenReturn(Optional.empty());
    when(telegramBot.execute(any(SendMessage.class))).thenReturn(response);

    volunteerProcessor.processVolunteersMessage(telegramMessage);

    ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
    verify(telegramBot).execute(argumentCaptor.capture());
    SendMessage actual = argumentCaptor.getValue();

    assertEquals(actual.getParameters().get("chat_id"), 1L);
    assertEquals(actual.getParameters().get("text"), ANSWERS.get(COMMAND_VOLUNTEER_NO_PERSON));
  }

  @Test
  public void volunteerTryingToSendWarningToUserThatHaveNotOpenedTheBotEverTest() {
    TelegramMessage telegramMessage = new TelegramMessage(1, "[REPORT-2]");

    when(lastCommandService.getByChatId(2L)).thenReturn(Optional.empty());
    when(telegramBot.execute(any(SendMessage.class))).thenReturn(response);

    volunteerProcessor.processVolunteersMessage(telegramMessage);

    ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
    verify(telegramBot).execute(argumentCaptor.capture());
    SendMessage actual = argumentCaptor.getValue();

    assertEquals(actual.getParameters().get("chat_id"), 1L);
    assertEquals(actual.getParameters().get("text"), ANSWERS.get(COMMAND_VOLUNTEER_NO_CHAT));
  }

  @Test
  public void volunteerEndingTheProbationTest() {
    TelegramMessage telegramMessage = new TelegramMessage(1, "[PROBATION-2] end");

    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(2L);

    Pet pet = new Pet();

    Person person = new Person();
    person.setChatId(2L);
    person.setPet(pet);

    when(lastCommandService.getByChatId(2L)).thenReturn(Optional.of(lastCommand));
    when(personService.getPersonByChatId(2L)).thenReturn(Optional.of(person));
    when(telegramBot.execute(any(SendMessage.class))).thenReturn(response);

    volunteerProcessor.processVolunteersMessage(telegramMessage);

    ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
    verify(telegramBot, times(2)).execute(argumentCaptor.capture());
    List<SendMessage> actual = argumentCaptor.getAllValues();

    assertEquals(actual.get(0).getParameters().get("chat_id"), 1L);
    assertEquals(actual.get(0).getParameters().get("text"), ANSWERS.get(COMMAND_VOLUNTEER_PROBATION_END_VOLUNTEER)
        .replace("user_chat_id", person.getChatId().toString()));

    assertEquals(actual.get(1).getParameters().get("chat_id"), 2L);
    assertEquals(actual.get(1).getParameters().get("text"), ANSWERS.get(COMMAND_VOLUNTEER_PROBATION_END_USER));

    assertEquals(pet.getStatus(), Status.ADOPTED);
    verify(petService).save(pet);
  }

  @Test
  public void volunteerExtendingTheProbationTest() {
    TelegramMessage telegramMessage = new TelegramMessage(1, "[PROBATION-2] 14");

    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(2L);

    Person person = new Person();
    person.setChatId(2L);

    when(lastCommandService.getByChatId(2L)).thenReturn(Optional.of(lastCommand));
    when(personService.getPersonByChatId(2L)).thenReturn(Optional.of(person));
    when(telegramBot.execute(any(SendMessage.class))).thenReturn(response);

    volunteerProcessor.processVolunteersMessage(telegramMessage);

    ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
    verify(telegramBot, times(2)).execute(argumentCaptor.capture());
    List<SendMessage> actual = argumentCaptor.getAllValues();

    assertEquals(actual.get(0).getParameters().get("chat_id"), 1L);
    assertEquals(actual.get(0).getParameters().get("text"), ANSWERS.get(COMMAND_VOLUNTEER_PROBATION_EXTENDED_VOLUNTEER)
        .replace("user_chat_id", person.getChatId().toString()));

    assertEquals(actual.get(1).getParameters().get("chat_id"), 2L);
    assertEquals(actual.get(1).getParameters().get("text"), ANSWERS.get(COMMAND_VOLUNTEER_PROBATION_EXTENDED_USER)
        .replace("%DAYS%", "14"));

    assertEquals(person.getProbationEnd(), LocalDate.now().plusDays(14));
  }

  @Test
  public void volunteerExtendingTheProbationWrongFormatTest() {
    TelegramMessage telegramMessage = new TelegramMessage(1, "[PROBATION-2] smth. 14");

    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(2L);

    Person person = new Person();
    person.setChatId(2L);

    when(lastCommandService.getByChatId(2L)).thenReturn(Optional.of(lastCommand));
    when(personService.getPersonByChatId(2L)).thenReturn(Optional.of(person));
    when(telegramBot.execute(any(SendMessage.class))).thenReturn(response);

    volunteerProcessor.processVolunteersMessage(telegramMessage);

    ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
    verify(telegramBot).execute(argumentCaptor.capture());
    SendMessage actual = argumentCaptor.getValue();

    assertEquals(actual.getParameters().get("chat_id"), 1L);
    assertEquals(actual.getParameters().get("text"), ANSWERS.get(COMMAND_VOLUNTEER_PROBATION_DECISION_WRONG_FORMAT));
  }

}
