module ru.nsu.zenin.snake {
    requires javafx.controls;
    requires javafx.fxml;

    exports ru.nsu.zenin.snake.app;
    exports ru.nsu.zenin.snake.view to
            javafx.fxml;

    opens ru.nsu.zenin.snake.view to
            javafx.fxml;
}
