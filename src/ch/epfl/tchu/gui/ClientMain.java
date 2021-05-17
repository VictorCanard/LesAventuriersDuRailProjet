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
        RemotePlayerClient remotePlayerClient1;
        RemotePlayerClient remotePlayerClient2;

        switch (parameters.size()) {
            case 0:
                remotePlayerClient1 = new RemotePlayerClient(new GraphicalPlayerAdapter(), "localhost", 5108);
                remotePlayerClient2 = new RemotePlayerClient(new GraphicalPlayerAdapter(), "localhost", 5108);
                break;
            case 1:
                remotePlayerClient1 = new RemotePlayerClient(new GraphicalPlayerAdapter(), parameters.get(0), 5108);
                remotePlayerClient2 = new RemotePlayerClient(new GraphicalPlayerAdapter(),  parameters.get(0), 5108);
                break;
            default:
                remotePlayerClient1 = new RemotePlayerClient(new GraphicalPlayerAdapter(), parameters.get(0), Integer.parseInt(parameters.get(1)));
                remotePlayerClient2 = new RemotePlayerClient(new GraphicalPlayerAdapter(),  parameters.get(0), Integer.parseInt(parameters.get(1)));
                break;
        }
        new Thread(remotePlayerClient1::run).start();
        new Thread(remotePlayerClient2::run).start();


    }
}
