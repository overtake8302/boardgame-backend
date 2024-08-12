//package com.elice.boardgame.auth.service;
//
//import com.elice.boardgame.auth.dto.CustomOAuth2User;
//import com.elice.boardgame.auth.dto.NaverResponse;
//import com.elice.boardgame.auth.dto.OAuth2Response;
//import com.elice.boardgame.auth.dto.UserDTO;
//import com.elice.boardgame.auth.entity.UserEntity;
//import com.elice.boardgame.auth.repository.UserEntityRepository;
//import com.elice.boardgame.auth.repository.UserRepository;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//@Service
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {
//
//    private final UserEntityRepository userEntityRepository;
//
//    public CustomOAuth2UserService(UserEntityRepository userEntityRepository) {
//
//        this.userEntityRepository = userEntityRepository;
//    }
//
//    //리소스 서버에서 유저 정보를 받아오는 함수
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//        System.out.println(oAuth2User);
//
//        String registrationId = userRequest.getClientRegistration().getRegistrationId();
//        OAuth2Response oAuth2Response = null;
//        if (registrationId.equals("naver")) {
//
//            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
//        }
////        else if (registrationId.equals("google")) {
////
////            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
////        }
//
//        else {
//
//            return null;
//        }
//        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
//        UserEntity existData = userEntityRepository.findByUsername(username);
//
//        if (existData == null) {
//
//            UserEntity userEntity = new UserEntity();
//            userEntity.setUsername(username);
//            userEntity.setEmail(oAuth2Response.getEmail());
//            userEntity.setName(oAuth2Response.getName());
//            userEntity.setRole("ROLE_USER");
//
//            userEntityRepository.save(userEntity);
//
//            UserDTO userDTO = new UserDTO();
//            userDTO.setUsername(username);
//            userDTO.setName(oAuth2Response.getName());
//            userDTO.setRole("ROLE_USER");
//
//            return new CustomOAuth2User(userDTO);
//        }
//        else {
//
//            existData.setEmail(oAuth2Response.getEmail());
//            existData.setName(oAuth2Response.getName());
//
//            userEntityRepository.save(existData);
//
//            UserDTO userDTO = new UserDTO();
//            userDTO.setUsername(existData.getUsername());
//            userDTO.setName(oAuth2Response.getName());
//            userDTO.setRole(existData.getRole());
//
//            return new CustomOAuth2User(userDTO);
//        }
//    }
//}
