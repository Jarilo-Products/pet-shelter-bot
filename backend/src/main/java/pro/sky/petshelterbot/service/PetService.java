package pro.sky.petshelterbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.model.Pet;
import pro.sky.petshelterbot.repository.PetRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PetService {

   private final PetRepository petRepository;

    @Autowired
    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Optional<Pet> getPetById(Long id) {
        Pet pet = petRepository.findPetById(id);
        return Optional.ofNullable(pet);
    }

    public List<Pet> getAll(){
        return petRepository.findAll();
    }

    public void save(Pet pet) {
        petRepository.save(pet);
    }

    public void delete(Long id){
        petRepository.deletePetById(id);
    }
}
