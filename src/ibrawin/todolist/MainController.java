package ibrawin.todolist;

import ibrawin.todolist.datamodel.TodoData;
import ibrawin.todolist.datamodel.TodoItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MainController {

    @FXML
    private ListView<TodoItem> todoItemListView;

    @FXML
    private TextArea todoItemDetails;

    @FXML
    private Label todoItemDeadline;

    @FXML
    private BorderPane mainView;

    public void initialize() {
        todoItemListView.getSelectionModel().selectedItemProperty().addListener((observableValue, todoItem, t1) -> {
            if (t1 != null) {
                TodoItem item = todoItemListView.getSelectionModel().getSelectedItem();
                todoItemDetails.setText(item.getDetails());
                DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                todoItemDeadline.setText(df.format(item.getDeadline()));
            }
        });

        todoItemListView.setItems(TodoData.INSTANCE.getTodoItems());
        todoItemListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoItemListView.getSelectionModel().selectFirst();
        todoItemListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<TodoItem> call(ListView<TodoItem> todoItemListView) {

                return new ListCell<>() {

                    @Override
                    protected void updateItem(TodoItem todoItem, boolean empty) {
                        super.updateItem(todoItem, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(todoItem.getShortDescription());
                            if (todoItem.getDeadline().isBefore(LocalDate.now())) {
                                setTextFill(Color.RED);
                            } else if (todoItem.getDeadline().isBefore(LocalDate.now().plusDays(1L))) {
                                setTextFill(Color.ORANGE);
                            } else {
                                setTextFill(Color.BLACK);
                            }
                        }
                    }
                };
            }
        });
    }

    public void showNewDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainView.getScene().getWindow());
        dialog.setTitle("New Todo Item");
        dialog.setHeaderText("Use this dialog to create a new todo item!");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("Dialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            TodoItem newItem = controller.processResults();
            todoItemListView.getSelectionModel().select(newItem);
        }
    }
}
