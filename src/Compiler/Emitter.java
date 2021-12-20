package Compiler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Stack;

public final class Emitter {

    private static ArrayList<LinkedHashMap<String, Object>> Instructions;
    private static ArrayList<Symbol> SymbolTable;
    private static Stack<Object> stack = new Stack<>();
    private static int ip = 0;

    // This part is the constructor for the Emitter
    public Emitter(ArrayList<LinkedHashMap<String, Object>> Instructions, ArrayList<Symbol> SymbolTable,
                   Stack<Object> stack, int ip) {
        Emitter.Instructions = Instructions;
        Emitter.SymbolTable = SymbolTable;
        Emitter.stack = stack;
        Emitter.ip = ip;
    }

    // This is the emit() where all the operations are made
    public static void emit() {
        while(true) {
            if (Instructions.get(ip).get("INSTRUCTION").equals("op_push")) {
                if (Instructions.get(ip).get("TOKEN").equals("TK_OTHER")) {
                    pushi(Instructions.get(ip).get("VALUE"));
                } else {
                    push(Instructions.get(ip).get("VALUE"));
                }
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_add")) {
                add();
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_minus")) {
                minus();
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_mult")) {
                multiply();
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_div")) {
                divide();
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_less_than")) {
                less();
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_greater_than")) {
                greater();
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_less_than_equals")) {
                less_equals();
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_greater_than_equals")) {
                greater_equals();
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_equals")) {
                equal();
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_not_equals")) {
                not_equal();
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_and")) {
                and();
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_or")) {
                or();
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_xor")) {
                xor();
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_not")) {
                not();
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_mod")) {
                mod();
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_writeln")) {
                writeln();
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_pop")) {
                pop(Instructions.get(ip).get("VALUE"));
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_jfalse")) {
                jfalse(Instructions.get(ip).get("VALUE"));
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_jmp")) {
                jmp(Instructions.get(ip).get("VALUE"));
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_jtrue")) {
                jtrue(Instructions.get(ip).get("VALUE"));
            } else if (Instructions.get(ip).get("INSTRUCTION").equals("op_halt")) {
                halt();
            } else {
                System.out.println("No instruction for the provided case");
            }
            ip++;
        }
    }

    //pushi, push, pop functions
    public static void pushi(Object val) {
        for (int i = 0; i < SymbolTable.size(); i++) {
            if(SymbolTable.get(i).getSymbolName().equals(val)) {
                stack.push(val);
            }
        }
        return;
    }

    public static void push(Object val) {
        stack.push(val);
        return;
    }

    public static void pop(Object val) {
        Object o = stack.pop();
        for (int i = 0; i < SymbolTable.size(); i++) {
            if(SymbolTable.get(i).getSymbolName().equals(val)) {
                SymbolTable.get(i).setSymbolValue(o);
            }
        }
        return;
    }

    //With more time I can fix the errors in all of these operations shown below
    public static void add() {
//        System.out.println(stack);
//        System.out.println(stack.empty());
        int v1 = Integer.parseInt((String)stack.pop());
//        System.out.println("is it:"+stack);
//        System.out.println(stack.empty());
        int v2 = Integer.parseInt((String)stack.pop());
//        System.out.println(stack);
//        System.out.println(stack.empty());
        stack.push(v1+v2);
        return;
    }

    public static void minus() {
        int v1 = (int)stack.pop();
        int v2 = (int)stack.pop();
        stack.push(v1-v2);
        return;
    }

    public static void multiply() {
        int v1 = (int)stack.pop();
        int v2 = (int)stack.pop();
        stack.push(v1*v2);
        return;
    }

    public static void divide() {
        float v1 = (float)stack.pop();
        float v2 = (float)stack.pop();
        stack.push(v1 / v2);
        return;
    }

    public static void less() {
        float v1 = (float)stack.pop();
        float v2 = (float)stack.pop();
        stack.push(v1 < v2);
        return;
    }

    public static void greater() {
        float v1 = (float)stack.pop();
        float v2 = (float)stack.pop();
        stack.push(v1 > v2);
        return;
    }

    public static void less_equals() {
        float v1 = (float)stack.pop();
        float v2 = (float)stack.pop();
        stack.push(v1 <= v2);
        return;
    }

//    public static void greater_equals() {
//        float v1 = (float)stack.pop();
//        float v2 = (float)stack.pop();
//        stack.push(v1 >= v2);
//        return;
//    }

    public static void greater_equals() {
        Integer intV1 = (Integer)stack.pop();
        Float v1 = (float) intV1;
        Integer intV2 = (Integer)stack.pop();
        Float v2 = (float) intV2;
        stack.push(v1 >= v2);
        return;
    }

//    public static void greater_equals() {
//        float v1 = (float)stack.pop();
//        float v2 = (float)stack.pop();
//        stack.push(v1 >= v2);
//        return;
//    }


//    public static void equal() {
//        Integer intV2 = (Integer)stack.pop();
//        Float v2 = (float) intV2;
//        Integer intV1 = (Integer)stack.pop();
//        Float v1 = (float) intV1;
//        stack.push(v1.equals(v2));
//        return;
//    }

    public static void equal() {
        Integer v1 = Integer.parseInt((String) stack.pop());
        Integer v2 = Integer.parseInt((String) stack.pop());
        Boolean b = v1.equals(v2);
        stack.push(b);
        return;
    }

    public static void not_equal() {
        Integer intV2 = (Integer)stack.pop();
        Float v2 = (float) intV2;
        Integer intV1 = (Integer)stack.pop();
        Float v1 = (float) intV1;
        stack.push(!v1.equals(v2));
        return;
    }

    public static void and() {
        Boolean v1 = (boolean)stack.pop();
        Boolean v2 = (boolean)stack.pop();
        stack.push(v1 && v2);
        return;
    }

    public static void or() {
        Boolean v1 = (boolean)stack.pop();
        Boolean v2 = (boolean)stack.pop();
        stack.push(v1 || v2);
        return;
    }

    public static void xor() {
        Boolean v1 = (boolean)stack.pop();
        Boolean v2 = (boolean)stack.pop();
        stack.push(v1 ^ v2);
        return;
    }

    public static void not() {
        Boolean v1 = (boolean)stack.pop();
        stack.push(!v1);
        return;
    }

    public static void mod() {
        int v1 = (int)stack.pop();
        int v2 = (int)stack.pop();
        stack.push(v1 % v2);
        return;
    }

    public static void writeln() {
        Object v1 = stack.pop();
        System.out.println(v1);
    }

    public static void jfalse(Object instruction) {
        boolean v1 = (boolean)stack.pop();
        int i = (int)instruction;
        if (!v1) {
            ip = i-1;
        }
    }

    public static void jmp(Object instruction) {
        int i = (int)instruction;
        ip = i-1;
    }

    public static void jtrue(Object instruction) {
        boolean v1 = (boolean)stack.pop();
        int i = (int)instruction;
        if (v1) {
            ip = i-1;
        }
    }

    public static void halt() {
        System.out.print("\nProgram finished compiling the Pascal code");
        System.exit(0);
    }


}
