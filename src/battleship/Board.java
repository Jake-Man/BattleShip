/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.FillTransition;
import javafx.animation.Timeline;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * @author Jake Manser 
 */
public class Board extends Parent {

    private final VBox rows = new VBox();
    // Can tell the difference between your board and the enemy board
    private boolean enemy = false;
    // Sets how many vessels you have
    public int vessels = 5;

    public Board(boolean enemy, EventHandler<? super MouseEvent> handler) {
        this.enemy = enemy;
        // Creats ten rows stack over each other and each row is fourteen cells long
        for (int y = 0; y < 10; y++) {
            HBox row = new HBox();
            for (int x = 0; x < 14; x++) {
                Cell c = new Cell(x, y, this);
                c.setOnMouseClicked(handler);
                row.getChildren().add(c);
            }

            // adds row to list of rows
            rows.getChildren().add(row);
        }

        getChildren().add(rows);
    }

    // Checks X & Y coordinate of a ship that is placed
    public boolean placeShip(Ship ship, int x, int y) {
        // If ship was placed return true
        if (canPlaceShip(ship, x, y)) {
            //Looks at the type of the ship to determine the length
            int length = ship.type;

            // if ship is verticle, check y values
            if (ship.vertical) {
                for (int i = y; i < y + length; i++) {
                    // Ship is referenced in class cell that it is assigned to
                    Cell cell = getCell(x, i);
                    // empty cell is replace by a ship
                    cell.ship = ship;
                    if (!enemy) {
                        cell.setFill(Color.SLATEGREY);
                        cell.setStroke(Color.SEAGREEN);
                    }
                }
            } // If ship is not verticle, check x values.
            else {
                for (int i = x; i < x + length; i++) {
                    Cell cell = getCell(i, y);
                    cell.ship = ship;
                    if (!enemy) {
                        cell.setFill(Color.SLATEGREY);
                        cell.setStroke(Color.SEAGREEN);
                    }
                }
            }

            return true;
        }

        // if ship was not placed return false
        return false;
    }

    // First gets children of the row, typcast it to Horizontal box to get x within the row
    // and typecaste it to cell
    public Cell getCell(int x, int y) {
        return (Cell) ((HBox) rows.getChildren().get(y)).getChildren().get(x);
    }

    // Finds neighbors by adding and subtracting one from X & Y
    private Cell[] getNeighbors(int x, int y) {
        Point2D[] points = new Point2D[]{
            new Point2D(x - 1, y),
            new Point2D(x + 1, y),
            new Point2D(x, y - 1),
            new Point2D(x, y + 1)
        };

        // puts Neighbors into an arraylist
        List<Cell> neighbors = new ArrayList<>();

        // check if point is valid
        for (Point2D p : points) {
            if (isValidPoint(p)) {
                // if point is valid, add cell to the neighbor arraylist
                neighbors.add(getCell((int) p.getX(), (int) p.getY()));
            }
        }

        return neighbors.toArray(new Cell[0]);
    }

    private boolean canPlaceShip(Ship ship, int x, int y) {
        int length = ship.type;

        // checks for verticle ship using Y values
        if (ship.vertical) {
            for (int i = y; i < y + length; i++) {
                // makes sure you plase ship in a valid place
                if (!isValidPoint(x, i)) {
                    return false;
                }

                Cell cell = getCell(x, i);
                // Checks if a ship is already occupying the cell
                if (cell.ship != null) // makes it sure you are unable to overlap ships
                {
                    return false;
                }

                for (Cell neighbor : getNeighbors(x, i)) {
                    // makes sure you can't make ships neighbor each other
                    if (!isValidPoint(x, i)) {
                        return false;
                    }

                    if (neighbor.ship != null) {
                        return false;
                    }
                }
            }
        } // Same thing but with X values
        else {
            for (int i = x; i < x + length; i++) {
                if (!isValidPoint(i, y)) {
                    return false;
                }

                Cell cell = getCell(i, y);
                if (cell.ship != null) {
                    return false;
                }

                for (Cell neighbor : getNeighbors(i, y)) {
                    if (!isValidPoint(i, y)) {
                        return false;
                    }

                    if (neighbor.ship != null) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    // reiterates the valid points
    private boolean isValidPoint(Point2D point) {
        return isValidPoint(point.getX(), point.getY());
    }

    // point to be valid X has to be greater than and equal to zero and less than 14
    // Y has to be greater than or equal to zero and less than ten
    private boolean isValidPoint(double x, double y) {
        return x >= 0 && x < 14 && y >= 0 && y < 10;
    }

    public class Cell extends Rectangle {

        public int x, y;
        public Ship ship = null;
        public boolean wasHit = false;

        private final Board board;

        // sets size of each cell on the board as well as their color
        public Cell(int x, int y, Board board) {
            super(30, 30);
            this.x = x;
            this.y = y;
            this.board = board;
            setFill(Color.NAVY);
            setStroke(Color.CADETBLUE);
        }

        public boolean fire() {
            // of shot was fired but not hit
            wasHit = true;

            // sets a transition between colors
            FillTransition ft = new FillTransition();
            // shape of the cell
            ft.setShape(this);
            // sets how long the transition will take
            ft.setDuration(new Duration(2000));
            // color will transition to white
            ft.setToValue(Color.WHITE);
            // does one cycle
            ft.setCycleCount(1);
            // color will not reverse back to Nave
            ft.setAutoReverse(false);
            // plays the animation
            ft.play();

            // If ship was there
            if (ship != null) {
                // You succesfully hit the enemy ship
                ship.directHit();
                // sets starting color to orange
                setFill(Color.ORANGE);

                // creates a transition called hit
                FillTransition hit = new FillTransition();
                // set to shape of the Cell
                hit.setShape(this);
                // Sets how long it takes to fill
                hit.setDuration(new Duration(2000));
                // Sets transition color to Crimson
                hit.setToValue(Color.CRIMSON);
                // Makes it have infinite cycles
                hit.setCycleCount(Timeline.INDEFINITE);
                // cycle will reverse back to original colors
                hit.setAutoReverse(true);
                //plays animation
                hit.play();

                // If ship is sunk the game will recognize that a player has lossed
                // a vessel
                if (!ship.notSunk()) {
                    board.vessels--;
                }
                return true;
            }

            return false;
        }
    }
}//Method end
