package Compiler;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Compiler {

    public static void main(String args[]) throws IOException {

        Lexer l = new Lexer("", "",1, 1,
                false, false, false, false,
                new ArrayList<Token>());
        ArrayList<Token> tokens = l.scan(new File("src/Pascal/writtenAdd.pas"));
        System.out.println("Generated Tokens:");
        for (int i = 0; i < tokens.size();i++)
        {
            System.out.println((tokens.get(i)).getString());
        }
        System.out.println();

        Parser p =  new Parser(tokens, tokens.iterator(), new ArrayList<LinkedHashMap<String, Object>>(), new ArrayList<Symbol>(),
                    0, 0, false, "");
        ArrayList<LinkedHashMap<String, Object>> parser = p.parse();
        ArrayList<Symbol> st = p.getSymbolTable();
        System.out.println("Generated Symbol Table:");
        for (int i = 0; i < st.size();i++)
        {
            System.out.println((st.get(i)).getString());
        }
        System.out.println();
        System.out.println("Generated Instructions:");
        for (int j = 0; j < parser.size();j++)
        {
            System.out.println((parser.get(j)));
        }
        System.out.println();

        // Need to work on emitter to make sure it works
        Emitter e = new Emitter(parser, st, new Stack<>(), 0);
        e.emit();

    }

}
