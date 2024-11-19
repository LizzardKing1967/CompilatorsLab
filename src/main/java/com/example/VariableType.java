package com.example;

public enum VariableType {
    INTEGER(""),
    FLOAT(""), OPERATOR(""), IDENTIFIER("");

    private final String description;

    VariableType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
