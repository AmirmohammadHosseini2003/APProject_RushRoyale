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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javafx.scene.control.Label;

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
    final int ALL_AGENTS = 6;
    final String LOG_FILE_PATH = "/media/fatima/Fatima/Term7/AP/midProj/RushRoyaleGame/src/main/java/com/example/rushroyalegame/log.txt";
    final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    boolean first = true;
    private Agent source, target;
    @FXML
    GridPane mainBoard;
    @FXML
    GridPane cardsBox;
    @FXML
    GridPane allAgents;
    @FXML
    AnchorPane anchorPane;
    @FXML
    Pane elixir;
    @FXML
    Label elixirLabel;

    List<Enemy> enemyList = new ArrayList<>();
    List<Enemy> currentEnemies = new ArrayList<>();
    List<Agent> cardsList = new ArrayList<>();
    List<Agent> currentAgents = new ArrayList<>();
    List<Agent> agentsInUse = new ArrayList<>();
    List<Agent> allAgentsList = new ArrayList<>();
    List<Agent> freezeAgents = new ArrayList<>();

    Agent selectedAgent;
    int enemyRespawnTime = 1;
    int elixirAmount;
    int gameTimer = 1;
    int restTime = 3;
    int waveNum = 1;
    int bossEnterTime = -1;
    String bossType;
    Boolean flag = false;
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
        mainBoard.setLayoutY(0);
        cardsBox = new GridPane();
        for (int i = 0; i < CARD_LIST_SIZE; i++) {
            Pane cell = new Pane();
            cell.setPrefSize(CELL_SIZE, CELL_SIZE);
            cell.setStyle("-fx-background-radius: 15; -fx-background-color: lightgreen");
            cardsBox.add(cell, i, 0);
        }

        cardsBox.setLayoutX(120);
        cardsBox.setLayoutY(260);


        Pane elixir = new Pane();
        elixir.setPrefSize(CELL_SIZE, CELL_SIZE);
        elixir.setStyle("-fx-background-radius: 15; -fx-background-color: #A888B5");
        elixirLabel = new Label(String.valueOf(elixirAmount));
        elixirLabel.setStyle("-fx-font-size: 20px;");
        elixirLabel.setLayoutX(20);
        elixirLabel.setLayoutY(10);

        elixir.setLayoutX(30);
        elixir.setLayoutY(260);
        elixir.getChildren().add(elixirLabel);


        anchorPane.getChildren().add(cardsBox);
        anchorPane.getChildren().add(mainBoard);
        anchorPane.getChildren().add(elixir);

        String jsonContent = new String(Files.readAllBytes(Paths.get("/media/fatima/Fatima/Term7/AP/midProj/RushRoyaleGame/src/main/java/com/example/rushroyalegame/EnemiesInfo.json")));
        JSONArray enemiesInfo = new JSONArray(jsonContent);
        for (int i = 0; i < enemiesInfo.length(); i++) {
            JSONObject enemyInfo = new JSONObject();
            enemyInfo = enemiesInfo.getJSONObject(i);
            Enemy enemy = new Enemy(enemyInfo.getInt("id"), enemyInfo.getString("path"));
            enemyList.add(enemy);
        }

        allAgents = new GridPane();

        jsonContent = new String(Files.readAllBytes(Paths.get("/media/fatima/Fatima/Term7/AP/midProj/RushRoyaleGame/src/main/java/com/example/rushroyalegame/CardsInfo.json")));
        JSONArray cardsInfo = new JSONArray(jsonContent);
        for (int i = 0; i < cardsInfo.length(); i++) {
            JSONObject cardInfo = new JSONObject();
            cardInfo = cardsInfo.getJSONObject(i);
            Agent agent = new Agent(cardInfo.getInt("id"), cardInfo.getString("path"), 0);
            cardsList.add(agent);
            addAgentToList(agent, allAgentsList);
            Pane cell = new Pane();
            cell.setPrefSize(CELL_SIZE, CELL_SIZE);
            cell.setStyle("-fx-background-radius: 15; -fx-background-color: #F8DE7E");
            allAgents.add(cell, i, 0);

            ImageView cardImageView = new ImageView();
            cardImageView.setImage(new Image("file:" + agent.getImagePath()));
            cardImageView.setFitWidth(CELL_SIZE);
            cardImageView.setFitHeight(CELL_SIZE);
            cell.getChildren().add(cardImageView);
        }

        allAgents.setLayoutX(80);
        allAgents.setLayoutY(310);
        anchorPane.getChildren().add(allAgents);

        for (int i = 0; i < CARD_LIST_SIZE; i++) {
            int index = getRandomIndex(cardsList.size());
            Agent randomAgent = addAgent(index);
            currentAgents.add(randomAgent);
            cardsList.remove(randomAgent);
        }
        for (int i = 0; i < currentAgents.size(); i++) {
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

        Timeline elixirTimeLine = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateElixir(true, 2)));
        elixirTimeLine.setCycleCount(Timeline.INDEFINITE);
        elixirTimeLine.play();
    }

    public Agent addAgent(int index) {
        int id = cardsList.get(index).id;
        String path = cardsList.get(index).imagePath;
        return switch (id) {
            case 1 -> new FirstStriker(id, path, 0, this);
            case 2 -> new EndStriker(id, path, 0, this);
            case 3 -> new RandomStriker(id, path, 0, this);
            case 4 -> new MaxHealthStriker(id, path, 0, this);
            case 5 -> new Bomb(id, path, 0);
            default -> new Trap(id, path, 0);
        };
    }

    public void addAgentToList(Agent agent, List<Agent> list) {
        int id = agent.id;
        String path = agent.imagePath;
        Agent newAgent = switch (id) {
            case 1 -> new FirstStriker(id, path, 1, this);
            case 2 -> new EndStriker(id, path, 1, this);
            case 3 -> new RandomStriker(id, path, 1, this);
            case 4 -> new MaxHealthStriker(id, path, 1, this);
            case 5 -> new Bomb(id, path, 1);
            default -> new Trap(id, path, 1);
        };
        list.add(newAgent);
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

//    public void addRandomEnemy() {
//        Random random = new Random();
//        int rand = random.nextInt(enemyList.size());
//        Enemy enemy = new Enemy(enemyList.get(rand).getId(), enemyList.get(rand).getImagePath());
//        enemy.setCol(0);
//        enemy.setRow(ROW - 1);
//        currentEnemies.add(enemy);
//        log("New enemy with id " + enemy.getId() + " created!");
//    }

    public void addRandomEnemy() {
        Random random = new Random();
        if (gameTimer > 0 && gameTimer <= 10){
            gameTimer++;
            int rand = random.nextInt(3,5);
            if(rand == 3) {
                RunnerSoldier enemy = new RunnerSoldier(enemyList.get(rand).getId(), enemyList.get(rand).getImagePath());
                enemy.setCol(0);
                enemy.setRow(ROW - 1);
                currentEnemies.add(enemy);
                log("New enemy with id " + enemy.getId() + " created!");
            } else{
                ShielderSoldier enemy = new ShielderSoldier(enemyList.get(rand).getId(), enemyList.get(rand).getImagePath());
                enemy.setCol(0);
                enemy.setRow(ROW - 1);
                currentEnemies.add(enemy);
                log("New enemy with id " + enemy.getId() + " created!");
            }
        } else if (currentEnemies.isEmpty()){
            restTime--;
            if(restTime == 0){
                waveNum++;
                gameTimer = 1;
                restTime = 3;
                if(waveNum % 2 == 0){
                    flag = false;
                    bossEnterTime = gameTimer;
                    int rand = random.nextInt(0,3);
                    if(rand == 0){
                        EraserBoss enemy = new EraserBoss(enemyList.get(rand).getId(), enemyList.get(rand).getImagePath());
//                        System.out.println(enemy.getHealth());
                        enemy.setCol(0);
                        enemy.setRow(ROW - 1);
                        currentEnemies.add(enemy);
                        log("New Eraser Boss with id " + enemy.getId() + " created!");
                        bossType = "Eraser-Boss";
                    } else if(rand == 1){
                        FreezerBoss enemy = new FreezerBoss(enemyList.get(rand).getId(), enemyList.get(rand).getImagePath());
//                        System.out.println(enemy.getHealth());
                        enemy.setCol(0);
                        enemy.setRow(ROW - 1);
                        currentEnemies.add(enemy);
                        log("New Freezer Boss with id " + enemy.getId() + " created!");
                        bossType = "Freezer-Boss";
                    } else {
                        DisarmerBoss enemy = new DisarmerBoss(enemyList.get(rand).getId(), enemyList.get(rand).getImagePath());
//                        System.out.println(enemy.getHealth());
                        enemy.setCol(0);
                        enemy.setRow(ROW - 1);
                        currentEnemies.add(enemy);
                        log("New Disarmer Boss with id " + enemy.getId() + " created!");
                        bossType = "Disarmer-Boss";
                    }
                }
            }
        }
    }

    public void updateMap() {
        for (int i = 0; i < currentEnemies.size(); i++) {
            Enemy currentEnemy = currentEnemies.get(i);
            if (currentEnemy.getCol() == -1 || currentEnemy.getRow() == -1)
                continue;
            else {
                if (currentEnemy.getRow() != ROW - 1 || currentEnemy.getCol() != 0) {
                    if (currentEnemy.getCol() == 0) {
                        Node node = findNode("mainBoard", currentEnemy.getRow() + 1, currentEnemy.getCol());
                        ((Pane) node).getChildren().removeIf(child -> child instanceof ImageView);
                    } else if (currentEnemy.getRow() == 0) {
                        Node node = findNode("mainBoard", currentEnemy.getRow(), currentEnemy.getCol() - 1);
                        ((Pane) node).getChildren().removeIf(child -> child instanceof ImageView);
                    } else {
                        Node node = findNode("mainBoard", currentEnemy.getRow() - 1, currentEnemy.getCol());
                        ((Pane) node).getChildren().removeIf(child -> child instanceof ImageView);
                    }
                }
                if (currentEnemy.getRow() >= ROW) {
                    currentEnemy.setCol(-1);
                    currentEnemy.setRow(-1);
                    currentEnemies.remove(currentEnemy);
                    log("Enemy with id " + currentEnemy.getId() + " entered the base!");
                    log("Game Over!");
                    System.exit(0);
                }
                Node node = findNode("mainBoard", currentEnemy.getRow(), currentEnemy.getCol());
                ImageView enemyImageView = new ImageView();
                enemyImageView.setImage(new Image("file:" + currentEnemy.getImagePath()));
                enemyImageView.setFitWidth(CELL_SIZE);
                enemyImageView.setFitHeight(CELL_SIZE);
                ((Pane) node).getChildren().add(enemyImageView);
                if (currentEnemy.getRow() > 0 && currentEnemy.getCol() == 0)
                    currentEnemy.setRow(currentEnemy.getRow() - 1);
                else if (currentEnemy.getRow() == 0 && currentEnemy.getCol() != COL - 1)
                    currentEnemy.setCol(currentEnemy.getCol() + 1);
                else
                    currentEnemy.setRow(currentEnemy.getRow() + 1);
            }
        }
        if(bossType != null && bossType.equals("Eraser-Boss") && (gameTimer - bossEnterTime) % 7 == 0){
            if(flag) {
                int index = getRandomIndex(agentsInUse.size());
                Node node = findNode("mainBoard", agentsInUse.get(index).getRow(), agentsInUse.get(index).getCol());
                ((Pane) node).getChildren().removeIf(child -> child instanceof ImageView);
                if (agentsInUse.get(index) instanceof Striker)
                    ((Striker) agentsInUse.get(index)).stop();
                agentsInUse.remove(index);
            } else{
                flag = true;
            }
        } else if(bossType != null && bossType.equals("Freezer-Boss") && (gameTimer - bossEnterTime) % 5 == 0){
            if(flag) {
                int index = getRandomIndex(agentsInUse.size());
                agentsInUse.get(index).setFreeze(true);
                freezeAgents.add(agentsInUse.get(index));
            } else{
                flag = true;
            }
        }
    }

    public Agent findByRowCol(int row, int col){
        for (Agent agent : agentsInUse) {
            if (agent.getRow() == row && agent.getCol() == col)
                return agent;
        }
        return null;
    }
    public Boolean updateElixir(Boolean b, int amount) {
        if (b && elixirAmount + amount < 11) {
            elixirAmount += amount;
            elixirLabel.setText(String.valueOf(elixirAmount));
            return true;
        } else if (!b && elixirAmount - amount > 0) {
            elixirAmount -= amount;
            elixirLabel.setText(String.valueOf(elixirAmount));
            return true;
        }
        return false;
    }

    public int getRandomIndex(int range) {
        Random random = new Random();
        return random.nextInt(range);
    }

    public String updateCell(String gridPane, int row, int col) {
        if (gridPane.equals("cardsBox")) {
            Node node = findNode("cardsBox", row, col);
            ((Pane) node).getChildren().removeIf(child -> child instanceof ImageView);
            Agent agent = currentAgents.get(col);
            ImageView cardImageView = new ImageView();
            cardImageView.setImage(new Image("file:" + agent.getImagePath()));
            cardImageView.setFitWidth(CELL_SIZE);
            cardImageView.setFitHeight(CELL_SIZE);
            ((Pane) node).getChildren().add(cardImageView);
            return "true";
        } else if (gridPane.equals("mainBoard")) {
            Node node = findNode("mainBoard", row, col);
            if (!hasImageView((Pane) node)) {
                if (updateElixir(false, 2)) {
                    Agent agent = selectedAgent;
                    selectedAgent.setRow(row);
                    selectedAgent.setCol(col);
                    selectedAgent.setLevel(findAgentLevel(selectedAgent));
                    agentsInUse.add(agent);
                    if (selectedAgent instanceof Striker) {
                        Thread agentThread = new Thread((Striker) agent);
                        agentThread.start();
                    }
                    ImageView cardImageView = new ImageView();
                    cardImageView.setImage(new Image("file:" + agent.getImagePath()));
                    cardImageView.setFitWidth(CELL_SIZE);
                    cardImageView.setFitHeight(CELL_SIZE);
                    ((Pane) node).getChildren().add(cardImageView);

                    if(selectedAgent instanceof Striker)
                        drawLevel(((Striker) selectedAgent).stage, (Pane) node);

                    log("Card with ID " + agent.getId() + " placed at {" + row + ", " + col + "}.");
                    return "true";
                } else {
                    System.out.println("You don't have enough Elixir.");
                    return "not enough elixir";
                }
            } else {
                System.out.println("The cell is already occupied!");
                return "occupied";
            }
        }
        return "false";
    }

    public Boolean drawLevel(int stage, Pane pane) {
        switch (stage) {
            case 1: {
                Circle circle = new Circle();
                circle.setCenterX(24);
                circle.setCenterY(24);
                circle.setRadius(24);
                circle.setStroke(Color.BLACK);
                circle.setFill(null);

                pane.getChildren().add(circle);
                return true;
            }
            case 2: {
                Polygon triangle = new Polygon();
                triangle.getPoints().addAll(
                        24.0, 2.0, // Vertex 1 (X, Y)
                        2.0, 46.0, // Vertex 2 (X, Y)
                        46.0, 46.0  // Vertex 3 (X, Y)
                );
                triangle.setStroke(Color.BLACK); // Set the outline color
                triangle.setFill(null);
                pane.getChildren().add(triangle);
                return true;
            }
            case 3: { // Square
                Polygon square = new Polygon();
                square.getPoints().addAll(
                        2.0, 2.0,   // Vertex 1 (Top-left)
                        46.0, 2.0,  // Vertex 2 (Top-right)
                        46.0, 46.0, // Vertex 3 (Bottom-right)
                        2.0, 46.0   // Vertex 4 (Bottom-left)
                );
                square.setStroke(Color.BLACK);
                square.setFill(null);
                pane.getChildren().add(square);
                return true;
            }
            case 4: { // Pentagon
                Polygon pentagon = new Polygon();
                pentagon.getPoints().addAll(
                        24.0, 2.0,  // Vertex 1 (Top)
                        2.0, 18.0,  // Vertex 2 (Top-left)
                        9.0, 45.0,  // Vertex 3 (Bottom-left)
                        39.0, 45.0, // Vertex 4 (Bottom-right)
                        46.0, 18.0  // Vertex 5 (Top-right)
                );
                pentagon.setStroke(Color.BLACK);
                pentagon.setFill(null);
                pane.getChildren().add(pentagon);
                return true;
            }
            case 5: { // Hexagon
                Polygon hexagon = new Polygon();
                hexagon.getPoints().addAll(
                        24.0, 2.0,   // Vertex 1 (Top)
                        2.0, 15.0,   // Vertex 2 (Top-left)
                        2.0, 34.0,   // Vertex 3 (Bottom-left)
                        24.0, 47.0,  // Vertex 4 (Bottom)
                        46.0, 34.0,  // Vertex 5 (Bottom-right)
                        46.0, 15.0   // Vertex 6 (Top-right)
                );
                hexagon.setStroke(Color.BLACK);
                hexagon.setFill(null);
                pane.getChildren().add(hexagon);
                return true;
            }
            default: {
                log("Invalid stage.");
                return false;
            }
        }
    }


    public int findAgentLevel(Agent agent) {
        for (Agent currentAgent : agentsInUse) {
            if (currentAgent.getClass() == agent.getClass())
                return currentAgent.getLevel();
        }
        return 1;
    }

    public void checkClick(double x, double y) {
        int row = -1, col = -1;
        if (x >= mainBoard.getLayoutX() + CELL_SIZE && x <= mainBoard.getLayoutX() + mainBoard.getWidth() - CELL_SIZE
                && y >= mainBoard.getLayoutY() + CELL_SIZE && y <= mainBoard.getLayoutY() + mainBoard.getHeight()) {
            row = (int) Math.floor((y - mainBoard.getLayoutY()) / CELL_SIZE);
            col = (int) Math.floor((x - mainBoard.getLayoutX()) / CELL_SIZE);
            if (selectedAgent != null) {
                String updated = updateCell("mainBoard", row, col);
                if (updated.equals("true")) {
                    int index = findCurrentCardsIndex(selectedAgent);
                    cardsList.add(selectedAgent);
                    selectedAgent = null;
                    int randomNum = getRandomIndex(cardsList.size());
                    Agent randomAgent = addAgent(randomNum);
                    randomAgent.setRow(row);
                    randomAgent.setCol(col);
                    currentAgents.set(index, randomAgent);
                    cardsList.remove(randomAgent);
                    updateCell("cardsBox", 0, index);
                    log("New card with id " + randomAgent.getId() + " added to your cards box!");
                }
            } else {
                Node node = findNode("mainBoard", row, col);
                if (hasImageView((Pane) node)) {
                    if (first) {
                        source = findByRowCol(row, col);
                        first = false;
                    } else {
                        target = findByRowCol(row, col);
                        first = true;
                        if (source instanceof Striker && target instanceof Striker &&
                                source.getClass() == target.getClass() &&
                                ((Striker) source).stage == ((Striker) target).stage) {
                            Node s_node = findNode("mainBoard", source.getRow(), source.getCol());
                            Node t_node = findNode("mainBoard", target.getRow(), target.getCol());
                            if (((Striker) source).stage == 1) {
                                ((Pane) s_node).getChildren().removeIf(child -> child instanceof Circle);
                                ((Pane) s_node).getChildren().removeIf(child -> child instanceof ImageView);
                                ((Pane) t_node).getChildren().removeIf(child -> child instanceof Circle);
                                ((Striker) target).stage++;
                                if (drawLevel(((Striker) target).stage, (Pane) t_node))
                                    ((Striker) target).speed *= 2;
                            } else if (((Striker) source).stage > 1) {
                                ((Pane) s_node).getChildren().removeIf(child -> child instanceof Polygon);
                                ((Pane) s_node).getChildren().removeIf(child -> child instanceof ImageView);
                                ((Pane) t_node).getChildren().removeIf(child -> child instanceof Polygon);
                                ((Striker) target).stage++;
                                if (drawLevel(((Striker) target).stage, (Pane) t_node))
                                    ((Striker) target).speed *= 2;
                            }
                        }
                    }
                }
            }
        } else if (x >= cardsBox.getLayoutX() && x <= cardsBox.getLayoutX() + cardsBox.getWidth()
                && y >= cardsBox.getLayoutY() && y <= cardsBox.getLayoutY() + cardsBox.getHeight()) {
            col = (int) Math.floor((x - cardsBox.getLayoutX()) / CELL_SIZE);
            selectedAgent = currentAgents.get(col);
        } else if (x >= allAgents.getLayoutX() && x <= allAgents.getLayoutX() + allAgents.getWidth()
                && y >= allAgents.getLayoutY() && y <= allAgents.getLayoutY() + allAgents.getHeight()) {
            col = (int) Math.floor((x - allAgents.getLayoutX()) / CELL_SIZE);
            Agent agent = allAgentsList.get(col);
            increaseLevel(agent);
        } else {
            System.out.println("The selected ares is not accessible!");
        }
    }

    public void increaseLevel(Agent agent) {
        if (existAgentType(agent)) {
            int level = agent.getLevel();
            switch (level) {
                case 1:
                    if (updateElixir(false, 2)) {
                        for (Agent a : agentsInUse) {
                            if (a.getClass() == agent.getClass()) {
                                a.setLevel(2);
                                agent.setLevel(2);
                                if (a instanceof Striker)
                                    ((Striker) a).hit *= 2;
                                else if (a instanceof Blocker)
                                    ((Blocker) a).destroyEnemyNum += 1;
                            }
                        }
                        log("Increase Level from 1 to 2.");
                        return;
                    }
                case 2:
                    if (updateElixir(false, 4)) {
                        for (Agent a : agentsInUse) {
                            if (a.getClass() == agent.getClass()) {
                                a.setLevel(3);
                                agent.setLevel(3);
                                if (a instanceof Striker)
                                    ((Striker) a).hit *= 2;
                                else if (a instanceof Blocker)
                                    ((Blocker) a).destroyEnemyNum += 1;
                            }
                        }
                        log("Increase Level from 2 to 3.");
                        return;
                    }
                case 3:
                    if (updateElixir(false, 6)) {
                        for (Agent a : agentsInUse) {
                            if (a.getClass() == agent.getClass()) {
                                a.setLevel(4);
                                agent.setLevel(4);
                                if (a instanceof Striker)
                                    ((Striker) a).hit *= 2;
                                else if (a instanceof Blocker)
                                    ((Blocker) a).destroyEnemyNum += 1;
                            }
                        }
                        log("Increase Level from 3 to 4.");
                        return;
                    }
                case 4:
                    if (updateElixir(false, 8)) {
                        for (Agent a : agentsInUse) {
                            if (a.getClass() == agent.getClass()) {
                                a.setLevel(5);
                                agent.setLevel(5);
                                if (a instanceof Striker)
                                    ((Striker) a).hit *= 2;
                                else if (a instanceof Blocker)
                                    ((Blocker) a).destroyEnemyNum += 1;
                            }
                        }
                        log("Increase Level from 4 to 5.");
                    }
            }
        } else {
            log("No such type of Agent to level up!");
        }
    }

    public Boolean existAgentType(Agent agent) {
        for (Agent value : agentsInUse) {
            if (value.getClass() == agent.getClass())
                return true;
        }
        return false;
    }

    public int findCurrentCardsIndex(Agent agent) {
        for (int i = 0; i < currentAgents.size(); i++) {
            if (currentAgents.get(i).getId() == agent.getId())
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