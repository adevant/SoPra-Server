package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
  @Test
  public void testCreateUser_fromUserPostDTO_toUser_success() {
    // create UserPostDTO
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("username");
    userPostDTO.setPassword("password");
    String handIn = "03-08-2024";
    userPostDTO.setBirthday(handIn);

    // MAP -> Create user
    User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // check content
    assertEquals(userPostDTO.getUsername(), user.getUsername());
    assertEquals(userPostDTO.getPassword(), user.getPassword());
    assertEquals(userPostDTO.getBirthday(), user.getBirthday());
  }

  @Test
  public void testGetUser_fromUser_toUserGetDTO_success() {
    // create User
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");

    // MAP -> Create UserGetDTO
    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

    // check content
    assertEquals(user.getId(), userGetDTO.getId());
    assertEquals(user.getUsername(), userGetDTO.getUsername());
    assertEquals(user.getStatus(), userGetDTO.getStatus());
  }

  // Own test
  @Test
  public void testUpdateUser_fromUserPutDTO_success() {
      // create UserPostDTO
      UserPutDTO userPutDTO = new UserPutDTO();
      userPutDTO.setId(1L);
      userPutDTO.setUsername("username");

      String handIn = "08-03-2023";
      userPutDTO.setBirthday(handIn);

      // MAP -> Create user
      User user = DTOMapper.INSTANCE.convertUserPutDTOToEntity(userPutDTO);

      // check content
      assertEquals(userPutDTO.getUsername(), user.getUsername());
      assertEquals(userPutDTO.getBirthday(), user.getBirthday());
      assertEquals(userPutDTO.getId(), user.getId());
      assertEquals(userPutDTO.getToken(), user.getToken());
  }
}
