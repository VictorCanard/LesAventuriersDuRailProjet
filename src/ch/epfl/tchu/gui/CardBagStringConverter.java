package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.util.StringConverter;

public class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

    /**
     * Redefines the toString method from StringConverter<SortedBag<Card>>, so that it uses
     * Info's cardNames method.
     *
     * @param bagOfCards : cards to turn into French with commas and separators.
     * @return the French textual representation of the bag of cards parameter.
     */
    @Override
    public String toString(SortedBag<Card> bagOfCards) {
        return Info.cardNames(bagOfCards);
    }

    @Override
    public SortedBag<Card> fromString(String string) {
        throw new UnsupportedOperationException();
    }
}
