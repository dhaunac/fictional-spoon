package game;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import static game.State.*;

import combat.Combat;
import entities.Entity;
import entities.EntityFactory;

public class Window extends Application {
	// constants
	public static final int SIZE_X = 1392, SIZE_Y = 896;

	// class members
	private AnimationTimer gameloop;

	private Game game;
	private World lvl;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		game = Game.getGame();
		lvl = World.getWorld();

		// root objects
		Group root = new Group();
		Scene scene = new Scene(root, SIZE_X, SIZE_Y, Paint.valueOf("#212121"));

		// main stage settings
		
		stage.setTitle("Soul Harvester");
		stage.setResizable(false);
		stage.setScene(scene);

		stage.setOnCloseRequest(event -> {
			gameloop.stop();
			// save game state here
			// System.out.println("game is saved");
		});

		// key events
		stage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {
				Events.getEvents().addCode(e);
			}
		});
		stage.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {
				Events.getEvents().removeCode(e);
			}
		});
		

		Menu menu = new Menu();
		menu.setList(new String[] { "Start", "Combat", "Help", "Credits", "Exit" });

		Canvas layerMain = new Canvas(SIZE_X, SIZE_Y);
		Canvas layerMsg = new Canvas(100, 100);

		// mainLayer.setCache(true);
		// canvas.setCacheShape(true);
		root.getChildren().add(layerMain);
		root.getChildren().add(layerMsg);
		layerMsg.relocate(100, 100);

		Font font = Font.font("Helvetica", FontWeight.BOLD, 24);
		layerMsg.getGraphicsContext2D().setFont(font);
		layerMsg.getGraphicsContext2D().setFill(Color.ALICEBLUE);
		layerMsg.getGraphicsContext2D().fillText("hello", 0, 0);

		GraphicsContext gc = layerMain.getGraphicsContext2D();

		gameloop = new AnimationTimer() {
			private int passedTicks = 0;
			private double lastNanoTime = System.nanoTime();
			private double time = 0;

			public void handle(long currentNanoTime) {
				// calculate time since last update.
				time += (currentNanoTime - lastNanoTime) / 1000000000.0;
				lastNanoTime = currentNanoTime;
				passedTicks = (int) Math.floor(time * 60.0);
				time -= passedTicks / 60.0;

				if (Events.getEvents().isESC()) {
					game.setState(MENU);
				}

				// compute a frame
				gc.clearRect(0, 0, SIZE_X, SIZE_Y);

				switch (game.getState()) {
				case MENU:
					stage.setScene(menu.getScene());
					menu.tick(passedTicks);
					menu.render();
					if (menu.isStarted()) {
						Entity player = EntityFactory.getFactory().getPlayer();
						// lvl.updateView();
						lvl.initCamera(player.getX(), player.getY());
						game.setState(VIEW);
					}
					break;

				case MAP:
				case VIEW:
					lvl.tick(time);
					lvl.render(gc);
					break;

				case COMBAT:
					Combat.startCombat(null, null).tick(passedTicks);
					Combat.startCombat(null, null).render(gc);
					break;

				default:
					throw new IllegalArgumentException("Unknown game state: " + game.getState());
				}
			}
		};

		stage.show();
		gameloop.start();
	}
}
