package pro.sky.petshelterbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.repository.PersonRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> getAll() {
        return personRepository.findAll();
    }

    public List<Person> getAllVolunteer() {
        return personRepository.getPersonByIsVolunteerIsTrue();
    }

    public boolean setPersonIsVolunteerIsTrue(Long id) {
        Person person = personRepository.findPersonById(id);
        if (person == null) {
            return false;
        }
        person.setIsVolunteer(true);
        personRepository.save(person);
        return true;
    }

    public boolean setPersonIsVolunteerIsFalse(Long id) {
        Person person = personRepository.findPersonById(id);
        if (person == null) {
            return false;
        }
        person.setIsVolunteer(false);
        personRepository.save(person);
        return true;
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