package Compiler;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public final class Lexer {

    private static String currentTokenType;
    private static String currentTokenValue;
    private static int currentColumn;
    private static int currentRow;

    private static boolean isString;
    private static boolean isComment;
    private static boolean isInteger;
    private static boolean isReal; //floats in pascal

    private static ArrayList<Token> Tokens = new ArrayList<>();

    // This part is the constructor for the Lexer/scanner
    public Lexer(String currentTokenType, String currentTokenValue,int currentColumn, int currentRow,
                 boolean isString, boolean isComment, boolean isInteger, boolean isReal,
                 ArrayList<Token> Tokens) {

        Lexer.currentTokenType = currentTokenType;
        Lexer.currentTokenValue = currentTokenValue;
        Lexer.currentColumn = currentColumn;
        Lexer.currentRow = currentRow;
        Lexer.isString = isString;
        Lexer.isComment = isComment;
        Lexer.isInteger = isInteger;
        Lexer.isReal = isReal;
        Lexer.Tokens = Tokens;

    }

    // Here I defined Keyword Tokens from Pascal into a HashMap
    private static final HashMap<String, String> KeywordTokens = new HashMap<>();
    static {
        KeywordTokens.put("ASM", "TK_ASM");
        KeywordTokens.put("BEGIN", "TK_BEGIN");
        KeywordTokens.put("CASE", "TK_CASE");
        KeywordTokens.put("CONST", "TK_CONST");
        KeywordTokens.put("CONSTRUCTOR", "TK_CONSTRUCTOR");
        KeywordTokens.put("DESTRUCTOR", "TK_DESTRUCTOR");
        KeywordTokens.put("DO", "TK_DO");
        KeywordTokens.put("DOWNTO", "TK_DOWNTO");
        KeywordTokens.put("ELSE", "TK_ELSE");
        KeywordTokens.put("END.", "TK_END");
        KeywordTokens.put("FILE", "TK_FILE");
        KeywordTokens.put("FOR", "TK_FOR");
        KeywordTokens.put("FUNCTION", "TK_FUNCTION");
        KeywordTokens.put("GOTO", "TK_GOTO");
        KeywordTokens.put("IF", "TK_IF");
        KeywordTokens.put("IMPLEMENTATION", "TK_IMPLEMENTATION");
        KeywordTokens.put("INLINE", "TK_INLINE");
        KeywordTokens.put("INTERFACE", "TK_INTERFACE");
        KeywordTokens.put("LABEL", "TK_LABEL");
        KeywordTokens.put("NIL", "TK_NIL");
        KeywordTokens.put("OF", "TK_OF");
        KeywordTokens.put("OTHER", "TK_OTHER");
        KeywordTokens.put("PACKED", "TK_PACKED");
        KeywordTokens.put("PROGRAM", "TK_PROGRAM");
        KeywordTokens.put("PROCEDURE", "TK_PROCEDURE");
        KeywordTokens.put("RECORD", "TK_RECORD");
        KeywordTokens.put("REPEAT", "TK_REPEAT");
        KeywordTokens.put("SET", "TK_SET");
        KeywordTokens.put("SHL", "TK_SHL");
        KeywordTokens.put("SHR", "TK_SHR");
        KeywordTokens.put("STRING", "TK_STRING");
        KeywordTokens.put("THEN", "TK_THEN");
        KeywordTokens.put("TO", "TK_TO");
        KeywordTokens.put("TYPE", "TK_TYPE");
        KeywordTokens.put("UNIT", "TK_UNIT");
        KeywordTokens.put("UNTIL", "TK_UNTIL");
        KeywordTokens.put("VAR", "TK_VAR");
        KeywordTokens.put("WHILE", "TK_WHILE");
        KeywordTokens.put("WRITELN", "TK_WRITELN");
        KeywordTokens.put("CHAR", "TK_CHAR_ID");
        KeywordTokens.put("INTEGER", "TK_INTEGER_ID");
        KeywordTokens.put("REAL", "TK_REAL_ID");
        KeywordTokens.put("BOOLEAN", "TK_BOOLEAN_ID");
    }

    // Operator Tokens from Pascal into a HashMap
    private static final HashMap<String, String> OperatorTokens = new HashMap<>();
    static {
        OperatorTokens.put(";", "TK_SEMICOLON");
        OperatorTokens.put(":", "TK_COLON");
        OperatorTokens.put(",", "TK_COMMA");
        OperatorTokens.put("\'", "TK_QUOTE");
        OperatorTokens.put("<", "TK_LESS_THAN");
        OperatorTokens.put(">", "TK_GREATER_THAN");
        OperatorTokens.put("<=", "TK_LESS_THAN_EQUALS");
        OperatorTokens.put(">=", "TK_GREATER_THAN_EQUALS");
        OperatorTokens.put("=", "TK_EQUALS");
        OperatorTokens.put("!=", "TK_NOT_EQUALS");
        OperatorTokens.put("!", "TK_EXCLAMATION");
        OperatorTokens.put(":=", "TK_ASSIGNMENT");
        OperatorTokens.put("(", "TK_LEFT_PARENTHESIS");
        OperatorTokens.put(")", "TK_RIGHT_PARENTHESIS");
        OperatorTokens.put("[", "TK_LEFT_BRACKET");
        OperatorTokens.put("]", "TK_RIGHT_BRACKET");
        OperatorTokens.put("(*", "TK_START_COMMENT");
        OperatorTokens.put("*)", "TK_END_COMMENT");
        OperatorTokens.put("~", "TK_RANGE");
        OperatorTokens.put("+", "TK_PLUS");
        OperatorTokens.put("-", "TK_MINUS");
        OperatorTokens.put("*", "TK_ASTERISK");
        OperatorTokens.put("/", "TK_SLASH");
        OperatorTokens.put("DIV", "TK_DIV");
        OperatorTokens.put("MOD", "TK_MOD");
        OperatorTokens.put("ABS", "TK_ABS");
        OperatorTokens.put("ARRAY", "TK_ARRAY");
        OperatorTokens.put("AND", "TK_AND");
        OperatorTokens.put("OR", "TK_OR");
        OperatorTokens.put("XOR", "TK_XOR");
        OperatorTokens.put("NOT", "TK_NOT");
    }

    // To understand what character the Lexer is scanning I created an enum and a Hashmap
    // to keep track of which character it is using Ascii values
    enum CharacterType {
        SPACE, DIGIT, SEMICOLON, COLON, DOT, LETTER,
        COMMA, EXCLAMATION, QUOTE, PLUS, MINUS, CR, //quote -> string, digit -> isdig numbers
        ASTERISK, SLASH, LESS_THAN, GREATER_THAN, EQUALS,
        LEFT_PARENTHESIS, RIGHT_PARENTHESIS, LEFT_BRACKET, RIGHT_BRACKET, RANGE;
    }
    private static final HashMap<String, CharacterType> Types = new HashMap<>();
    static {
        // Ascii for SPACE
        for (int i = 0; i <= 32; i++) {
            String currentChar = String.valueOf(Character.toChars(i)[0]);
            Types.put(currentChar, CharacterType.SPACE);
        }
        // Ascii for DIGIT
        for (int i = 48; i < 58; i++){
            // Add digits
            String currentChar = String.valueOf(Character.toChars(i)[0]);
            Types.put(currentChar, CharacterType.DIGIT);
        }
        // Ascii for SEMICOLON
        Types.put(String.valueOf(Character.toChars(59)[0]), CharacterType.SEMICOLON);
        // Ascii for COLON
        Types.put(String.valueOf(Character.toChars(58)[0]), CharacterType.COLON);
        // Ascii for DOT
        Types.put(String.valueOf(Character.toChars(46)[0]), CharacterType.DOT);
        // Ascii for COMMA
        Types.put(String.valueOf(Character.toChars(44)[0]), CharacterType.COMMA);
        // Ascii for EXCLAMATION
        Types.put(String.valueOf(Character.toChars(33)[0]), CharacterType.EXCLAMATION);
        // Ascii for QUOTE
        Types.put(String.valueOf(Character.toChars(39)[0]), CharacterType.QUOTE);
        // Ascii for PLUS
        Types.put(String.valueOf(Character.toChars(43)[0]), CharacterType.PLUS);
        // Ascii for MINUS
        Types.put(String.valueOf(Character.toChars(45)[0]), CharacterType.MINUS);
        // Ascii for ASTERISK
        Types.put(String.valueOf(Character.toChars(42)[0]), CharacterType.ASTERISK);
        // Ascii for SLASH
        Types.put(String.valueOf(Character.toChars(47)[0]), CharacterType.SLASH);
        // Ascii for LESS_THAN
        Types.put(String.valueOf(Character.toChars(60)[0]), CharacterType.LESS_THAN);
        // Ascii for GREATER_THAN
        Types.put(String.valueOf(Character.toChars(62)[0]), CharacterType.GREATER_THAN);
        // Ascii for EQUALS
        Types.put(String.valueOf(Character.toChars(61)[0]), CharacterType.EQUALS);
        // Ascii for LEFT_PARENTHESIS
        Types.put(String.valueOf(Character.toChars(40)[0]), CharacterType.LEFT_PARENTHESIS);
        // Ascii for RIGHT_PARENTHESIS
        Types.put(String.valueOf(Character.toChars(41)[0]), CharacterType.RIGHT_PARENTHESIS);
        // Ascii for LEFT_BRACKET
        Types.put(String.valueOf(Character.toChars(91)[0]), CharacterType.LEFT_BRACKET);
        // Ascii for RIGHT_BRACKET
        Types.put(String.valueOf(Character.toChars(93)[0]), CharacterType.RIGHT_BRACKET);
        // Ascii for RANGE
        Types.put(String.valueOf(Character.toChars(126)[0]), CharacterType.RANGE);
        // Ascii for CR
        Types.put(String.valueOf(Character.toChars(13)[0]), CharacterType.CR);
        // Ascii for LETTER
        for (int i = 65; i < 91; i++){
            // Add letters
            String currentChar = String.valueOf(Character.toChars(i)[0]);
            Types.put(currentChar, CharacterType.LETTER);
        }
        for (int i = 97; i < 123; i++){
            // Add letters
            String currentChar = String.valueOf(Character.toChars(i)[0]);
            Types.put(currentChar, CharacterType.LETTER);
        }
    }

//    public void printType(char character) {
//        System.out.println(Types.get(String.valueOf(character)));
//    }

    // This scans the file and goes through each character to find comments,
    // strings, digits, etc
    public static ArrayList<Token> scan(File file) throws IOException {
        BufferedReader b = new BufferedReader(new FileReader(file));
        int c = 0;
        while((c = b.read()) != -1) {
            char character = (char) c;
            // Comments
            if(isComment) {
                handleComments(character);
                if(Types.get(String.valueOf(character)) == CharacterType.CR) {
                    currentColumn = 1;
                    currentRow += 1;
                }
                currentColumn += 1;
            }
            // Strings
            else if(isString) {
                handleStrings(character);
                if(Types.get(String.valueOf(character)) == CharacterType.CR) {
                    currentColumn = 1;
                    currentRow += 1;
                }
                currentColumn += 1;
            }
            // Digits
            else if(isInteger) {
                handleDigits(character);
                if(Types.get(String.valueOf(character)) == CharacterType.CR) {
                    currentColumn = 1;
                    currentRow += 1;
                }
                currentColumn += 1;
            }
            // Generic
            else {
                handleGenericCharacter(character);
                if(Types.get(String.valueOf(character)) == CharacterType.CR) {
                    currentColumn = 1;
                    currentRow += 1;
                }
                currentColumn += 1;
            }
        }
        Tokens.add(getToken("EOF", "0",0,0));
        return Tokens;
    }

    // This handles all the generic characters while scanning in scan()
    public static void handleGenericCharacter(char character) {
//        if ((Types.get(String.valueOf(character))) == CharacterType.SPACE) { //turn this into function later
        if((int)(character) <= 32) {
            if(!currentTokenType.equals("")) { //current token is not empty
                if(KeywordTokens.containsKey(currentTokenValue.toUpperCase())) {
                    currentTokenType = getTokenType(KeywordTokens, currentTokenValue.toUpperCase());
                    Tokens.add(getToken(currentTokenType, currentTokenValue.toLowerCase(), currentColumn - 1, currentRow));
                    currentTokenType = "";
                    currentTokenValue = "";
                    return;
                }
                if(OperatorTokens.containsKey(currentTokenValue.toUpperCase())) {
                    currentTokenType = getTokenType(OperatorTokens, currentTokenValue.toUpperCase());
                    Tokens.add(getToken(currentTokenType, currentTokenValue.toLowerCase(), currentColumn - 1, currentRow));
                    currentTokenType = "";
                    currentTokenValue = "";
                    return;
                }
                if(!OperatorTokens.containsKey(currentTokenValue.toUpperCase())) {
                    if(!KeywordTokens.containsKey(currentTokenValue.toUpperCase())) {
                        if(currentTokenType.equals("TK_COLON")) {
                            Tokens.add(getToken(currentTokenType, ":", currentColumn - 1, currentRow));
                            currentTokenType = "";
                            currentTokenValue = "";
                        }
                        else if(currentTokenType.equals("TK_LEFT_PARENTHESIS")) {
                            Tokens.add(getToken(currentTokenType, "(", currentColumn - 1, currentRow));
                            currentTokenType = "";
                            currentTokenValue = "";
                            return;
                        }
                        else {
                            Tokens.add(getToken(currentTokenType, currentTokenValue.toLowerCase(), currentColumn - 1, currentRow));
                            currentTokenType = "";
                            currentTokenValue = "";
                            return;
                        }
                    }
                }
            }
            if(currentTokenType.equals("")) { //current token is empty
                return;
            }
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.DIGIT) {
            isInteger = true;
            currentTokenValue += character;
            return;
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.SEMICOLON && !isInteger) {
            if(!currentTokenType.equals("")) {
                if (KeywordTokens.containsKey(currentTokenValue.toUpperCase())) {
                    currentTokenType = getTokenType(KeywordTokens, currentTokenValue.toUpperCase());
                }
                Tokens.add(getToken(currentTokenType, currentTokenValue.toLowerCase(), currentColumn - 1, currentRow));
                currentTokenType = "";
                currentTokenValue = "";
            }
            Tokens.add(getToken("TK_SEMICOLON", ";", currentColumn, currentRow));
            return;
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.COLON) {
            if (currentTokenType.equals("")) {
                currentTokenType = "TK_COLON";
                return;
            }
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.DOT) {
            if(!currentTokenType.equals("")) {
                Tokens.add(getToken("TK_END", "end.", currentColumn, currentRow));
                currentTokenType = "";
                return;
            }
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.COMMA) {
            Tokens.add(getToken("TK_COMMA", ",", currentColumn, currentRow));
            currentTokenType = "";
            return;
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.EXCLAMATION) {
            if(currentTokenType.equals("")) {
                currentTokenType = "TK_EXCLAMATION";
            }
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.QUOTE) { //starting quote
            isString = true;
            currentTokenValue += character;
            return;
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.PLUS) {
            Tokens.add(getToken("TK_PLUS", "+", currentColumn, currentRow));
            currentTokenType = "";
            return;
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.MINUS) {
            Tokens.add(getToken("TK_MINUS", "-", currentColumn, currentRow));
            currentTokenType = "";
            return;
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.ASTERISK) {
            if(currentTokenType.equals("")) {
                Tokens.add(getToken("TK_ASTERISK", "*", currentColumn, currentRow));
                return;
            }
            if(!currentTokenType.equals("")) { //(* currt = (
                Tokens.add(getToken("TK_START_COMMENT", "(*", currentColumn, currentRow));
                currentTokenType = "";
                isComment = true;
                return;
            }
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.SLASH) {
            Tokens.add(getToken("TK_SLASH", "/", currentColumn, currentRow));
            currentTokenType = "";
            return;
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.LESS_THAN) {
            if(currentTokenType.equals("")) {
                currentTokenType = "TK_LESS";
            }
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.GREATER_THAN) {
            if(currentTokenType.equals("")) {
                currentTokenType = "TK_GREATER";
            }
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.EQUALS) {
            if(currentTokenType.equals("")) {
                Tokens.add(getToken("TK_EQUALS", "=", currentColumn, currentRow));
                currentTokenType = "";
                return;
            }
            if(currentTokenType.equals("TK_COLON")) { //:=, <=, >=
                Tokens.add(getToken("TK_ASSIGNMENT", ":=", currentColumn - 1, currentRow));
                currentTokenType = "";
                return;
            }
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.LEFT_PARENTHESIS) {
            if(!currentTokenType.equals("")) {
                Tokens.add(getToken("TK_LEFT_PARENTHESIS", "(", currentColumn - 1, currentRow));
                currentTokenType = "";
                return;
            }
            if(currentTokenType.equals("")) {
                currentTokenType = "TK_LEFT_PARENTHESIS";
                return;
            }
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.RIGHT_PARENTHESIS) {
            Tokens.add(getToken("TK_RIGHT_PARENTHESIS", ")", currentColumn, currentRow));
            currentTokenType = "";
            return;
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.LEFT_BRACKET) {
            Tokens.add(getToken("TK_LEFT_BRACKET", "[", currentColumn, currentRow));
            currentTokenType = "";
            return;
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.RIGHT_BRACKET) {
            Tokens.add(getToken("TK_RIGHT_BRACKET", "]", currentColumn, currentRow));
            currentTokenType = "";
            return;
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.RANGE) {
            Tokens.add(getToken("TK_RANGE", "~", currentColumn, currentRow));
            currentTokenType = "";
            return;
        }
        currentTokenValue += character;
        if(!OperatorTokens.containsKey(currentTokenValue.toUpperCase())) {
            if (!KeywordTokens.containsKey(currentTokenValue.toUpperCase())) {
                currentTokenType = "TK_OTHER";
            }
        }
    }

    // Handling
    public static void handleComments(char character) {
        if ((Types.get(String.valueOf(character))) == CharacterType.ASTERISK) {
            currentTokenType = "TK_ASTERISK";
            return;
        }
        if ((Types.get(String.valueOf(character))) == CharacterType.RIGHT_PARENTHESIS) {
            if(currentTokenType.equals("")) {
                ;
            }
            if(currentTokenType.equals("TK_ASTERISK")) {
                isComment = false;
                Tokens.add(getToken("TK_END_COMMENT", "*)", currentColumn, currentRow));
                currentTokenType = "";
            }
        }

    }

    public static void handleStrings(char character) {
        if(Types.get(String.valueOf(character)) == CharacterType.QUOTE) {
            currentTokenValue += character;
            isString = false;
            Tokens.add(getToken("TK_STRING", currentTokenValue, currentColumn, currentRow));
            currentTokenValue = ""; //br
        }
        else {
            currentTokenValue += character;
        }
    }

    // This handles digits in scan()
    public static void handleDigits(char character) {
        if(Types.get(String.valueOf(character)) == CharacterType.DIGIT) {
            currentTokenValue += character;
        }
        if((int)character > 57 || Types.get(String.valueOf(character)) == CharacterType.SPACE) {
            isInteger = false;
            if(isReal) {
                Tokens.add(getToken("TK_REAL", currentTokenValue, currentColumn - 1, currentRow));
                currentTokenValue = "";
                isReal = false;
            }
            else {
                Tokens.add(getToken("TK_INTEGER", currentTokenValue, currentColumn - 1, currentRow));
                currentTokenValue = "";
            }
        }
        if(Types.get(String.valueOf(character)) == CharacterType.DOT) {
            currentTokenValue += character;
            isReal = true;
        }
    }

    // Helper methods
    public static String getTokenType(HashMap<String, String> map, String key) {
        String tokenType = map.get(key);
        return tokenType;
    }

    public static Token getToken(String tokenType, String tokenValue, int tokenColumn, int tokenRow) {
        Token token = new Token(tokenType, tokenValue, tokenColumn, tokenRow);
        return token;
    }

}