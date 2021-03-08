package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class Deck<C extends Comparable<C>> {
    private final List<C> DECK_CARDS;

    private Deck(List<C> shuffledCards){
        DECK_CARDS = shuffledCards;
    }

    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){
        List<C> listOfCards = cards.toList();
        Collections.shuffle(listOfCards, rng);

        return new Deck<>(listOfCards);
    }
    public int size(){
        return DECK_CARDS.size();
    }
    public boolean isEmpty(){
        return DECK_CARDS.isEmpty();
    }

    public C topCard(){
        Preconditions.checkArgument(!isEmpty());

        return DECK_CARDS.get(0);
    }

    public Deck<C> withoutTopCard(){
        Preconditions.checkArgument(!isEmpty());

        return new Deck<>(DECK_CARDS.subList(1, size()));

    }
    public SortedBag<C> topCards(int count){
        Preconditions.checkArgument(0<=count && count <=size());

        SortedBag<C> topCards = SortedBag.of(DECK_CARDS.subList(0, count));
        return topCards;
    }

    public Deck<C> withoutTopCards(int count){
        Preconditions.checkArgument(0<=count && count <=size());

        return new Deck<>(DECK_CARDS.subList(count,size()));
    }
}
