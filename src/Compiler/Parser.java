package Compiler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

public final class Parser {

    private static Token currentToken;
    private static Iterator<Token> it;

    private static ArrayList<Token> tokens;
    private static ArrayList<LinkedHashMap<String, Object>> Instructions = new ArrayList<>();
    private static ArrayList<Symbol> SymbolTable = new ArrayList<>();
    private static int ip; //instruction pointer
    private static int address;

    private static boolean currentOperation;
    private static String lhs;

    public Parser(ArrayList<Token> tokens, Iterator<Token> it, ArrayList<LinkedHashMap<String, Object>> Instructions,
                  ArrayList<Symbol> SymbolTable, int ip, int address, boolean currentOperation, String lhs) {
        Parser.currentToken = currentToken;
        Parser.it = it;
        Parser.tokens = tokens;
        Parser.Instructions = Instructions;
        Parser.SymbolTable = SymbolTable;
        Parser.ip = ip;
        Parser.address = address;
        Parser.currentOperation = currentOperation;
        Parser.lhs = lhs;
    }

    public static ArrayList<LinkedHashMap<String, Object>> parse() { // Return instructions and symbol table
        getToken();
        program();
        return Instructions;
    }

    public static void program() {
        if (currentToken.getTokenType().equals("TK_PROGRAM")) {
            match("TK_PROGRAM");
            match("TK_OTHER");
            match("TK_SEMICOLON");
            //declarations();
            varDeclaration();
        }
    }

    public static void begin() {
        if (currentToken.getTokenType().equals("TK_BEGIN")) {
            match("TK_BEGIN");
            while (!currentToken.getTokenType().equals("TK_END")) {
                statements();
            }
            if (currentToken.getTokenType().equals("TK_END")) {
                Instructions.add(getInstructions2("op_halt", ip, "END."));
            }
        }
    }

    //    public static void declarations() {
//        // Call all declarations
//        varDeclaration();
//    }

    public static void varDeclaration() {
        if (currentToken.getTokenType().equals("TK_VAR")) {
            match("TK_VAR");
        } else {
            begin();
            return;
        }
        while(true) {
            if (currentToken.getTokenType().equals("TK_OTHER")) {
                SymbolTable.add(getSymbol(currentToken.getTokenValue(), "none", 0, address));
                address += 4;
                match("TK_OTHER");
            }
            if (currentToken.getTokenType().equals("TK_COMMA")) {
                match("TK_COMMA");
            }
            if (currentToken.getTokenType().equals("TK_COLON")) {
                match("TK_COLON");
                if (currentToken.getTokenType().equals("TK_ARRAY")) {
                    SymbolTable.get(0).setSymbolType("ARRAY");
                    SymbolTable.get(0).setSymbolValue(new ArrayList<>());
                    match("TK_ARRAY");
                    match("TK_LEFT_BRACKET");
                    int low = Integer.parseInt(currentToken.getTokenValue());
                    match("TK_INTEGER");
                    match("TK_RANGE");
                    int high = Integer.parseInt(currentToken.getTokenValue());
                    match("TK_INTEGER");
                    match("TK_RIGHT_BRACKET");
                    match("TK_OF");
                    match("TK_INTEGER_ID");
                    match("TK_SEMICOLON");
                    ArrayList<Integer> arr = new ArrayList<>();
                    for (int i = low; i <= high; i++) {
                        arr.add(i);
                    }
                    SymbolTable.get(0).setSymbolValue(arr);
                    break;
                } else {
                    break;
                }
            }
        }
        if (currentToken.getTokenType().equals("TK_INTEGER_ID")) {
            for (int i = 0; i < SymbolTable.size(); i++) {
                if(SymbolTable.get(i).getSymbolType().equals("none")) {
                    SymbolTable.get(i).setSymbolType("integer");
                }
            }
            match("TK_INTEGER_ID");
        }
        if (currentToken.getTokenType().equals("TK_REAL_ID")) {
            for (int i = 0; i < SymbolTable.size(); i++)
            {
                if(SymbolTable.get(i).getSymbolType().equals("none")) {
                    SymbolTable.get(i).setSymbolType("real");
                }
            }
            match("TK_REAL_ID");
        }
        if (currentToken.getTokenType().equals("TK_SEMICOLON")) {
            match("TK_SEMICOLON");
        }
        varDeclaration();
    }

