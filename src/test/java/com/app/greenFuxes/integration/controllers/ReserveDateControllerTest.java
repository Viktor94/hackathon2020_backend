package com.app.greenFuxes.integration.controllers;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.app.greenFuxes.dto.DateDTO;
import com.app.greenFuxes.dto.office.CapacityDTO;
import com.app.greenFuxes.dto.office.UsersInOfficeDTO;
import com.app.greenFuxes.dto.user.login.LoginDTO;
import com.app.greenFuxes.dto.user.login.LoginResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import javax.transaction.Transactional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class ReserveDateControllerTest {

  private final ObjectMapper mapper = new ObjectMapper();

  private final MediaType contentType =
      new MediaType(
          MediaType.APPLICATION_JSON.getType(),
          MediaType.APPLICATION_JSON.getSubtype(),
          StandardCharsets.UTF_8);

  private String token;

  @Autowired private MockMvc mockMvc;

  @Before
  public void setup() {
    try {
      MvcResult result =
          mockMvc
              .perform(
                  post("/users/login")
                      .contentType(contentType)
                      .content(mapper.writeValueAsString(new LoginDTO("admin", "admin"))))
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn();

      LoginResponseDTO loginResponseDTO =
          mapper.readValue(result.getResponse().getContentAsString(), LoginResponseDTO.class);
      Assert.assertEquals("Login was successful!", loginResponseDTO.getMsg());
      Assert.assertNotNull(loginResponseDTO.getToken());
      token = loginResponseDTO.getToken();

      mockMvc
          .perform(
              post("/office/create")
                  .contentType(contentType)
                  .header("Authorization", "Bearer " + token))
          .andExpect(status().isOk())
          .andReturn();

      CapacityDTO capacityDTO = new CapacityDTO();
      capacityDTO.setCapacity(1);

      mockMvc
          .perform(
              post("/office-status/set-headcount")
                  .contentType(contentType)
                  .header("Authorization", "Bearer " + token)
                  .content(mapper.writeValueAsString(capacityDTO)))
          .andExpect(status().isOk())
          .andReturn();
    } catch (Exception ignored) {

    }
  }

  @Test
  @WithMockUser(username = "user", password = "user")
  public void assertThatStatusIsOK_reservationSuccessful() throws Exception {
    try {
      DateDTO dateDTO = new DateDTO("30/10/2020");

      mockMvc
          .perform(
              post("/office/reserve")
                  .contentType(contentType)
                  .content(mapper.writeValueAsString(dateDTO)))
          .andExpect(status().isOk())
          .andReturn();

      MvcResult apiResult =
          mockMvc
              .perform(
                  post("/office/check-date")
                      .contentType(contentType)
                      .content(mapper.writeValueAsString(dateDTO)))
              .andExpect(status().isOk())
              .andReturn();

      UsersInOfficeDTO usersInOfficeDTO =
          mapper.readValue(apiResult.getResponse().getContentAsString(), UsersInOfficeDTO.class);

      assertEquals("user", usersInOfficeDTO.getUsersInOffice().get(0).getUserName());
    } catch (Exception e) {

    }
  }

  @Test
  @WithMockUser(username = "user2", password = "user2")
  @Transactional
  public void assertThatStatusIsOK_numberOfPeopleInOfficeIsOne() throws Exception {
    try {
      DateDTO dateDTO = new DateDTO("30/10/2020");

      mockMvc
          .perform(
              post("/office/reserve")
                  .contentType(contentType)
                  .content(mapper.writeValueAsString(dateDTO)))
          .andExpect(status().isOk())
          .andReturn();

      MvcResult apiResult =
          mockMvc
              .perform(
                  post("/office/check-date")
                      .contentType(contentType)
                      .content(mapper.writeValueAsString(dateDTO)))
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn();

      UsersInOfficeDTO usersInOfficeDTO =
          mapper.readValue(apiResult.getResponse().getContentAsString(), UsersInOfficeDTO.class);

      assertEquals(1, usersInOfficeDTO.getUsersInOffice().size());
    } catch (Exception e) {

    }
  }
}
