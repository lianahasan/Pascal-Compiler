package Compiler;

// For SymbolTable
public final class Symbol {

    private String symbolName;
    private String symbolType;
    private Object symbolValue;
    private int symbolAddress;

    public Symbol(String symbolName, String symbolType, Object symbolValue, int symbolAddress) {
        this.symbolName = symbolName;
        this.symbolType = symbolType;
        this.symbolValue = symbolValue;
        this.symbolAddress = symbolAddress;
    }

    //Setters
    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }
    public void setSymbolType(String symbolType) {
        this.symbolType = symbolType;
    }
    public void setSymbolValue(Object symbolValue) {
        this.symbolValue = symbolValue;
    }
    public void setSymbolAddress(int symbolAddress) {
        this.symbolAddress = symbolAddress;
    }

    //Getters
    public String getSymbolName() {
        return symbolName;
    }
    public String getSymbolType() {
        return symbolType;
    }
    public Object getSymbolValue() {
        return symbolValue;
    }
    public int getSymbolAddress() {
        return symbolAddress;
    }

    public String getString() {
        String s = "Name: " + symbolName + ", Type: " + symbolType + ", Value: " + symbolValue + ", Address: " + symbolAddress;
        return s;
    }
}
