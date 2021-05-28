package ch.epfl.tchu.gui;

/**
 * Class for all the utilities common to multiple classes in the gui package
 * @author : Victor Canard-Duchêne (326913)
 */
class GuiUtils {
    private GuiUtils(){}

    /**
     * Port to be used to connect the client and server
     */
    static final int PORT = 5108;
    /**
     * The css for the colors used in the game
     */
    static final String COLORS = "colors.css";
    /**
     * String corresponding to the filled class attribute in colors.css
     */
    static final String FILLED = "filled";
    /**
     * String corresponding to the locomotive card's color
     */
    static final String NEUTRAL = "NEUTRAL";
}
