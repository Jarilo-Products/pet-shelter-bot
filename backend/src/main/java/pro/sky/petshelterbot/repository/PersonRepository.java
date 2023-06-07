package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.petshelterbot.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {



}
