package com.elice.boardgame.auth.service;

import com.elice.boardgame.auth.dto.JoinDTO;
import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.post.service.S3Uploader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class JoinService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final S3Uploader s3Uploader; // S3Uploader 주입

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, S3Uploader s3Uploader) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.s3Uploader = s3Uploader;
    }

    public void joinProcess(JoinDTO joinDTO) {
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();
        Integer age = joinDTO.getAge();
        String phonenumber = joinDTO.getPhonenumber();
        String name = joinDTO.getName();
        MultipartFile profileImage = joinDTO.getProfileImage(); // 이미지 파일 받기

        // 사용자 이름 및 비밀번호 유효성 검사
        validateUsername(username);
        validatePassword(password);

        Boolean isExist = userRepository.existsByUsername(username);

        if (isExist) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setRole("ROLE_USER");

        if (age != null) {
            user.setAge(age);
        }

        if (phonenumber != null) {
            user.setPhonenumber(phonenumber);
        }

        if (name != null) {
            user.setName(name);
        }

        // 이미지 업로드 처리
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                String imageUrl = s3Uploader.uploadFile(profileImage);
                user.setProfileImageUrl(imageUrl); // User 클래스에 profileImageUrl 필드가 필요함
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
            }
        }

        System.out.println("sage : " + joinDTO.getAge());
        System.out.println("sphone : " + joinDTO.getPhonenumber());
        System.out.println("sname :" + joinDTO.getName());
        System.out.println("surl : " + joinDTO.getProfileImage());
        userRepository.save(user);
    }

    private void validateUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("아이디를 입력해야 합니다.");
        }

        if (username.length() < 6) {
            throw new IllegalArgumentException("아이디는 6자리 이상이어야 합니다.");
        }
    }

    private void validatePassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("비밀번호를 입력해야 합니다.");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자리 이상이어야 합니다.");
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }
        }

        int count = 0;
        if (hasUpperCase) count++;
        if (hasLowerCase) count++;
        if (hasDigit) count++;
        if (hasSpecialChar) count++;

        if (count < 2) {
            throw new IllegalArgumentException("비밀번호는 대문자, 소문자, 숫자, 특수기호 중 2개 이상의 종류를 포함해야 합니다.");
        }
    }
}