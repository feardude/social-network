package ru.smax.social.network.user;

import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Builder
public record User(
        String username,
        String password,
        String firstName,
        String lastName,
        LocalDate birthday,
        String biography,
        String city,
        String gender
) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.username();
    }

    @Override
    public String getPassword() {
        return this.password();
    }
}
