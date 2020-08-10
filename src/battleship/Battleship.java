/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import battleship.Board.Cell;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javax.swing.JFrame;
/**
 *
 * @author jacob
 */
public class Battleship extends Application {

    // chreats the following characteristic for the background
    private static final Paint SCENE_FILL = new RadialGradient(
            0, 0, 300, 300, 500, false, CycleMethod.NO_CYCLE,
            FXCollections.observableArrayList(new Stop(0, Color.ROYALBLUE), new Stop(1, Color.SILVER))
    );

    // If running is false you're placing ships, if true game on
    private boolean running = false;
    // references to your board and the opponents board
    private Board opponentsBoard, yourBoard;

    public JFrame parent = new JFrame();
    // total ships to place
    private int shipsToPlace = 5;

// creates final exit button
    public Button exit = new Button("Exit");

    // if false it isn't you're turn, if true it is.
    private boolean opponentsTurn = false;

    // generates a random variable
    private final Random random = new Random();

    private Parent createDesign() {

        // creates a BorderPane to be used for scene display
        BorderPane root = new BorderPane();

        // sets sise of stage
        root.setPrefSize(1000, 800);

        // Sets backround for the stage
        root.setBackground(Background.EMPTY);

        //Rules for the Game
        String controls = "CONTROLS \n _______________________________\n"
                + "Step 1: Place your 5 ships first\n Step 2:Fire at the enemy until\none of you lose\n"
                + "*If you hit an enemy ship you\ncan shoot again\n _______________________________\n"
                + "Place Ship Horizontally - Left Click \n*Starts"
                + " from the leftmost end\nPlace  Ship Vertically - Right Click\n*Starts"
                + " from the top most end\nPlace 5 Ships from biggest to \nsmallest\n"
                + "Aircraft Carrier = 5 Spaces\nBattleship = 4 Spaces\n Destroyer"
                + " = 3 Spaces\nSubmarine = 2 Spaces\nPatrol Boat = 1 Space\n"
                + "_______________________________\n<--- Enemy Board"
                + "\n\n\n\n\n\n\n\n\n\n\n <---Your Board ";

        // sets String controls in a text called rulesAndControls
        Text rulesAndControls = new Text(controls);
        // Rows 
        String rows = "\n                                                                 A\n"
                + "\n                                                                 B\n"
                + "\n                                                                 C\n"
                + "\n                                                                 D\n"
                + "\n                                                                 E\n"
                + "\n                                                                 F\n"
                + "\n                                                                 G\n"
                + "\n                                                                 H\n"
                + "\n                                                                 I\n"
                + "\n                                                                 J\n";
        //sets String rows in a text called rowCoordinates
        Text rowCoordinates = new Text(rows);

        // Collumns
        String collumns = "\n\n1       2       3"
                + "     4      5      6       7      8      9      10    "
                + "11     12     13     14";
        //sets String collumns in a text called collumnCoordinates
        Text collumnCoordinates = new Text(collumns);

        // If new board is enemy board it refers to a handler
        opponentsBoard = new Board(true, new EventHandler<MouseEvent>() {

            // If the game is not running, you haven't placed down your ships
            public void handle(MouseEvent event) {
                if (!running) {
                    return;
                }

                // optain source of event, if cell was shot return result if hit
                Cell cell = (Cell) event.getSource();
                if (cell.wasHit) {
                    return;
                }

                // get the result if it missed
                opponentsTurn = !cell.fire();

                // If the enemy has no ships left you win
                if (opponentsBoard.vessels == 0) {

                    System.out.println("YOU WIN");

                    // Alerts the player that they have achieved victory
                    Alert winner = new Alert(AlertType.INFORMATION);
                    winner.setTitle("Winner");
                    winner.setHeaderText("You win");
                    winner.setContentText("Congratulation");
                    // waits for user to take further action
                    winner.showAndWait();

                    // closes the program
                    System.exit(0);
                }
                // if it is the opponents turn, the enemy's move
                if (opponentsTurn) {
                    enemyMove();
                }
            }
        });

        //You're board is not the enemy board. Enemy targets your board.
        yourBoard = new Board(false, event -> {
            // if game is running return.
            if (running) {
                return;
            }

            // retain cell,
            Cell cell = (Cell) event.getSource();
            // if left click it places vertically, if right click it places horizontally
            if (yourBoard.placeShip(new Ship(shipsToPlace, event.getButton() == MouseButton.PRIMARY), cell.x, cell.y)) {
                // if ships to place equals 0 then the game begins
                if (--shipsToPlace == 0) {
                    startGame();
                }
            }
        });

        // adds padding around the edges
        exit.setPadding(new Insets(5, 5, 5, 5));

        // adds a grid for the textbox Name and the buttons Submit, cancel and Label
        // the Label label
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        final TextField name = new TextField();
        name.setPromptText("Name.");
        name.setPrefColumnCount(10);
        name.getText();
        GridPane.setConstraints(name, 0, 0);
        grid.getChildren().add(name);

        Button submit = new Button("Submit");
        GridPane.setConstraints(submit, 1, 0);
        grid.getChildren().add(submit);

        Button clear = new Button("Clear");
        GridPane.setConstraints(clear, 1, 1);
        grid.getChildren().add(clear);

        final Label label = new Label();
        GridPane.setConstraints(label, 0, 3);
        GridPane.setColumnSpan(label, 1);
        grid.getChildren().add(label);
        // Addss and event handler if submit is pressed
        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if ( // If contains something and the text is not empty
                        (name.getText() != null && !name.getText().isEmpty())) {
                    // Prints the label as whatever the user inputed and adds "'s board"
                    label.setText(name.getText() + "'s board ");
                } else {
                    // If nothing is inputed when submit is pressed, sets text to "No name"
                    label.setText("No name");
                }
            }
        });
        // event handler is clear is pressed
        clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                // Name is cleared
                name.clear();
                // label goes away
                label.setText(null);
            }
        });

        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.exit(0);
            }
        });

        // Puts the listed buttons and TextField in a HBOX and centers them
        HBox buttons = new HBox(exit, name, submit, clear);
        buttons.setAlignment(Pos.CENTER);
        label.setAlignment(Pos.CENTER);

        //Creates a VBox that that contains the opponentsBoard on top, then the HBox
        // buttons, then yourBoard, then your label
        VBox vbox = new VBox(opponentsBoard, buttons, yourBoard, label);
        // aligns it to the center
        vbox.setAlignment(Pos.CENTER);

        // Creates a VBox that containt the following Text and aligns them as followed.
        VBox rules = new VBox(rulesAndControls);
        rules.setAlignment(Pos.TOP_RIGHT);
        VBox leftRows = new VBox(rowCoordinates);
        leftRows.setAlignment(Pos.TOP_LEFT);
        VBox topCollumns = new VBox(collumnCoordinates);
        topCollumns.setAlignment(Pos.TOP_CENTER);

        // centers it in the middle in accordance to the border pan
        root.setCenter(vbox);
        // sets in right in accordance to the border pan
        root.setRight(rules);
        // left
        root.setLeft(leftRows);
        // top
        root.setTop(topCollumns);

        // returns changes made BorderPane root to  Start(Stage Primary)
        return root;

    }

    // Enemy will randomly choose a target within the following quadrants
    private void enemyMove() {
        while (opponentsTurn) {
            int x = random.nextInt(14);
            int y = random.nextInt(10);

            // if cell was hit opponent it continues
            Cell cell = yourBoard.getCell(x, y);
            if (cell.wasHit) {
                continue;
            }

            // opponent will fire when it's their turn
            opponentsTurn = cell.fire();

            // if you have no ships you lose
            if (yourBoard.vessels == 0) {
                System.out.println("YOU LOSE");
                // Alerts the player that they are a loser
                Alert loser = new Alert(AlertType.INFORMATION);
                loser.setTitle("Loser");
                loser.setHeaderText("You lost!!!");
                loser.setContentText("Can't believe you failed.");
                loser.showAndWait();

                // exits the game
                System.exit(0);
            }
        }
    }

    private void startGame() {
        // place enemy ships
        int type = 5;

        while (type > 0) {
            // opponent will choose a random x and y value in accordance with how 
            // many there are
            int x = random.nextInt(14);
            int y = random.nextInt(10);

            // The opponent will randomly place ships
            if (opponentsBoard.placeShip(new Ship(type, Math.random() < 0.5), x, y)) {
                // The next ship you place takes up one less space
                type--;
            }
        }

        // game has started
        running = true;

    }

    @Override
    public void start(Stage primaryStage) {

        // Refers to createDesign for scene apearance
        Scene scene = new Scene(createDesign(), 1000, 800, SCENE_FILL);

        // Title of the page
        primaryStage.setTitle("Battleship by Jake Manser");
        // sets the scene of scene
        primaryStage.setScene(scene);
        // cant resize the scene
        primaryStage.setResizable(false);
        // Shows the scene
        primaryStage.show();

    }

    
    public static void main(String[] args) {
        //Launches program
        launch(args);
    }

}// Method end
