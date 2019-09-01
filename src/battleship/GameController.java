package battleship;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;



public class GameController implements Initializable, MqttCallback {
	@FXML private VBox EnemyBox;
	@FXML private VBox PlayerBox;
	@FXML private VBox chatBox;
	@FXML private ScrollPane chatContainer;
	@FXML private Label connectionStatus;
	@FXML private Label yourLife;
	@FXML private Label oppLife;
	@FXML private Label orientationLabel;
	@FXML private Button startButton;
	@FXML private Button battleshipBtn;
	@FXML private Button cruiserBtn;
	@FXML private Button submarineBtn;
	@FXML private Button destroyerBtn;
	@FXML private Button carrierBtn;
	@FXML private Button resetBtn;
	@FXML private Button connectBtn;
	@FXML private Button orientationBtn;
	@FXML private TextField enemyName;
	@FXML private TextField userName;
	@FXML private TextField chatSend;
	private List<Label> messages = new ArrayList<>();
	
	private char[][] MyBoard;
	private char[][] EnemyBoard;
	public boolean isServer;
	private Ship Carrier = new Ship(5, true, true);
	private Ship Battleship = new Ship(4, true, true);
	private Ship Cruiser = new Ship(3, true, true);
	private Ship Submarine = new Ship(3, true, true);
	private Ship Destroyer = new Ship(2, true, true);
	private int life = 17;
	private int enemyLife = 17;
	int a,b;
	boolean shot = false, hasStarted = false;
	int xRecv,yRecv;
	int index = 0;
	StringProperty connStatus = null;
	StringProperty oppString = new SimpleStringProperty();
	StringProperty yourString = new SimpleStringProperty();
	StringProperty chatString = new SimpleStringProperty();
	String username = null, enemyname=null;
	int ships = 5;
	MqttClient mqttClient = null;
	String recvTopic = null, sendTopic = null;
	String chatTopic = "/battleship/chat";
	public void placeBattleship() {
		battleshipBtn.setDisable(true);
		ships --;
		for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Cell c = getPlayerCell(x, y);
                c.setOnMouseClicked(event -> placeShip(Battleship,c.getCellX(),c.getCellY()));
            }

	}
	}
	
	public void placeCruiser() {
		cruiserBtn.setDisable(true);
		for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Cell c = getPlayerCell(x, y);
                c.setOnMouseClicked(event -> placeShip(Cruiser,c.getCellX(),c.getCellY()));
            }

	}
	}
	
	public void placeSubmarine() {
		submarineBtn.setDisable(true);
		for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Cell c = getPlayerCell(x, y);
                c.setOnMouseClicked(event -> placeShip(Submarine,c.getCellX(),c.getCellY()));
            }

	}
	}
	
	public void placeDestroyer() {
		destroyerBtn.setDisable(true);
		for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Cell c = getPlayerCell(x, y);
                c.setOnMouseClicked(event -> placeShip(Destroyer,c.getCellX(),c.getCellY()));
            }

	}
	}
	
	public void placeCarrier() {
		carrierBtn.setDisable(true);
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
		if(Destroyer.vertical) {
			orientationLabel.setText("Vertical");
		}else {
			orientationLabel.setText("Horizontal");
		}
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
	    
	    System.out.println(ships);
		if(ships < 1) {
			orientationBtn.setDisable(true);
			if(connectBtn.isDisabled()){
				startButton.setDisable(false);
				connStatus.setValue("Start the Game");
			}else {
				connStatus.setValue("Press connect");
			}
			
		}
		ships --;
	    }
	}
	    
	}
	
	
	
	public Cell getPlayerCell(int x, int y) {
		HBox box = (HBox) PlayerBox.getChildren().get(y);
        Cell cell = (Cell) box.getChildren().get(x);
        return cell;
        
    }
	
	public Cell getEnemyCell(int x, int y) {
		HBox box = (HBox) EnemyBox.getChildren().get(y);
        Cell cell = (Cell) box.getChildren().get(x);
        return cell;
        
    }
	
	public void reset() {
		connStatus.setValue("RESET");
		for (int y=0; y<10; y++) {
			for (int x=0; x<10; x++) {
				Cell cell = getPlayerCell(x, y);
                MyBoard[x][y]='E';
                cell.setFill(Color.AQUAMARINE);
			}
		}
		battleshipBtn.setDisable(false);
		carrierBtn.setDisable(false);
		submarineBtn.setDisable(false);
		cruiserBtn.setDisable(false);
		destroyerBtn.setDisable(false);
		orientationBtn.setDisable(false);
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
		orientationLabel.setText("Vertical");
		ships = 5;
	}
	
	public void resetAll() {
		connStatus.setValue("RESET");
		for (int y=0; y<10; y++) {
			for (int x=0; x<10; x++) {
				Cell cell = getPlayerCell(x, y);
				Cell cellE = getEnemyCell(x,y);
                MyBoard[x][y]='E';
                EnemyBoard[x][y]='E';
                cell.setFill(Color.AQUAMARINE);
                cellE.setFill(Color.AQUAMARINE);
			}
		}
		battleshipBtn.setDisable(false);
		carrierBtn.setDisable(false);
		submarineBtn.setDisable(false);
		cruiserBtn.setDisable(false);
		destroyerBtn.setDisable(false);
		orientationBtn.setDisable(false);
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
		orientationLabel.setText("Vertical");
		ships = 5;
		life = 17;
		enemyLife = 17;
	}	
	
	public void sendMessage(KeyEvent key) {
		if(key.getCode() == KeyCode.ENTER) {
			String msg = "from "+username+": "+chatSend.getText();
			try {
				mqttClient.publish(chatTopic, msg.getBytes(), 0, false);
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void onConnect() {
		username = userName.getText();
		enemyname = enemyName.getText();
		if(username.isEmpty() || enemyname.isEmpty()) {
			connStatus.setValue("Missing Usernames!");
			return;
		}
		if(username.equals(enemyname)) {
			connStatus.setValue("Same usernames!");
			return;
		}

		System.out.println("connecting");
		try {
			mqttClient = new MqttClient( 
				    "tcp://mqtt.eclipse.org:1883", //URI 
				    MqttClient.generateClientId(), //ClientId 
				    new MemoryPersistence());
			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(10);
			mqttClient.setCallback(this);
			mqttClient.connect(options);
			
			recvTopic = "/battleship/"+userName.getText()+"/game";
			sendTopic = "/battleship/"+enemyName.getText()+"/game";
			//chatPanel.textProperty().bind(chatString);
			mqttClient.subscribe(chatTopic);
			mqttClient.subscribe(recvTopic);
			String msg = "hello from "+userName.getText();
			mqttClient.publish(chatTopic, msg.getBytes(), 0, false);
			connectBtn.setDisable(true);
			if(orientationBtn.isDisabled()) {
				startButton.setDisable(false);
				connStatus.setValue("Start the Game");
			}else {
				connStatus.setValue("Place the Ships");
			}
		}catch(MqttException e) {
			e.printStackTrace();
		}
	}
	
	public void onStart() throws IOException {
		oppString.setValue(Integer.toString(enemyLife));
		yourString.setValue(Integer.toString(life)); 
		oppLife.textProperty().bind(oppString);
		yourLife.textProperty().bind(yourString);
		System.out.print("ready");
		for (int y=0; y<10; y++) {
			for (int x=0; x<10; x++) {
				Cell cell = getEnemyCell(x, y);
				cell.setOnMouseClicked(event -> shoot(cell.getCellX(),cell.getCellY()));
			}
		}
		if(!hasStarted) connStatus.setValue("Waiting...");
		try {
			mqttClient.publish(sendTopic, "START".getBytes(), 0, false);
		} catch (MqttException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	

	}
	public void shoot (int x, int y){
		if(shot == true) return;
		if(life < 1) {
			connStatus.setValue("You Lost.");
			return;
		}
		if(EnemyBoard[x][y]=='H' || EnemyBoard [x][y] == 'M') {
			connStatus.setValue("You already shot this cell.");
			return;
		}
		
		char hit = MyBoard[xRecv][yRecv];
		String msg = String.format("%d%d%c", x,y,hit);
		System.out.print(msg);
		try {
			mqttClient.publish(sendTopic, msg.getBytes(), 0, false);
			shot =true;
		} catch (MqttException e) {
			e.printStackTrace();
		}
		a=x;
		b=y;
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Platform.runLater(new Runnable() {
		            @Override public void run() {
		            	oppString.setValue(Integer.toString(enemyLife));
		        		yourString.setValue(Integer.toString(life));   
		        		connStatus.setValue("Opponent's Turn.");
		            }
				});
	            return null;
	        }
		};
		new Thread(task).start();
	}
	
	public char getHit(int x, int y) {
		if (MyBoard[x][y]=='S') {
			return 'H';
				}
		else {
			return 'M';
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		MyBoard= new char[10][10];
		EnemyBoard = new char [10][10];
		connStatus = new SimpleStringProperty();
		connStatus.setValue("Place the ships.");
		connectionStatus.textProperty().bind(connStatus);
		orientationLabel.setText("Vertical");
	    chatContainer.setContent(chatBox); 
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
                Cell c = new Cell(x, y, false, MyBoard);
                //c.setOnMouseClicked(handler);
                column.getChildren().add(c);
            }

            EnemyBox.getChildren().add(column);
        }
		
		//System.out.println(getPlayerCell(1,1).getCellX()+"/"+ getPlayerCell(1,1).getCellY());
		
	}

	@Override
	public void connectionLost(Throwable arg0) {

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {

	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		String payload = new String(message.getPayload());
	    System.out.println(topic + ": " + payload);
	    
		if(topic.equals(recvTopic)) {
		String rcv = "";
		rcv = new String(message.getPayload());
		
		Cell c = getEnemyCell(a,b);
		if(rcv.equals("START")) {
			hasStarted = true;
			Platform.runLater(new Runnable() {
	            @Override public void run() {
			if(recvTopic.compareTo(sendTopic) > 0) {
				isServer = true;
				shot = false;
				connStatus.setValue("Your turn.");
			}else {
				isServer = false;
				shot = true;
				connStatus.setValue("Opponent's Turn");
			}
			resetBtn.setDisable(true);
	            }
			});
			return;
		}
		if(rcv.equals("GAME OVER")) {
			c.setFill(Color.RED);
			Platform.runLater(new Runnable() {
	            @Override public void run() {
	            	connStatus.setValue("You Won.");
	            	Alert alert = new Alert(AlertType.INFORMATION);
	    			alert.setTitle("GAME OVER");
	    			alert.setHeaderText(null);
	    			alert.setContentText("You Won! Would you like to start again?");
	    			ButtonType Yes = new ButtonType("Yes");
	    			ButtonType No = new ButtonType("No");
	    			alert.getButtonTypes().setAll(Yes,No);
	    			Optional<ButtonType> result = alert.showAndWait();
	    			if (result.get() == Yes){
	    			    resetAll();
	    			} else {
	    				System.exit(0);
	    			}
	            }
			});
			
			return;
		}
		//System.out.print(rcv);
		xRecv = Character.getNumericValue(rcv.charAt(0));
		yRecv = Character.getNumericValue(rcv.charAt(1));
		char Colpito = rcv.charAt(2);
		System.out.println(Colpito);
		Cell cell = getPlayerCell(xRecv,yRecv);
		if (Colpito=='H') {
			EnemyBoard[a][b]='H';
			c.setFill(Color.RED);	
			enemyLife --;
			
		}
		if(Colpito == 'M'){
			EnemyBoard[a][b]='M';
			c.setFill(Color.DARKBLUE);
		}
		
		if (MyBoard[xRecv][yRecv]=='S') {
			MyBoard[xRecv][yRecv]='H';
			cell.setFill(Color.RED);
			life--;
			
			
		}
		else {
			MyBoard[xRecv][yRecv]='M';
			cell.setFill(Color.DARKBLUE);
		}
		if(life < 1) {
			
			Platform.runLater(new Runnable() {
	            @Override public void run() {
	            	connStatus.setValue("You Lost.");
	            	
	            	Task<Void> task = new Task<Void>() {
	        			@Override
	        			protected Void call() throws Exception {
	        					try {
	        						mqttClient.publish(sendTopic, "GAME OVER".getBytes(), 0, false);
	        					}catch(MqttException e) {
	        						e.printStackTrace();
	        					}
	        	            return null;
	        	        }
	        		};
	        		System.out.println("test");
	        		new Thread(task).start();
	            	Alert alert = new Alert(AlertType.INFORMATION);
	    			alert.setTitle("GAME OVER");
	    			alert.setHeaderText(null);
	    			alert.setContentText("You Lost! Would you like to start again?");
	    			ButtonType Yes = new ButtonType("Yes");
	    			ButtonType No = new ButtonType("No");
	    			alert.getButtonTypes().setAll(Yes,No);
	    			Optional<ButtonType> result = alert.showAndWait();
	    			if (result.get() == Yes){
	    			    resetAll();
	    			} else {
	    				System.exit(0);
	    			}
	            }
			});
			
			
		}else {
		System.out.println(String.format("Your Life: %d, Enemy Life: %d", life,enemyLife));
		shot = false;
		Platform.runLater(new Runnable() {
            @Override public void run() {
            	oppString.setValue(Integer.toString(enemyLife));
        		yourString.setValue(Integer.toString(life));  
            	connStatus.setValue("Your Turn.");
            }
		});
		}
	}else if(topic.equals(chatTopic)) {
		String rcv = new String(message.getPayload());
	    Platform.runLater(new Runnable() {
            @Override public void run() {
            	String chat = chatString.getValueSafe()+rcv+'\n';
            	chatString.setValue(chat);
            	messages.add(new Label(rcv));
            	messages.get(index).setMinWidth(160);
            	messages.get(index).setMaxWidth(160);
            	messages.get(index).setWrapText(true);
            	messages.get(index).setMinHeight(Region.USE_PREF_SIZE);
            	if(rcv.contains("from "+enemyname)) {
            		messages.get(index).setBackground(new Background(new BackgroundFill(Color.INDIANRED, CornerRadii.EMPTY, Insets.EMPTY)));
            	}else {
            		messages.get(index).setBackground(new Background(new BackgroundFill(Color.DARKTURQUOISE, CornerRadii.EMPTY, Insets.EMPTY)));
            	}
            	chatBox.heightProperty().addListener(observable -> chatContainer.setVvalue(1D));
            	chatBox.getChildren().add(messages.get(index));
                index++;
            }
		});
	}
	}
}
	


