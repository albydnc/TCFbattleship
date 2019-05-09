package battleship;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Cell extends Rectangle {
	    private int x, y;
	    private boolean mine;
	    public Cell (int x, int y, boolean mine, char myboard[][]) {
	        super(26, 26);
	        this.x=x;
	        this.y=y;
	        this.mine = mine;
	        setFill(Color.AQUAMARINE);
	        setStroke(Color.BLACK);
	    }

	public boolean getCellMine (Cell cell) {
		return cell.mine;
		}
	
	public int getCellX() {
		return x;
	}

	public void setCellX(int x) {
		this.x = x;
	}

	public int getCellY() {
		return y;
	}

	public void setCellY(int y) {
		this.y = y;
	}
	
	
	
	
	
	}
	



