package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A deck of cards of a specified type
 * @author Victor Jean Canard-Duchene (326913)
 * @param <C> : the type of card. In this project: card/locomotive or tickets
 */
public final class Deck<C extends Comparable<C>> {
    private final List<C> DECK_CARDS;

    private Deck(List<C> shuffledCards){
        DECK_CARDS = shuffledCards;
    }

    /**
     *Construction method for creating a deck of cards
     * @param cards : the group of cards to be used to form the deck
     * @param rng : an instance of a random number generator
     * @param <C> : the specified type of cards
     * @return a randomly shuffled deck of cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){
        List<C> listOfCards = cards.toList();
        Collections.shuffle(listOfCards, rng);

        return new Deck<>(listOfCards);
    }

    /**
     *Getter for the size of a deck
     * @return the size of the deck
     */
    public int size(){
        return DECK_CARDS.size();
    }

    /**
     *Determines if a given deck of cards is empty
     * @return true if the deck has no cards, false otherwise
     */
    public boolean isEmpty(){
        return DECK_CARDS.isEmpty();
    }

    /**
     * Getter for the top card of the deck
     * @throws IllegalArgumentException if the deck is empty
     * @return the top card of the deck
     */
    public C topCard(){
        Preconditions.checkArgument(!isEmpty());
        return DECK_CARDS.get(0);
    }

    /**
     *"Removes" the top card from a given deck
     * @throws IllegalArgumentException if the deck is empty
     * @return a new deck without the top card (giving the illusion of removing the top card)
     */
    public Deck<C> withoutTopCard(){
        Preconditions.checkArgument(!isEmpty());

        return new Deck<>(DECK_CARDS.subList(1, size()));
    }

    /**
     *Determines the top cards of the given deck
     * @param count : the number of cards to be revealed
     * @throws IllegalArgumentException if count is out of bounds of the size of the deck
     * @return a group of cards from the top of the deck
     */
    public SortedBag<C> topCards(int count){
        Preconditions.checkArgument(0<=count && count <=size());

        SortedBag<C> topCards = SortedBag.of(DECK_CARDS.subList(0, count));
        return topCards;
    }

    /**
     *"Removes" a number of cards from the top of the deck
     * @param count : the number of cards to be "removed"
     * @throws IllegalArgumentException if count is out of bounds of the size of the deck
     * @return a new deck with the specified number of cards removed from the top
     */
    public Deck<C> withoutTopCards(int count){
        Preconditions.checkArgument(0<=count && count <=size());

        return new Deck<>(DECK_CARDS.subList(count,size()));
    }
}
