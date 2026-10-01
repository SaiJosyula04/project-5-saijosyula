import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class ReversiView extends Application implements Observer {

    private ReversiModel model;
    private ReversiController controller;

    private GridPane grid;
    private Label score;

    @Override
    public void start(Stage stage) {
        model = new ReversiModel();          // no load/save in this API
        controller = new ReversiController(model);
        model.addObserver(this);

        BorderPane root = new BorderPane();
        root.setTop(buildMenu());
        root.setCenter(buildBoard());
        score = new Label("");
        BorderPane.setMargin(score, new Insets(8));
        root.setBottom(score);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Reversi");
        stage.show();

        // initial paint
        update(model, model.makeBoard());
    }

    private MenuBar buildMenu() {
        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(e -> {
            model.startGame();  // resets board and notifies observers
        });
        Menu file = new Menu("File");
        file.getItems().addAll(newGame);
        return new MenuBar(file);
    }

    private GridPane buildBoard() {
        grid = new GridPane();
        grid.setPadding(new Insets(8));
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setStyle("-fx-background-color: green;");

        for (int r = 0; r < ReversiBoard.SIZE; r++) {
            for (int c = 0; c < ReversiBoard.SIZE; c++) {
                StackPane cell = new StackPane();
                cell.setBorder(new Border(new BorderStroke(
                        Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

                Circle disc = new Circle(20);
                disc.setFill(Color.TRANSPARENT);
                cell.getChildren().add(disc);

                final int rr = r, cc = c;
                cell.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
                    boolean played = controller.humanTurn(rr, cc);
                    if (played) {
                        // the model already notifies observers inside doMove()
                        // now let the AI play once
                        controller.computerTurn();
                        // AI move will notify observers too
                    }
                    if (controller.gameOver()) {
                        String winner = switch (controller.winner()) {
                            case WHITE -> "White";
                            case BLACK -> "Black";
                            default -> "Tie";
                        };
                        new Alert(Alert.AlertType.INFORMATION,
                                "Game over. Winner: " + winner).showAndWait();
                    }
                });

                grid.add(cell, c, r);
            }
        }
        return grid;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof ReversiBoard b)) return;

        // draw pieces
        for (int r = 0; r < ReversiBoard.SIZE; r++) {
            for (int c = 0; c < ReversiBoard.SIZE; c++) {
                StackPane cell = (StackPane) getNodeFromGridPane(grid, c, r);
                if (cell == null) continue;
                Circle disc = (Circle) cell.getChildren().get(0);
                switch (b.get(r, c)) {
                    case WHITE -> disc.setFill(Color.WHITE);
                    case BLACK -> disc.setFill(Color.BLACK);
                    default -> disc.setFill(Color.TRANSPARENT);
                }
            }
        }

        int w = controller.score(ReversiBoard.Cell.WHITE);
        int k = controller.score(ReversiBoard.Cell.BLACK);
        score.setText("White: " + w + "   Black: " + k);
    }

    private static javafx.scene.Node getNodeFromGridPane(GridPane grid, int col, int row) {
        for (javafx.scene.Node n : grid.getChildren()) {
            Integer c = GridPane.getColumnIndex(n);
            Integer r = GridPane.getRowIndex(n);
            if ((c == null ? 0 : c) == col && (r == null ? 0 : r) == row) return n;
        }
        return null;
    }
}
