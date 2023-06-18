package pro.sky.petshelterbot.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.petshelterbot.model.Pet;
import pro.sky.petshelterbot.repository.PetRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

  private static List<Pet> pets;

  @Mock
  private PetRepository petRepository;

  @InjectMocks
  private PetService petService;

  @BeforeAll
  public static void initPets() {
    Pet pet1 = new Pet();
    pet1.setId(1L);
    Pet pet2 = new Pet();
    pet2.setId(2L);
    pets = new ArrayList<>();
    pets.add(pet1);
    pets.add(pet2);
  }

  @Test
  void getPetByIdTest() {
    Pet pet = new Pet();
    when(petRepository.findPetById(1L)).thenReturn(Optional.of(pet));
    when(petRepository.findPetById(2L)).thenReturn(Optional.empty());

    assertTrue(petService.getPetById(1L).isPresent());
    assertFalse(petService.getPetById(2L).isPresent());
  }

  @Test
  void getAllTest() {
    when(petRepository.findAll()).thenReturn(pets);

    assertEquals(petService.getAll(), pets);
  }

  @Test
  void saveTest() {
    Pet pet3 = new Pet();
    petService.save(pet3);

    verify(petRepository, times(1)).save(pet3);
  }

  @Test
  void deleteTest() {
    petService.delete(1L);

    verify(petRepository, times(1)).deletePetById(1L);
  }
}