    public static void statements() {
        while(true) {
            if (currentToken.getTokenType().equals("TK_IF")) {
                ifStatement();
            } else if (currentToken.getTokenType().equals("TK_FOR")) {
                forStatement();
            } else if (currentToken.getTokenType().equals("TK_WHILE")) {
                whileStatement();
            } else if (currentToken.getTokenType().equals("TK_REPEAT")) {
                repeatStatement();
            } else if (currentToken.getTokenType().equals("TK_WRITELN")) {
                writelnStatement();
            } else if (currentToken.getTokenType().equals("TK_OTHER")) {
                lhs = currentToken.getTokenValue();
                match("TK_OTHER");
            }
            if (currentToken.getTokenType().equals("TK_RIGHT_BRACKET")) {
                match("TK_LEFT_BRACKET");
                factor();
                match("TK_RIGHT_BRACKET");
            }
            if (currentToken.getTokenType().equals("TK_ASSIGNMENT")) {
                currentOperation = true;
                match("TK_ASSIGNMENT");
            }
            condition();
            if (currentToken.getTokenType().equals("TK_SEMICOLON")) {
                match("TK_SEMICOLON");
                if (currentOperation) {
                    Instructions.add(getInstructions2("op_pop", ip, lhs));
                    ip++;
                    currentOperation = false;
                }
            }
            if (currentToken.getTokenType().equals("TK_ELSE")) {
                return;
            }
            if (currentToken.getTokenType().equals("TK_UNTIL")) {
                return;
            }
            if (currentToken.getTokenType().equals("TK_TO")) {
                return;
            }
            if (currentToken.getTokenType().equals("TK_END")) {
                break;
            }
        }
    }

    public static void ifStatement() {
        match("TK_IF");
        condition();
        match("TK_THEN");
        int hole1 = ip; //instruction pointer saved if cond is true
        Instructions.add(getInstructions2("op_jfalse", ip, 0)); //value patched in patch()
        ip++;
        statements();
        if(currentToken.getTokenType().equals("TK_ELSE")) {
            int hole2 = ip;
            Instructions.add(getInstructions2("op_jmp", ip, 0));
            ip++;
            match("TK_ELSE");
            patch(hole1);
            statements();
            patch(hole2);
        }
    }

    public static void forStatement() {
        match("TK_FOR");
        statements();
        String variableName = SymbolTable.get(0).getSymbolName(); //save variable name
        match("TK_TO");
        int target = ip;
        Instructions.add(getInstructions("op_push", ip, variableName, "TK_OTHER"));
        ip++;
        factor();
        match("TK_DO");
        Instructions.add(getInstructions2("op_greater_than", ip, "greater_than"));
        ip++;
        int hole1 = ip;
        Instructions.add(getInstructions2("op_jtrue", ip, hole1));
        ip++;
        statements();
        //loop
        Instructions.add(getInstructions("op_push", ip, variableName, "TK_OTHER"));
        ip++;
        Instructions.add(getInstructions("op_push", ip, 1, "TK_INTEGER"));
        ip++;
        Instructions.add(getInstructions2("op_add", ip, "+"));
        ip++;
        Instructions.add(getInstructions("op_pop", ip, variableName, "TK_OTHER"));
        ip++;
        Instructions.add(getInstructions2("op_jmp", ip, target));
        ip++;
        patch(hole1);
    }

    public static void whileStatement() {
        match("TK_WHILE");
        int target = ip;
        condition();
        match("TK_DO");
        int hole1 = ip;
        Instructions.add(getInstructions2("op_jfalse", ip, target));
        ip++;
        statements();
        Instructions.add(getInstructions2("op_jmp", ip, target));
        ip++;
        patch(hole1);
        return;
    }

    public static void repeatStatement() {
        match("TK_REPEAT");
        int target = ip;
        statements();
        match("TK_UNTIL");
        condition();
        Instructions.add(getInstructions2("op_jfalse", ip, target));
        ip++;
    }

    public static void writelnStatement() {
        match("TK_WRITELN");
        match("TK_LEFT_PARENTHESIS");
        condition();
        match("TK_RIGHT_PARENTHESIS");
        Instructions.add(getInstructions2("op_writeln", ip, ""));
        ip++;
    }

