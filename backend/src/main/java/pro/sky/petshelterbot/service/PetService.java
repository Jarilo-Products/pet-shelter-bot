package pro.sky.petshelterbot.service;

import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.exceptions.NotFoundException;
import pro.sky.petshelterbot.model.Pet;
import pro.sky.petshelterbot.repository.PetRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PetService {

  private final PetRepository petRepository;

  public PetService(PetRepository petRepository) {
    this.petRepository = petRepository;
  }

  public Optional<Pet> getPetById(Long id) {
    return petRepository.findPetById(id);
  }

  public List<Pet> getAll() {
    return petRepository.findAll();
  }

  public void save(Pet pet) {
    petRepository.save(pet);
  }

  public void delete(Long id) {
    petRepository.deletePetById(id);
  }

  public Pet findAnimalInTheDatabase(Long id) {
    Optional<Pet> pet = petRepository.findPetById(id);
      if (pet.isPresent()) {
        return pet.get();
      } else {
        throw new NotFoundException("Указанное животное отсутствует в базе!");
      }
  }
}