package net.yorksolutions.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService service;
    @Autowired
    public UserController(@NonNull UserService service){
        this.service = service;
    }

    @PostMapping("/registerAdmin")
    @CrossOrigin
    public void registerAdmin(@RequestBody UserRegister userRegister){
        service.registerAdmin(userRegister);
    }
    @PostMapping("/registerRecruiter")
    @CrossOrigin
    public void registerRecruiter(@RequestBody UserRegister userRegister){
        service.registerRecruiter(userRegister);
    }
    @PostMapping("/registerApplicant")
    @CrossOrigin
    public void registerApplicant(@RequestBody UserRegister userRegister){
        service.registerApplicant(userRegister);
    }
    @GetMapping("/login")
    @CrossOrigin
    public UUID login(@RequestParam String username, @RequestParam String password,@RequestParam String role){
        return service.login(username,password,role);
    }
    @GetMapping("/impersonate")
    @CrossOrigin
    public Map<String, String> loginAsUser(@RequestParam String username){
        Map<String, String> value= new HashMap<>();
        UUID uuid = service.loginAsUser(username);
        value.put("token", uuid.toString());
        return  value;
    }
    @GetMapping("/getAll")
    @CrossOrigin
    public List<UserDTO> getAllUsers(@RequestParam UUID token){
        return service.getAllUsers(token);
    }
    @PutMapping("/editUser")
    @CrossOrigin
    public UserDTO updateRole(@RequestParam UUID token,@RequestParam Long id,@RequestParam String role){
        return service.updateRole(token,id,role);
    }
    @DeleteMapping("/deleteUser")
    @CrossOrigin
    public void deleteUser(@RequestParam UUID token){
        service.deleteUser(token);
    }
    public void setService(UserService service) {

        this.service = service;
    }
}
