// Franco Vidal
// CSE 122
// TA: Elizabeth Shirakian
// 5/11/2023
// This program allows the user to interact with the terminal to play "Absurdle";
// a word guessing game similar to wordle. The program gives the impression that
// it chooses a single word at the beginning like wordle does, but in reality it
// but changes the word to prolong the game as long as it can.

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Absurdle  {
    public static final String GREEN = "🟩";
    public static final String YELLOW = "🟨";
    public static final String GRAY = "⬜";

    // [[ ALL OF MAIN PROVIDED ]]
    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        System.out.println("Welcome to the game of Absurdle.");

        System.out.print("What dictionary would you like to use? ");
        String dictName = console.next();

        System.out.print("What length word would you like to guess? ");
        int wordLength = console.nextInt();

        List<String> contents = loadFile(new Scanner(new File(dictName)));
        Set<String> words = pruneDictionary(contents, wordLength);

        List<String> guessedPatterns = new ArrayList<>();
        while (!isFinished(guessedPatterns)) {
            System.out.print("> ");
            String guess = console.next();
            String pattern = record(guess, words, wordLength);
            guessedPatterns.add(pattern);
            System.out.println(": " + pattern);
            System.out.println();
        }
        System.out.println("Absurdle " + guessedPatterns.size() + "/∞");
        System.out.println();
        printPatterns(guessedPatterns);
    }

    // [[ PROVIDED ]]
    // Prints out the given list of patterns.
    // - List<String> patterns: list of patterns from the game
    public static void printPatterns(List<String> patterns) {
        for (String pattern : patterns) {
            System.out.println(pattern);
        }
    }

    // [[ PROVIDED ]]
    // Returns true if the game is finished, meaning the user guessed the word. Returns
    // false otherwise.
    // - List<String> patterns: list of patterns from the game
    public static boolean isFinished(List<String> patterns) {
        if (patterns.isEmpty()) {
            return false;
        }
        String lastPattern = patterns.get(patterns.size() - 1);
        return !lastPattern.contains("⬜") && !lastPattern.contains("🟨");
    }

    // [[ PROVIDED ]]
    // Loads the contents of a given file Scanner into a List<String> and returns it.
    // - Scanner dictScan: contains file contents
    public static List<String> loadFile(Scanner dictScan) {
        List<String> contents = new ArrayList<>();
        while (dictScan.hasNext()) {
            contents.add(dictScan.next());
        }
        return contents;
    }

/*
* This method creates a set off words from the given dictionary file that are all the size of
* the given word length. 
* Exceptions
*    - throws an IllegalArgumentException if the given word length is less than one
* Parameters:
*    - contents: list containing all of the words from the given file
*    - wordLength: integer representing the length of the words being chosen from by the user
* Returns:
*    - words: Set of Strings containing all of the words of equal length to wordLength
*/
    public static Set<String> pruneDictionary(List<String> contents, int wordLength) {
        if (wordLength < 1) {
            throw new IllegalArgumentException ();
        }
        Set<String> words = new TreeSet<>();
        for (int i = 0; i < contents.size(); i++) {
            if (contents.get(i).length() == wordLength) {
                words.add(contents.get(i));                
            }
        }
        return words;
    }

/*
* This method determines which resulting pattern correlates to the largest amount of words
* in the word bank. It also updates the current word base to be the word base corresponding
* to the most common pattern.
* Exceptions
*    - throws an IllegalArgumentException if there are no words in the words set or the
*    length of the guessed
*    word are not equal to the given word length  
* Parameters:
*    - guess: String representing the current guess given by the user  
*    - words: Set of Strings containing all of the words of equal length to wordLength
*    - wordLength: integer representing the length of the words being chosen from by the user
* Returns:
*    - returns the pattern correlated with the most words
*/
    public static String record(String guess, Set<String> words, int wordLength) {
        if (words.isEmpty() || guess.length() != wordLength) {
            throw new IllegalArgumentException();
        }
        Map<String, Set<String>> map = new TreeMap<>();
        for (String word : words) {
            String pattern = patternFor(word, guess);
            if (!map.containsKey(pattern)) {
                map.put(pattern, new TreeSet<String>());
                map.get(pattern).add(word);
            } else {
                map.get(pattern).add(word);
            }
        }
        String maxKey = "";
        int maxSize = 0;
        for (String current : map.keySet()) {
            int currentSize = map.get(current).size();
            if (currentSize > maxSize) {
                maxKey = current;
                maxSize = currentSize;
            }
        }
        words.clear();
        words.addAll(map.get(maxKey));
        return maxKey;
    }

