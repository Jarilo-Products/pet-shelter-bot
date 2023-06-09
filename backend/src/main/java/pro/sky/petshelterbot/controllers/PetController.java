package pro.sky.petshelterbot.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pro.sky.petshelterbot.model.Pet;
import pro.sky.petshelterbot.model.enums.Status;
import pro.sky.petshelterbot.service.PetService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/pets")
@Tag(name = "Домашние питомцы", description = "CRUD-операции и другие эндпоинты для работы с питомцами")
public class PetController {

  private final PetService petService;

  public PetController(PetService petService) {
    this.petService = petService;
  }

  @GetMapping
  @Operation(
      summary = "Позволяет увидеть всех питомцев.",
      description = "Можно увидеть всех питомцев, соответсвующих схемой обекта Pet"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "питомцы находящиеся в приютах найдены"
      ),
      @ApiResponse(
          responseCode = "204",
          description = "в данный момент нет ни одного питомца"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "произошла ошибка, не зависящая от вызывающей стороны"
      )
  })
  public ResponseEntity<?> getAllPets() {
    List<Pet> pets = petService.getAll();
    if (!pets.isEmpty()) {
      return ResponseEntity.ok(pets);
    } else {
      return ResponseEntity.noContent().build();
    }
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Возвращает питомца найденного по id.",
      description = "Можно получить питомца в соответствии с его id"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "удалось найти питомца"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "питомца с данным id не существует в базе"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "произошла ошибка, не зависящая от вызывающей стороны"
      )
  })
  public ResponseEntity<?> getPet(@PathVariable Long id) {
    return ResponseEntity.of(petService.getPetById(id));
  }

  @PostMapping
  @Operation(
      summary = "Добавляет питомца в приют.",
      description = "Для добавления питомца необходимо заполнить поля строго в соответствии с схемой объекта<br>" +
          "<br>" +
          "<code>type</code> - необходимо написать <b>СТРОГО</b> в верхнем регистре. Допустимые значениия:<code>CAT, DOG</code><br>" +
          "<br>" +
          "<code>name</code> необходимо <b>ЗАПОЛНИТЬ</b>. Например:<code>Мурзик</code><br>" +
          "<br>" +
          "<code>birthdate</code> необходимо написать <b>СТРОГО</b> в верхнем регистре. Допустимые значениия:<code>2023-06-12</code><br>" +
          "<br>" +
          "<code>status</code> в данном поле устанавливается статус OWNERLESS <b>ПО-УМОЛЧАНИЮ</b>. Вне зависимости от введенного значения<br>" +
          "<br>" +
          "<code>healthStatus</code> необходимо написать <b>СТРОГО</b> в верхнем регистре. Допустимые значениия:<code>HEALTHY, HEALTH_RESTRICTIONS</code><br>" +
          "<br>" +
          "<code>sex</code> необходимо написать <b>СТРОГО</b> в верхнем регистре. Допустимые значениия:<code>MALE, FEMALE</code><br>"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "удалось добавить питомца"
      ),
      @ApiResponse(
          responseCode = "400",
          description = "введены некорректные параметры"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "произошла ошибка, не зависящая от вызывающей стороны"
      )
  })
  public ResponseEntity<?> addPet(@RequestBody @Validated Pet pet) {
    pet.setStatus(Status.OWNERLESS);
    petService.save(pet);
    return ResponseEntity.ok().body(pet);
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Обновляет информацию о питомце по его id.",
      description = "Для обновления информации о питомце необходимо заполнить поля строго в соответствии с схемой объекта<br>" +
          "<br>" +
          "<code>id</code> - введите id питомца, информацию о котором хотите поменять<br>" +
          "<br>" +
          "<code>type</code> - необходимо написать <b>СТРОГО</b> в верхнем регистре. Допустимые значениия:<code>CAT, DOG</code><br>" +
          "<br>" +
          "<code>name</code> необходимо <b>ЗАПОЛНИТЬ</b>. Например:<code>Мурзик</code><br>" +
          "<br>" +
          "<code>birthdate</code> необходимо написать <b>СТРОГО</b> в верхнем регистре. Допустимые значениия:<code>2023-06-12</code><br>" +
          "<br>" +
          "<code>status</code> в данном поле устанавливается статус OWNERLESS <b>ПО-УМОЛЧАНИЮ</b>. Вне зависимости от введенного значения<br>" +
          "<br>" +
          "<code>healthStatus</code> необходимо написать <b>СТРОГО</b> в верхнем регистре. Допустимые значениия:<code>HEALTHY, HEALTH_RESTRICTIONS</code><br>" +
          "<br>" +
          "<code>sex</code> необходимо написать <b>СТРОГО</b> в верхнем регистре. Допустимые значениия:<code>MALE, FEMALE</code><br>"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "удалось обновить информацию"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "питомца с данным id не существует в базе"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "произошла ошибка, не зависящая от вызывающей стороны"
      )
  })
  public ResponseEntity<?> editPet(@RequestBody @Valid Pet pet, @PathVariable Long id) {
    if (pet.getId() == null) {
      pet.setId(id);
    }
    if (petService.getPetById(id).isPresent() && Objects.equals(pet.getId(), id)) {
      petService.save(pet);
      return ResponseEntity.ok().body(pet);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Удаляет информацию о питомце.",
      description = "Можно удалить информацию о питомце по id"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "удалось удалить информацию"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "питомца с данным id не существует в базе"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "произошла ошибка, не зависящая от вызывающей стороны"
      )
  })
  public ResponseEntity<?> deletePet(@PathVariable Long id) {
    Optional<Pet> pet = petService.getPetById(id);
    if (pet.isPresent()) {
      petService.delete(id);
    }
    return ResponseEntity.of(pet);
  }
}
