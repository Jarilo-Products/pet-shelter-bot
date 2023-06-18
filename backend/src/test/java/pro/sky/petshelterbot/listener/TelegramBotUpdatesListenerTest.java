package pro.sky.petshelterbot.listener;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.petshelterbot.model.LastCommand;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.model.enums.Type;
import pro.sky.petshelterbot.processor.TextMessageProcessor;
import pro.sky.petshelterbot.service.LastCommandService;
import pro.sky.petshelterbot.service.PersonService;

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
import static org.mockito.Mockito.*;
import static pro.sky.petshelterbot.utility.TextUtils.*;

@ExtendWith(MockitoExtension.class)
class TelegramBotUpdatesListenerTest {

  @Mock
  private TelegramBot telegramBot;

  @Mock
  private LastCommandService lastCommandService;

  @Mock
  private PersonService personService;

  @InjectMocks
  private TextMessageProcessor textMessageProcessor;

  private TelegramBotUpdatesListener telegramBotUpdatesListener;

  @BeforeEach
  public void initTelegramBotUpdatesListener() {
    telegramBotUpdatesListener = new TelegramBotUpdatesListener(telegramBot, textMessageProcessor);
  }

  @Test
  public void exceptionThrowTest() throws URISyntaxException, IOException {
    String json = Files.readString(
        Path.of(TelegramBotUpdatesListenerTest.class.getResource("update.json").toURI()));
    Update update = BotUtils.fromJson(
        json.replace("%text%", COMMAND_START), Update.class);

    when(lastCommandService.getByChatId(any())).thenReturn(Optional.empty());
    when(telegramBot.execute(any())).thenThrow(RuntimeException.class);

    telegramBotUpdatesListener.process(Collections.singletonList(update));
  }

  @Test
  public void sendResponseIsNotOkTest() throws URISyntaxException, IOException {
    String json = Files.readString(
        Path.of(TelegramBotUpdatesListenerTest.class.getResource("update.json").toURI()));
    Update update = BotUtils.fromJson(
        json.replace("%text%", COMMAND_START), Update.class);
    SendResponse sendResponse = BotUtils.fromJson("""
        {
          "ok": false
        }
        """, SendResponse.class);

    when(lastCommandService.getByChatId(any())).thenReturn(Optional.empty());
    when(telegramBot.execute(any())).thenReturn(sendResponse);

    telegramBotUpdatesListener.process(Collections.singletonList(update));
  }

  @Test
  public void firstStartCommandTest() throws URISyntaxException, IOException {
    when(lastCommandService.getByChatId(any())).thenReturn(Optional.empty());

    checkResponse(COMMAND_START, ANSWERS.get(COMMAND_START));
    verify(lastCommandService).save(any());
  }

  @Test
  public void notFirstStartCommandWhenLastCommandIsClosedTest() throws URISyntaxException, IOException {
    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(1L);
    lastCommand.setIsClosed(true);

    when(lastCommandService.getByChatId(any())).thenReturn(Optional.of(lastCommand));

    checkResponse(COMMAND_START, ANSWERS.get(COMMAND_START + "_registered"));
    assertFalse(lastCommand.getIsClosed());
    assertEquals(lastCommand.getLastCommand(), COMMAND_START);
    verify(lastCommandService).save(lastCommand);
  }

  @Test
  public void choosingCatTest() throws URISyntaxException, IOException {
    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(1L);
    lastCommand.setLastCommand(COMMAND_START);
    lastCommand.setIsClosed(false);

    when(lastCommandService.getByChatId(any())).thenReturn(Optional.of(lastCommand));

    checkResponse("1", ANSWERS.get("chosen_CAT"));
    assertTrue(lastCommand.getIsClosed());
    assertEquals(lastCommand.getLastCommand(), COMMAND_START);
    assertEquals(lastCommand.getActiveType(), Type.CAT);
    verify(lastCommandService).save(lastCommand);
  }

  @Test
  public void choosingDogTest() throws URISyntaxException, IOException {
    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(1L);
    lastCommand.setLastCommand(COMMAND_START);
    lastCommand.setIsClosed(false);

    when(lastCommandService.getByChatId(any())).thenReturn(Optional.of(lastCommand));

    checkResponse("2", ANSWERS.get("chosen_DOG"));
    assertTrue(lastCommand.getIsClosed());
    assertEquals(lastCommand.getLastCommand(), COMMAND_START);
    assertEquals(lastCommand.getActiveType(), Type.DOG);
    verify(lastCommandService).save(lastCommand);
  }

