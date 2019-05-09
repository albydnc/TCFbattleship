

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Cell extends Rectangle {
	    private int x, y;
	    private boolean mine;
	    //public Ship ship = null;
	
	public Cell (int x, int y, boolean mine) {
	        super(26, 26);
	        this.x = x;
	        this.y = y;
	        this.mine = mine;
	        setFill(Color.AQUAMARINE);
	        setStroke(Color.BLACK);
	    }

}

