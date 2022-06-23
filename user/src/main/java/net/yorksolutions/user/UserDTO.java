package net.yorksolutions.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class UserDTO {
    @JsonProperty
    String username;
    @JsonProperty
    Long id;
    @JsonProperty
    String role;

    public UserDTO(String username, String role,Long id) {
        this.username = username;
        this.id = id;
        this.role= role;
    }

    public UserDTO(UserAccount userAccount) {
        this.username = userAccount.getUsername();
        this.id = userAccount.getId();
        this.role = userAccount.getRole();
    }

    public UserDTO() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDTO)) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(getUsername(), userDTO.getUsername()) && Objects.equals(getId(), userDTO.getId()) && Objects.equals(getRole(), userDTO.getRole());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getId(), getRole());
    }
}