  @Test
  public void incorrectPetChoosingTest() throws URISyntaxException, IOException {
    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(1L);
    lastCommand.setLastCommand(COMMAND_START);
    lastCommand.setIsClosed(false);

    when(lastCommandService.getByChatId(any())).thenReturn(Optional.of(lastCommand));

    checkResponse("2134125", ANSWERS.get(COMMAND_START + "_registered"));
    assertFalse(lastCommand.getIsClosed());
    assertEquals(lastCommand.getLastCommand(), COMMAND_START);
    verify(lastCommandService).save(lastCommand);
  }

  @Test
  public void singleAnswerCommandTest() throws URISyntaxException, IOException {
    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(1L);
    lastCommand.setActiveType(Type.CAT);
    lastCommand.setIsClosed(true);

    when(lastCommandService.getByChatId(any())).thenReturn(Optional.of(lastCommand));

    checkResponse(COMMAND_INFO, ANSWERS.get(COMMAND_INFO + "_" + Type.CAT.name()));
    assertTrue(lastCommand.getIsClosed());
    assertEquals(lastCommand.getLastCommand(), COMMAND_INFO);
    verify(lastCommandService).save(lastCommand);
  }

  @Test
  public void unknownCommandTest() throws URISyntaxException, IOException {
    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(1L);
    lastCommand.setActiveType(Type.CAT);
    lastCommand.setIsClosed(true);

    when(lastCommandService.getByChatId(any())).thenReturn(Optional.of(lastCommand));

    checkResponse("abrakadabra", UNKNOWN_COMMAND);
  }

  @Test
  public void volunteerCommandWhenThereIsNoVolunteersTest() throws URISyntaxException, IOException {
    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(1L);
    lastCommand.setActiveType(Type.CAT);
    lastCommand.setIsClosed(true);

    when(lastCommandService.getByChatId(any())).thenReturn(Optional.of(lastCommand));
    when(personService.getVolunteers()).thenReturn(Collections.emptyList());

    checkResponse(COMMAND_VOLUNTEER, ANSWERS.get(COMMAND_VOLUNTEER + "_empty"));
    assertTrue(lastCommand.getIsClosed());
    assertEquals(lastCommand.getLastCommand(), COMMAND_VOLUNTEER);
    verify(lastCommandService).save(lastCommand);
  }

  @Test
  public void volunteerCommandWhenThereIsOneOrMoreVolunteersTest() throws URISyntaxException, IOException {
    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(1L);
    lastCommand.setActiveType(Type.CAT);
    lastCommand.setIsClosed(true);

    Person person = new Person();
    person.setId(2L);
    person.setChatId(200L);
    person.setFirstName("Ivan");

    when(lastCommandService.getByChatId(any())).thenReturn(Optional.of(lastCommand));
    when(personService.getVolunteers()).thenReturn(List.of(person));

    processUpdate(COMMAND_VOLUNTEER);

    ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
    verify(telegramBot, times(2)).execute(argumentCaptor.capture());
    List<SendMessage> actual = argumentCaptor.getAllValues();

    assertEquals(actual.get(0).getParameters().get("chat_id"), lastCommand.getChatId());
    assertEquals(actual.get(0).getParameters().get("text"),
        ANSWERS.get(COMMAND_VOLUNTEER).replace("[name]", person.getFirstName()));
    assertEquals(lastCommand.getLastCommand(), COMMAND_VOLUNTEER + " " + person.getChatId());
    assertFalse(lastCommand.getIsClosed());

    assertEquals(actual.get(1).getParameters().get("chat_id"), person.getChatId());
    assertEquals(actual.get(1).getParameters().get("text"),
        ANSWERS.get(COMMAND_VOLUNTEER + "_memo").replace("user_chat_id",
            lastCommand.getChatId().toString()));
  }

  @Test
  public void volunteerAnsweringToNotExistingChatTest() throws URISyntaxException, IOException {
    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(1L);

    when(lastCommandService.getByChatId(1L)).thenReturn(Optional.of(lastCommand));
    when(lastCommandService.getByChatId(2L)).thenReturn(Optional.empty());
    when(personService.isChatOfVolunteer(1L)).thenReturn(true);

    checkResponse("[2] smth.", ANSWERS.get(COMMAND_VOLUNTEER + "_no_chat"));
  }