    public static void condition() {
        if (currentToken.getTokenType().equals("TK_LESS_THAN")) {
            build(currentToken);
            match("TK_LESS_THAN");
            expression();
        } else if (currentToken.getTokenType().equals("TK_GREATER_THAN")) {
            build(currentToken);
            match("TK_GREATER_THAN");
            expression();
        } else if (currentToken.getTokenType().equals("TK_LESS_THAN_EQUALS")) {
            build(currentToken);
            match("TK_LESS_THAN_EQUALS");
            expression();
        } else if (currentToken.getTokenType().equals("TK_GREATER_THAN_EQUALS")) {
            build(currentToken);
            match("TK_GREATER_THAN_EQUALS");
            expression();
        } else if (currentToken.getTokenType().equals("TK_EQUALS")) {
            build(currentToken);
            match("TK_EQUALS");
            expression();
        } else if (currentToken.getTokenType().equals("TK_NOT_EQUALS")) {
            build(currentToken);
            match("TK_NOT_EQUALS");
            expression();
        } else {
            expression();
        }
    }

    public static void expression() {
        term();
        buildExpression();
    }

    public static void buildExpression() {
        if (currentToken.getTokenType().equals("TK_PLUS")) {
            build(currentToken);
            match("TK_PLUS");
            term();
            buildExpression();
        } else if (currentToken.getTokenType().equals("TK_MINUS")) {
            build(currentToken);
            match("TK_MINUS");
            term();
            buildExpression();
        } else if (currentToken.getTokenType().equals("TK_OR")) {
            build(currentToken);
            match("TK_OR");
            term();
            buildExpression();
        } else if (currentToken.getTokenType().equals("TK_XOR")) {
            build(currentToken);
            match("TK_XOR");
            term();
            buildExpression();
        } else {
            ;
        }
    }

    public static void term() {
        factor();
        buildTerm();
    }

    public static void buildTerm() {
        if (currentToken.getTokenType().equals("TK_AND")) {
            build(currentToken);
            match("TK_AND");
            factor();
            buildTerm();
        } else if(currentToken.getTokenType().equals("TK_MOD")) {
            build(currentToken);
            match("TK_MOD");
            factor();
            buildTerm();
        } else if(currentToken.getTokenType().equals("TK_ASTERISK")) {
            build(currentToken);
            match("TK_ASTERISK");
            factor();
            buildTerm();
        } else if (currentToken.getTokenType().equals("TK_SLASH")) {
            build(currentToken);
            match("TK_SLASH");
            factor();
            buildTerm();
        } else if (currentToken.getTokenType().equals("TK_LESS_THAN")) {
            build(currentToken);
            match("TK_LESS_THAN");
            expression();
        } else if (currentToken.getTokenType().equals("TK_GREATER_THAN")) {
            match("TK_GREATER_THAN");
            expression();
            build(currentToken);
        } else if (currentToken.getTokenType().equals("TK_LESS_THAN_EQUALS")) {
            build(currentToken);
            match("TK_LESS_THAN_EQUALS");
            expression();
        } else if (currentToken.getTokenType().equals("TK_GREATER_THAN_EQUALS")) {
            build(currentToken);
            match("TK_GREATER_THAN_EQUALS");
            expression();
        } else if (currentToken.getTokenType().equals("TK_EQUALS")) {
            build(currentToken);
            match("TK_EQUALS");
            expression();
        } else if (currentToken.getTokenType().equals("TK_NOT_EQUALS")) {
            build(currentToken);
            match("TK_NOT_EQUALS");
            expression();
        } else {
            ;
        }
    }

    public static void factor() {
        if (currentToken.getTokenType().equals("TK_OTHER")) {
            build(currentToken);
            match("TK_OTHER");
            return;
        }
        if (currentToken.getTokenType().equals("TK_INTEGER")) {
            build(currentToken);
            match("TK_INTEGER");
            return;
        }
        if (currentToken.getTokenType().equals("TK_STRING")) {
            build(currentToken);
            match("TK_STRING");
            return;
        }
        if (currentToken.getTokenType().equals("TK_NOT")) {
            build(currentToken);
            match("TK_NOT");
            factor();
            return;
        }
        if (currentToken.getTokenType().equals("TK_LEFT_PARENTHESIS")) {
            match("TK_LEFT_PARENTHESIS");
            condition();
            match("TK_RIGHT_PARENTHESIS");
            return;
        }
    }

