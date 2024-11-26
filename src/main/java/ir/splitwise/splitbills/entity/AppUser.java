package ir.splitwise.splitbills.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AppUser extends BaseEntity implements UserDetails {//todo what happen if i want to add someOne is not in app
    private String firstname;
    private String lastName;
    private String email;
    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
//    @ToString.Exclude
    private List<ShareGroup> groupIds;
    @OneToMany(mappedBy = "appUser")
//    @ToString.Exclude
    private List<Expense> expenses;

    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    //todo profile picture

    @Builder
    public AppUser(String firstname, String lastName, String email,
                   String password, Role role) {
        this.firstname = firstname;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
//        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
//        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
//        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return true;
//        return UserDetails.super.isEnabled();
    }
}
