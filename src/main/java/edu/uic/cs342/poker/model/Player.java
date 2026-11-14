package edu.uic.cs342.poker.model;

import java.util.ArrayList;

public class Player {

    private ArrayList<Card> hand;
    private int anteBet;
    private int pairPlusBet;
    private int playBet;
    private int totalWinnings;
    private boolean folded;

    public Player() {
        hand = new ArrayList<>();
        anteBet = 0;
        pairPlusBet = 0;
        playBet = 0;
        totalWinnings = 0;
        folded = false;
    }


    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public int getAnteBet() {
        return anteBet;
    }

    public void setAnteBet(int anteBet) {
        this.anteBet = anteBet;
    }

    public int getPairPlusBet() {
        return pairPlusBet;
    }

    public void setPairPlusBet(int pairPlusBet) {
        this.pairPlusBet = pairPlusBet;
    }

    public int getPlayBet() {
        return playBet;
    }

    public void setPlayBet(int playBet) {
        this.playBet = playBet;
    }


    public boolean isFolded() {
        return folded;
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
    }

    public int getTotalWinnings() {
        return totalWinnings;
    }

    public void addWinnings(int amount) {
        totalWinnings += amount;
    }

    public void resetRound() {
        hand = new ArrayList<>();
        anteBet = 0;
        pairPlusBet = 0;
        playBet = 0;
        folded = false;
    }
}