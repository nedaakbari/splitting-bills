package ir.splitwise.splitbills.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemRequest {
    private String name;
    private double cost;
    private List<UserItem> userItems;
}