/*
* This method creates a pattern representing the accuracy of the guess with the current word.
* If the letter at a given position is correct in terms of both the character and the position,
* a green box is filled. If the letter at a given position is correct in terms of character but
* not position, the box is yellow. If it's wrong in both of these fields, the box is gray.
* Exceptions
*    - None 
* Parameters:
*    - words: Set of Strings containing all of the words of equal length to wordLength
*    - guess: String representing the current guess given by the user  
* Returns:
*    - pattern: string of boxes representing the accuracy of the given guess
*/
    public static String patternFor(String word, String guess) {
        List<String> guessedWord = new ArrayList<>();
        for (int i = 0; i < guess.length(); i++) {
            guessedWord.add(String.valueOf(guess.charAt(i)));
        }        
        Map<Character, Integer> letterCount = new TreeMap<>();
        for (int j = 0; j < guess.length(); j++) {
            if (!letterCount.containsKey(word.charAt(j))) {
                letterCount.put(word.charAt(j), 1);
            } else {
                letterCount.put(word.charAt(j), letterCount.get(word.charAt(j)) + 1);
            }
        }
        checkGreens(guessedWord, letterCount, word);
        checkYellows(guessedWord, letterCount);
        fillGrays(guessedWord);
        String pattern = "";
        for (int k = 0; k < guessedWord.size(); k++) {
            pattern += guessedWord.get(k);
        }
        return pattern;
    }
    
/*
* This method checks the word for any fully accurate guesses as green boxes, meaning that the
* guess is correct in terms
* of character as well as position
* Exceptions
*    - None 
* Parameters:
*    - guessedWord: list of Strings created in the patternFor method containing the letters
* of the guessed word
*    - letterCount: map of characters as keys and integers as values that tracks the amount 
*    - of times each letter
*    appears in the guessed word
*    - words: Set of Strings containing all of the words of equal length to wordLength
* Returns:
*    - None
*/
    public static void checkGreens(List<String> guessedWord, Map<Character, Integer> letterCount,
    String word) {
        for (int i = 0; i < guessedWord.size(); i++) {
            char character = stringToChar(i, guessedWord);
            if (word.charAt(i) == character) {
                updateStructs(i, GREEN, guessedWord, letterCount, character);
            }
        }
    }

/*
* This method checks the word for any partially accurate guesses as yellow boxes, meaning that
* the guess is correct in terms
* of character but not in terms of position. Since this method is called after checkGreens,
* none of the letters
* that were already marked as green will be marked yellow
* Exceptions
*    - None 
* Parameters:
*    - guessedWord: list of Strings containing the letters in the word that was guessed as
*    well as the green boxes where the
*    guess was fully correct 
*    - letterCount: map of characters as keys and integers as values that tracks the amount
*    of times each letter
* Returns:
*    - None
*/
    public static void checkYellows(List<String> guessedWord, Map<Character, Integer> letterCount) {
        for (int i = 0; i < guessedWord.size(); i++) {
            char character = stringToChar(i, guessedWord);
            if (!guessedWord.get(i).equals(GREEN) && letterCount.containsKey(character)
                && letterCount.get(character) != 0) {
                updateStructs(i, YELLOW, guessedWord, letterCount, character);
            }
        }
    }

/*
* This method fills all of the remaining non-yellow or green spaces in the guessed word with 
* gray boxes
* Exceptions
*    - None
* Parameters:
*    - guessedWord: list of Strings containing the letters in the word that was guessed as well
*    as the green boxes where the
*    guess was fully correct 
* Returns:
*    - None 
*/
    public static void fillGrays(List<String> guessedWord) {
        for (int i = 0; i < guessedWord.size(); i++) {
            if (guessedWord.get(i) != GREEN && guessedWord.get(i) != YELLOW) {
                guessedWord.set(i, GRAY);
            }
        }
    }

/*
* This method changes the letter from a given index in the given String list from a String to a character
* Exceptions
*    - None
* Parameters:
*    - i: integer that represents the index of the letter being modified
*    - guessedWord: list of Strings containing the letters in the word that was guessed as well as the
*    green boxes where the
*    guess was fully correct and the yellow boxes where the guess was partially correct 
* Returns:
*    - character: character of the given letter from the String list
*/
    public static char stringToChar(int i, List<String> guessedWord) {
        String currentChar = guessedWord.get(i);
        char character = currentChar.charAt(0);
        return character;
    }


/*
* This method updates the given set and map to represent the new remaining character values 
* Exceptions
*    - None
* Parameters:
*    - i: integer that represents the index of the letter being modified
*    - color: String representing the color that is being applied to the set
*    - guessedWord: list of Strings containing the letters in the word that was guessed as well as
*    the green boxes where the
*    guess was fully correct and the yellow boxes where the guess was partially correct 
*    - letterCount: map of characters as keys and integers as values that tracks the amount of
*    times each letter
*    - key: character representing the key that needs to be modified to update the number count
* Returns:
*    - None
*/
    public static void updateStructs(int i, String color, List<String> guessedWord,
    Map<Character, Integer> letterCount, char key) {
        guessedWord.set(i, color);
        letterCount.put(key, letterCount.get(key) - 1);
    }
}