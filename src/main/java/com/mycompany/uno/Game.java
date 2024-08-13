/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.uno;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author COHOTECH.VN
 */
public class Game {

    private int currentPlayer;
    private String[] playerIds;
    
    private UnoDeck deck;
    private ArrayList<ArrayList<UnoCard>> playerHand;
    private ArrayList<UnoCard> stockpile;
     
    private UnoCard.Color validColor;
    private UnoCard.Value validValue;
     
    boolean gameDirection;
     
    public Game(String[] pids) {
        deck = new UnoDeck();
        deck.shuffle();
        stockpile = new ArrayList<>();
        
        playerIds = pids;
        currentPlayer = 0;
        gameDirection = false;
        
        playerHand = new ArrayList<>();
        
        for (int i = 0; i < pids.length; i++) {
            ArrayList<UnoCard> hand = new ArrayList<>(Arrays.asList(deck.drawCard(7)));
            playerHand.add(hand);
        }
    }
    
    public void start(Game game) {
        UnoCard card = deck.drawCard();
        validColor = card.getColor();
        validValue = card.getValue();
        
        if (card.getValue() == UnoCard.Value.Wild_Four || card.getValue() == UnoCard.Value.DrawTwo) {
            start(game);
        }
        
        if (card.getValue() == UnoCard.Value.Skip) {
            JLabel message = new JLabel(playerIds[currentPlayer] + " was Skipped!");
            message.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message);
            
            if (!gameDirection) {
                currentPlayer = (currentPlayer + 1) % playerIds.length;
            } else {
                currentPlayer = (currentPlayer - 1 + playerIds.length) % playerIds.length; // Adjusted to ensure positive index
            }
        }
        
