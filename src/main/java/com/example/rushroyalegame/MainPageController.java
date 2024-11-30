package com.example.rushroyalegame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainPageController {
    final int CELL_SIZE = 50;
    final int ROW = 5;
    final int COL = 6;
    final int CARD_LIST_SIZE = 4;
    final String LOG_FILE_PATH = "/media/fatima/Fatima/Term7/AP/midProj/RushRoyaleGame/src/main/java/com/example/rushroyalegame/log.txt";
    final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    GridPane mainBoard;
    @FXML
    GridPane cardsBox;
    @FXML
    AnchorPane anchorPane;

    List<Enemy> enemyList = new ArrayList<>();
    List<Enemy> currentEnemies = new ArrayList<>();
    List<Card> cardsList = new ArrayList<>();
    List<Card> currentCards = new ArrayList<>();
    Card selectedCard;
    int enemyRespawnTime = 5;

    double clickedX, clickedY;

    @FXML
    public void initialize() throws Exception {
        mainBoard = new GridPane();
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                Pane cell = new Pane();
                cell.setOnMouseClicked(event -> {
                });
                cell.setPrefSize(CELL_SIZE, CELL_SIZE);
                if (j == 0 || j == ROW || i == 0)
                    cell.setStyle("-fx-background-radius: 15; -fx-background-color: #FFCCCB");
                else
                    cell.setStyle("-fx-background-radius: 15; -fx-background-color: lightblue");
                mainBoard.add(cell, j, i);
            }
        }
        mainBoard.setLayoutX(70);
        mainBoard.setLayoutY(50);
        cardsBox = new GridPane();
        for (int i = 0; i < CARD_LIST_SIZE; i++) {
            Pane cell = new Pane();
            cell.setPrefSize(CELL_SIZE, CELL_SIZE);
            cell.setStyle("-fx-background-radius: 15; -fx-background-color: lightgreen");
            cardsBox.add(cell, i, 0);
        }
        cardsBox.setLayoutX(120);
        cardsBox.setLayoutY(310);
        anchorPane.getChildren().add(cardsBox);
        anchorPane.getChildren().add(mainBoard);

        String jsonContent = new String(Files.readAllBytes(Paths.get("/media/fatima/Fatima/Term7/AP/midProj/RushRoyaleGame/src/main/java/com/example/rushroyalegame/EnemiesInfo.json")));
        JSONArray enemiesInfo = new JSONArray(jsonContent);
        for (int i = 0; i < enemiesInfo.length(); i++) {
            JSONObject enemyInfo = new JSONObject();
            enemyInfo = enemiesInfo.getJSONObject(i);
            Enemy enemy = new Enemy(enemyInfo.getInt("id"), enemyInfo.getString("path"));
            enemyList.add(enemy);
        }

        jsonContent = new String(Files.readAllBytes(Paths.get("/media/fatima/Fatima/Term7/AP/midProj/RushRoyaleGame/src/main/java/com/example/rushroyalegame/CardsInfo.json")));
        JSONArray cardsInfo = new JSONArray(jsonContent);
        for (int i = 0; i < cardsInfo.length(); i++) {
            JSONObject cardInfo = new JSONObject();
            cardInfo = cardsInfo.getJSONObject(i);
            Card card = new Card(cardInfo.getInt("id"), cardInfo.getString("path"));
            cardsList.add(card);
        }
        for (int i = 0; i < CARD_LIST_SIZE; i++) {
            Card randomCard = getRandomCard();
            currentCards.add(randomCard);
            cardsList.remove(randomCard);
        }
        for (int i = 0; i < currentCards.size(); i++) {
            updateCell("cardsBox", 0, i);
        }
        log("Game Started");

        anchorPane.setOnMouseClicked(event -> {
            checkClick(event.getX(), event.getY());
        });

        Timeline enemyTimeline = new Timeline(new KeyFrame(Duration.seconds(enemyRespawnTime), event -> addRandomEnemy()));
        enemyTimeline.setCycleCount(Timeline.INDEFINITE);
        enemyTimeline.play();

        Timeline mapUpdateTimeLine = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateMap()));
        mapUpdateTimeLine.setCycleCount(Timeline.INDEFINITE);
        mapUpdateTimeLine.play();
    }

    public void log(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            String timestamp = LocalDateTime.now().format(FORMATTER);
            writer.write("[" + timestamp + "] " + message);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }

    public void addRandomEnemy() {
        Random random = new Random();
        int rand = random.nextInt(enemyList.size());
        Enemy enemy = new Enemy(enemyList.get(rand).getId(), enemyList.get(rand).getImagePath());
        enemy.setCol(0);
        enemy.setRow(ROW - 1);
        currentEnemies.add(enemy);
        log("New enemy with id " + enemy.getId() + " created!");
    }

    public void updateMap() {
        for (int i = 0; i < currentEnemies.size(); i++) {
            Enemy currentEnemy = currentEnemies.get(i);
            if (currentEnemy.getCol() == -1 || currentEnemy.getRow() == -1)
                continue;
            else {
                if(currentEnemy.getRow() != ROW - 1 || currentEnemy.getCol() != 0) {
                    if(currentEnemy.getCol() == 0) {
                        Node node = findNode("mainBoard", currentEnemy.getRow() + 1, currentEnemy.getCol());
                        ((Pane) node).getChildren().removeIf(child -> child instanceof ImageView);
                    } else if(currentEnemy.getRow() == 0){
                        Node node = findNode("mainBoard", currentEnemy.getRow(), currentEnemy.getCol() - 1);
                        ((Pane) node).getChildren().removeIf(child -> child instanceof ImageView);
                    } else{
                        Node node = findNode("mainBoard", currentEnemy.getRow() - 1, currentEnemy.getCol());
                        ((Pane) node).getChildren().removeIf(child -> child instanceof ImageView);
                    }
                }
                if(currentEnemy.getRow() >= ROW){
                    log("Enemy with id " + currentEnemy.getId() + " entered the base!");
                    currentEnemies.remove(currentEnemy);
                    i--;
                    continue;
                }
                Node node = findNode("mainBoard", currentEnemy.getRow(), currentEnemy.getCol());
                ImageView enemyImageView = new ImageView();
                enemyImageView.setImage(new Image("file:" + currentEnemy.getImagePath()));
                enemyImageView.setFitWidth(CELL_SIZE);
                enemyImageView.setFitHeight(CELL_SIZE);
                ((Pane) node).getChildren().add(enemyImageView);
                if(currentEnemy.getRow() > 0 && currentEnemy.getCol() == 0)
                    currentEnemy.setRow(currentEnemy.getRow() - 1);
                else if(currentEnemy.getRow() == 0 && currentEnemy.getCol() != COL - 1)
                    currentEnemy.setCol(currentEnemy.getCol() + 1);
                else
                    currentEnemy.setRow(currentEnemy.getRow() + 1);
            }
        }
    }

    public Card getRandomCard() {
        Random random = new Random();
        int index = random.nextInt(cardsList.size());
        return cardsList.get(index);
    }

    public boolean updateCell(String gridPane, int row, int col) {
        if (gridPane.equals("cardsBox")) {
            Node node = findNode("cardsBox", row, col);
            ((Pane) node).getChildren().removeIf(child -> child instanceof ImageView);
            Card card = currentCards.get(col);
            ImageView cardImageView = new ImageView();
            cardImageView.setImage(new Image("file:" + card.getImagePath()));
            cardImageView.setFitWidth(CELL_SIZE);
            cardImageView.setFitHeight(CELL_SIZE);
            ((Pane) node).getChildren().add(cardImageView);
            return true;
        } else if (gridPane.equals("mainBoard")) {
            Node node = findNode("mainBoard", row, col);
            if (!hasImageView((Pane) node)) {
                Card card = selectedCard;
                ImageView cardImageView = new ImageView();
                cardImageView.setImage(new Image("file:" + card.getImagePath()));
                cardImageView.setFitWidth(CELL_SIZE);
                cardImageView.setFitHeight(CELL_SIZE);
                ((Pane) node).getChildren().add(cardImageView);
                log("Card with ID " + card.getId() + " placed at {" + row + ", " + col + "}.");
                return true;
            } else {
                System.out.println("The cell is already occupied!");
                return false;
            }
        }
        return false;
    }

    public void checkClick(double x, double y) {
        int row = -1, col = -1;
        if (x >= mainBoard.getLayoutX() + CELL_SIZE && x <= mainBoard.getLayoutX() + mainBoard.getWidth() - CELL_SIZE
                && y >= mainBoard.getLayoutY() + CELL_SIZE && y <= mainBoard.getLayoutY() + mainBoard.getHeight()) {
            row = (int) Math.floor((y - mainBoard.getLayoutY()) / CELL_SIZE);
            col = (int) Math.floor((x - mainBoard.getLayoutX()) / CELL_SIZE);
            if (selectedCard != null) {
                boolean updated = updateCell("mainBoard", row, col);
                if (updated) {
                    int index = findCurrentCardsIndex(selectedCard);
                    cardsList.add(selectedCard);
                    selectedCard = null;
                    Card randomCard = getRandomCard();
                    currentCards.set(index, randomCard);
                    cardsList.remove(randomCard);
                    updateCell("cardsBox", 0, index);
                    log("New card with id " + randomCard.getId() + " added to your cards box!");
                }
            }
        } else if (x >= cardsBox.getLayoutX() && x <= cardsBox.getLayoutX() + cardsBox.getWidth()
                && y >= cardsBox.getLayoutY() && y <= cardsBox.getLayoutY() + cardsBox.getHeight()) {
            col = (int) Math.floor((x - cardsBox.getLayoutX()) / CELL_SIZE);
            selectedCard = currentCards.get(col);
            System.out.println(selectedCard.getId());
        } else {
            System.out.println("The selected ares is not accessible!");
        }
    }

    public int findCurrentCardsIndex(Card card) {
        for (int i = 0; i < currentCards.size(); i++) {
            if (currentCards.get(i).getId() == card.getId())
                return i;
        }
        return -1;
    }

    public Node findNode(String gridPane, int row, int col) {
        if (gridPane.equals("mainBoard")) {
            for (Node node : mainBoard.getChildren())
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row)
                    return node;
        } else if (gridPane.equals("cardsBox")) {
            for (Node node : cardsBox.getChildren())
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row)
                    return node;
        }
        return null;
    }

    private boolean hasImageView(Pane pane) {
        for (Node child : pane.getChildren())
            if (child instanceof ImageView)
                return true;
        return false;
    }
}
