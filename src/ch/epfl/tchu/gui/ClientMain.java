package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

public class ClientMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){ //did we need to handle any exception here or no?
        List<String> parameters = getParameters().getRaw();
        RemotePlayerClient rpc;
        switch(parameters.size()){
            case 0:
                rpc = new RemotePlayerClient(new GraphicalPlayerAdapter(), "localHost", 5108);
                break;
            case 1:
                rpc = new RemotePlayerClient(new GraphicalPlayerAdapter(),
                        parameters.get(0),
                        5108);
                break;
            default:
                rpc = new RemotePlayerClient(new GraphicalPlayerAdapter(),
                        parameters.get(0),
                        Integer.parseInt(parameters.get(1)));
                break;
        }
        new Thread(rpc::run).start();
    }
}
