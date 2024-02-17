package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.Date;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

public class UserPostDTO {

  private Long id;
  private String username;
  private String password;
  private String birthday;
  private UserStatus status;
  private String token;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
}

  public void setPassword(String password) { this.password = password; }

  public String getBirthday() { return birthday; }

  public void setBirthday(String birthday) { this.birthday = birthday; }

  public UserStatus getStatus() {
    return status;
}

  public void setStatus(UserStatus status) { this.status = status; }

  public String getToken() {return token;}
  public void setToken(String token) { this.token = token; }


}
