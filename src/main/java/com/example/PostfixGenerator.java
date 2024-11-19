package com.example;

import java.util.ArrayList;
import java.util.List;

public class PostfixGenerator {
    private SymbolTable symbolTable;
    private List<String> postfixCode;

    public PostfixGenerator(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.postfixCode = new ArrayList<>();
    }

    public List<String> generate(Node syntaxTree) {
        generatePostfix(syntaxTree);
        return postfixCode;
    }

    private void generatePostfix(Node node) {
        if (node == null) return;

        if (node.getValue().equals("+") || node.getValue().equals("-") || node.getValue().equals("*") || node.getValue().equals("/")) {
            generatePostfix(node.getLeft());
            generatePostfix(node.getRight());

            // Проверка типов операндов и добавление i2f при необходимости
            VariableType leftType = getNodeType(node.getLeft());
            VariableType rightType = getNodeType(node.getRight());
            VariableType resultType = determineResultType(leftType, rightType);

            if (leftType == VariableType.INTEGER && resultType == VariableType.FLOAT) {
                postfixCode.add("i2f");
            }
            if (rightType == VariableType.INTEGER && resultType == VariableType.FLOAT) {
                postfixCode.add("i2f");
            }

            postfixCode.add(node.getValue());
        } else if (node.getValue().equals("Int2Float")) {
            generatePostfix(node.getLeft());
            postfixCode.add("i2f");
        } else {
            // Leaf node (identifier or constant)
            postfixCode.add(formatValue(node.getValue()));
        }
    }

    private VariableType determineResultType(VariableType leftType, VariableType rightType) {
        if (leftType == VariableType.FLOAT || rightType == VariableType.FLOAT) {
            return VariableType.FLOAT;
        } else {
            return VariableType.INTEGER;
        }
    }

    private String formatValue(String value) {
        if (value.startsWith("<IDENTIFIER,")) {
            String[] parts = value.split(",");
            int id = Integer.parseInt(parts[1].replace(">", ""));
            return "<id," + id + ">";
        } else if (value.startsWith("<CONSTANT,")) {
            String[] parts = value.split(",");
            String constantValue = parts[1].replace(">", "");
            return "<const," + constantValue + ">";
        } else if (value.matches("\\d+\\.\\d+")) {
            return "<const," + value + ">";
        } else if (value.matches("\\d+")) {
            return "<const," + value + ">";
        } else {
            return value;
        }
    }

    private VariableType getNodeType(Node node) {
        String value = node.getValue();

        if (value.startsWith("<IDENTIFIER,")) {
            String[] parts = value.split(",");
            if (parts.length > 1) {
                String idPart = parts[1];
                return symbolTable.getSymbolType(Integer.parseInt(idPart));
            }
        } else if (value.startsWith("<CONSTANT,")) {
            String[] parts = value.split(",");
            if (parts.length > 1) {
                String typePart = parts[1];
                if (typePart.matches("\\d+\\.\\d+")) {
                    return VariableType.FLOAT;
                } else if (typePart.matches("\\d+")) {
                    return VariableType.INTEGER;
                }
            }
        } else if (value.matches("\\d+\\.\\d+")) {
            return VariableType.FLOAT;
        } else if (value.matches("\\d+")) {
            return VariableType.INTEGER;
        } else if (value.equals("Int2Float")) {
            return VariableType.FLOAT;
        } else if (isOperator(value) || isParenthesis(value)) {
            return VariableType.OPERATOR;
        } else {
            System.err.println("Семантическая ошибка! Неизвестный тип узла: " + value);
            System.exit(1);
            return null;
        }
        return null;
    }

    private boolean isOperator(String value) {
        return "+-*/".contains(value);
    }

    private boolean isParenthesis(String value) {
        return "()".contains(value);
    }
}