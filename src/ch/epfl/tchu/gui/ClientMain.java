package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * Main program of a tCHu client
 *
 * @author Anne-Marie Rusu (296098)
 */

public class ClientMain extends Application {

    /**
     * Launches the application with the given args
     *
     * @param args : args to pass to the launch method
     */
    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Starts the client with a new Graphical Player Adapter, a default name and a default PORT.
     * If a name or PORT is specified then uses these for the remote player client creation.
     *
     * @param primaryStage : unused parameter
     */
    @Override
    public void start(Stage primaryStage) {
        List<String> parameters = getParameters().getRaw();

        String name = "localhost";
        int port = GuiUtils.PORT;

        switch (parameters.size()) {
            case 2:
                port = Integer.parseInt(parameters.get(1));
            case 1:
                name = parameters.get(0);
        }
        RemotePlayerClient remotePlayerClient = new RemotePlayerClient(new GraphicalPlayerAdapter(), name, port);

        new Thread(remotePlayerClient::run).start();
    }
}
