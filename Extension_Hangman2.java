
/*
 * File: Extension_Hangman.java
 * ------------------
 * This program improves some UX of the game Hangman:
 * (1) Add sound effects.
 * (2) Allow player to play G_TURNS of games.
 * (3) Karel free falls when player loses all the games.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Extension_Hangman2 extends ConsoleProgram {

	/***********************************************************
	 * CONSTANTS *
	 ***********************************************************/

	/* The number of guesses in one game of Hangman */
	private static final int N_GUESSES = 7;
	/* The width and the height to make the karel image */
	private static final int KAREL_SIZE = 150;
	/* The y-location to display karel */
	private static final int KAREL_Y = 230;
	/* The width and the height to make the parachute image */
	private static final int PARACHUTE_WIDTH = 300;
	private static final int PARACHUTE_HEIGHT = 130;
	/* The y-location to display the parachute */
	private static final int PARACHUTE_Y = 50;
	/* The y-location to display the partially guessed string */
	private static final int PARTIALLY_GUESSED_Y = 430;
	/* The y-location to display the incorrectly guessed letters */
	private static final int INCORRECT_GUESSES_Y = 460;
	/* The fonts of both labels */
	private static final String PARTIALLY_GUESSED_FONT = "Courier-36";
	private static final String INCORRECT_GUESSES_FONT = "Courier-26";
	/* The total number of turns a player can play */
	private static final int G_TURNS = 3;
	/* Time interval between printing one line and another */
	private static final int PRINTLN_DELAY = 400;
	/* Time interval between one game and another */
	private static final int GAME_DELAY = 1000;
	/* Time interval of karel falling */
	private static final int FALL_DELAY = 80;
	/* Falling speed of karel */
	private static final int FALL_SPEED = 30;

	/***********************************************************
	 * Instance Variables *
	 ***********************************************************/

	/* An object that can produce pseudo random numbers */
	private RandomGenerator rg = new RandomGenerator();

	// Instance variables that are going to be called throughout the program:
	private GCanvas canvas = new GCanvas();
	private int wrongGuesses = 0; // Total number of wrong guesses. Initialized
									// as 0.
	private String guess = ""; // The result of the player's guess. To be
								// initialized when one random word is generated
								// for the game, and updated whenever player
								// makes a guess.
	private String answer = ""; // aka the word generated at the beginning of
								// the game.
	private String wrongChars = ""; // Shows on the canvas all the previous
									// wrong guesses.
	private GLabel guessLabel = new GLabel(""); // Shows the value of "guess" on
												// the canvas.
	private GLabel wrongCharsLabel = new GLabel(""); // Shows the value of
														// "wrongChars" on the
														// canvas.
	private int turnsLeft = G_TURNS; // Shows how many more turns player can
										// play.
	private int hint = 0; // Holds the value of hints given per game.
	

	// Import and define the images to be shown on the canvas.
	private GImage bg = new GImage("background.jpg");
	private GImage parachute = new GImage("parachute.png");
	private GImage karel = new GImage("karel.png");
	private GImage karelFlipped = new GImage("karelFlipped.png");
	
	AudioClip wind = MediaTools.loadAudioClip("wind.au");
	AudioClip breaks = MediaTools.loadAudioClip("breaks.au");

	// Define two arraylists to include the lines connected to the parachute.
	// One is for drawing the lines in order, the other is for breaking them in
	// order.
	private ArrayList<GLine> lines = new ArrayList<GLine>();
	private ArrayList<GLine> linesBreak = new ArrayList<GLine>();

	// Create the Arraylist and import the lexicon into it.
	private ArrayList<String> lexicon = new ArrayList<String>();
	

	/***********************************************************
	 * Methods *
	 ***********************************************************/

	public void run() {
		
		introduce();
		
		wind.loop();
		while (turnsLeft > 0) {
			playOneGame();
			
		}
		println ("Argh.........");
		pause(PRINTLN_DELAY);
		println("You killed Karel!!!");
		killKarel();

	}
	

	private void killKarel() {
		while (true){
			karelFlipped.move(0, FALL_SPEED);
			pause(FALL_DELAY);
		}		
	}


	private void introduce() {
		println("Welcome to Hangman!");
		pause(PRINTLN_DELAY);
		println("You will play " + G_TURNS + " games of Hangman.");
		pause(PRINTLN_DELAY);
		println("In each game, you will have " + N_GUESSES + " chances to guess.");
		pause(PRINTLN_DELAY);
		println("If you run out of guesses, you lose the game,");
		pause(PRINTLN_DELAY);
		println("and poor Karel will fall but be revived;");
		pause(PRINTLN_DELAY);
		println("But if you lose all the lives...");
		pause(PRINTLN_DELAY);
		println("T_T..............");
		pause(PRINTLN_DELAY);
		println("Save Karel!");	
		println("");
		pause(PRINTLN_DELAY);
	}

	private void playOneGame() {
		
		// Initialize the variables before each game:
		wrongGuesses = 0;
		guess = ""; 
		answer = ""; 
		wrongChars = ""; 
		hint = 0;
		canvas.removeAll(); // Wipe everything out of the previous game.
		
		setUp();// Set up the canvas and all the images needed for the game.

		// Introduce the player to the game and randomly generate a word for
		// guess.
		// For a single game, the length of the given word is determined.
		
		answer = getRandomWord();
		int len = answer.length();

		// The initial value before guess would be many dashes '-'.
		for (int i = 0; i < len; i++) {
			guess = guess + '-';
		}

		addLabels();// After generating the word, place the label onto the canvas.		
		showTurnsLeft(); // Tell the player how many more turns they have.		

		// Game starts. Player can play until guesses run out, or until they win.
		while (wrongGuesses < N_GUESSES && !win()) {
			play();
		}

		// Define the events on a win or a failure.
		if (win()) {
			println("You win.");
			println("The word was " + answer + ".");
		}
		if (wrongGuesses == N_GUESSES) {
			turnsLeft --; // Player loses one turn.
			canvas.remove(karel);
			canvas.add(karelFlipped, canvas.getWidth() * 0.5 - KAREL_SIZE * 0.5, KAREL_Y);
			println("You're completely hung.");
			println("The word was: " + answer + ".");
			println("");
			pause(GAME_DELAY);
		}

	}

	private void showTurnsLeft() {
		if (turnsLeft == G_TURNS){
			println("You are now in game one.");
		} else if (turnsLeft < G_TURNS && turnsLeft > 1){
			println("Now you can play " + turnsLeft + " more times.");
		} else if (turnsLeft ==1){
			println("Now you only have " + turnsLeft + " more chance.");
		}		
	}


	/*
	 * Method addLabels adds the initial value of the string guess (which is
	 * "----...") to the canvas below karel. The value of the string is updated
	 * through other methods later on.
	 */
	private void addLabels() {
		guessLabel.setLabel(guess);
		guessLabel.setFont(PARTIALLY_GUESSED_FONT);
		guessLabel.setColor(Color.BLACK);
		canvas.add(guessLabel, canvas.getWidth() * 0.5 - guessLabel.getWidth() * 0.5, PARTIALLY_GUESSED_Y);

		wrongCharsLabel.setFont(INCORRECT_GUESSES_FONT);
	}

	private void setUp() {
		openFile(); // import lexicon.txt

		drawBackground(); // import the sky pic
		drawParachute(); // import the parachute pic
		drawKarel(); // place karel
		drawLines(); // attach strings to karel and the parachute

	}

	/*
	 * Method openFile imports lexicon.txt into the string arraylist - lexicon.
	 */
	private void openFile() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("HangmanLexicon.txt"));
			String readLine = br.readLine();
			while (readLine != null) {
				lexicon.add(readLine);
				readLine = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Method drawLines draws seven lines (strings attached to karel from the
	 * parachute). Each line is also added to GLine arraylist lines as an object
	 * since later they will be removed from canvas separately.
	 */
	private void drawLines() {
		
		// Clear everything in the two lines arraylists before each game.
		lines.clear();
		linesBreak.clear();
		
		double x = parachute.getX();
		double deltaX = PARACHUTE_WIDTH / (N_GUESSES - 1);
		double y1 = parachute.getY() + PARACHUTE_HEIGHT;
		double x2 = canvas.getWidth() * 0.5;
		double y2 = karel.getY();

		// Draw seven lines using the coordinates and intervals set above.
		for (int i = 0; i < N_GUESSES; i++) {
			GLine line = new GLine(x + deltaX * i, y1, x2, y2);
			canvas.add(line); // add lines to canvas.
			lines.add(line); // add lines to arraylist "lines".
		}

		// Since lines break in a different order from how they are added,
		// add the lines to a different arraylist "linesBreak",
		// rearranging the order into the right one in which they'll break,
		// so that the rightmost line breaks first, then the leftmost one, etc.
		linesBreak.add(lines.get(6)); // linesBreak(0)
		linesBreak.add(lines.get(0)); // linesBreak(1)
		linesBreak.add(lines.get(5)); // etc...
		linesBreak.add(lines.get(1));
		linesBreak.add(lines.get(4));
		linesBreak.add(lines.get(2));
		linesBreak.add(lines.get(3));

	}

	public void init() {
		add(canvas); // Initialize canvas.
	}

	private void drawBackground() {
		bg.setSize(canvas.getWidth(), canvas.getHeight());
		canvas.add(bg, 0, 0); // Add bluesky image.
	}

	private void drawParachute() {
		parachute.setSize(PARACHUTE_WIDTH, PARACHUTE_HEIGHT);
		double x = canvas.getWidth() * 0.5 - PARACHUTE_WIDTH * 0.5;

		canvas.add(parachute, x, PARACHUTE_Y); // Add and center parachute.
	}

	private void drawKarel() {
		karel.setSize(KAREL_SIZE, KAREL_SIZE);
		double x = canvas.getWidth() * 0.5 - KAREL_SIZE * 0.5;
		canvas.add(karel, x, KAREL_Y);

		karelFlipped.setSize(KAREL_SIZE, KAREL_SIZE); // Add and center karel.
	}

	/*
	 * Boolean win sets the condition that stops the game once the player gets
	 * the right answer.
	 */
	private boolean win() {
		if (guess.equals(answer))
			return true;
		return false;
	}

	/*
	 * Method play defines the logic of play and the update principles of the
	 * (instance) variables.
	 */
	private void play() {
		
		checkIfToGiveHint();
		
		int len = answer.length();

		// charCount changes if the player's guess matches at least one
		// character in the word.
		// It will be zero if player's got a wrong guess.
		int charCount = 0;


		// Read what player enters.
		String guessChar = readLine("Your guess: ");

		// What player enters should be a single letter, therefore:
		boolean moreDigit = guessChar.length() != 1;
		boolean notLetter = guessChar.length() == 1 && (guessChar.charAt(0) < 'A'
				|| (guessChar.charAt(0) > 'Z' && guessChar.charAt(0) < 'a') || guessChar.charAt(0) > 'z');
		boolean notValid = moreDigit || notLetter;

		// While entry is not valid, ask the player to re-enter.
		while (notValid) {
			guessChar = readLine("Please enter only ONE LETTER: ");

			// Update booleans so that notValid will be rechecked at the start
			// of the loop.
			moreDigit = guessChar.length() != 1;
			notLetter = guessChar.length() == 1 && (guessChar.charAt(0) < 'A'
					|| (guessChar.charAt(0) > 'Z' && guessChar.charAt(0) < 'a') || guessChar.charAt(0) > 'z');
			notValid = moreDigit || notLetter;
		}

		for (int i = 0; i < len; i++) {
			char ch = answer.charAt(i); // Scan each character in the given
										// word.
			String str = Character.toString(ch); // And make that character a
													// string so that it can be
													// compared.

			// If the validly entered string is the same as some of the
			// characters in the word,
			// string "guess" is updated to show that correct guess.
			// charCount will no longer be zero.
			if (guessChar.toLowerCase().equals(str.toLowerCase())) {
				guess = guess.substring(0, i) + ch + guess.substring(i + 1);
				charCount++;
			}

		}

		// If player didn't get it right, then:
		if (charCount == 0) {
			println("There are no " + guessChar.toUpperCase() + "'s in the word.");
			wrongGuesses++; // One more wrong guess.

			// Update wrongChars to include all incorrect guesses.
			wrongChars = (wrongChars + guessChar).toUpperCase(); 
			// Set the label value to be string wrongChars
			wrongCharsLabel.setLabel(wrongChars); 
			// It should be repositioned.
			wrongCharsLabel.setLocation((canvas.getWidth() - wrongCharsLabel.getWidth()) * 0.5, INCORRECT_GUESSES_Y); 
			canvas.add(wrongCharsLabel); // Re-add label to canvas.

			// The first wrong guess will cause the rightmost line to break,
			// that is, the first element in the linesBreak arraylist (index 0).
			canvas.remove(linesBreak.get(wrongGuesses - 1));
			breaks.play();

		} else {
			println("That guess is correct.");
			guessLabel.setLabel(guess);
		}

	}

	private void checkIfToGiveHint() {
		// Set two conditions to decide if to given the player a hint.
		// (1) When player arrives at the third last guess;
		// (2) And player still hasn't got half the word right.
		// Hint is only given once per game.
		int n_CorrectGuesses = 0;
		
		// Create an arraylist to store the indices of the chars unguessed.
		ArrayList<Integer> indicesUnguessed = new ArrayList<Integer>();
				
		for (int i = 0; i < guess.length(); i++){
			char ch = guess.charAt(i);
			if (ch != '-'){
				n_CorrectGuesses ++;				
			} else {
				indicesUnguessed.add(i);
			}
		}
		
		boolean giveHint = n_CorrectGuesses <= guess.length() / 2 && wrongGuesses == N_GUESSES - 2;
		
		if (giveHint && hint == 0){
			
			println ("Are you stuck? Here is a hint for you.");
			// Randomly give a char that has not been guessed by the user.
			int randomIndex = rg.nextInt(indicesUnguessed.size());
			guess = guess.substring(0, randomIndex) + answer.charAt(randomIndex) + guess.substring(randomIndex + 1);
			hint ++;
			
			println("Your word now looks like this: " + guess);
			guessLabel.setLabel(guess);
			
			println("You have " + (N_GUESSES - wrongGuesses) + " guess(es) left.");
		} else {

			// If no hint is given, print regular messages.
			println("Your word now looks like this: " + guess);
			println("You have " + (N_GUESSES - wrongGuesses) + " guess(es) left.");
		}
		
		
	}


	/**
	 * Method: Get Random Word ------------------------- This method returns a
	 * word to use in the hangman game. It randomly selects from among the
	 * arrayList "lexicon" that contains the "lexicon.txt".
	 */
	private String getRandomWord() {
		int index = rg.nextInt(lexicon.size());
		return lexicon.get(index);
	}

}
