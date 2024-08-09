package com.elice.boardgame.auth.service;

import com.elice.boardgame.auth.dto.JoinDTO;
import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinDTO joinDTO) {

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();
        Integer age = joinDTO.getAge();
        String phonenumber = joinDTO.getPhonenumber();
        String location = joinDTO.getLocation();
        String detail_location = joinDTO.getDetailLocation();
        Integer post_code = joinDTO.getPost_code();
        String name = joinDTO.getName();


        Boolean isExist = userRepository.existsByUsername(username);

        if (isExist) {
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setRole("ROLE_ADMIN");

        if (age != null) {
            user.setAge(age);
        }

        if (phonenumber != null) {
            user.setPhonenumber(phonenumber);
        }

        if (location != null) {
            user.setLocation(location);
        }

        if (detail_location != null) {
            user.setDetail_location(detail_location);
        }

        if (post_code != null) {
            user.setPost_code(post_code);
        }

        if (name != null) {
            user.setName(name);
        }

        userRepository.save(user);
    }
}