    public static void build(Token token) {
        if(token.getTokenType().equals("TK_OTHER")) {
            Instructions.add(getInstructions("op_push", ip, token.getTokenValue(), token.getTokenType()));
            ip++;
        } else if(token.getTokenType().equals("TK_INTEGER")) {
            Instructions.add(getInstructions("op_push", ip, token.getTokenValue(), token.getTokenType()));
            ip++;
        } else if(token.getTokenType().equals("TK_STRING")) {
            Instructions.add(getInstructions("op_push", ip, token.getTokenValue(), token.getTokenType()));
            ip++;
        } else if(token.getTokenType().equals("TK_LESS_THAN")) {
            Instructions.add(getInstructions("op_less_than", ip, "less_than", "TK_LESS_THAN"));
            ip++;
        } else if(token.getTokenType().equals("TK_GREATER_THAN")) {
            Instructions.add(getInstructions("op_greater_than", ip, "greater_than", "TK_GREATER_THAN"));
            ip++;
        } else if(token.getTokenType().equals("TK_LESS_THAN_EQUALS")) {
            Instructions.add(getInstructions("op_less_than_equals", ip, "less_than_equals", "TK_LESS_THAN_EQUALS"));
            ip++;
        } else if(token.getTokenType().equals("TK_GREATER_THAN_EQUALS")) {
            Instructions.add(getInstructions("op_greater_than_equals", ip, "greater_than_equals", "TK_GREATER_THAN_EQUALS"));
            ip++;
        } else if(token.getTokenType().equals("TK_EQUALS")) {
            Instructions.add(getInstructions("op_equals", ip, "equals", "TK_EQUALS"));
            ip++;
        } else if(token.getTokenType().equals("TK_NOT_EQUALS")) {
            Instructions.add(getInstructions("op_not_equals", ip, "not_equals", "TK_NOT_EQUALS"));
            ip++;
        } else if(token.getTokenType().equals("TK_PLUS")) {
            Instructions.add(getInstructions("op_add", ip, "+", "+"));
            ip++;
        } else if(token.getTokenType().equals("TK_MINUS")) {
            Instructions.add(getInstructions("op_minus", ip, "-", "-"));
            ip++;
        } else if(token.getTokenType().equals("TK_ASTERISK")) {
            Instructions.add(getInstructions("op_mult", ip, "*", "*"));
            ip++;
        } else if(token.getTokenType().equals("TK_SLASH")) {
            Instructions.add(getInstructions("op_div", ip, "/", "/"));
            ip++;
        } else if(token.getTokenType().equals("TK_AND")) {
            Instructions.add(getInstructions("op_and", ip, "and", "TK_AND"));
            ip++;
        } else if(token.getTokenType().equals("TK_OR")) {
            Instructions.add(getInstructions("op_or", ip, "or", "TK_OR"));
            ip++;
        } else if(token.getTokenType().equals("TK_XOR")) {
            Instructions.add(getInstructions("op_xor", ip, "xor", "TK_XOR"));
            ip++;
        } else if(token.getTokenType().equals("TK_NOT")) {
            Instructions.add(getInstructions("op_not", ip, "not", "TK_NOT"));
            ip++;
        } else if(token.getTokenType().equals("TK_MOD")) {
            Instructions.add(getInstructions("op_mod", ip, "mod", "TK_MOD"));
            ip++;
        } else {
            ;
        }
    }


    public static void match(String tokenType) {
        if (tokenType.equals(currentToken.getTokenType())) {
            getToken();
        } else {
            throw new Error(String.format("Token Type expected was (%s) but Current Token Type was (%s) and does not match!", tokenType, currentToken.getTokenType()));
        }
    }

//    public static Iterator<Token> setIterator(ArrayList<Token> tokens) {
//        it = tokens.iterator();
//        return it;
//    }

    public static void setIterator(ArrayList<Token> tokens) {
        it = tokens.iterator();
    }

    public static void getToken() {
        if(it.hasNext()) {
            currentToken = it.next();
        }
    }

    public static Symbol getSymbol(String symbolName, String symbolType, Object symbolValue, int symbolAddress) {
        Symbol symbol = new Symbol(symbolName, symbolType, symbolValue, symbolAddress);
        return symbol;
    }

    public static void patch(int hole) {
        LinkedHashMap<String, Object> patched = Instructions.get(hole);
        patched.put("VALUE", ip);
        Instructions.set(hole, patched);
    }

    public static LinkedHashMap<String,Object> getInstructions(String instruction, int ip, Object value, String tokenType) {
        LinkedHashMap<String,Object> map = new LinkedHashMap<>();
        map.put("INSTRUCTION", instruction);
        map.put("IP", ip);
        map.put("VALUE", value);
        map.put("TOKEN", tokenType);
        return map;
    }

    public static LinkedHashMap<String,Object> getInstructions2(String instruction, int ip, Object value) { //not including token
        LinkedHashMap<String,Object> map = new LinkedHashMap<>();
        map.put("INSTRUCTION", instruction);
        map.put("IP", ip);
        map.put("VALUE", value);
        return map;
    }

    public static ArrayList<Symbol> getSymbolTable() {
        return SymbolTable;
    }



}
