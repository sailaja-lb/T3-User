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
        if (repository.findByUsername(userRegister.getUsername()).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);
//        if(!(userRegister.getRole()).equals("admin"))
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            String userAccountRole =userRegister.getRole();

            //Optional<UserAccount> userAccount = repository.findById(userId);

                if (userAccountRole.equals("admin")) {
                    repository.save(new UserAccount(userRegister));
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
            }
    public void registerRecruiter(UserRegister userRegister) {
        if (repository.findByUsername(userRegister.getUsername()).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);
//        if(!(userRegister.getRole()).equals("admin"))
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        String userAccountRole =userRegister.getRole();

        //Optional<UserAccount> userAccount = repository.findById(userId);

        if (userAccountRole.equals("recruiter")) {
            repository.save(new UserAccount(userRegister));
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
    public void registerApplicant(UserRegister userRegister) {
        if (repository.findByUsername(userRegister.getUsername()).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);
//        if(!(userRegister.getRole()).equals("admin"))
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        String userAccountRole =userRegister.getRole();

        //Optional<UserAccount> userAccount = repository.findById(userId);

        if (userAccountRole.equals("applicant")) {
            repository.save(new UserAccount(userRegister));
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
    public UUID login(String username, String password) {
        var result = repository.findByUsernameAndPassword(username, password);
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
            if (userAccountId.get().getRole().equals("admin")) {
                List<UserAccount> userAccountsList = repository.findAll();
                for (UserAccount userAccount : userAccountsList) {
                    userDataList.add(new UserDTO(userAccount.getUsername(),userAccount.getRole(),userAccount.getId()));
                }
            }
        }
        return userDataList;
    }
    public void deleteUser(UUID token, Long id) {
        Long userId = tokenMap.get(token);
        Optional<UserAccount> userAccount = repository.findById(userId);
        if (userAccount.isPresent()) {
            if (userAccount.get().getRole().equals("admin")) {

                repository.deleteById(id);
            }

        }
    }

    public UserDTO updateRole(UUID token,Long id,String role){
        Long userId = tokenMap.get(token);
        Optional<UserAccount> userAccount = repository.findById(userId);
        if (userAccount.get().getRole().equals("admin")){
            return updateRole(id, role);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private UserDTO updateRole(Long id, String role) {
        UserAccount existingUser = repository.findById(id).get();
        existingUser.setRole(role);
        repository.save(existingUser);
        return new UserDTO(repository.findById(id).get());
    }
}


