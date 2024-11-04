package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LexicalAnalyzer {
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z_0-9]*$");
    private static final Pattern CONSTANT_PATTERN = Pattern.compile("^\\d+\\.\\d+|\\d+$");

    private SymbolTable symbolTable = new SymbolTable();
    private List<Token> tokens = new ArrayList<>();

    public List<Token> analyze(String expression) {
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char current = expression.charAt(i);

            if (Character.isWhitespace(current)) {
                if (token.length() > 0) {
                    processToken(token.toString(), i - token.length());
                    token.setLength(0);
                }
                continue;
            }

            if (isOperator(current)) {
                if (token.length() > 0) {
                    processToken(token.toString(), i - token.length());
                    token.setLength(0);
                }

                tokens.add(new Token(String.valueOf(current), null, getOperatorDescription(current)));

            } else if (isParenthesis(current)) {
                if (token.length() > 0) {
                    processToken(token.toString(), i - token.length());
                    token.setLength(0);
                }

                tokens.add(new Token(String.valueOf(current), null, getParenthesisDescription(current)));

            } else if (Character.isLetterOrDigit(current) || current == '.' || current == '_') {
                token.append(current);

            } else {
                System.err.println("Лексическая ошибка! Недопустимый символ '" + current + "' на позиции " + i);
                return null;
            }
        }

        if (token.length() > 0) {
            processToken(token.toString(), expression.length() - token.length());
        }

        return tokens;
    }

    private void processToken(String token, int position) {
        if (isIdentifier(token)) {
            int id = symbolTable.addSymbol(token);
            tokens.add(new Token("id", String.valueOf(id), "идентификатор с именем " + token));

        } else if (isConstant(token)) {
            String description = token.contains(".") ? "константа вещественного типа" : "константа целого типа";
            tokens.add(new Token(token, null, description));

        } else {
            System.err.println("Лексическая ошибка! Некорректный токен '" + token + "' на позиции " + position);
        }
    }

    private boolean isOperator(char c) {
        return "+-*/".indexOf(c) != -1;
    }

    private boolean isParenthesis(char c) {
        return "()".indexOf(c) != -1;
    }

    private boolean isIdentifier(String token) {
        return IDENTIFIER_PATTERN.matcher(token).matches();
    }

    private boolean isConstant(String token) {
        return CONSTANT_PATTERN.matcher(token).matches();
    }

    private String getOperatorDescription(char operator) {
        switch (operator) {
            case '+': return "операция сложения";
            case '-': return "операция вычитания";
            case '*': return "операция умножения";
            case '/': return "операция деления";
            default: return "неизвестная операция";
        }
    }

    private String getParenthesisDescription(char parenthesis) {
        return parenthesis == '(' ? "открывающая скобка" : "закрывающая скобка";
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
}
