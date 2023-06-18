package pro.sky.petshelterbot.controllers;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:application-test.properties")
class PetControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void addPetTest() throws Exception {
    JSONObject json = new JSONObject();
    json.put("type", "CAT");
    json.put("name", "Barsik");
    json.put("birthdate", "2014-07-13");
    json.put("healthStatus", "HEALTHY");
    json.put("sex", "MALE");

    mockMvc.perform(post("/pets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.status").value("OWNERLESS"));
  }

  @Test
  public void editPetTest() throws Exception {
    JSONObject json = new JSONObject();
    json.put("type", "CAT");
    json.put("name", "Barsik");
    json.put("birthdate", "2014-07-13");
    json.put("healthStatus", "HEALTHY");
    json.put("sex", "MALE");

    mockMvc.perform(put("/pets/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.toString()))
        .andExpect(status().isNotFound());

    mockMvc.perform(post("/pets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.id").value(1));

    json.put("birthdate", "2015-07-13");
    json.put("healthStatus", "HEALTH_RESTRICTIONS");
    json.put("status", "OWNERLESS");
    mockMvc.perform(put("/pets/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.birthdate").value("2015-07-13"))
        .andExpect(jsonPath("$.healthStatus").value("HEALTH_RESTRICTIONS"));
  }

  @Test
  public void getAllPetsTest() throws Exception {
    mockMvc.perform(get("/pets"))
        .andExpect(status().isNoContent());

    JSONObject json = new JSONObject();
    json.put("type", "CAT");
    json.put("name", "Barsik");
    json.put("birthdate", "2014-07-13");
    json.put("healthStatus", "HEALTHY");
    json.put("sex", "MALE");
    mockMvc.perform(post("/pets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.id").value(1));

    mockMvc.perform(get("/pets"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value("1"));
  }

  @Test
  public void getPetTest() throws Exception {
    mockMvc.perform(get("/pets/1"))
        .andExpect(status().isNotFound());

    JSONObject json = new JSONObject();
    json.put("type", "CAT");
    json.put("name", "Barsik");
    json.put("birthdate", "2014-07-13");
    json.put("healthStatus", "HEALTHY");
    json.put("sex", "MALE");
    mockMvc.perform(post("/pets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.id").value(1));

    mockMvc.perform(get("/pets/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("1"));
  }

  @Test
  public void deletePetTest() throws Exception {
    mockMvc.perform(delete("/pets/1"))
        .andExpect(status().isNotFound());

    JSONObject json = new JSONObject();
    json.put("type", "CAT");
    json.put("name", "Barsik");
    json.put("birthdate", "2014-07-13");
    json.put("healthStatus", "HEALTHY");
    json.put("sex", "MALE");
    mockMvc.perform(post("/pets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.id").value(1));

    mockMvc.perform(delete("/pets/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("1"));
  }

}