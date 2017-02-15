
/*
 * File: Hangman.java
 * ------------------
 * This program will eventually play the Hangman game from
 * Assignment #4.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Hangman extends ConsoleProgram {

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
	private static final int PARTIALLY_GUESSED_Y = 450;
	/* The y-location to display the incorrectly guessed letters */
	private static final int INCORRECT_GUESSES_Y = 480;
	/* The fonts of both labels */
	private static final String PARTIALLY_GUESSED_FONT = "Courier-36";
	private static final String INCORRECT_GUESSES_FONT = "Courier-26";

	/***********************************************************
	 * Instance Variables *
	 ***********************************************************/

	/* An object that can produce pseudo random numbers */
	private RandomGenerator rg = new RandomGenerator();

	private GCanvas canvas = new GCanvas();
	private int wrongGuesses = 0;
	private String guess = "";
	private String answer = "";
	private String wrongChars = "";
	
	GImage bg = new GImage ("background.jpg");
	GImage parachute = new GImage ("parachute.png");
	GImage karel = new GImage ("karel.png");
	GImage karelFlipped = new GImage("karelFlipped.png");
	GLabel guessLabel = new GLabel ("");
	GLabel wrongCharsLabel = new GLabel ("");
	
	ArrayList<GLine> lines = new ArrayList<GLine>();
	ArrayList<GLine> linesBreak = new ArrayList<GLine>();
	
	ArrayList<String> lexicon = new ArrayList<String>();

	/***********************************************************
	 * Methods *
	 ***********************************************************/

	public void run() {
		
		setUp();
		
		println("Welcome to Hangman");
		answer = getRandomWord();
		int len = answer.length();
		for (int i = 0; i < len; i++) {
			guess = guess + '-';
		}
		
		addLabels();
		
		while (wrongGuesses < N_GUESSES && !win() ) {
			play();
		}
		
		if (win()){
			println("You win.");
			println("The word was " + answer + ".");
		}
		if (wrongGuesses == N_GUESSES){
			canvas.remove(karel);
			canvas.add(karelFlipped, canvas.getWidth() * 0.5 - KAREL_SIZE * 0.5, KAREL_Y);
			println("You're completely hung.");
			println("The word was: " + answer + ".");
		}
		
	}

	private void addLabels() {
		guessLabel.setLabel(guess);
		guessLabel.setFont(PARTIALLY_GUESSED_FONT);
		guessLabel.setColor(Color.BLACK);
		canvas.add(guessLabel, canvas.getWidth() * 0.5 - guessLabel.getWidth() * 0.5, PARTIALLY_GUESSED_Y);
		
		wrongCharsLabel.setFont(INCORRECT_GUESSES_FONT);
	}

	private void setUp() {
		openFile();
		
		drawBackground();
		drawParachute();
		drawKarel();
		drawLines();
		
	}

	private void openFile() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("HangmanLexicon.txt"));
			String readLine = br.readLine();
			while (readLine != null){
				lexicon.add(readLine);
				readLine = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void drawLines() {
		double x = parachute.getX();
		double deltaX = PARACHUTE_WIDTH / (N_GUESSES - 1);
		double y1 = parachute.getY() + PARACHUTE_HEIGHT;
		double x2 = canvas.getWidth() * 0.5;
		double y2 = karel.getY();
		
		for (int i = 0; i < N_GUESSES; i ++){
			GLine line = new GLine(x+deltaX*i, y1, x2, y2);
			canvas.add(line);
			lines.add(line);
		}
		
		linesBreak.add(lines.get(6));
		linesBreak.add(lines.get(0));
		linesBreak.add(lines.get(5));
		linesBreak.add(lines.get(1));
		linesBreak.add(lines.get(4));
		linesBreak.add(lines.get(2));
		linesBreak.add(lines.get(3));
		
		
	}

	public void init() {
		add (canvas);
	}
	
	private void drawBackground(){
		bg.setSize(canvas.getWidth(), canvas.getHeight());
		canvas.add(bg,0,0);
	}
	
	private void drawParachute(){
		parachute.setSize(PARACHUTE_WIDTH, PARACHUTE_HEIGHT);
		double x = canvas.getWidth() * 0.5 - PARACHUTE_WIDTH * 0.5;

		canvas.add(parachute, x, PARACHUTE_Y);
	}
	
	private void drawKarel(){
		karel.setSize(KAREL_SIZE, KAREL_SIZE);
		double x = canvas.getWidth() * 0.5 - KAREL_SIZE * 0.5;
		canvas.add(karel, x, KAREL_Y);
		
		karelFlipped.setSize(KAREL_SIZE, KAREL_SIZE);
	}
	
	private boolean win() {
		if (guess.equals(answer)) return true;
		return false;
	}

	private void play() {
		int len = answer.length();
		int charCount = 0;

		println("Your word now looks like this: " + guess);
		println("You have " + (N_GUESSES - wrongGuesses) + " guesses left.");
		
		
		String guessChar = readLine("Your guess: ");
		
		boolean notDigit = guessChar.length() != 1;
		boolean notLetter = guessChar.length() == 1 && ((guessChar.charAt(0) <'A' || (guessChar.charAt(0) > 'Z' && guessChar.charAt(0) < 'a')) || (guessChar.charAt(0) > 'z'));
		while (guessChar.length() != 1){
			guessChar = readLine("Please enter only ONE letter: ");
		}

		for (int i = 0; i < len; i++) {
			char ch = answer.charAt(i);
			String str = Character.toString(ch);
			if (guessChar.toLowerCase().equals(str.toLowerCase())) {
				guess = guess.substring(0,i) + ch + guess.substring(i+1);
				charCount++;
			} 

		}
		
		
		
		if (charCount == 0) {
			println("There are no " + guessChar.toUpperCase() + "'s in the word.");
			wrongGuesses++;
			
		
			wrongChars = (wrongChars + guessChar).toUpperCase();
			wrongCharsLabel.setLabel(wrongChars);
			wrongCharsLabel.setLocation((canvas.getWidth() - wrongCharsLabel.getWidth()) * 0.5, INCORRECT_GUESSES_Y);
			canvas.add(wrongCharsLabel);
			
			canvas.remove(linesBreak.get(wrongGuesses - 1));
		} else {
			println("That guess is correct.");
			guessLabel.setLabel(guess);
		}

	}

	private void updateWrongGuesses() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Method: Get Random Word ------------------------- This method returns a
	 * word to use in the hangman game. It randomly selects from among 10
	 * choices.
	 */
	private String getRandomWord() {
		int index = rg.nextInt(lexicon.size());
		return lexicon.get(index);
	}

}
