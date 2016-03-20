package gameviews;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class InfoView extends GameView {

	public InfoView(Canvas layer) {
		super(layer);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void tick(int ticks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
//		clear screen
		gc.clearRect(0, 0, layer.getWidth(), layer.getHeight());
		
		gc.setFill(Color.LIGHTGREEN);
		gc.fillRect(0, 0, layer.getWidth(), layer.getHeight());
	}

}
