package pro.sky.petshelterbot.repository;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.PatchMapping;
import pro.sky.petshelterbot.model.Person;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {

    List<Person> getPersonByIsVolunteerIsTrue();

    Person findPersonById(Long id);

}
