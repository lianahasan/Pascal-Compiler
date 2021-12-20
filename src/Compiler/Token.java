package Compiler;

public final class Token {

    private String tokenType; //"TK_SEMICOLON"
    private  String tokenValue; //";"
    private int tokenColumn;
    private int tokenRow;

    public Token(String tokenType, String tokenValue, int tokenColumn, int tokenRow) {
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;
        this.tokenColumn = tokenColumn;
        this.tokenRow = tokenRow;
    }

    //Setters
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }
    public void setTokenColumn(int tokenColumn) {
        this.tokenColumn = tokenColumn;
    }
    public void setTokenRow(int tokenRow) {
        this.tokenRow = tokenRow;
    }

    //Getters
    public String getTokenType() {
        return tokenType;
    }
    public String getTokenValue() {
        return tokenValue;
    }
    public int getTokenColumn() {
        return tokenColumn;
    }
    public int getTokenRow() {
        return tokenRow;
    }

    public String getString() {
        String s = "Token: " + tokenType + ", Value: " + tokenValue + ", Column: " + tokenColumn + ", Row: " + tokenRow;
        return s;
    }

}
