package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User getUserById(Long userId) {return userRepository.findById(userId).get();}

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    String currentDate = dateFormat.format(new Date());
    newUser.setCreation_date(currentDate);

    newUser.setStatus(UserStatus.ONLINE);
    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToCheck
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToCheck) {
    User userByUsername = userRepository.findByUsername(userToCheck.getUsername());

    String baseErrorMessage = "The %s provided is not unique. Therefore, the request cannot be accepted!";
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          String.format(baseErrorMessage, "username"));
    }
  }

  public User login(User loginUser/*String loginUsername, String loginPassword*/ /*User inputUser*/) {

      User userByUsername = userRepository.findByUsername(loginUser.getUsername());
      if (userByUsername != null && loginUser.getPassword().equals(userByUsername.getPassword()) ) {
          //userByUsername.setStatus;
          userByUsername.setToken(UUID.randomUUID().toString());
          userByUsername.setStatus(UserStatus.ONLINE);
          userByUsername = userRepository.save(userByUsername);
          userRepository.flush();
          return userByUsername;
      }
      else {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                  "Username or Password wrong");
      }
          //return userByUsername;
  }
  public void logout(String tokenOfUser) {
      //throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //      "Token: " + to.getToken());

      User foundUser = userRepository.findByToken(tokenOfUser);
      if(foundUser!=null) {
          foundUser.setStatus(UserStatus.OFFLINE);
          foundUser = userRepository.save(foundUser);
          userRepository.flush();

      } else {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                  "User not found by token");
      }
  }

    public HttpStatus updateUser(User userToBeUpdated) {
        User userByToken = userRepository.findByToken(userToBeUpdated.getToken());

        if (getUserById(userToBeUpdated.getId()) == null) {
            return HttpStatus.NOT_FOUND;
        }

        if (userByToken == null || !userToBeUpdated.getId().equals(userByToken.getId())) {
            return HttpStatus.BAD_REQUEST;
        }

        String newBirthday = userToBeUpdated.getBirthday();
        if (newBirthday != null && !newBirthday.trim().isEmpty()) {
            String DATE_REGEX = "^(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-(\\d{4})$";
            Pattern pattern = Pattern.compile(DATE_REGEX);
            Matcher matcher = pattern.matcher(newBirthday);
            if (!matcher.matches()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid birthday format. Please provide a valid date in the format DD-MM-YYYY");
            }
        }

        String newUsername = userToBeUpdated.getUsername();

        if (newUsername == null || newUsername.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }

        if (newUsername.equals(userByToken.getUsername())) {
            // Update other fields if needed
            userByToken.setBirthday(userToBeUpdated.getBirthday());
            userRepository.save(userByToken);
            userRepository.flush();
            return HttpStatus.NO_CONTENT;
        } else {
            // Check if the new username is already taken
            User existingUser = userRepository.findByUsername(newUsername);
            if (existingUser == null || existingUser.getId().equals(userByToken.getId())) {
                // Update other fields if needed
                userByToken.setUsername(newUsername);
                userByToken.setBirthday(userToBeUpdated.getBirthday());
                userRepository.save(userByToken);
                userRepository.flush();
                return HttpStatus.NO_CONTENT;
            } else {
                return HttpStatus.CONFLICT;
            }
        }

        // Save the updated user

    }

}
