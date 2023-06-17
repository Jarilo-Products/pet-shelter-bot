package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.model.Person;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {

  Person findPersonById(Long id);

  Person getPersonByChatIdAndIsVolunteerIsTrue(Long chatId);

  List<Person> getPeopleByIsVolunteerIsTrue();

}
