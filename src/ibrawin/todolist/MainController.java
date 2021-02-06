package ibrawin.todolist;

import ibrawin.todolist.datamodel.TodoItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    private List<TodoItem> todoItems;

    @FXML
    private ListView<TodoItem> todoItemListView;

    @FXML
    private TextArea todoItemDetails;

    @FXML
    private Label todoItemDeadline;

    public void initialize() {
        TodoItem item1 = new TodoItem("Mail birthday Card", "Buy a 35th birthday card for Lola",
                LocalDate.of(2021, Month.FEBRUARY, 5));
        TodoItem item2 = new TodoItem("See physio", "For posture and leg shape",
                LocalDate.of(2021, Month.AUGUST, 17));
        TodoItem item3 = new TodoItem("Finish project", "Complete potential payout project by the end of the quarter",
                LocalDate.of(2021, Month.MARCH, 23));
        TodoItem item4 = new TodoItem("Expected freedom", "Travel, Have fun or do anything when corona cases have cleared up",
                LocalDate.of(2021, Month.DECEMBER, 31));
        TodoItem item5 = new TodoItem("Meet Iz", "As soon as lockdown ends",
                LocalDate.of(2021, Month.MARCH, 15));

        todoItems = new ArrayList<>();
        todoItems.add(item1);
        todoItems.add(item2);
        todoItems.add(item3);
        todoItems.add(item4);
        todoItems.add(item5);

        todoItemListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
            @Override
            public void changed(ObservableValue<? extends TodoItem> observableValue, TodoItem todoItem, TodoItem t1) {
                if(t1 != null) {
                    TodoItem item = todoItemListView.getSelectionModel().getSelectedItem();
                    todoItemDetails.setText(item.getDetails());
                    todoItemDeadline.setText(item.getDeadline().toString());
                }
            }
        });

        todoItemListView.getItems().setAll(todoItems);
        todoItemListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoItemListView.getSelectionModel().selectFirst();
    }
}
