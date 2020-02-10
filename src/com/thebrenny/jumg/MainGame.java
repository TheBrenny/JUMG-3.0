package com.thebrenny.jumg;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseMotionListener;
import java.util.Random;

import javax.swing.JFrame;

import com.thebrenny.jumg.engine.Engine;
import com.thebrenny.jumg.gui.ScreenManager;
import com.thebrenny.jumg.input.Handler;
import com.thebrenny.jumg.input.KeyBindings;
import com.thebrenny.jumg.items.Item;
import com.thebrenny.jumg.level.tiles.Tile;
import com.thebrenny.jumg.util.ArgumentOrganizer;
import com.thebrenny.jumg.util.Images;
import com.thebrenny.jumg.util.Logger;

/**
 * The MainGame class is the core of the game. Not the {@link Engine}, but the
 * brain of the code connecting all the smaller components together. Extending
 * this class is necessary when wanting to build a game using JUMG.
 * 
 * See the demo game and tutorial on how to use JUMG in your game!
 * 
 * @author TheBrenny
 * @date 01 Aug 19
 */
public abstract class MainGame {
	/** The global instance of the game. */
	private static MainGame INSTANCE;
	/** The default scale of the game window. */
	private static float DEFAULT_SCALE = 1.0F;
	/** The default zoom of the game's content. */
	private static float DEFAULT_ZOOM = 1.0F;
	/** The default width of the game window. */
	private static int DEFAULT_WIDTH = 900;
	/**
	 * The default height of the game window. Equal to nine twelfths of the
	 * width. ({@link #DEFAULT_WIDTH} * 9/12)
	 */
	private static int DEFAULT_HEIGHT = DEFAULT_WIDTH / 12 * 9;
	public static float SCALE = DEFAULT_SCALE;
	public static float ZOOM = DEFAULT_ZOOM; // TODO: Implement zoom!
	public static int WIDTH = DEFAULT_WIDTH;
	public static int HEIGHT = DEFAULT_HEIGHT;
	public static Dimension SIZE = new Dimension((int) (WIDTH * SCALE), (int) (HEIGHT * SCALE));
	
	protected GameInfo gameInfo;
	protected JFrame frame;
	protected Display display;
	protected Handler handler;
	protected Engine engine;
	protected int engineTicks = 60;
	protected String username = System.getProperty("user.name", "Player" + new Random().nextLong());
	
	protected boolean debugging = false;
	protected boolean running = false;
	
	/**
	 * The constructor to call to begin initialising the game. Should be called
	 * within the program insertion method (aka
	 * {@code public static void main(String... args)}).
	 * 
	 * @param args
	 *        - The arguments from the command line.
	 * @param gameName
	 *        - The name of the game.
	 * @param packageRoot
	 *        - The root package director.
	 */
	public MainGame(String[] args, String gameName, String packageRoot) {
		this(args, new GameInfo(gameName, packageRoot));
	}
	/**
	 * Called from {@link #MainGame(String[], String, String)} after creating a
	 * new {@link GameInfo} object.
	 * 
	 * @param args
	 *        - The arguments from the command line.
	 * @param gi
	 *        - The GameInfo object which holds information about the game.
	 */
	protected MainGame(String[] args, GameInfo gi) {
		new ArgumentOrganizer(args);
		Logger.startSection("mainGameInit", "Starting up the game!");
		if(INSTANCE == null) INSTANCE = this;
		debugging = ArgumentOrganizer.getOrganizedArguments().boolVal("debug");
		
		this.gameInfo = gi;
		
		Logger.startSection("imageLoad", "Initialising images class.");
		new Images();
		Images.addImage("com.thebrenny.jumg.res", "gui_buttons", "splash", "hud_menu_map");
		loadImages();
		Logger.endLatestSection("Images initialised.");
		
		Logger.startSection("tileLoad", "Initialising all tiles.");
		loadTiles();
		Logger.endLatestSection("Tiles initialised.");
		
		Logger.startSection("itemLoad", "Initialising all items.");
		loadItems();
		Logger.endLatestSection("Items initialised.");
		
		Logger.startSection("frameInit", "Creating frame.");
		setDimensions(ArgumentOrganizer.getOrganizedArguments().floatVal("scale"));
		makeFrame();
		Logger.endLatestSection("Frame created and visible.");
		
		Logger.startSection("keyInit", "Setting up key bindings.");
		setupKeyBinds();
		Logger.endLatestSection("Key bindings complete.");
		
		Logger.startSection("startGame", "Starting game...");
		start();
		Logger.endSection("startGame", null);
		
		Logger.endSection("mainGameInit", null);
	}
	
	/**
	 * Scales the window of the game.
	 * 
	 * @param scale
	 *        - A scale factor of the {@link #DEFAULT_WIDTH} and
	 *        {@link #DEFAULT_HEIGHT}.
	 */
	public void setDimensions(float scale) {
		if(scale <= 1.0F) return;
		MainGame.SCALE = scale;
		MainGame.WIDTH = (int) ((float) MainGame.DEFAULT_WIDTH * scale);
		MainGame.HEIGHT = (int) ((float) MainGame.DEFAULT_HEIGHT * scale);
		MainGame.SIZE.setSize(MainGame.WIDTH, MainGame.HEIGHT);
	}
	
