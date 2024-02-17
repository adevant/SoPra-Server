package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {

        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;

  }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUser(@PathVariable(name="userId") String userIdAsStr) { //changed here from () <-------------------- (@RequestBody String... params

        Long userId;
        if(userIdAsStr == null) {
            userId = null;
        } else {
            userId = Long.parseLong(userIdAsStr, 10);
        }


        User userById = new User();
        try {
            userById = userService.getUserById(userId);
        } catch(NoSuchElementException e) {
            userById = null;
        }

        if(userById != null) {
            return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userById);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No User with the ID:  " + userId);
        }

    }


  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
      // convert API user to internal representation
      User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

      // create user
      User createdUser = userService.createUser(userInput);
      // convert internal representation of user back to API
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

    @PostMapping("/users/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO loginUser(@RequestBody UserPostDTO userPostDTO) {

    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userService.login(userInput));
    }

    @PostMapping("/users/logout")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void logoutUser(@RequestBody UserPostDTO userPostDTO) {

        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        String userToken = userInput.getToken();
        userService.logout(userToken);

    }

    @PutMapping("/users")
    @ResponseStatus
    @ResponseBody
    public ResponseEntity<Void> updateUser(@RequestBody UserPutDTO userPutDTO) {
        try {
            // convert API user to internal representation
            User userInput = DTOMapper.INSTANCE.convertUserPutDTOToEntity(userPutDTO);

            // update user and get the HTTP status code
            HttpStatus httpStatus = userService.updateUser(userInput);

            // return ResponseEntity based on the HTTP status code
            return ResponseEntity.status(httpStatus).build();
        } catch (ResponseStatusException e) {
            // handle other exceptions if needed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
