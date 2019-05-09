package battleship;

import javafx.scene.Parent;

public class Ship extends Parent {
	    public int Length;
	    public boolean vertical = true;
	    public boolean control = true;

	    private int health;

	    public Ship(int Length, boolean vertical, boolean control) {
	        this.Length = Length;
	        this.vertical = vertical;
	        this.control = control;
	        health = Length;

	        /*VBox vbox = new VBox();
	        for (int i = 0; i < type; i++) {
	            Rectangle square = new Rectangle(30, 30);
	            square.setFill(null);
	            square.setStroke(Color.BLACK);
	            vbox.getChildren().add(square);
	        }
	        getChildren().add(vbox);*/
	    }

	    public void hit() {
	        health--;
	    }

	    public boolean isAlive() {
	        return health > 0;
	    }
	}

