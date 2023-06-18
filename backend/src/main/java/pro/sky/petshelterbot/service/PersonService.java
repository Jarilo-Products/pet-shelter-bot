package pro.sky.petshelterbot.service;

import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.repository.PersonRepository;

import java.util.*;

@Service
public class PersonService {

  private final PersonRepository personRepository;

  public PersonService(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  public List<Person> getAll() {
    return personRepository.findAll();
  }

  public List<Person> getAllVolunteer() {
    return personRepository.getPeopleByIsVolunteerIsTrue();
  }

  public Optional<Person> getPersonById(Long id) {
    return personRepository.findPersonById(id);
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

}
