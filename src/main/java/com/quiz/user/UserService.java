package com.quiz.user;

import com.quiz.exception.UserFoundException;
import com.quiz.entities.User;
import com.quiz.entities.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    //creating user
    
    public User createUser(User user, Set<UserRole> userRoles) throws Exception {


        User local = this.userRepository.findByUsername(user.getUsername());
        if (local != null) {
            System.out.println("User is already there !!");
            throw new UserFoundException();
        } else {
            //user create
            for (UserRole ur : userRoles) {
                roleRepository.save(ur.getRole());
            }

            user.getUserRoles().addAll(userRoles);
            local = this.userRepository.save(user);

        }

        return local;
    }

    //getting user by username
    
    public User getUser(String username) {
        return this.userRepository.findByUsername(username);
    }

    
    public void deleteUser(Long userId) {
        this.userRepository.deleteById(userId);
    }


}
