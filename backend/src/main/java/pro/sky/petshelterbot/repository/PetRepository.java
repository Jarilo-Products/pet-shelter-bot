package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.petshelterbot.model.Pet;

public interface PetRepository extends JpaRepository<Pet, Long>{

    Pet findPetById(Long id);

    @Transactional
    void deletePetById(Long id);
}
