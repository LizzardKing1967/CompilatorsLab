// Token.java
package com.example;

public class Token {
    private TokenType type;
    private String value;
    private String description;

    public Token(TokenType type, String value, String description) {
        this.type = type;
        this.value = value;
        this.description = description;
    }

    @Override
    public String toString() {
        return "<" + type + (value != null ? "," + value : "") + "> - " + description;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
