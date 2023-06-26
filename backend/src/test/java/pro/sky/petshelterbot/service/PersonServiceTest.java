package pro.sky.petshelterbot.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.petshelterbot.exceptions.NotFoundException;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.model.Pet;
import pro.sky.petshelterbot.repository.PersonRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

  private static List<Person> persons;

  @Mock
  private PersonRepository personRepository;
  @Mock
  private PetService petService;

  @InjectMocks
  private PersonService personService;
  private static Person person1;
  private static Pet pet1;

  @BeforeAll
  public static void setUp() {
    person1 = new Person();
    person1.setId(1L);
    pet1 = new Pet();
    pet1.setId(1L);
    person1.setPet(pet1);

    Person person2 = new Person();
    person1.setId(2L);
    persons = new ArrayList<>();
    persons.add(person1);
    persons.add(person2);
  }

  @Test
  public void getAllTest() {
    when(personRepository.findAll()).thenReturn(persons);

    assertEquals(personService.getAll(), persons);
  }

  @Test
  public void getAllVolunteerTest() {
    when(personRepository.getPeopleByIsVolunteerIsTrue()).thenReturn(persons);

    assertEquals(personService.getAllVolunteer(), persons);
  }

  @Test
  public void getPersonByIdTest() {
    Person person = new Person();
    person.setId(1L);

    when(personRepository.findPersonById(1L)).thenReturn(Optional.of(person));
    when(personRepository.findPersonById(2L)).thenReturn(Optional.empty());

    Optional<Person> presentPerson = personService.getPersonById(1L);
    Optional<Person> emptyPerson = personService.getPersonById(2L);

    assertTrue(presentPerson.isPresent());
    assertEquals(presentPerson.get(), person);
    assertTrue(emptyPerson.isEmpty());
  }

  @Test
  public void setPersonIsVolunteerIsTrueTest() {
    Person person = new Person();

    personService.setPersonIsVolunteerIsTrue(person);

    assertTrue(person.getIsVolunteer());
    verify(personRepository).save(person);
  }

  @Test
  public void setPersonIsVolunteerIsFalseTest() {
    Person person = new Person();

    personService.setPersonIsVolunteerIsFalse(person);

    assertFalse(person.getIsVolunteer());
    verify(personRepository).save(person);
  }

  @Test
  public void getVolunteersTest() {
    when(personRepository.getPeopleByIsVolunteerIsTrue()).thenReturn(persons);

    assertEquals(personService.getVolunteers(), persons);
  }

  @Test
  public void isChatOfVolunteerTest() {
    Person person = new Person();
    when(personRepository.getPersonByChatIdAndIsVolunteerIsTrue(1L)).thenReturn(person);
    when(personRepository.getPersonByChatIdAndIsVolunteerIsTrue(2L)).thenReturn(null);

    assertTrue(personService.isChatOfVolunteer(1L));
    assertFalse(personService.isChatOfVolunteer(2L));
  }

  @Test
  public void saveTest() {
    Person person = new Person();

    personService.save(person);

    verify(personRepository).save(person);
  }

  @Test
  void shouldAddAnAnimalToAPerson() {
    when(personService.getPersonByChatId(person1.getChatId())).thenReturn(Optional.of(person1));
    when(petService.findAnimalInTheDatabase(pet1.getId())).thenReturn(pet1);

    personService.addAnAnimalToAPerson(person1.getChatId(), pet1.getId());

    assertEquals(pet1, person1.getPet());
    assertEquals(LocalDate.now().plus(Period.ofMonths(1)), person1.getProbationEnd());
  }

  @Test
  void shouldBeAnExceptionIfThereIsNoPersonInTheDatabase() {
    when(personRepository.getPersonByChatId(person1.getChatId())).thenReturn(Optional.empty());

    NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
            () -> personService.addAnAnimalToAPerson(person1.getChatId(), pet1.getId()));
    Assertions.assertEquals("Указанный человек отсутствует в базе!", exception.getMessage());
  }
}