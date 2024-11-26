package ir.splitwise.splitbills.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AppUser extends BaseEntity {//todo what happen if i want to add someOne is not in app
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
    public AppUser(String firstname, String lastName,String email,
                   String password, Role role) {
        this.firstname = firstname;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
