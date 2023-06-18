package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.petshelterbot.model.Pet;

import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

  Optional<Pet> findPetById(Long id);

  @Transactional
  void deletePetById(Long id);

}
