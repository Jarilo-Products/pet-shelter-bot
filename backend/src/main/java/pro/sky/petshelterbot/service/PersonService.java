package pro.sky.petshelterbot.service;

import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.exceptions.NotFoundException;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.model.Pet;
import pro.sky.petshelterbot.repository.PersonRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
public class PersonService {

  private final PetService petService;
  private final PersonRepository personRepository;

  public PersonService(PetService petService, PersonRepository personRepository) {
    this.petService = petService;
    this.personRepository = personRepository;
  }

  public List<Person> getAll() {
    return personRepository.findAll();
  }

  public Optional<Person> getPersonByChatId(Long chat_id) {
    return personRepository.getPersonByChatId(chat_id);
  }

  public void setPersonIsVolunteerIsTrue(Person person) {
    person.setIsVolunteer(true);
    personRepository.save(person);
  }

  public void setPersonIsVolunteerIsFalse(Person person) {
    person.setIsVolunteer(false);
    personRepository.save(person);
  }

  public List<Person> getVolunteers() {
    return personRepository.getPeopleByIsVolunteerIsTrue();
  }

  public boolean isChatOfVolunteer(Long chatId) {
    return personRepository.getPersonByChatIdAndIsVolunteerIsTrue(chatId) != null;
  }

  public void save(Person person) {
    personRepository.save(person);
  }

  public void addAnAnimalToAPerson(Long chat_id, Long id) {
     Optional<Person> person = getPersonByChatId(chat_id);
     if (person.isPresent()) {
      Pet pet = petService.findAnimalInTheDatabase(id);
      person.get().setPet(pet);
      person.get().setProbationEnd(LocalDate.now().plus(Period.ofMonths(1)));
     } else {
        throw new NotFoundException("Указанный человек отсутствует в базе!");
     }
  }
}
