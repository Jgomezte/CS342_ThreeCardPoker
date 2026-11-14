package edu.uic.cs342.poker.model;

public enum Suit {
    CLUBS(1), DIAMONDS(2), HEARTS(3), SPADES(4);

    private final int value;

    Suit(int value) {
        this.value = value;
    }

    public int getValue(){
        return  value;
    }

}
