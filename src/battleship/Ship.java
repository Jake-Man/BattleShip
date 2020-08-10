/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;

import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author jacob
 */
public class Ship extends Parent {

    // Sets an it to identify what kind of ship

    public int type;
    public boolean vertical = true;

    // Sets an interger to identify how much health the ship has
    private int health;

    public Ship(int type, boolean vertical) {
        this.type = type;
        this.vertical = vertical;
        health = type;

        // creates a VBoc
        VBox vbox = new VBox();
        for (int i = 0; i < type; i++) {
            Rectangle square = new Rectangle(30, 30);
            square.setFill(null);
            square.setStroke(Color.BLACK);

            vbox.getChildren().add(square);
        }
        getChildren().add(vbox);
    }

    public void directHit() {
        // If it is a dierect hit, the ship loses health
        health--;
    }

    public boolean notSunk() {
        // Ship is alive if it has more than 0 health
        return health > 0;
    }
}//Method End

