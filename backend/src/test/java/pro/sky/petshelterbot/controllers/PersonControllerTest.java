package pro.sky.petshelterbot.controllers;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import pro.sky.petshelterbot.model.Person;
import pro.sky.petshelterbot.model.Pet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:application-test.properties")
class PersonControllerTest {

  @Autowired
  private MockMvc mockMvc;
  private static Person person;
  private static Pet pet;

  @BeforeEach
  void setUp() {
    person = new Person();
    person.setChatId(1L);
    pet = new Pet();
    pet.setId(1L);
  }

  @Test
  public void getAllPersonTest() throws Exception {
    mockMvc.perform(get("/persons"))
        .andExpect(status().isNoContent());

    JSONObject json = new JSONObject();
    json.put("chatId", "100");
    json.put("firstName", "Ivan");
    json.put("lastName", "Ivanov");
    json.put("birthdate", "2000-01-01");
    json.put("isVolunteer", "false");
    mockMvc.perform(post("/persons")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.id").value(1));

    mockMvc.perform(get("/persons"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].chatId").value("100"));
  }

  @Test
  public void getAllVolunteerTest() throws Exception {
    mockMvc.perform(get("/persons/volunteers"))
        .andExpect(status().isNoContent());

    JSONObject json = new JSONObject();
    json.put("chatId", "100");
    json.put("firstName", "Ivan");
    json.put("lastName", "Ivanov");
    json.put("birthdate", "2000-01-01");
    json.put("isVolunteer", "false");
    mockMvc.perform(post("/persons")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.id").value(1));

    json = new JSONObject();
    json.put("chatId", "200");
    json.put("firstName", "Volonter");
    json.put("lastName", "Volonterov");
    json.put("birthdate", "2002-01-01");
    json.put("isVolunteer", "true");
    mockMvc.perform(post("/persons")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.id").value(2));

    mockMvc.perform(get("/persons/volunteers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].chatId").value("200"));
  }

  @Test
  public void changeIsVolunteerIsTrue() throws Exception {
    mockMvc.perform(patch("/persons/volunteers/1"))
        .andExpect(status().isNotFound());

    JSONObject json = new JSONObject();
    json.put("chatId", "100");
    json.put("firstName", "Ivan");
    json.put("lastName", "Ivanov");
    json.put("birthdate", "2000-01-01");
    json.put("isVolunteer", "false");
    mockMvc.perform(post("/persons")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.id").value(1));

    mockMvc.perform(patch("/persons/volunteers/1"))
        .andExpect(status().isOk());
  }

  @Test
  public void changeIsVolunteerIsFalse() throws Exception {
    mockMvc.perform(patch("/persons/volunteers/1/revoke"))
        .andExpect(status().isNotFound());

    JSONObject json = new JSONObject();
    json.put("chatId", "100");
    json.put("firstName", "Ivan");
    json.put("lastName", "Ivanov");
    json.put("birthdate", "2000-01-01");
    json.put("isVolunteer", "false");
    mockMvc.perform(post("/persons")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.id").value(1));

    mockMvc.perform(patch("/persons/volunteers/1/revoke"))
        .andExpect(status().isOk());
  }

  @Test
  void shouldAssignTheAnimalToThePerson() throws Exception {
    mockMvc.perform(patch("/persons/assignAnimal/{chat_id}/{id}",
            person.getChatId(), pet.getId()))
            .andExpect(status().isOk());
  }
}