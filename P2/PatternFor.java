import java.util.*;
import java.io.*;
public class PatternFor {
    public static final String GREEN = "🟩";
    public static final String YELLOW = "🟨";
    public static final String GRAY = "⬜";

    public static void main(String[] args) {
        System.out.println(patternFor("abbey", "bebop"));
        System.out.println(patternFor("abbey", "ether"));
        System.out.println(patternFor("abbey", "keeps"));
        System.out.println(patternFor("bebop", "abbey"));
    }

    public static String patternFor(String word, String guess) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < guess.length(); i++) {
            list.add(String.valueOf(guess.charAt(i)));
        }        
        Map<Character, Integer> map = new TreeMap<>();
        for (int j = 0; j < guess.length(); j++) {
            if (!map.containsKey(word.charAt(j))) {
                map.put(word.charAt(j), 1);
            } else {
                map.put(word.charAt(j), map.get(word.charAt(j)) + 1);
            }
        }

        checkGreens(list, map, word);
        checkYellows(list, map);
        fillGrays(list);

        String pattern = "";
        for (int k = 0; k < list.size(); k++) {
            pattern += list.get(k);
        }
        return pattern;
    }
    
    public static void checkGreens(List<String> list, Map<Character, Integer> map, String word) {
        for (int i = 0; i < list.size(); i++) {
            char character = stringToChar(i, list);
            if (word.charAt(i) == character) {
                updateStructs(i, GREEN, list, map, character);
            }
        }
    }

    public static void checkYellows(List<String> list, Map<Character, Integer> map) {
        for (int i = 0; i < list.size(); i++) {
            char character = stringToChar(i, list);
            if (!list.get(i).equals(GREEN) && map.containsKey(character) && map.get(character) != 0) {
                updateStructs(i, YELLOW, list, map, character);
            }
        }
    }

    public static void fillGrays(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != GREEN && list.get(i) != YELLOW) {
                list.set(i, GRAY);
            }
        }
    }

    public static char stringToChar(int i, List<String> list) {
        String currentChar = list.get(i);
        char character = currentChar.charAt(0);
        return character;
    }

    public static void updateStructs(int i, String color, List<String> list, Map<Character, Integer> map, char key) {
        list.set(i, color);
        map.put(key, map.get(key) - 1);
    }
}