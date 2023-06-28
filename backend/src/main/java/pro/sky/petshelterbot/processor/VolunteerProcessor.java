package pro.sky.petshelterbot.processor;

import com.pengrad.telegrambot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.sky.petshelterbot.message.TelegramMessage;
import pro.sky.petshelterbot.model.LastCommand;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.model.Pet;
import pro.sky.petshelterbot.model.Report;
import pro.sky.petshelterbot.model.enums.Status;
import pro.sky.petshelterbot.service.LastCommandService;
import pro.sky.petshelterbot.message.MessageSendingClass;
import pro.sky.petshelterbot.service.PersonService;
import pro.sky.petshelterbot.service.PetService;
import pro.sky.petshelterbot.service.ReportService;

import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Pattern;

import static pro.sky.petshelterbot.utility.CallbackUtils.BUTTONS;
import static pro.sky.petshelterbot.utility.TextUtils.ANSWERS;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_MAIN;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_BAD_REPORT;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_CHAT_NOT_OPENED;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_END_USER;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_END_VOLUNTEER;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_NO_CHAT;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_NO_PERSON;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_NO_REPORT;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_PROBATION_DECISION_WRONG_FORMAT;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_PROBATION_END_USER;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_PROBATION_END_VOLUNTEER;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_PROBATION_EXTENDED_USER;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_PROBATION_EXTENDED_VOLUNTEER;
import static pro.sky.petshelterbot.utility.TextUtils.COMMAND_VOLUNTEER_WARNING_SENT;

@Component
@Slf4j
public class VolunteerProcessor extends MessageSendingClass {

  public static final Pattern VOLUNTEER_ANSWERING_TO_USER_PATTERN = Pattern.compile(
      "^ *[\\[]USER-\\d+[\\]](.|\\n)*$");
  public static final Pattern VOLUNTEER_CHECKING_UP_THE_REPORT_PATTERN = Pattern.compile(
      "^ *[\\[]REPORT-\\d+[\\]](.|\\n)*$");
  public static final Pattern VOLUNTEER_PROBATION_DECISION_PATTERN = Pattern.compile(
      "^ *[\\[]PROBATION-\\d+[\\]].+$");

  private final LastCommandService lastCommandService;
  private final PersonService personService;
  private final ReportService reportService;
  private final PetService petService;

  public VolunteerProcessor(TelegramBot telegramBot,
                            LastCommandService lastCommandService,
                            PersonService personService,
                            ReportService reportService,
                            PetService petService) {
    super(telegramBot);
    this.lastCommandService = lastCommandService;
    this.personService = personService;
    this.reportService = reportService;
    this.petService = petService;
  }

  public void processVolunteersMessage(TelegramMessage message) {
    if (VOLUNTEER_ANSWERING_TO_USER_PATTERN.matcher(message.getText()).matches()) {
      processVolunteerAnswering(message);
      return;
    }
    if (VOLUNTEER_CHECKING_UP_THE_REPORT_PATTERN.matcher(message.getText()).matches()) {
      processVolunteerSendingWarningToUserBecauseOfBadReport(message);
      return;
    }
    if (VOLUNTEER_PROBATION_DECISION_PATTERN.matcher(message.getText()).matches()) {
      processVolunteersDecisionOfProbation(message);
    }
  }

  /**
   * Обработка ответов волонтера на сообщения пользователей. Если волонтер пытается ответить в несуществующий чат
   * или в чат, в котором не требуется его помощь, то он получит соответствующее сообщение. Также волонтер может
   * "закрыть" чат для общения с пользователем когда все вопросы пользователя были обсуждены – волонтер должен
   * отправить сообщение "[user_chat_id] end"
   *
   * @param volunteerMessage сообщение волонтера (включает chat_id, текст и file_id)
   */
  private void processVolunteerAnswering(TelegramMessage volunteerMessage) {
    String[] arr = volunteerMessage.getText().split("[\\[|\\]]");
    Long userChatId = Long.parseLong(arr[1].substring(5));
    Optional<LastCommand> userLastCommandOptional = lastCommandService.getByChatId(userChatId);
    TelegramMessage messageToVolunteer = new TelegramMessage(volunteerMessage.getChatId());
    if (userLastCommandOptional.isEmpty()) {
      messageToVolunteer.setText(ANSWERS.get(COMMAND_VOLUNTEER_NO_CHAT));
      sendMessage(messageToVolunteer);
      return;
    }
    LastCommand userLastCommand = userLastCommandOptional.get();
    if (!userLastCommand.getLastCommand().startsWith(COMMAND_VOLUNTEER)
        || userLastCommand.getIsClosed()) {
      messageToVolunteer.setText(ANSWERS.get(COMMAND_VOLUNTEER_CHAT_NOT_OPENED));
      sendMessage(messageToVolunteer);
      return;
    }

    String answer = volunteerMessage.getText().substring(volunteerMessage.getText().indexOf(']') + 1).trim();
    TelegramMessage messageToUser = new TelegramMessage(userChatId);
    if (answer.equalsIgnoreCase("end")) {
      messageToVolunteer.setText(ANSWERS.get(COMMAND_VOLUNTEER_END_VOLUNTEER)
          .replace("user_chat_id", userChatId.toString()));
      sendMessage(messageToVolunteer);

      messageToUser.setText(ANSWERS.get(COMMAND_VOLUNTEER_END_USER));
      Integer messageId = sendMessage(messageToUser, BUTTONS.get(COMMAND_MAIN));
      userLastCommand.setLastCommand(COMMAND_VOLUNTEER);
      userLastCommand.setIsClosed(true);
      userLastCommand.setLastMessageId(messageId);
      lastCommandService.save(userLastCommand);
    } else if (!answer.isBlank() || volunteerMessage.getFileId() != null) {
      messageToUser.setText(answer);
      messageToUser.setFileId(volunteerMessage.getFileId());
      sendMessage(messageToUser);
    }
  }

