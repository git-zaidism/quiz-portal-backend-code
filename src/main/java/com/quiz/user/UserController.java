package com.quiz.user;

import com.quiz.exception.UserFoundException;
import com.quiz.entities.Role;
import com.quiz.entities.User;
import com.quiz.entities.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  // creating user
  @PostMapping("/")
  public User createUser(@RequestBody User user) throws Exception {

    user.setProfile("default.png");
    // encoding password with bcryptpasswordencoder

    user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));

    Set<UserRole> roles = new HashSet<>();

    Role role = new Role();
    role.setRoleId(45L);
    role.setRoleName("NORMAL");

    UserRole userRole = new UserRole();
    userRole.setUser(user);
    userRole.setRole(role);

    roles.add(userRole);
    return this.userService.createUser(user, roles);
  }

  @GetMapping("/{username}")
  public User getUser(@PathVariable("username") String username) {
    return this.userService.getUser(username);
  }

  // delete the user by id
  @DeleteMapping("/{userId}")
  public void deleteUser(@PathVariable("userId") Long userId) {
    this.userService.deleteUser(userId);
  }

  // update api

  @ExceptionHandler(UserFoundException.class)
  public ResponseEntity<?> exceptionHandler(UserFoundException ex) {
    return ResponseEntity.ok(ex.getMessage());
  }
}
