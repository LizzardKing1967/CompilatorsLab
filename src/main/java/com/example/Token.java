// Token.java
package com.example;

public class Token {
    private TokenType type;
    private String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "<" + type + (value != null ? "," + value : "");
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