        if (card.getValue() == UnoCard.Value.Reverse) {
            JLabel message = new JLabel(playerIds[currentPlayer] + " The game direction changed!");
            message.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message);
            gameDirection = !gameDirection; // Changed from bitwise XOR to logical NOT
            currentPlayer = (currentPlayer + playerIds.length - 1) % playerIds.length; // Adjusted to ensure positive index
        }
        stockpile.add(card);
    }
    
    public UnoCard getTopCard() {
        return new UnoCard(validColor, validValue);
    }
    
    public ImageIcon getTopCardImage() {
        return new ImageIcon(validColor + "_" + validValue + ".png");
    }
    
    public boolean isGameOver() {
        for (String player : this.playerIds) {
            if (hasEmptyHand(player)) {
                return true;
            }
        }
        return false;
    }
    
    public String getCurrentPlayer() {
        return this.playerIds[this.currentPlayer];
    }
    
    public String getPreviousPlayer(int i) { // FIXED: Renamed to `getPreviousPlayer` from `getPreviousPlayre`
        int index = (this.currentPlayer - i + playerIds.length) % playerIds.length; // Adjusted to ensure positive index
        return this.playerIds[index];
    }
    
    public String[] getPlayers() {
        return playerIds;
    }
    
    public ArrayList<UnoCard> getPlayerHand(String pid) {
        int index = Arrays.asList(playerIds).indexOf(pid);
        return playerHand.get(index);
    }
    
    public int getPlayerHandSize(String pid) {
        return getPlayerHand(pid).size();
    }
    
    public UnoCard getPlayerCard(String pid, int choice) {
        ArrayList<UnoCard> hand = getPlayerHand(pid);
        return hand.get(choice);
    }
    
    public boolean hasEmptyHand(String pid) {
        return getPlayerHand(pid).isEmpty();
    }
    
    public boolean validCardPlay(UnoCard card) {
        return card.getColor() == validColor || card.getValue() == validValue || card.getColor() == UnoCard.Color.Wild;
    }
    
    public void checkPlayerTurn(String pid) throws InvalidPlayerTurnException {
        if (!this.playerIds[this.currentPlayer].equals(pid)) { // FIXED: Comparison should be using equals
            throw new InvalidPlayerTurnException("It is not " + pid + "'s turn", pid); // FIXED: Added space
        }
    }
    
    public void submitDraw(String pid) throws InvalidPlayerTurnException {
        checkPlayerTurn(pid);
        if (deck.isEmpty()) {
            deck.replaceDeckWith(stockpile);
            deck.shuffle();
        }
        getPlayerHand(pid).add(deck.drawCard());
        if (!gameDirection) {
            currentPlayer = (currentPlayer + 1) % playerIds.length;
        } else {
            currentPlayer = (currentPlayer - 1 + playerIds.length) % playerIds.length; // Adjusted to ensure positive index
        }
    }
    
    public void setCardColor(UnoCard.Color color) {
        validColor = color;
    }
    
    public void submitPlayerCard(String pid, UnoCard card, UnoCard.Color declaredColor)
        throws InvalidColorSubmissionException, InvalidValueSubmissionException, InvalidPlayerTurnException {
        checkPlayerTurn(pid);
        ArrayList<UnoCard> pHand = getPlayerHand(pid);
        
        if (!validCardPlay(card)) {
            if (card.getColor() == UnoCard.Color.Wild) {
                validColor = card.getColor();
                validValue = card.getValue();
            } else if (card.getColor() != validColor) {
                JLabel message = new JLabel("Invalid player move, expected color: " + validColor + " but got color " + card.getColor());
                message.setFont(new Font("Arial", Font.BOLD, 48)); // FIXED: Changed "arial" to "Arial" to match other messages
                JOptionPane.showMessageDialog(null, message);
                throw new InvalidColorSubmissionException("Invalid player move, expected color: " + validColor + " but got color " + card.getColor(), card.getColor(), validColor); // FIXED: Corrected exception instantiation
            } else if (card.getValue() != validValue) { // FIXED: Corrected method call to getValue()
                JLabel message2 = new JLabel("Invalid player move, expected value: " + validValue + " but got value " + card.getValue());
                message2.setFont(new Font("Arial", Font.BOLD, 48)); // FIXED: Changed "arial" to "Arial" to match other messages
                JOptionPane.showMessageDialog(null, message2);
                throw new InvalidValueSubmissionException("Invalid player move, expected value: " + validValue + " but got value " + card.getValue(), card.getValue(), validValue); // FIXED: Corrected exception instantiation
            }
        }
        
        pHand.remove(card);
        if (hasEmptyHand(this.playerIds[currentPlayer])) {
            JLabel message2 = new JLabel(this.playerIds[currentPlayer] + " won the game! Thank you for playing"); // FIXED: Corrected spelling
            message2.setFont(new Font("Arial", Font.BOLD, 48)); // FIXED: Changed "arial" to "Arial" to match other messages
            JOptionPane.showMessageDialog(null, message2);
            System.exit(0);
        }
        
        validColor = card.getColor();
        validValue = card.getValue();
        stockpile.add(card);
        
        if (!gameDirection) {
            currentPlayer = (currentPlayer + 1) % playerIds.length;
        } else {
            currentPlayer = (currentPlayer - 1 + playerIds.length) % playerIds.length; // Adjusted to ensure positive index
        }
        
        if (card.getColor() == UnoCard.Color.Wild) {
            validColor = declaredColor;
        }
        
        if (card.getValue() == UnoCard.Value.DrawTwo) {
            pid = playerIds[currentPlayer];
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            JLabel message = new JLabel(pid + " drew 2 cards!");
            message.setFont(new Font("Arial", Font.BOLD, 48)); // FIXED: Added this line to set font for consistency
            JOptionPane.showMessageDialog(null, message);
        }
        
        if (card.getValue() == UnoCard.Value.Wild_Four) {
            pid = playerIds[currentPlayer];
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            JLabel message = new JLabel(pid + " drew 4 cards!");
            message.setFont(new Font("Arial", Font.BOLD, 48)); // FIXED: Added this line to set font for consistency
            JOptionPane.showMessageDialog(null, message);
        }
        
        if (card.getValue() == UnoCard.Value.Skip) {
            JLabel message = new JLabel(playerIds[currentPlayer] + " was Skipped!"); // FIXED: Corrected spelling
            message.setFont(new Font("Arial", Font.BOLD, 48)); // FIXED: Corrected method call to setFont
            JOptionPane.showMessageDialog(null, message);
            if (!gameDirection) {
                currentPlayer = (currentPlayer + 1) % playerIds.length;
            } else {
                currentPlayer = (currentPlayer - 1 + playerIds.length) % playerIds.length; // Adjusted to ensure positive index
            }
        }
        
        if (card.getValue() == UnoCard.Value.Reverse) {
            JLabel message = new JLabel(playerIds[currentPlayer] + " changed the game direction!");
            message.setFont(new Font("Arial", Font.BOLD, 48)); // FIXED: Corrected method call to setFont
            JOptionPane.showMessageDialog(null, message);
            
            gameDirection = !gameDirection; // Changed from bitwise XOR to logical NOT
            if (gameDirection) {
                currentPlayer = (currentPlayer - 2 + playerIds.length) % playerIds.length; // Adjusted to ensure positive index
            } else {
                currentPlayer = (currentPlayer + 2) % playerIds.length; // FIXED: Added missing semicolon
            }
        }
    }
    
    class InvalidPlayerTurnException extends Exception{
        String playerId;
        public InvalidPlayerTurnException(String message, String pid){
            super(message);
             playerId = pid;
        }
        public String getPid(){
        return playerId;
        }
    }
    
    class InvalidColorSubmissionException extends Exception{
        private UnoCard.Color expected;
        private UnoCard.Color actual;
        public InvalidColorSubmissionException(String message,UnoCard.Color actual, UnoCard.Color expected){
        this.actual = actual;
        this.expected = expected;
        }
    }
    class InvalidValueSubmissionException extends Exception{
        private UnoCard.Value expected;
        private UnoCard.Value actual;
        public InvalidValueSubmissionException(String message,UnoCard.Value actual, UnoCard.Value expected){
        this.actual = actual;
        this.expected = expected;
        }
    }
}

