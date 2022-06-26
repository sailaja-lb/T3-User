package net.yorksolutions.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class UserService {
    private UserAccountRepository repository;
    private HashMap<UUID, Long> tokenMap;
    @Autowired
    public UserService(@NonNull UserAccountRepository repository) {
        this.repository = repository;
        tokenMap  = new HashMap<>();
    }
    public UserService(UserAccountRepository repository,
                       HashMap<UUID, Long> tokenMap) {
        this.repository = repository;
        this.tokenMap = tokenMap;
    }
    public void registerAdmin(UserRegister userRegister) {
        var result = repository.findByUsernameAndPasswordAndRole(userRegister.getUsername(), userRegister.getPassword(), userRegister.getRole());
        if(result.isPresent() || result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
//        if (repository.findByUsername(userRegister.getUsername()).isPresent())
//            throw new ResponseStatusException(HttpStatus.CONFLICT);
//        if(!(userRegister.getRole()).equals("admin"))
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            String userAccountRole =userRegister.getRole();

            //Optional<UserAccount> userAccount = repository.findById(userId);

                if (userAccountRole.equals("Admin")) {
                    repository.save(new UserAccount(userRegister));
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
            }
    public void registerRecruiter(UserRegister userRegister) {
//        if (repository.findByUsernameAndPasswordAndRole(((userRegister.getUsername()).isPresent() ) &&
//                ((userRegister.getPassword()).isPresent
        var result = repository.findByUsernameAndPasswordAndRole(userRegister.getUsername(), userRegister.getPassword(), userRegister.getRole());
        if(result.isPresent() || result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
//        if(!(userRegister.getRole()).equals("admin"))
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        String userAccountRole =userRegister.getRole();

        //Optional<UserAccount> userAccount = repository.findById(userId);

        if (userAccountRole.equals("Recruiter")) {
            repository.save(new UserAccount(userRegister));
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
    public void registerApplicant(UserRegister userRegister) {
//        if (repository.findByUsername(userRegister.getUsername()).isPresent())
        var result = repository.findByUsernameAndPasswordAndRole(userRegister.getUsername(), userRegister.getPassword(), userRegister.getRole());
        if(result.isPresent() || result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
//        if(!(userRegister.getRole()).equals("admin"))
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        String userAccountRole =userRegister.getRole();

        //Optional<UserAccount> userAccount = repository.findById(userId);

        if (userAccountRole.equals("Applicant")) {
            repository.save(new UserAccount(userRegister));
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

public UUID login(String username, String password,String role) {
    var result = repository.findByUsernameAndPasswordAndRole(username, password,role);
    if (result.isEmpty())
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    final UUID token = UUID.randomUUID();
    tokenMap.put(token, result.get().id);
    return token;
}

    public UUID loginAsUser(String username) {
        var result = repository.findByUsername(username);
        if (result.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        final UUID token = UUID.randomUUID();
        tokenMap.put(token, result.get().id);
        return token;
    }
    public List<UserDTO> getAllUsers(UUID token) {
        List<UserDTO> userDataList = new ArrayList<>();
        Long userId = tokenMap.get(token);
        Optional<UserAccount> userAccountId = repository.findById(userId);
        if(userAccountId.isPresent()) {
           // if (userAccountId.get().getRole().equals("Admin")) {
                List<UserAccount> userAccountsList = repository.findAll();
                for (UserAccount userAccount : userAccountsList) {
                    userDataList.add(new UserDTO(userAccount.getUsername(),userAccount.getRole(),userAccount.getId()));
                }
           // }
        }
        return userDataList;
    }
    public void deleteUser(UUID token,Long id) {
        Long userId = tokenMap.get(token);
        Optional<UserAccount> userAccount = repository.findById(userId);
        if (userAccount.isPresent()) {
            if (userAccount.get().getRole().equals("Admin")) {
                Optional<UserAccount> user = repository.findById(id);
                if (user.isPresent()) {
                    repository.deleteById(id);
                }
            }
        }
    }

    public UserDTO updateRole(UUID token,Long id,String role){
        Long userId = tokenMap.get(token);
        Optional<UserAccount> userAccount = repository.findById(userId);
        if (userAccount.get().getRole().equals("Admin")){
            return updateWithRole(id, role);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private UserDTO updateWithRole(Long id, String role) {
        UserAccount existingUser = repository.findById(id).get();
        existingUser.setRole(role);
        var result = repository.findByUsernameAndPasswordAndRole(existingUser.getUsername(), existingUser.getPassword(), existingUser.getRole());
        if(result.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        repository.save(existingUser);
        return new UserDTO(repository.findById(id).get());
    }
}



