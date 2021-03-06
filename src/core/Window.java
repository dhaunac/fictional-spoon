package core;

import game.control.GameControl;
import game.control.ScreenControl;
import game.screens.ComboScreen;
import game.screens.CreditsScreen;
import game.screens.FinishScreen;
import game.screens.HelpScreen;
import game.screens.MenuScreen;
import game.screens.SoulsDecorator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Window extends Application {
    public static boolean music = false;
    
    // make the stage acessible
    private static Scene scene;
    private static boolean newStage = false;
    
    // class members
    private AnimationTimer gameloop;
    private int passedTicks = 0;
    private double lastNanoTime = System.nanoTime();
    private double time = 0;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) {
        // stage settings
        stage.setTitle(Global.TITLE);
        stage.setResizable(false);
        stage.centerOnScreen();
        
        // event handling
        stage.setOnCloseRequest(event -> {
            gameloop.stop();
            // save game state here
        });
        
        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            Events.addCode(event);
        });
        
        stage.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            Events.removeCode(event);
        });
        
        ScreenControl ctrl = ScreenControl.getCtrl();
        ctrl.setScreen("menu", new SoulsDecorator(MenuScreen.getScreen()));
        ctrl.addScreen("combo", new ComboScreen());
        ctrl.addScreen("credits", new SoulsDecorator(CreditsScreen.getScreen()));
        ctrl.addScreen("help", new SoulsDecorator(new HelpScreen("menu", 2000)));
        
        ctrl.addScreen("game won", new SoulsDecorator(new FinishScreen(true)));
        ctrl.addScreen("game over", new SoulsDecorator(new FinishScreen(false)));
        // ctrl.addScreen("", );
        
        // precompute the game initialization
        GameControl.getControl();
        
        gameloop = new AnimationTimer() {
            
            @Override
            public void handle(long currentNanoTime) {
                // calculate time since last update.
                time += (currentNanoTime - lastNanoTime) / 1000000000.0;
                lastNanoTime = currentNanoTime;
                passedTicks = (int) Math.floor(time * 60.0);
                time -= passedTicks / 60.0;
                
                // adjust stage if necessary
                if (Window.newStage) {
                    newStage = false;
                    
                    stage.setScene(scene);
                }
                
                if (Events.isESC()) {
                    ctrl.setScreen("menu");
                }
                
                // compute a frame
                ctrl.tick(passedTicks);
                ctrl.render();
            }
        };
        
        stage.show();
        gameloop.start();
    }
    
    public static void setScene(Scene scene) {
        Window.newStage = true;
        Window.scene = scene;
    }
}
