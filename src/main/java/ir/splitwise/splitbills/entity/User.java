package ir.splitwise.splitbills.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {//todo what happen if i want to add someOne is not in app
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//todo AUTO=sequence is better or identity
    private Long id;

    private String username;
    private String password;
    private String email;
    //todo List<String> roles
    //todo profile picture
}
