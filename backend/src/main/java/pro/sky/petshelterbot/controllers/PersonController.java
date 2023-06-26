package pro.sky.petshelterbot.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.service.PersonService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/persons")
public class PersonController {

  private final PersonService personService;

  public PersonController(PersonService personService) {
    this.personService = personService;
  }

  @GetMapping
  @Operation(
      summary = "Получаем всех людей.",
      description = "Можно получить все личности, соответствующие объекту Person")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = " находящиеся личности в базе усыновителей найдены"
      ),
      @ApiResponse(
          responseCode = "204",
          description = "в данный момент нет ни одной личности"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "произошла ошибка, не зависящая от вызывающей стороны"
      )
  })
  public ResponseEntity<?> getAllPerson() {
    List<Person> personList = personService.getAll();
    if (!personList.isEmpty()) {
      return ResponseEntity.ok(personList);
    } else {
      return ResponseEntity.noContent().build();
    }
  }

  @GetMapping("/volunteers")
  @Operation(
      summary = "Получаем всех волонтеров.",
      description = "Можно получить всех волонтеров, работающих в приюте")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = " находящиеся личности в базе волонтеров найдены"
      ),
      @ApiResponse(
          responseCode = "204",
          description = "в данный момент нет ни одного волонтера"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "произошла ошибка, не зависящая от вызывающей стороны"
      )
  })
  public ResponseEntity<?> getAllVolunteer() {
    List<Person> allVolunteer = personService.getAllVolunteer();
    if (!allVolunteer.isEmpty()) {
      return ResponseEntity.ok(allVolunteer);
    } else {
      return ResponseEntity.noContent().build();
    }
  }

  @PatchMapping("/volunteers/{chat_id}")
  @Operation(
      summary = "Обновляет информацию о личности по ее chat_id.",
      description = "Для присвоения статуса - волонтер, необходимо выбрать true")

  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "удалось присвоить статус"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "личности с данным id не существует в базе"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "произошла ошибка, не зависящая от вызывающей стороны"
      )
  })
  public ResponseEntity<?> changeIsVolunteerIsTrue(@PathVariable Long chat_id) {
    Optional<Person> person = personService.getPersonByChatId(chat_id);
    person.ifPresent(personService::setPersonIsVolunteerIsTrue);
    return ResponseEntity.of(person);
  }

  @PatchMapping("/volunteers/{chat_id}/revoke")
  @Operation(
      summary = "Обновляет информацию о личности по ее id.",
      description = "Для отзыва статуса - волонтер, необходимо выбрать false")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "удалось отозвать статус"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "личности с данным id не существует в базе"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "произошла ошибка, не зависящая от вызывающей стороны"
      )
  })
  public ResponseEntity<?> changeIsVolunteerIsFalse(@PathVariable Long chat_id) {
    Optional<Person> person = personService.getPersonByChatId(chat_id);
    person.ifPresent(personService::setPersonIsVolunteerIsFalse);
    return ResponseEntity.of(person);
  }

  @PostMapping
  @Operation(summary = "Добавление пользователя (ДЛЯ ТЕСТОВ)")
  public Person addPerson(@RequestBody Person person) {
    personService.save(person);
    return person;
  }

  @PatchMapping("/assignAnimal/{chat_id}/{id}")
  @Operation(summary = "Назначает животное человеку")
  public ResponseEntity<HttpStatus> assignTheAnimalToThePerson(@PathVariable Long chat_id,
                                                               @PathVariable Long id) {
      personService.addAnAnimalToAPerson(chat_id, id);
      return ResponseEntity.ok(HttpStatus.OK);
  }
}
