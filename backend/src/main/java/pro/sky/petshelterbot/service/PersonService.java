package pro.sky.petshelterbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.repository.PersonRepository;

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

}
