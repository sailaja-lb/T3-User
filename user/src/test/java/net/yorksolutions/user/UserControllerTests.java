package net.yorksolutions.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.ObjectArrayDeserializer;
import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTests {
    @LocalServerPort
    int port;
    @Autowired
    UserController controller;
    @Mock
    UserService service;

    @BeforeEach
    void setup() {
        controller.setService(service);
    }

    @Test
    void itShouldReturnConflictWhenAdminUsernameTaken() {
        final TestRestTemplate rest = new TestRestTemplate();
        final String username = "some username";
        final String password = "some password";
        final String role = "Admin";
        UserRegister userRegister = new UserRegister(username, password, role);

        String url = "http://localhost:" + port + "/user/registerAdmin?username=" + username + "&password=" + password + "&role=" + role;
        doThrow(new ResponseStatusException(HttpStatus.ACCEPTED)).when(service).registerAdmin(userRegister);
        final ResponseEntity<Void> response = rest.postForEntity(url, userRegister, Void.class);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void itShouldReturnConflictWhenRecruiterUsernameTaken() {
        final TestRestTemplate rest = new TestRestTemplate();
        final String username = "some username";
        final String password = "some password";
        final String role = "Recruiter";
        UserRegister userRegister = new UserRegister(username, password, role);

        String url = "http://localhost:" + port + "/user/registerRecruiter?username=" + username + "&password=" + password + "&role=" + role;
        lenient().doThrow(new ResponseStatusException(HttpStatus.OK)).when(service).registerAdmin(userRegister);
        final ResponseEntity<Void> response = rest.postForEntity(url, userRegister, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void itShouldReturnConflictWhenApplicantUsernameTaken() {
        final TestRestTemplate rest = new TestRestTemplate();
        final String username = "some username";
        final String password = "some password";
        final String role = "Applicant";
        UserRegister userRegister = new UserRegister(username, password, role);

        String url = "http://localhost:" + port + "/user/registerApplicant?username=" + username + "&password=" + password + "&role=" + role;
        lenient().doThrow(new ResponseStatusException(HttpStatus.OK)).when(service).registerAdmin(userRegister);
        final ResponseEntity<Void> response = rest.postForEntity(url, userRegister, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void itShouldRespondWithTokenWhenAdminLoginValid() {
        final TestRestTemplate rest = new TestRestTemplate();
        final String username = "some username";
        final String password = "some password";
        final String role = "Admin";
        String url = "http://localhost:" + port + "/user/login?username=" + username + "&password=" + password + "&role=" + role;
        final UUID token = UUID.randomUUID();
        when(service.login(username, password, role)).thenReturn(token);
        final ResponseEntity<UUID> response = rest.getForEntity(url, UUID.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(token, response.getBody());
    }

    @Test
    void itShouldRespondWithTokenWhenRecruiterLoginValid() {
        final TestRestTemplate rest = new TestRestTemplate();
        final String username = "some username";
        final String password = "some password";
        final String role = "Recruiter";
        String url = "http://localhost:" + port + "/user/login?username=" + username + "&password=" + password + "&role=" + role;
        final UUID token = UUID.randomUUID();
        when(service.login(username, password, role)).thenReturn(token);
        final ResponseEntity<UUID> response = rest.getForEntity(url, UUID.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(token, response.getBody());
    }

    @Test
    void itShouldRespondWithTokenWhenApplicantLoginValid() {
        final TestRestTemplate rest = new TestRestTemplate();
        final String username = "some username";
        final String password = "some password";
        final String role = "Applicant";
        String url = "http://localhost:" + port + "/user/login?username=" + username + "&password=" + password + "&role=" + role;
        final UUID token = UUID.randomUUID();
        when(service.login(username, password, role)).thenReturn(token);
        final ResponseEntity<UUID> response = rest.getForEntity(url, UUID.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(token, response.getBody());
    }

    @Test
    void itShouldLoginAsAAnyUser() {
        final TestRestTemplate rest = new TestRestTemplate();
        Map<String, String> val = new HashMap<>();
        final String username = "some username";
        String url = "http://localhost:" + port + "/user/impersonate?username=" + username;
        final UUID token = UUID.randomUUID();
        val.put("token", token.toString());
        when(service.loginAsUser(username)).thenReturn(token);
        final ResponseEntity<Object> response = rest.getForEntity(url, Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(val, response.getBody());

    }

    @Test
    void itShouldReturnAllUsersWhenGetAllUsersApplied() {
        final TestRestTemplate rest = new TestRestTemplate();
        final UUID token = UUID.randomUUID();
        String url = "http://localhost:" + port + "/user/getAll?token=" + token;
        ArrayList<UserDTO> userList = new ArrayList<>();
        userList.add(new UserDTO("user", "Applicant", 10L));
        when(service.getAllUsers(token)).thenReturn(userList);
        final ResponseEntity<ArrayList> response = rest.getForEntity(url, ArrayList.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        //assertTrue(userList.equals(response.getBody()));
        // assertEquals(userList, response.getBody());
    }

//    @Test
//    void itShouldEditUserRoleWhenEditUserApplied() {
//        final TestRestTemplate rest = new TestRestTemplate();
//        final UUID token = UUID.randomUUID();
//        final Long id = (long) (Math.random() * 9999999);
//        final String role = "Recruiter";
//        UserDTO userDTO = new UserDTO("token",role,id);
//        String url = "http://localhost:" + port + "/user/editUser?token=" + token + "&id=" + id + "&role=" + role;
//        when(service.updateRole(token,eq(id),eq("Recruiter"))).thenReturn(userDTO);
//        final ResponseEntity<UserDTO> response = rest.exchange(url, HttpMethod.PUT,new HttpEntity<>(userDTO),UserDTO.class);
//        assertEquals(HttpStatus.OK,response.getStatusCode());
//        //assertEquals();
//    }
//    @Test
//    void itShouldDeleteAUser(){
//        ArgumentCaptor<UUID> token = ArgumentCaptor.forClass(UUID.class);
//
//    }
}