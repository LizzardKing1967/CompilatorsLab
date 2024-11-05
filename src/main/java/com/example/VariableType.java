package com.example;

public enum VariableType {
    INTEGER("целый"),
    FLOAT("вещественный"), OPERATOR("оператор");

    private final String description;

    VariableType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
