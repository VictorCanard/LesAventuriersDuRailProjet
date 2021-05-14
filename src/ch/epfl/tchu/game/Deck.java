package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents a deck of cards of a specified type
 *
 * @param <C> : the type of card. In this project: cards or tickets
 * @author Victor Jean Canard-Duchene (326913)
 */
public final class Deck<C extends Comparable<C>> {
    private final List<C> listOfCards;

    /**
     * Private constructor to attribute the shuffled cards to this
     *
     * @param shuffledCards : cards that should be put in the deck's list of cards
     */
    private Deck(List<C> shuffledCards) {
        listOfCards = List.copyOf(shuffledCards);
    }

    /**
     * Construction method for creating a deck of cards
     *
     * @param cards : the group of cards to be used to form the deck
     * @param rng   : an instance of a random number generator
     * @param <C>   : the specified type of cards
     * @return a randomly shuffled deck of cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> listOfCards = cards.toList();
        Collections.shuffle(listOfCards, rng);

        return new Deck<>(listOfCards);
    }

    /**
     * Getter for the size of a deck
     *
     * @return the size of the deck
     */
    public int size() {
        return listOfCards.size();
    }

    /**
     * Determines if a given deck of cards is empty
     *
     * @return true if the deck has no cards, false otherwise
     */
    public boolean isEmpty() {
        return listOfCards.isEmpty();
    }

    /**
     * Getter for the top card of the deck
     *
     * @return the top card of the deck
     * @throws IllegalArgumentException if the deck is empty
     */
    public C topCard() {
        Preconditions.checkArgument(!isEmpty());

        return listOfCards.get(0);
    }

    /**
     * "Removes" the top card from a given deck
     *
     * @return a new deck without the top card (giving the illusion of removing the top card)
     * @throws IllegalArgumentException if the deck is empty
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!isEmpty());

        return withoutTopCards(1);
    }

    /**
     * Determines the top cards of the given deck
     *
     * @param count : the number of cards to be revealed
     * @return a group of cards from the top of the deck
     * @throws IllegalArgumentException if count is out of bounds of the size of the deck or if it is strictly negative
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(0 <= count && count <= size());

        return SortedBag.of(listOfCards.subList(0, count));
    }

    /**
     * "Removes" a number of cards from the top of the deck
     *
     * @param count : the number of cards to be "removed"
     * @return a new deck with the specified number of cards removed from the top
     * @throws IllegalArgumentException if count is out of bounds of the size of the deck or if it is strictly negative
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(0 <= count && count <= size());

        return new Deck<>(listOfCards.subList(count, size()));
    }
}