  @Test
  public void volunteerAnsweringToUserThatDoesNotNeedHelpTest() throws URISyntaxException, IOException {
    LastCommand volunteerLastCommand = new LastCommand();
    volunteerLastCommand.setChatId(1L);

    LastCommand userLastCommand = new LastCommand();
    userLastCommand.setChatId(2L);
    userLastCommand.setLastCommand(COMMAND_VOLUNTEER);
    userLastCommand.setIsClosed(true);

    when(lastCommandService.getByChatId(1L)).thenReturn(Optional.of(volunteerLastCommand));
    when(lastCommandService.getByChatId(2L)).thenReturn(Optional.of(userLastCommand));
    when(personService.isChatOfVolunteer(1L)).thenReturn(true);

    checkResponse("[2] smth.", ANSWERS.get(COMMAND_VOLUNTEER + "_chat_not_opened"));
  }

  @Test
  public void volunteerAnsweringToCorrectUserTest() throws URISyntaxException, IOException {
    LastCommand volunteerLastCommand = new LastCommand();
    volunteerLastCommand.setChatId(1L);

    LastCommand userLastCommand = new LastCommand();
    userLastCommand.setChatId(2L);
    userLastCommand.setLastCommand(COMMAND_VOLUNTEER);
    userLastCommand.setIsClosed(false);

    when(lastCommandService.getByChatId(1L)).thenReturn(Optional.of(volunteerLastCommand));
    when(lastCommandService.getByChatId(2L)).thenReturn(Optional.of(userLastCommand));
    when(personService.isChatOfVolunteer(1L)).thenReturn(true);

    processUpdate("[2] smth.");

    ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
    verify(telegramBot).execute(argumentCaptor.capture());
    SendMessage actual = argumentCaptor.getValue();

    assertEquals(actual.getParameters().get("chat_id"), userLastCommand.getChatId());
    assertEquals(actual.getParameters().get("text"), "smth.");
  }

  @Test
  public void volunteerClosingChatWithUserTest() throws URISyntaxException, IOException {
    LastCommand volunteerLastCommand = new LastCommand();
    volunteerLastCommand.setChatId(1L);

    LastCommand userLastCommand = new LastCommand();
    userLastCommand.setChatId(2L);
    userLastCommand.setLastCommand(COMMAND_VOLUNTEER);
    userLastCommand.setIsClosed(false);

    when(lastCommandService.getByChatId(1L)).thenReturn(Optional.of(volunteerLastCommand));
    when(lastCommandService.getByChatId(2L)).thenReturn(Optional.of(userLastCommand));
    when(personService.isChatOfVolunteer(1L)).thenReturn(true);

    processUpdate("[2] end");

    ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
    verify(telegramBot, times(2)).execute(argumentCaptor.capture());
    List<SendMessage> actual = argumentCaptor.getAllValues();

    assertEquals(actual.get(0).getParameters().get("chat_id"), volunteerLastCommand.getChatId());
    assertEquals(actual.get(0).getParameters().get("text"),
        ANSWERS.get(COMMAND_VOLUNTEER + "_end").replace("user_chat_id", userLastCommand.getChatId().toString()));

    assertEquals(actual.get(1).getParameters().get("chat_id"), userLastCommand.getChatId());
    assertEquals(actual.get(1).getParameters().get("text"), ANSWERS.get(COMMAND_VOLUNTEER + "_end_user"));
    assertEquals(userLastCommand.getLastCommand(), COMMAND_VOLUNTEER);
    assertTrue(userLastCommand.getIsClosed());

    verify(lastCommandService).save(userLastCommand);
  }

  @Test
  public void userSendingMessageToVolunteerTest() throws URISyntaxException, IOException {
    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(1L);
    lastCommand.setLastCommand(COMMAND_VOLUNTEER + " 2");
    lastCommand.setIsClosed(false);

    when(lastCommandService.getByChatId(1L)).thenReturn(Optional.of(lastCommand));

    processUpdate("smth.");

    ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
    verify(telegramBot).execute(argumentCaptor.capture());
    SendMessage actual = argumentCaptor.getValue();

    assertEquals(actual.getParameters().get("chat_id"), 2L);
    assertEquals(actual.getParameters().get("text"), '[' + lastCommand.getChatId().toString() + "] smth.");
  }

