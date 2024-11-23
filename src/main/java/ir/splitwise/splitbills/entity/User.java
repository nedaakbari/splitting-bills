package ir.splitwise.splitbills.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User extends BaseEntity {//todo what happen if i want to add someOne is not in app
    private String username;
    private String password;
    private String email;
    //todo List<String> roles
    //todo profile picture
}
