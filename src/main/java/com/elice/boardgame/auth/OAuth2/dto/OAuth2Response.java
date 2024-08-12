package com.elice.boardgame.auth.OAuth2.dto;

public interface OAuth2Response {

    //제공자 (Ex. naver, google, ...)
    String getProvider();

    //제공자에서 발급해주는 번호
    String getProviderId();

    //이메일
    String getEmail();

    //사용자 실명 (설정한 이름)
    String getName();
}
