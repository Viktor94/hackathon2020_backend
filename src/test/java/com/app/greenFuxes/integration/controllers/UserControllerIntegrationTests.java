package com.app.greenFuxes.integration.controllers;

import com.app.greenFuxes.dto.http.HttpResponse;
import com.app.greenFuxes.dto.picture.PictureDTO;
import com.app.greenFuxes.dto.user.login.LoginDTO;
import com.app.greenFuxes.dto.user.login.LoginResponseDTO;
import com.app.greenFuxes.dto.user.registration.RegistrationDTO;
import com.app.greenFuxes.entity.user.ConfirmationToken;
import com.app.greenFuxes.entity.user.User;
import com.app.greenFuxes.service.confirmationtoken.ConfirmationTokenService;
import com.app.greenFuxes.service.email.EmailSenderService;
import com.app.greenFuxes.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserControllerIntegrationTests {

  private ObjectMapper objectMapper;

  @Autowired private WebApplicationContext context;

  @Mock private EmailSenderService emailSenderService;

  @Autowired private UserService userService;

  @Autowired private ConfirmationTokenService confirmationTokenService;

  private MockMvc mockMvc;

  private RegistrationDTO asdRegistrationDTO;

  private MediaType mediaType;

  private String token;

  @Before
  public void before() {
    mediaType =
        new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

    objectMapper = new ObjectMapper();
    asdRegistrationDTO = new RegistrationDTO("asd", "asd", "asd");
  }

  @Test
  public void register_successful_assertEquals() throws Exception {
    try {
      Mockito.doNothing().when(emailSenderService).sendQueueNotificationEmail(any(), any());
      mockMvc
          .perform(
              post("/users/register")
                  .contentType(mediaType)
                  .content(objectMapper.writeValueAsString(asdRegistrationDTO)))
          .andExpect(status().isCreated())
          .andDo(print());
    } catch (Exception e) {
    }
  }

  @Test
  public void activate_successful_assertEquals() throws Exception {
    register_successful_assertEquals();
    User user = userService.findByUsername(asdRegistrationDTO.getUserName());
    ConfirmationToken confToken = confirmationTokenService.findByUser(user);

    MvcResult result =
        mockMvc
            .perform(post("/users/confirm?token=" + confToken.getConfirmationToken()))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

    HttpResponse httpResponse =
        objectMapper.readValue(result.getResponse().getContentAsString(), HttpResponse.class);
    Assert.assertEquals("ACTIVATION WAS SUCCESSFUL!", httpResponse.getMessage());
  }

  @Test
  public void login_successful_assertEquals() throws Exception {
    activate_successful_assertEquals();
    try {
      MvcResult result =
          mockMvc
              .perform(
                  post("/users/login")
                      .contentType(mediaType)
                      .content(
                          objectMapper.writeValueAsString(
                              new LoginDTO(
                                  asdRegistrationDTO.getUserName(),
                                  asdRegistrationDTO.getPassword()))))
              .andExpect(status().isOk())
              .andDo(print())
              .andReturn();

      LoginResponseDTO loginResponseDTO =
          objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponseDTO.class);
      Assert.assertEquals("Login was successful!", loginResponseDTO.getMsg());
      Assert.assertNotNull(loginResponseDTO.getToken());
      token = loginResponseDTO.getToken();
    } catch (Exception e) {
    }
  }

  @Test
  @WithMockUser
  public void getProfileImg_successful_assertEquals() throws Exception {
    register_successful_assertEquals();
    User user = userService.findByUsername(asdRegistrationDTO.getUserName());

    MvcResult result =
        mockMvc
            .perform(get("/users/" + user.getId() + "/profile/image"))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

    PictureDTO pictureDTO =
        objectMapper.readValue(result.getResponse().getContentAsString(), PictureDTO.class);
    Assert.assertEquals(user.getProfileImageUrl(), pictureDTO.getUrl());
  }

  @Test
  @WithMockUser(authorities = "admin")
  public void getUserById_successful_assertEquals() throws Exception {
    register_successful_assertEquals();
    User user = userService.findByUsername(asdRegistrationDTO.getUserName());
    MvcResult result =
        mockMvc
            .perform(get("/users/" + user.getId()))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

    User responseUser =
        objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
    Assert.assertEquals(responseUser.getProfileImageUrl(), user.getProfileImageUrl());
  }
}
