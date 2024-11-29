package com.example.rushroyalegame;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.json.JSONArray;
import org.json.JSONObject;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MainPageController {
    final int CELL_SIZE = 50;
    final int ROW = 5;
    final int COL = 6;
    final int CARD_LIST_SIZE = 4;

    @FXML
    GridPane mainBoard;
    @FXML
    GridPane cardsBox;
    @FXML
    AnchorPane anchorPane;

    List<Card> cardsList = new ArrayList<>();
    List<Card> currentCards = new ArrayList<>();
    Card selectedCard;

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

        String jsonContent = new String(Files.readAllBytes(Paths.get("/media/fatima/Fatima/Term7/AP/midProj/RushRoyaleGame/src/main/java/com/example/rushroyalegame/CardsInfo.json")));
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

        anchorPane.setOnMouseClicked(event -> {
            checkClick(event.getX(), event.getY());
        });
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
                if(updated) {
                    int index = findCurrentCardsIndex(selectedCard);
                    cardsList.add(selectedCard);
                    selectedCard = null;
                    Card randomCard = getRandomCard();
                    currentCards.set(index, randomCard);
                    cardsList.remove(randomCard);
                    updateCell("cardsBox", 0, index);
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
