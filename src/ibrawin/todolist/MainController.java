package ibrawin.todolist;

import ibrawin.todolist.datamodel.TodoData;
import ibrawin.todolist.datamodel.TodoItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;

import java.time.format.DateTimeFormatter;

public class MainController {

    @FXML
    private ListView<TodoItem> todoItemListView;

    @FXML
    private TextArea todoItemDetails;

    @FXML
    private Label todoItemDeadline;

    public void initialize() {
        todoItemListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
            @Override
            public void changed(ObservableValue<? extends TodoItem> observableValue, TodoItem todoItem, TodoItem t1) {
                if(t1 != null) {
                    TodoItem item = todoItemListView.getSelectionModel().getSelectedItem();
                    todoItemDetails.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                    todoItemDeadline.setText(df.format(item.getDeadline()));
                }
            }
        });

        todoItemListView.getItems().setAll(TodoData.INSTANCE.getTodoItems());
        todoItemListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoItemListView.getSelectionModel().selectFirst();
    }
}