  private void processVolunteerSendingWarningToUserBecauseOfBadReport(TelegramMessage volunteerMessage) {
    String[] arr = volunteerMessage.getText().split("[\\[|\\]]");
    Long userChatId = Long.parseLong(arr[1].substring(7));
    Optional<Person> reportingPersonOptional = checkUser(userChatId, volunteerMessage);
    if (reportingPersonOptional.isEmpty()) return;
    Optional<Report> reportOptional = reportService.getReportByPersonAndDate(
        reportingPersonOptional.get(),
        LocalDate.now());
    if (reportOptional.isEmpty()) {
      sendMessage(new TelegramMessage(
          volunteerMessage.getChatId(),
          ANSWERS.get(COMMAND_VOLUNTEER_NO_REPORT)));
      return;
    }
    sendMessage(new TelegramMessage(userChatId, ANSWERS.get(COMMAND_VOLUNTEER_BAD_REPORT)));
    sendMessage(new TelegramMessage(volunteerMessage.getChatId(), ANSWERS.get(COMMAND_VOLUNTEER_WARNING_SENT)));
  }

  private void processVolunteersDecisionOfProbation(TelegramMessage volunteerMessage) {
    String[] arr = volunteerMessage.getText().split("[\\[|\\]]");
    Long userChatId = Long.parseLong(arr[1].substring(10));
    Optional<Person> reportingPersonOptional = checkUser(userChatId, volunteerMessage);
    if (reportingPersonOptional.isEmpty()) return;
    Person reportingPerson = reportingPersonOptional.get();

    TelegramMessage messageToVolunteer = new TelegramMessage(volunteerMessage.getChatId());
    TelegramMessage messageToUser = new TelegramMessage(userChatId);
    String answer = volunteerMessage.getText().substring(volunteerMessage.getText().indexOf(']') + 1).trim();
    if (answer.equalsIgnoreCase("end")) {
      messageToVolunteer.setText(ANSWERS.get(COMMAND_VOLUNTEER_PROBATION_END_VOLUNTEER)
          .replace("user_chat_id", userChatId.toString()));
      sendMessage(messageToVolunteer);

      messageToUser.setText(ANSWERS.get(COMMAND_VOLUNTEER_PROBATION_END_USER));
      sendMessage(messageToUser);

      Pet pet = reportingPerson.getPet();
      pet.setStatus(Status.ADOPTED);
      petService.save(pet);
    } else {
      try {
        Integer daysToAddToProbation = Integer.parseInt(answer);
        reportingPerson.setProbationEnd(LocalDate.now().plusDays(daysToAddToProbation));
        personService.save(reportingPerson);

        messageToVolunteer.setText(ANSWERS.get(COMMAND_VOLUNTEER_PROBATION_EXTENDED_VOLUNTEER)
            .replace("user_chat_id", userChatId.toString()));
        sendMessage(messageToVolunteer);

        messageToUser.setText(ANSWERS.get(COMMAND_VOLUNTEER_PROBATION_EXTENDED_USER)
            .replace("%DAYS%", daysToAddToProbation.toString()));
        sendMessage(messageToUser);
      } catch (NumberFormatException e) {
        messageToVolunteer.setText(ANSWERS.get(COMMAND_VOLUNTEER_PROBATION_DECISION_WRONG_FORMAT));
        sendMessage(messageToVolunteer);
      }
    }
  }

  private Optional<Person> checkUser(long userChatId, TelegramMessage volunteerMessage) {
    Optional<LastCommand> userLastCommandOptional = lastCommandService.getByChatId(userChatId);
    TelegramMessage messageToVolunteer = new TelegramMessage(volunteerMessage.getChatId());
    if (userLastCommandOptional.isEmpty()) {
      messageToVolunteer.setText(ANSWERS.get(COMMAND_VOLUNTEER_NO_CHAT));
      sendMessage(messageToVolunteer);
      return Optional.empty();
    }
    Optional<Person> reportingPersonOptional = personService.getPersonByChatId(userChatId);
    if (reportingPersonOptional.isEmpty()) {
      messageToVolunteer.setText(ANSWERS.get(COMMAND_VOLUNTEER_NO_PERSON));
      sendMessage(messageToVolunteer);
    }

    return reportingPersonOptional;
  }

}
