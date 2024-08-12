package com.elice.boardgame.auth.dto;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;


//customuserDetailsService에서 사용할 dto
public class CustomUserDetails implements UserDetails {

    private final User user;
    public CustomUserDetails(User user){
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

//    public Integer getAge(){ return user.getAge(); }
//
//    public String getPhoneNumber() { return user.getPhonenumber(); }
//
//    public String getLocation() { return user.getLocation(); }
//
//    public String getDetailLocation() { return user.getDetail_location(); }
//
//    public Integer getPost_code() { return user.getPost_code(); }
//
//    public String getName(){ return user.getName(); }


}
