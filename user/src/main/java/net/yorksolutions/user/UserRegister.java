package net.yorksolutions.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class UserRegister {
    @JsonProperty
    String username;
    @JsonProperty
    String password;
    @JsonProperty
    String role;
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UserRegister(String username, String password, String role) {
        this.username=username;
        this.password = password;
        this.role= role;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRegister)) return false;
        UserRegister that = (UserRegister) o;
        return Objects.equals(getUsername(), that.getUsername()) && Objects.equals(getPassword(), that.getPassword()) && Objects.equals(getRole(), that.getRole());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getPassword(), getRole());
    }
}
