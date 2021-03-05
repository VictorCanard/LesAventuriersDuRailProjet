package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.Random;

public final class Deck<C extends Comparable<C>> {
    private Deck(){

    }
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){

    }
    public int size(){

    }
    public boolean isEmpty(){}

    public C topCard(){}
    public Deck<C> withoutTopCard(){

    }
    public SortedBag<C> topCards(int count){}

    public Deck<C> withoutTopCards(int count){}
}
