package net.yorksolutions.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    @Spy
    UserService service;
    @Mock
    UserAccountRepository repository;
    @Mock
    HashMap<UUID, Long> tokenMap;
    @Test
    void itShouldReturnUnauthWhenUserIsWrong() {
        final var username = "someUser";
        final var password = "somePass";
        final var role ="some role";
        lenient().when(repository.findByUsernameAndPasswordAndRole(username, password,role))
                .thenReturn(Optional.empty());
        lenient().when(repository.findByUsernameAndPasswordAndRole(not(eq(username)), eq(password),eq(role)))
                .thenReturn(Optional.of(new UserAccount()));
        assertThrows(ResponseStatusException.class, () -> service.login(username, password,role));
    }
    @Test
    void itShouldReturnUnauthWhenPassIsWrong() {
        final var username = "user";
        final var password = "wrongPass";
        final var role = "Some role";
        lenient().when(repository.findByUsernameAndPasswordAndRole(username, password,role))
                .thenReturn(Optional.empty());
        lenient().when(repository.findByUsernameAndPasswordAndRole(eq(username), not(eq(password)),eq(role)))
                .thenReturn(Optional.of(new UserAccount()));
        assertThrows(ResponseStatusException.class, () -> service.login(username, password,role));
    }
    @Test
    void itShouldReturnUnauthWhenRoleIsWrong(){
        final var username = "someUser";
        final var password = "somePass";
        final var role ="wrong role";
        lenient().when(repository.findByUsernameAndPasswordAndRole(username, password,role))
                .thenReturn(Optional.empty());
        lenient().when(repository.findByUsernameAndPasswordAndRole(eq(username), eq(password),not(eq(role))))
                .thenReturn(Optional.of(new UserAccount()));
        assertThrows(ResponseStatusException.class, () -> service.login(username, password,role));
    }
    @Test
    void itShouldMapTheUUIDToTheIdWhenLoginSuccess() {
        final var username = "some user";
        final var password = "some pass";
        final var role = "some role";
        final Long id = (long) (Math.random() * 9999999); // the id of the user account associated with username, password
        final UserAccount expected = new UserAccount();
        expected.id = id;
        expected.username = username;
        expected.password = password;
        expected.role = role;
        when(repository.findByUsernameAndPasswordAndRole(username, password,role))
                .thenReturn(Optional.of(expected));
        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        when(tokenMap.put(captor.capture(), eq(id))).thenReturn(0L);
        final var token = service.login(username, password,role);
        assertEquals(token, captor.getValue());
    }
    @Test
    void itShouldSaveANewUserAccountWithAdminWhenUserIsUnique() {

        final String username = "some username";
        final String password = "some password";
        final String role = "admin";
        UserRegister userRegister = new UserRegister(username,password,role);
        when(repository.findByUsername(username)).thenReturn(Optional.empty());
        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        when(repository.save(captor.capture())).thenReturn(new UserAccount(userRegister));
        Assertions.assertDoesNotThrow(() -> service.registerAdmin(userRegister));
        assertEquals(new UserAccount(userRegister), captor.getValue());
    }
    @Test
    void itShouldSaveANewUserAccountWithRecruiterWhenUserIsUnique() {

        final String username = "some username";
        final String password = "some password";
        final String role = "recruiter";
        UserRegister userRegister = new UserRegister(username,password,role);
        when(repository.findByUsername(username)).thenReturn(Optional.empty());
        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        when(repository.save(captor.capture())).thenReturn(new UserAccount(userRegister));
        Assertions.assertDoesNotThrow(() -> service.registerRecruiter(userRegister));
        assertEquals(new UserAccount(userRegister), captor.getValue());
    }
    @Test
    void itShouldSaveANewUserAccountWithApplicantWhenUserIsUnique() {

        final String username = "some username";
        final String password = "some password";
        final String role = "applicant";
        UserRegister userRegister = new UserRegister(username,password,role);
        when(repository.findByUsername(username)).thenReturn(Optional.empty());
        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        when(repository.save(captor.capture())).thenReturn(new UserAccount(userRegister));
        Assertions.assertDoesNotThrow(() -> service.registerApplicant(userRegister));
        assertEquals(new UserAccount(userRegister), captor.getValue());
    }
    @Test
    void itShouldReturnInvalidIfAdminUsernameExists() {
        final String username = "some username";
        UserRegister userRegister = new UserRegister(username,"","");
        when(repository.findByUsername(username)).thenReturn(Optional.of(
                new UserAccount()));
        assertThrows(ResponseStatusException.class, () -> service.registerAdmin(userRegister));
    }
    @Test
    void itShouldReturnInvalidIfRecruiterUsernameExists() {
        final String username = "some username";
        UserRegister userRegister = new UserRegister(username,"","");
        when(repository.findByUsername(username)).thenReturn(Optional.of(
                new UserAccount()));
        assertThrows(ResponseStatusException.class, () -> service.registerRecruiter(userRegister));
    }
    @Test
    void itShouldReturnInvalidIfApplicantUsernameExists() {
        final String username = "some username";
        UserRegister userRegister = new UserRegister(username,"","");
        when(repository.findByUsername(username)).thenReturn(Optional.of(
                new UserAccount()));
        assertThrows(ResponseStatusException.class, () -> service.registerApplicant(userRegister));
    }
    @Test
    void itShouldMapTheUUIDToTheIdWhenImpersonateLoginSuccess() {
        final var username = "some user";
        final Long id = (long) (Math.random() * 9999999); // the id of the user account associated with username, password
        final UserAccount expected = new UserAccount();
        expected.id = id;
        expected.username = username;
        when(repository.findByUsername(username))
                .thenReturn(Optional.of(expected));
        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        when(tokenMap.put(captor.capture(), eq(id))).thenReturn(0L);
        final var token = service.loginAsUser(username);
        assertEquals(token, captor.getValue());
    }
    @Test
    void itShouldReturnAllUsersWhenGetAllUsersCalled(){
        List<UserDTO> userDataList = new ArrayList<>();
        userDataList.add(new UserDTO("someUser","admin",1L));
    }

}
