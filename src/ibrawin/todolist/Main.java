package ibrawin.todolist;

import ibrawin.todolist.datamodel.TodoData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void init() {
        try {
            TodoData.INSTANCE.loadTodoItems();
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        primaryStage.setTitle("Todo List");
        primaryStage.setScene(new Scene(root, 900, 500));
        primaryStage.show();
    }


    @Override
    public void stop() {
        try {
            TodoData.INSTANCE.storeTodoItems();
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
