package com.slimene.memorycardgame;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameController {

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

    @FXML private Label titleLabel;
    @FXML private Label movesLabel;
    @FXML private Label timeLabel;
    @FXML private Label bestMovesLabel;
    @FXML private Label bestTimeLabel;
    @FXML private Label statusLabel;

    @FXML private ComboBox<String> difficultyBox;
    @FXML private Button restartButton;

    @FXML private GridPane grid;

    @FXML
    public void initialize() {
        difficultyBox.getItems().addAll("Easy", "Medium", "Hard");
        difficultyBox.setValue("Easy");

        restartButton.setOnAction(e -> setupGame());
        difficultyBox.setOnAction(e -> setupGame());

        setupGame();
    }

    private void setupGame() {
        if (timer != null) timer.stop();

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

        bestMovesLabel.setText(bestMoves == Integer.MAX_VALUE ? "Best Moves: -" : "Best Moves: " + bestMoves);
        bestTimeLabel.setText(bestTime == Integer.MAX_VALUE ? "Best Time: -" : "Best Time: " + bestTime + " s");

        grid.getChildren().clear();
        grid.setPadding(new Insets(20));

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            seconds++;
            timeLabel.setText("Time: " + seconds + " s");
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();

        List<String> emojiPool = List.of("🐶","🐱","🐸","🦊","🐼","🐵","🦁","🐰");

        List<String> values = new ArrayList<>();
        for (int i = 0; i < totalPairs; i++) {
            values.add(emojiPool.get(i));
            values.add(emojiPool.get(i));
        }

        Collections.shuffle(values);

        for (int i = 0; i < values.size(); i++) {
            Button card = new Button("?");
            card.setPrefSize(cardSize, cardSize);
            card.getStyleClass().add("card");
            card.setStyle("-fx-font-size: " + fontSize + "px; -fx-font-family: 'Segoe UI Emoji';");

            String hiddenValue = values.get(i);

            card.setOnAction(e -> handleCardClick(card, hiddenValue));

            int row = i / columns;
            int col = i % columns;
            grid.add(card, col, row);
        }
    }

    private void handleCardClick(Button card, String value) {
        if (!canClick || card == firstCard || !card.getText().equals("?")) return;

        card.setText(value);

        if (firstCard == null) {
            firstCard = card;
            firstValue = value;
        } else {
            moves++;
            movesLabel.setText("Moves: " + moves);
            canClick = false;

            if (firstValue.equals(value)) {
                matchedPairs++;
                statusLabel.setText("Match found!");

                card.getStyleClass().add("matched");
                firstCard.getStyleClass().add("matched");

                firstCard = null;
                firstValue = null;
                canClick = true;

                if (matchedPairs == totalPairs) {
                    timer.stop();
                    statusLabel.setText("🎉 You won in " + moves + " moves and " + seconds + " s!");

                    if (moves < bestMoves) bestMoves = moves;
                    if (seconds < bestTime) bestTime = seconds;
                }

            } else {
                statusLabel.setText("Not a match!");

                Button secondCard = card;

                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(ev -> {
                    firstCard.setText("?");
                    secondCard.setText("?");

                    firstCard = null;
                    firstValue = null;
                    canClick = true;

                    statusLabel.setText("Try again!");
                });
                pause.play();
            }
        }
    }
}