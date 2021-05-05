package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.util.StringConverter;

public class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

    @Override
    public String toString(SortedBag<Card> bagOfCards) {
        return Info.cardNames(bagOfCards);
    }

    @Override
    public SortedBag<Card> fromString(String string) {
        throw new UnsupportedOperationException();
    }
}
