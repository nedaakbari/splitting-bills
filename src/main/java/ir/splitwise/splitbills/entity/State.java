package ir.splitwise.splitbills.entity;

import lombok.Getter;

@Getter
public enum State {
    NEW,
    DELETE,
    IN_PROGRESS,
    FINISH//when all member payed
}
