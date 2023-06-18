package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.model.Person;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

  Optional<Person> findPersonById(Long id);

  Person getPersonByChatIdAndIsVolunteerIsTrue(Long chatId);

  List<Person> getPeopleByIsVolunteerIsTrue();

}