	/**
	 * Builds the window of the game.
	 */
	private void makeFrame() {
		this.frame = new JFrame(this.gameInfo.gameName());
		this.display = new Display(SIZE);
		this.handler = new Handler();
		
		this.frame.setPreferredSize(SIZE);
		this.frame.setMinimumSize(SIZE);
		this.frame.setMaximumSize(SIZE);
		this.frame.setSize(SIZE);
		
		this.frame.setLocationRelativeTo(null);
		this.frame.setFocusTraversalKeysEnabled(false);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.frame.addWindowListener(this.handler.winH);
		this.frame.addKeyListener(this.handler.keyH);
		this.frame.addMouseListener(this.handler.mouseH);
		
		this.frame.setResizable(false);
		this.frame.setLayout(new BorderLayout());
		
		this.display.addMouseListener(this.handler.mouseH);
		this.display.addMouseMotionListener((MouseMotionListener) this.handler.mouseH);
		this.display.setFocusTraversalKeysEnabled(false);
		
		this.frame.getContentPane().add(this.display, BorderLayout.CENTER);
		this.frame.pack();
		this.frame.setVisible(true);
	}
	
	protected void setCursor(Cursor c) {
		this.frame.setCursor(c);
	}
	
	/**
	 * The method where all images should be loaded.
	 * 
	 * @see {@link Images}
	 * @see {@link Images#addImage(String, String...)}
	 */
	protected abstract void loadImages();
	
	/**
	 * The method where all tiles should be loaded.
	 * 
	 * @see {@link Tile}
	 * @see {@link Tile#registerTile(Tile)}
	 */
	protected abstract void loadTiles();
	
	/**
	 * The method where all items should be loaded.
	 * 
	 * @see {@link Item}
	 * @see {@link Item#registerItem(Item)}
	 */
	protected abstract void loadItems();
	
	/**
	 * The method where all keybindings should be set up.
	 * 
	 * @see {@link KeyBindings}
	 * @see {@link KeyBindings#addKey(String, int, boolean, boolean)}
	 */
	protected abstract void setupKeyBinds();
	
	/**
	 * Essentially acts as the final spark plug to the {@link Engine}. This must
	 * return true for the game to actually begin running.
	 * 
	 * @return continue - True if the game should boot up. False otherwise.
	 */
	public abstract boolean initialise();
	
	/**
	 * Turns the ignition to start the engine up.
	 */
	private void start() {
		Logger.startSection("enginePrep", "Creating engine.");
		INSTANCE.engine = new Engine(engineTicks);
		new Thread(INSTANCE.engine).start();
		Logger.endSection("enginePrep", "Engine created and started.");
	}
	
	/**
	 * Called by {@link Engine#tick()} to update the game logic.
	 */
	public void update() {
		display.screenManager.update();
	}
	/**
	 * Called by {@link Engine#render()} to update the game visuals.
	 */
	public void render() {
		display.repaint();
	}
	/**
	 * Cleanly kills the game.
	 */
	public void stop() {
		Logger.log("STOPPING.");
		setRunning(false);
	}
	
	/**
	 * Sets whether the game should enter debugging mode.
	 * 
	 * @param debug
	 *        - True to enter debug. False to exit debug.
	 */
	public void setDebug(boolean debug) {
		if(debug != this.debugging) Logger.log("Visual debugging is [{0}].", (Object) (debug ? "on" : "off"));
		this.debugging = debug;
	}
	/**
	 * Returns whether or not the game is in debugging mode.
	 * 
	 * @return True if in debug mode. False otherwise.
	 */
	public boolean isDebugging() {
		return this.debugging;
	}
	
	/**
	 * Sets whether the game should start or stop running.
	 * 
	 * @param run
	 *        - Whether the game should run.
	 */
	public void setRunning(boolean run) {
		// Maybe provide logic to print stack trace if not called from stop or the engine.
		this.running = run;
	}
	/**
	 * Returns whether or not the game is currently running.
	 * 
	 * @return True if the game is currently running.
	 */
	public boolean isRunning() {
		return this.running;
	}
	
	/**
	 * Returns the {@link Display} object attached to the game window.
	 * 
	 * @return The game window's {@code Display} object.
	 */
	public Display getDisplay() {
		return display;
	}
	/**
	 * Returns the {@link ScreenManager} object attached to the game window's
	 * {@link Display} object.
	 * 
	 * @return The game window's {@code ScreenManager} object.
	 */
	public ScreenManager getScreenManager() {
		return display.screenMan();
	}
	/**
	 * Returns the input handler for the game window.
	 * 
	 * @return The {@link Handler} object for the game window.
	 */
	public Handler getHandler() {
		return handler;
	}
	/**
	 * Returns the {@link Engine} which is running the game.
	 * 
	 * @return The {@code Engine} object.
	 */
	public Engine getEngine() {
		return engine;
	}
	
	/**
	 * Returns the {@link GameInfo} object for the game.
	 * 
	 * @return The {@code GameInfo} object.
	 */
	public GameInfo getGameInfo() {
		return gameInfo;
	}
	
	/**
	 * Returns the username of the player. Set to
	 * {@code System.getProperty("user.name")}, unless changed by using
	 * {@link #setUsername(String)}.
	 * 
	 * @return The username of the player.
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * Sets the username of the player.
	 * 
	 * @param username
	 *        - The new username to use.
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Returns the current instance of the game. This should be overridden to
	 * return the actual class object of the extended class.
	 * 
	 * @return The static instance of {@code this}.
	 */
	public static MainGame getMainGame() {
		return INSTANCE;
	}
}
