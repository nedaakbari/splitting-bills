package ir.splitwise.splitbills.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Getter
@Setter
public class AppUser extends BaseEntity {//todo what happen if i want to add someOne is not in app
    private String username;
    private String password;
    private String email;
    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ShareGroup> groupIds;
    //todo List<String> roles
    //todo profile picture
}
