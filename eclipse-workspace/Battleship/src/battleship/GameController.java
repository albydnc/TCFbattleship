package battleship;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class GameController implements Initializable {

	@FXML private VBox EnemyBox;
	@FXML private VBox PlayerBox;
	
	private char[][] MyBoard;
	private Ship Carrier = new Ship(5, true, true);
	private Ship Battleship = new Ship(4, true, true);
	private Ship Cruiser = new Ship(3, true, true);
	private Ship Submarine = new Ship(3, true, true);
	private Ship Destroyer = new Ship(2, true, true);
	//private int NumberofShips = 5;
	
	public void placeBattleship() {
		for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Cell c = getPlayerCell(x, y);
                c.setOnMouseClicked(event -> placeShip(Battleship,c.getCellX(),c.getCellY()));
            }

	}
	}
	
	public void placeCruiser() {
		for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Cell c = getPlayerCell(x, y);
                c.setOnMouseClicked(event -> placeShip(Cruiser,c.getCellX(),c.getCellY()));
            }

	}
	}
	
	public void placeSubmarine() {
		for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Cell c = getPlayerCell(x, y);
                c.setOnMouseClicked(event -> placeShip(Submarine,c.getCellX(),c.getCellY()));
            }

	}
	}
	
	public void placeDestroyer() {
		for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Cell c = getPlayerCell(x, y);
                c.setOnMouseClicked(event -> placeShip(Destroyer,c.getCellX(),c.getCellY()));
            }

	}
	}
	
	public void placeCarrier() {
		for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Cell c = getPlayerCell(x, y);
                c.setOnMouseClicked(event -> placeShip(Carrier,c.getCellX(),c.getCellY()));
            }

	}
	}
	
	public void changeOrientation() {
		Carrier.vertical=!Carrier.vertical;
		Battleship.vertical=!Battleship.vertical;
		Cruiser.vertical=!Cruiser.vertical;
		Submarine.vertical=!Submarine.vertical;
		Destroyer.vertical=!Destroyer.vertical;
	}
	
	
	boolean canPlaceShip(Ship ship, int x, int y) {
		if (ship.vertical) {
			if (y+ship.Length<=10) {
				for (int i = y; i < y+ship.Length; i++) {
					if (MyBoard[x][i]=='S') {
						return false;
					}
				}
				return true;
			}
			else {
				return false;
			}
		}
		else {
			if (x+ship.Length<=10) {
				for (int i = x; i < x+ship.Length; i++) {
					if (MyBoard[i][y]=='S') {
						return false;
					}
				}
				return true;
			
			}
			else {
				return false;
			}
		}
		}
	
	public void placeShip(Ship ship, int x, int y) {
	    if (canPlaceShip(ship, x, y)) {
	    	if (ship.control) {
	        int length = ship.Length;

	        if (ship.vertical) {
	            for (int i = y; i < y + length; i++) {
	                Cell cell = getPlayerCell(x, i);
	                MyBoard[x][i]='S';
	                if (cell.getCellMine(getPlayerCell(x,i))) {
	                    cell.setFill(Color.GREEN);
	                    cell.setStroke(Color.BLACK);
	                }
	            }
	        }
	        else {
	            for (int i = x; i < x + length; i++) {
	                Cell cell = getPlayerCell(i, y);
	                MyBoard[i][y]='S';
	                if (cell.getCellMine(getPlayerCell(i,y))) {
	                    cell.setFill(Color.GREEN);
	                    cell.setStroke(Color.BLACK);
	                }
	            }
	        }
	    ship.control=false;	
	    }
	    	else {
	    		//startgame();
	    	}
	}
	}
	
	
	
	public Cell getPlayerCell(int x, int y) {
		HBox box = (HBox) PlayerBox.getChildren().get(y);
        Cell cell = (Cell) box.getChildren().get(x);
        return cell;
        
    }
	
	public void reset() {
		for (int y=0; y<10; y++) {
			for (int x=0; x<10; x++) {
				Cell cell = getPlayerCell(x, y);
                MyBoard[x][y]='E';
                cell.setFill(Color.AQUAMARINE);
			}
		}
		Carrier.control=true;
		Carrier.vertical=true;
		Battleship.control=true;
		Battleship.vertical=true;
		Cruiser.control=true;
		Cruiser.vertical=true;
		Submarine.control=true;
		Submarine.vertical=true;
		Destroyer.control=true;
		Destroyer.vertical=true;
	}
	
	public void onStart() throws IOException {
		String banana = "gianni";
		Udp UDP = new Udp();
		UDP.send(banana);
		UDP.receive();
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
		MyBoard= new char[10][10];
		
		for (int y = 0; y < 10; y++) {
            HBox column = new HBox();
            for (int x = 0; x < 10; x++) {
                Cell c = new Cell(x, y, true, MyBoard);
              //c.setOnMouseClicked(event -> placeShip(Carrier,c.getCellX(),c.getCellY()));
                column.getChildren().add(c);
                MyBoard[x][y]='E';
            }

            PlayerBox.getChildren().add(column);
        }
		
		for (int y = 0; y < 10; y++) {
            HBox column = new HBox();
            for (int x = 0; x < 10; x++) {
                Cell c = new Cell(x, y, true, MyBoard);
                //c.setOnMouseClicked(handler);
                column.getChildren().add(c);
            }

            EnemyBox.getChildren().add(column);
        }
		
		//System.out.println(getPlayerCell(1,1).getCellX()+"/"+ getPlayerCell(1,1).getCellY());
		
	}
}
	