  @Test
  public void sendContactsCommandTest() throws URISyntaxException, IOException {
    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(1L);
    lastCommand.setIsClosed(true);

    when(lastCommandService.getByChatId(any())).thenReturn(Optional.of(lastCommand));

    checkResponse(COMMAND_SEND_CONTACTS, ANSWERS.get(COMMAND_SEND_CONTACTS));
    assertFalse(lastCommand.getIsClosed());
    assertEquals(lastCommand.getLastCommand(), COMMAND_SEND_CONTACTS);
    verify(lastCommandService).save(lastCommand);
  }

  @Test
  public void sendingNotEnoughLinesAsContactsTest() throws URISyntaxException, IOException {
    String contacts = "";

    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(1L);
    lastCommand.setLastCommand("/sendcontacts");
    lastCommand.setIsClosed(false);

    when(lastCommandService.getByChatId(any())).thenReturn(Optional.of(lastCommand));

    checkResponse(contacts, ANSWERS.get(COMMAND_SEND_CONTACTS + "_notenough"));
  }

  @Test
  public void sendingInvalidContactsTest() throws URISyntaxException, IOException {
    String contacts = """
        Петров Петр
        01-01-2000
        89001002030
        mail.ru
        г.Астана, ул. Коллективная, д.200, кв.354
        """;

    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(1L);
    lastCommand.setLastCommand("/sendcontacts");
    lastCommand.setIsClosed(false);

    when(lastCommandService.getByChatId(any())).thenReturn(Optional.of(lastCommand));

    checkResponse(contacts, ANSWERS.get(COMMAND_SEND_CONTACTS + "_invalid_name")
        + '\n' + ANSWERS.get(COMMAND_SEND_CONTACTS + "_invalid_bithdate")
        + '\n' + ANSWERS.get(COMMAND_SEND_CONTACTS + "_invalid_email")
        + '\n' + ANSWERS.get(COMMAND_SEND_CONTACTS + "_invalid"));
  }

  @Test
  public void sendingValidContactsTest() throws URISyntaxException, IOException {
    String contacts = """
        Петров Петр Петрович
        01.01.2000
        89001002030
        email@mail.ru
        г.Астана, ул. Коллективная, д.200, кв.354
        """;

    LastCommand lastCommand = new LastCommand();
    lastCommand.setChatId(1L);
    lastCommand.setLastCommand("/sendcontacts");
    lastCommand.setIsClosed(false);

    Person person = new Person(
        lastCommand.getChatId(),
        "Петр",
        "Петров",
        "Петрович",
        LocalDate.of(2000, 1, 1),
        "89001002030",
        "email@mail.ru",
        "г.Астана, ул. Коллективная, д.200, кв.354");

    when(lastCommandService.getByChatId(any())).thenReturn(Optional.of(lastCommand));

    checkResponse(contacts, ANSWERS.get("acceptedcontacts"));

    assertTrue(lastCommand.getIsClosed());

    ArgumentCaptor<Person> argumentCaptor = ArgumentCaptor.forClass(Person.class);
    verify(personService).save(argumentCaptor.capture());
    Person actual = argumentCaptor.getValue();
    assertEquals(person.getChatId(), actual.getChatId());
    assertEquals(person.getFirstName(), actual.getFirstName());
    assertEquals(person.getLastName(), actual.getLastName());
    assertEquals(person.getMiddleName(), actual.getMiddleName());
    assertEquals(person.getBirthdate(), actual.getBirthdate());
    assertEquals(person.getPhone(), actual.getPhone());
    assertEquals(person.getEmail(), actual.getEmail());
    assertEquals(person.getAddress(), actual.getAddress());
  }

  private void checkResponse(String input, String expectedOutput) throws URISyntaxException, IOException {
    Update update = processUpdate(input);

    ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
    verify(telegramBot).execute(argumentCaptor.capture());
    SendMessage actual = argumentCaptor.getValue();

    Assertions.assertEquals(actual.getParameters().get("chat_id"),
        update.message().chat().id());
    Assertions.assertEquals(actual.getParameters().get("text"),
        expectedOutput);
  }

  private Update processUpdate(String input) throws URISyntaxException, IOException {
    String json = Files.readString(
        Path.of(TelegramBotUpdatesListenerTest.class.getResource("update.json").toURI()));
    Update update = BotUtils.fromJson(
        json.replace("%text%", input), Update.class);
    SendResponse sendResponse = BotUtils.fromJson("""
        {
          "ok": true
        }
        """, SendResponse.class);

    when(telegramBot.execute(any())).thenReturn(sendResponse);

    telegramBotUpdatesListener.process(Collections.singletonList(update));

    return update;
  }

}
