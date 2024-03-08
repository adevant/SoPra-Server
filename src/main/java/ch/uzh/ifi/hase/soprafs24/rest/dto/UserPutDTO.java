package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.Date;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

public class UserPutDTO {

    private Long id;
    private String username;
    private String birthday;
    private String token;

    // Add any other fields needed for updating user data

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


    public String getBirthday() { return birthday; }

    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getToken() {return token;}
    public void setToken(String token) { this.token = token; }


}