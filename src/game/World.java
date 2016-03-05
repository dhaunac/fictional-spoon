package game;

import entities.Entity;
import entities.EntityFactory;
import gen.Generator;
import gen.environment.Map;
import javafx.scene.canvas.GraphicsContext;
import static game.State.*;

public class World {

	/** SINGELTON */
	private static World singleton;

	// class components
	private Generator gen;
	private Map map;
	private EntityFactory fac;
	private Game game;

	// variables
	private int size = 5;
	private int offsetX, offsetY, viewSizeX, viewSizeY;

	public static World getWorld() {
		if (singleton == null) {

			singleton = new World();
		}
		return singleton;
	}

	private World() {
		gen = new Generator(350, 225);
		map = gen.newLevel();
		fac = EntityFactory.getFactory();
		game = Game.getGame();

		updateView();
	}

	public Map getMap() {
		return this.map;
	}

	public void setSize(int size) {
		this.size = size;

	}

	public void tick(double elapsedTime) {
		fac.getPlayer().tick(elapsedTime);
		for (Entity mob : fac.getMobs()) {
			mob.tick(elapsedTime);
		}
		fac.smartDeletNow();

		if (Events.getEvents().isESC()) {
			game.setState(MENU);
		}
		if (Events.getEvents().isM()) {
			if (game.getState() == MAP) {
				Entity player = EntityFactory.getFactory().getPlayer();

				game.setState(VIEW);
				updateView();
				initView(player.getX(), player.getY());
			} else {
				game.setState(MAP);
				updateView();
			}
			Events.getEvents().clear();
		}
	}

	public void render(GraphicsContext gc) {
		// set color and render ground tile
		// if (Game.getGame().getState() != MAP)
		for (int x = 0; x < viewSizeX; x++) {
			for (int y = 0; y < viewSizeY; y++) {
				gc.setFill(Game.getColor(map.getGround(x + offsetX, y + offsetY)));
				gc.fillRect(x * size, y * size, size, size);
			}
		}

		for (Entity mob : fac.getMobs()) {
			mob.render(gc, size, offsetX, offsetY);
		}
		fac.getPlayer().render(gc, size, offsetX, offsetY);
	}

	public void updateView() {
		// set size parameters
		if (Game.getGame().getState() == MAP) {
			size = 4;
		} else {
			size = 20;
		}

		// set view size and be sure to be smaller than the map
		viewSizeX = Math.min(1400 / size, map.getN());
		viewSizeY = Math.min(900 / size, map.getM());

		checkOffset();
	}

	public void initView(int centerX, int centerY) {
		this.offsetX = centerX - viewSizeX / 2;
		this.offsetY = centerY - viewSizeY / 2;

		checkOffset();
	}

	public void setView(int centerX, int centerY) {
		int viewPaddingX = viewSizeX / 5; // 20%
		int viewPaddingY = viewSizeY / 5;

		if (centerX - viewPaddingX < offsetX) {
			offsetX = centerX - viewPaddingX;
		}
		if (centerX + viewPaddingX - viewSizeX > offsetX) {
			offsetX = centerX + viewPaddingX - viewSizeX;
		}
		if (centerY - viewPaddingY < offsetY) {
			offsetY = centerY - viewPaddingY;
		}
		if (centerY + viewPaddingY - viewSizeY > offsetY) {
			offsetY = centerY + viewPaddingY - viewSizeY;
		}

		checkOffset();
	}

	private void checkOffset() {
		if (offsetX < 0) {
			offsetX = 0;
		}
		if (offsetY < 0) {
			offsetY = 0;
		}

		if (offsetX >= map.getN() - viewSizeX) {
			offsetX = map.getN() - viewSizeX;
		}
		if (offsetY >= map.getM() - viewSizeY) {
			offsetY = map.getM() - viewSizeY;
		}
	}
}
