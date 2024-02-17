package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
  }

  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setUsername("testUsername");
    user.setPassword("somePass");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("testUsername");
    user.setPassword("somePass");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.password", is(user.getPassword())))
        .andExpect(jsonPath("$.token", is(user.getToken())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  // Own tests


    //Tests Post for code 409
  @Test
  public void createUser_invalidInput_userAlreadyExists() throws Exception {
      // given
      User user = new User();
      user.setId(1L);
      user.setUsername("testUsername");
      user.setPassword("somePass");
      String today = "08-03-2024";
      user.setBirthday(today);
      user.setToken("1");
      user.setStatus(UserStatus.ONLINE);

      UserPostDTO userPostDTO = new UserPostDTO();
      userPostDTO.setId(1L);
      userPostDTO.setUsername("testUsername");
      userPostDTO.setPassword("somePass");
      userPostDTO.setBirthday(today);
      userPostDTO.setToken("1");
      userPostDTO.setStatus(UserStatus.ONLINE);

      given(userService.createUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder postRequest = post("/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userPostDTO));

      // then
      mockMvc.perform(postRequest)
              .andExpect(status().isConflict());
  }

  //Tests Get for a specific user
    @Test
    public void givenUser_whenGetUsersById_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setPassword("somePassword");
        user.setId(1L);
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.getUserById(1L)).willReturn(user);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    //Tests for when a user is not found (404)

    @Test
    public void givenInvalidUser_whenGetUsersById_thenThrowNotFound() throws Exception {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setPassword("somePassword");
        user.setId(1L);
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.getUserById(0L)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}", 0L)
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isNotFound());

    }



  @Test
  public void updateUser_validInput_userUpdated() throws Exception {
      // given
      User user = new User();
      user.setId(1L);
      user.setUsername("testUsername");
      user.setPassword("somePass");
      String today = "08-03-2024";
      user.setBirthday(today);
      user.setToken("1");
      user.setStatus(UserStatus.ONLINE);

      UserPutDTO userPutDTO = new UserPutDTO();
      String uniqueUsername = "testUsername_" + System.currentTimeMillis();
      userPutDTO.setUsername(uniqueUsername);
      String past = "01-01-2000";
      userPutDTO.setToken("1");
      user.setId(1L);
      userPutDTO.setBirthday(past);

      User convertedUser = DTOMapper.INSTANCE.convertUserPutDTOToEntity(userPutDTO);
      //given(userService.updateUser(Mockito.any())).willReturn();
      given(userService.updateUser(Mockito.any())).willReturn(HttpStatus.NO_CONTENT);

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder putRequest = put("/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(convertedUser));

      // then
      mockMvc.perform(putRequest)
              .andExpect(status().isNoContent());
              // Put requests don't return any content, so leave rest empty
  }

    @Test
    public void updateUser_invalidUserId_notFound() throws Exception {
        // given
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setId(-1L);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        given(userService.updateUser(Mockito.any())).willReturn(HttpStatus.NOT_FOUND);

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateUser_incorrectToken_badRequest() throws Exception {
        User user = new User();
        user.setId(1L);
        // given
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setId(1L);
        userPutDTO.setToken("incorrectToken");

        given(userService.updateUser(Mockito.any())).willReturn(HttpStatus.BAD_REQUEST);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isBadRequest());
    }





  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}