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
	 *              CONSTANTS                                  *
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
	 *              Instance Variables                         *
	 ***********************************************************/
	
	/* An object that can produce pseudo random numbers */
	private RandomGenerator rg = new RandomGenerator();
	
	private GCanvas canvas = new GCanvas();	
	private int wrongGuesses = 0;
	private String guess = "";
	private String answer = "";
	
	/***********************************************************
	 *                    Methods                              *
	 ***********************************************************/
	
	public void run() {
		println("Welcome to Hangman");
		answer = getRandomWord();
		int len = answer.length();
		for (int i = 0; i < len; i++){
			guess = guess + '-';
		}
		while(wrongGuesses < 7){
			play();
		}
		println("You're completely hung.");
		println("The word was: " + answer);
	}
	
	private void play() {
		int len = answer.length();
		int charCount = 0;
		
		println("Your word now looks like this: " + guess);
		println("You have " + (N_GUESSES - wrongGuesses) +" guesses left.");
		String guessChar = readLine("Your guess: ");
		
		for (int i = 0; i < len; i++){
			guess = "";
			char ch = answer.charAt(i);
			if ((Character.toString(ch)).toLowerCase() == guessChar.toLowerCase()){
				guess = guess + ch;	
				charCount ++;
			} 
		}
		
		if (charCount == 0){
			println("There is no " + guessChar.toUpperCase() + "'s in the word.");
			wrongGuesses ++;
		} else {
			println("That guess is correct.");
		}
		
		
		
		
	}

	/**
	 * Method: Get Random Word
	 * -------------------------
	 * This method returns a word to use in the hangman game. It randomly 
	 * selects from among 10 choices.
	 */
	private String getRandomWord() {
		int index = rg.nextInt(10);
		if(index == 0) return "BUOY";
		if(index == 1) return "COMPUTER";
		if(index == 2) return "CONNOISSEUR";
		if(index == 3) return "DEHYDRATE";
		if(index == 4) return "FUZZY";
		if(index == 5) return "HUBBUB";
		if(index == 6) return "KEYHOLE";
		if(index == 7) return "QUAGMIRE";
		if(index == 8) return "SLITHER";
		if(index == 9) return "ZIRCON";
		throw new ErrorException("getWord: Illegal index");
	}

}
