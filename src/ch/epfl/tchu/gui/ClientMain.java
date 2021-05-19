package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * Main program of a tCHu client
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
     * Starts the client with a new Graphical Player Adapter, a default name and a default port.
     * If a name or port is specified then uses these for the remote player client creation.
     *
     * @param primaryStage : unused parameter
     */
    @Override
    public void start(Stage primaryStage) {
        List<String> parameters = getParameters().getRaw();
        RemotePlayerClient remotePlayerClient;
        String hostname = "localhost";
        int port = 5108;
        switch (parameters.size()) {
            case 0:
                remotePlayerClient = new RemotePlayerClient(new GraphicalPlayerAdapter(), hostname, port);
                break;
            case 1:
                remotePlayerClient = new RemotePlayerClient(new GraphicalPlayerAdapter(),
                        parameters.get(0),
                        port);
                break;
            default:
                remotePlayerClient = new RemotePlayerClient(new GraphicalPlayerAdapter(),
                        parameters.get(0),
                        Integer.parseInt(parameters.get(1)));
                break;
        }
        new Thread(remotePlayerClient::run).start();
    }
}
