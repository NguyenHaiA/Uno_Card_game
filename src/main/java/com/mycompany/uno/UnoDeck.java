/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.uno;

import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;

/**
 *
 * @author COHOTECH.VN
 */
public class UnoDeck {
    private UnoCard[] cards;
    private int cardInDeck;

    public UnoDeck() {
        cards = new UnoCard[108];
        reset(); // Khởi tạo bộ bài khi tạo đối tượng
    }

    public void reset() {
        UnoCard.Color[] colors = UnoCard.Color.values();
        cardInDeck = 0;
        for (int i = 0; i < colors.length - 1; i++) {
            UnoCard.Color color = colors[i];
            cards[cardInDeck++] = new UnoCard(color, UnoCard.Value.getValue(0));
            for (int j = 1; j < 10; j++) {
                cards[cardInDeck++] = new UnoCard(color, UnoCard.Value.getValue(j));
                cards[cardInDeck++] = new UnoCard(color, UnoCard.Value.getValue(j));
            }
            UnoCard.Value[] values = new UnoCard.Value[]{UnoCard.Value.DrawTwo, UnoCard.Value.Reverse, UnoCard.Value.Skip};
            for (UnoCard.Value value : values) {
                cards[cardInDeck++] = new UnoCard(color, value);
                cards[cardInDeck++] = new UnoCard(color, value);
            }
        }
        UnoCard.Value[] values = new UnoCard.Value[]{UnoCard.Value.Wild, UnoCard.Value.Wild_Four};
        for (UnoCard.Value value : values) {
            for (int i = 0; i < 4; i++) {
                cards[cardInDeck++] = new UnoCard(UnoCard.Color.Wild, value);
            }
        }
        shuffle(); // Trộn bài khi khởi tạo
    }

    public void replaceDeckWith(ArrayList<UnoCard> cards) {
        this.cards = cards.toArray(new UnoCard[cards.size()]);
        this.cardInDeck = this.cards.length;
    }

    public boolean isEmpty() {
        return cardInDeck == 0;
    }

    public void shuffle() {
        Random random = new Random();
        for (int i = 0; i < cards.length; i++) {
            int randomValue = i + random.nextInt(cards.length - i);
            UnoCard randomCard = cards[randomValue];
            cards[randomValue] = cards[i];
            cards[i] = randomCard;
        }
    }

    public UnoCard drawCard() throws IllegalArgumentException {
        if (isEmpty()) {
            throw new IllegalArgumentException("Cannot draw a card since there are no cards in the deck");
        }
        return cards[--cardInDeck];
    }

    public ImageIcon drawCardImage() throws IllegalArgumentException {
        if (isEmpty()) {
            throw new IllegalArgumentException("Cannot draw a card since the deck is empty");
        }
        return new ImageIcon(cards[--cardInDeck].toString() + ".png");
    }

    public UnoCard[] drawCard(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Must draw a positive number of cards but tried to draw " + n + " cards");
        }
        if (n > cardInDeck) {
            throw new IllegalArgumentException("Cannot draw " + n + " cards since there are only " + cardInDeck + " cards.");
        }
        UnoCard[] rst = new UnoCard[n];
        for (int i = 0; i < n; i++) {
            rst[i] = cards[--cardInDeck];
        }
        return rst;
    }
}
    
    
    
    
       
    

