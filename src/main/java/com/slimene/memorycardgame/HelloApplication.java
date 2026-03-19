package com.slimene.memorycardgame;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HelloApplication extends Application {

    private Button firstCard = null;
    private String firstValue = null;
    private boolean canClick = true;

    private int moves = 0;
    private int matchedPairs = 0;
    private int seconds = 0;
    private int totalPairs = 4;

    private int bestMoves = Integer.MAX_VALUE;
    private int bestTime = Integer.MAX_VALUE;

    private Timeline timer;

    private Label titleLabel = new Label("Memory Card Game");
    private Label movesLabel = new Label("Moves: 0");
    private Label timeLabel = new Label("Time: 0 s");
    private Label bestMovesLabel = new Label("Best Moves: -");
    private Label bestTimeLabel = new Label("Best Time: -");
    private Label statusLabel = new Label("Find all matching pairs!");
    private Label difficultyLabel = new Label("Difficulty:");
    private ComboBox<String> difficultyBox = new ComboBox<>();
    private Button restartButton = new Button("Restart");

    private BorderPane root = new BorderPane();
    private GridPane grid = new GridPane();

    @Override
    public void start(Stage stage) {
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
        movesLabel.setStyle("-fx-font-size: 16px;");
        timeLabel.setStyle("-fx-font-size: 16px;");
        bestMovesLabel.setStyle("-fx-font-size: 16px;");
        bestTimeLabel.setStyle("-fx-font-size: 16px;");
        statusLabel.setStyle("-fx-font-size: 16px;");
        difficultyLabel.setStyle("-fx-font-size: 16px;");
        restartButton.setStyle("-fx-font-size: 14px; -fx-padding: 8 16 8 16;");
        difficultyBox.setStyle("-fx-font-size: 14px;");
        difficultyBox.setPrefWidth(140);

        difficultyBox.getItems().addAll("Easy", "Medium", "Hard");
        difficultyBox.setValue("Easy");

        VBox topBox = new VBox(
                10,
                titleLabel,
                difficultyLabel,
                difficultyBox,
                movesLabel,
                timeLabel,
                bestMovesLabel,
                bestTimeLabel,
                statusLabel,
                restartButton
        );
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(20));

        grid.setAlignment(Pos.CENTER);

        root.setTop(topBox);
        root.setCenter(grid);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #e6f2ff, #ffffff);");

        restartButton.setOnAction(e -> setupGame());
        difficultyBox.setOnAction(e -> setupGame());

        setupGame();

        Scene scene = new Scene(root, 700, 700);
        stage.setTitle("Memory Card Game");
        stage.setScene(scene);
        stage.show();
    }

    private void setupGame() {
        if (timer != null) {
            timer.stop();
        }

        String difficulty = difficultyBox.getValue();

        int cardSize;
        int fontSize;
        int columns = 4;

        if (difficulty.equals("Easy")) {
            totalPairs = 4;
            cardSize = 120;
            fontSize = 32;
            grid.setHgap(12);
            grid.setVgap(12);
        } else if (difficulty.equals("Medium")) {
            totalPairs = 6;
            cardSize = 100;
            fontSize = 28;
            grid.setHgap(10);
            grid.setVgap(10);
        } else {
            totalPairs = 8;
            cardSize = 85;
            fontSize = 24;
            grid.setHgap(8);
            grid.setVgap(8);
        }

        firstCard = null;
        firstValue = null;
        canClick = true;

        moves = 0;
        matchedPairs = 0;
        seconds = 0;

        movesLabel.setText("Moves: 0");
        timeLabel.setText("Time: 0 s");
        statusLabel.setText("Find all matching pairs!");

        if (bestMoves == Integer.MAX_VALUE) {
            bestMovesLabel.setText("Best Moves: -");
        } else {
            bestMovesLabel.setText("Best Moves: " + bestMoves);
        }

        if (bestTime == Integer.MAX_VALUE) {
            bestTimeLabel.setText("Best Time: -");
        } else {
            bestTimeLabel.setText("Best Time: " + bestTime + " s");
        }

        grid.getChildren().clear();
        grid.setPadding(new Insets(20));

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            seconds++;
            timeLabel.setText("Time: " + seconds + " s");
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();

        List<String> emojiPool = new ArrayList<>();
        emojiPool.add("🐶");
        emojiPool.add("🐱");
        emojiPool.add("🐸");
        emojiPool.add("🦊");
        emojiPool.add("🐼");
        emojiPool.add("🐵");
        emojiPool.add("🦁");
        emojiPool.add("🐰");

        List<String> values = new ArrayList<>();

        for (int i = 0; i < totalPairs; i++) {
            values.add(emojiPool.get(i));
            values.add(emojiPool.get(i));
        }

        Collections.shuffle(values);

        for (int i = 0; i < values.size(); i++) {
            Button card = new Button("?");
            card.setPrefSize(cardSize, cardSize);

            String hiddenStyle =
                    "-fx-font-size: " + fontSize + "px;" +
                            "-fx-font-family: 'Segoe UI Emoji';" +
                            "-fx-background-color: #4da6ff;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 12;";

            String revealedStyle =
                    "-fx-font-size: " + fontSize + "px;" +
                            "-fx-font-family: 'Segoe UI Emoji';" +
                            "-fx-background-color: #ffffff;" +
                            "-fx-text-fill: #333333;" +
                            "-fx-border-color: #4da6ff;" +
                            "-fx-border-width: 2;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-radius: 12;";

            String matchedStyle =
                    "-fx-font-size: " + fontSize + "px;" +
                            "-fx-font-family: 'Segoe UI Emoji';" +
                            "-fx-background-color: #7ed957;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 12;";

            card.setStyle(hiddenStyle);

            String hiddenValue = values.get(i);

            card.setOnAction(e -> {
                if (!canClick || card == firstCard || !card.getText().equals("?")) {
                    return;
                }

                card.setText(hiddenValue);
                card.setStyle(revealedStyle);

                if (firstCard == null) {
                    firstCard = card;
                    firstValue = hiddenValue;
                } else {
                    moves++;
                    movesLabel.setText("Moves: " + moves);
                    canClick = false;

                    if (firstValue.equals(hiddenValue)) {
                        matchedPairs++;
                        statusLabel.setText("Match found!");

                        firstCard.setStyle(matchedStyle);
                        card.setStyle(matchedStyle);

                        firstCard = null;
                        firstValue = null;
                        canClick = true;

                        if (matchedPairs == totalPairs) {
                            timer.stop();
                            canClick = false;

                            if (moves < bestMoves) {
                                bestMoves = moves;
                                bestMovesLabel.setText("Best Moves: " + bestMoves);
                            }

                            if (seconds < bestTime) {
                                bestTime = seconds;
                                bestTimeLabel.setText("Best Time: " + bestTime + " s");
                            }

                            statusLabel.setText("🎉 You won in " + moves + " moves and " + seconds + " s!");
                        }
                    } else {
                        statusLabel.setText("Not a match!");

                        Button secondCard = card;
                        PauseTransition pause = new PauseTransition(Duration.seconds(1));

                        pause.setOnFinished(ev -> {
                            firstCard.setText("?");
                            secondCard.setText("?");

                            firstCard.setStyle(hiddenStyle);
                            secondCard.setStyle(hiddenStyle);

                            firstCard = null;
                            firstValue = null;
                            canClick = true;
                            statusLabel.setText("Try again!");
                        });

                        pause.play();
                    }
                }
            });

            int row = i / columns;
            int col = i % columns;
            grid.add(card, col, row);
        }
    }
}