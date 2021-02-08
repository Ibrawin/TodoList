package ibrawin.todolist.datamodel;

import javafx.collections.FXCollections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public enum TodoData {
    INSTANCE;
    private static String fileName = "TodoListItems.txt";
    private List<TodoItem> todoItems;
    private DateTimeFormatter formatter;

    TodoData() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public List<TodoItem> getTodoItems() {
        return todoItems;
    }

    public void loadTodoItems() throws IOException {
        todoItems = FXCollections.observableArrayList();
        Path path = Paths.get(fileName);
        String input;

        try (
                BufferedReader br = Files.newBufferedReader(path)
        ) {
            while ((input = br.readLine()) != null) {
                String[] itemPieces = input.split("\t");

                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String deadlineString = itemPieces[2];

                LocalDate deadline = LocalDate.parse(deadlineString, formatter);

                TodoItem todoItem = new TodoItem(shortDescription, details, deadline);
                todoItems.add(todoItem);
            }
        }
    }

    public void storeTodoItems() throws IOException {
        Path path = Paths.get(fileName);

        try (
                BufferedWriter bw = Files.newBufferedWriter(path)
        ) {
            for (TodoItem item : todoItems) {
                bw.write(String.format("%s\t%s\t%s",
                        item.getShortDescription(),
                        item.getDetails(),
                        item.getDeadline()));
                bw.newLine();
            }
        }
    }
}
