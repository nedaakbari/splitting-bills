package ir.splitwise.splitbills.entity;

import lombok.Getter;

@Getter
public enum State {
    NEW,
    IN_PROGRESS,
    FINISH//when all member payed
}
