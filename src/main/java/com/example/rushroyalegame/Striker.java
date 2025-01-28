package com.example.rushroyalegame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.List;

public class Striker extends Agent implements Runnable {
    private static MainPageController controller;
    final int elixir;
    public float hit;
    public int stage;
    int speed;
    private volatile boolean running = true;

    public Striker(int id, String imagePath, int level, int elixir, float hit, MainPageController controller) {
        super(id, imagePath, level);
        this.elixir = elixir;
        this.hit = hit;
        setController(controller);
        stage = 1;
        speed = 1;
    }

    public static void setController(MainPageController ctrl) {
        controller = ctrl;
    }

    @Override
    public void run() {
        while (true) {
            try {
                strikerAttack(this);
                Thread.sleep(1000L / speed);
            } catch (InterruptedException e) {
                System.err.println("Striker thread interrupted: " + e.getMessage());
                break;
            }
        }
    }

    public void stop(){
        running = false;
    }

    public void strikerAttack(Striker striker) {
        if (!controller.currentEnemies.isEmpty()) {
            Enemy target = null;
            if (striker instanceof FirstStriker)
                target = controller.currentEnemies.get(0);
            else if (striker instanceof EndStriker)
                target = controller.currentEnemies.get(controller.currentEnemies.size() - 1);
            else if (striker instanceof RandomStriker) {
                int index = controller.getRandomIndex(controller.currentEnemies.size());
                target = controller.currentEnemies.get(index);
            } else if (striker instanceof MaxHealthStriker) {
                target = findMax();
            }
            if (target != null) {
                throwBall(striker, target);
            }
        }
    }

    private static Enemy findMax() {
        int maxHealth = -1, index = -1;
        for (int i = 0; i < controller.currentEnemies.size(); i++) {
            if (controller.currentEnemies.get(i).getHealth() > maxHealth)
                index = i;
        }
        return controller.currentEnemies.get(index);
    }

    private static void throwBall(Striker striker, Enemy target) {
        Platform.runLater(() -> {
            Circle ball = new Circle(5, Color.RED);
            Pane strikerPane = (Pane) controller.findNode("mainBoard", striker.getRow(), striker.getCol());
            Pane targetPane = (Pane) controller.findNode("mainBoard", target.getRow(), target.getCol());

            double startX = strikerPane.getLayoutX() + 3 * controller.CELL_SIZE / 2;
            double startY = strikerPane.getLayoutY() + controller.CELL_SIZE / 2;
            double endX = targetPane.getLayoutX() + controller.CELL_SIZE / 2;
            double endY = targetPane.getLayoutY() + controller.CELL_SIZE / 2;

            ball.setLayoutX(startX);
            ball.setLayoutY(startY);
            controller.anchorPane.getChildren().add(ball);

            double distance = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));

            double duration = distance / 300;

            Timeline timeline = new Timeline();
            KeyFrame keyFrame = new KeyFrame(Duration.millis(100), event -> {
                double deltaX = (endX - startX + 50) / 4;
                double deltaY = (endY - startY) / 4;
                ball.setLayoutX(ball.getLayoutX() + deltaX);
                ball.setLayoutY(ball.getLayoutY() + deltaY);

                // Check if the ball reaches the target
                if (Math.abs(ball.getLayoutX() - endX) < 5 && Math.abs(ball.getLayoutY() - endY) < 5) {
                    controller.anchorPane.getChildren().remove(ball); // Remove the ball
                    target.setHealth(target.getHealth() - striker.hit); // Reduce target health
                    if (target.isDead()) {
                        controller.currentEnemies.remove(target);
                        controller.log("Enemy with ID " + target.getId() + " is dead!");
                    }
                    ((Pane) controller.findNode("mainBoard", target.getRow(), target.getCol()))
                            .getChildren()
                            .removeIf(child -> child instanceof ImageView); // Remove the enemy image
                    timeline.stop(); // Stop the timeline explicitly
                }
            });

            timeline.getKeyFrames().add(keyFrame);
            timeline.setCycleCount((int) (duration * 100)); // Set cycle count
            timeline.play();
        });
    }

    public float getHit() {
        return hit;
    }

    public void setHit(float hit) {
        this.hit = hit;
    }
}
