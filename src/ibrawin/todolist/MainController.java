package ibrawin.todolist;

import ibrawin.todolist.datamodel.TodoData;
import ibrawin.todolist.datamodel.TodoItem;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class MainController {

    private ContextMenu contextMenu;
    private FilteredList<TodoItem> filteredList;
    private Predicate<TodoItem> allItems;
    private Predicate<TodoItem> existingItems;

    @FXML
    private ListView<TodoItem> todoItemListView;

    @FXML
    private TextArea todoItemDetails;

    @FXML
    private Label todoItemDeadline;

    @FXML
    private BorderPane mainView;

    @FXML
    private ToggleButton filterToggleButton;

    public void initialize() {
        contextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem editMenuItem = new MenuItem("Edit");
        deleteMenuItem.setOnAction(actionEvent -> {
            TodoItem selectedItem = todoItemListView.getSelectionModel().getSelectedItem();
            deleteItem(selectedItem);
        });
        editMenuItem.setOnAction(actionEvent -> {
            todoItemListView.getSelectionModel().getSelectedItem();
            editItem();
        });

        contextMenu.getItems().addAll(deleteMenuItem);
        contextMenu.getItems().addAll(editMenuItem);

        todoItemListView.getSelectionModel().selectedItemProperty().addListener((observableValue, todoItem, t1) -> {
            if (t1 != null) {
                TodoItem item = todoItemListView.getSelectionModel().getSelectedItem();
                todoItemDetails.setText(item.getDetails());
                DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                todoItemDeadline.setText(df.format(item.getDeadline()));
            }
        });

        allItems = todoItem -> true;

        existingItems = todoItem -> todoItem.getDeadline().isAfter(LocalDate.now().minusDays(1L));

        filteredList = new FilteredList<>(TodoData.INSTANCE.getTodoItems(), allItems);

        SortedList<TodoItem> sortedList = new SortedList<>(filteredList, Comparator.comparing(TodoItem::getDeadline));

        todoItemListView.setItems(sortedList);
        todoItemListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoItemListView.getSelectionModel().selectFirst();

        todoItemListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<TodoItem> call(ListView<TodoItem> todoItemListView) {

                ListCell<TodoItem> listCell = new ListCell<>() {

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

                listCell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if(isNowEmpty) {
                                listCell.setContextMenu(null);
                            } else {
                                listCell.setContextMenu(contextMenu);
                            }
                        });
                return listCell;
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

    public void handleKeyPressed(KeyEvent keyEvent) {
        TodoItem selectedItem = todoItemListView.getSelectionModel().getSelectedItem();
        if(selectedItem != null) {
            if(keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(selectedItem);
            }
        }
    }

    public void deleteItem(TodoItem todoItem) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo Item");
        alert.setHeaderText("Delete item: " + todoItem.getShortDescription());
        alert.setContentText("Are you sure you want to delete this Todo Item?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            TodoData.INSTANCE.deleteTodoItem(todoItem);
        }
    }

    public void deleteSelectedItem() {
        TodoItem selectedItem = todoItemListView.getSelectionModel().getSelectedItem();

        deleteItem(selectedItem);
    }

    public void editItem() {
        TodoItem selectedItem = todoItemListView.getSelectionModel().getSelectedItem();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainView.getScene().getWindow());
        dialog.setTitle("Edit Todo Item");
        dialog.setHeaderText("Edit existing Todo Item!");

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
            TodoItem existingItem = controller.editResults(selectedItem);
            todoItemListView.getSelectionModel().clearSelection();
            todoItemListView.getSelectionModel().select(existingItem);
        }
    }

    public void handleFilterButton() {
        TodoItem selectedItem = todoItemListView.getSelectionModel().getSelectedItem();
        if(filterToggleButton.isSelected()) {
            filteredList.setPredicate(existingItems);
            if(filteredList.isEmpty()) {
                todoItemDetails.clear();
                todoItemDeadline.setText(null);
            } else if(filteredList.contains(selectedItem)) {
                todoItemListView.getSelectionModel().select(selectedItem);
            } else {
                todoItemListView.getSelectionModel().selectFirst();
            }
        } else {
            filteredList.setPredicate(allItems);
            todoItemListView.getSelectionModel().select(selectedItem);
        }
    }

    public void handleExit() {
        Platform.exit();
    }
}
