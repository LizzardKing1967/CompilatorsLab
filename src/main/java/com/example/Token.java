package com.example;

public class Token {
    private String type;
    private String value;
    private String description;
    public Token(String type, String value, String description) {
        this.type = type;
        this.value = value;
        this.description = description;
    }

    @Override
    public String toString() {
        return "<" + type + (value != null ? "," + value : "") + "> - " + description;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
