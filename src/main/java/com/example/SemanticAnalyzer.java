package com.example;

public class SemanticAnalyzer {

    public Node analyze(Node syntaxTree, SymbolTable symbolTable) {
        if (syntaxTree == null) {
            return null;
        }

        // Проверка на деление на ноль
        checkDivisionByZero(syntaxTree);

        // Модификация дерева с учетом типов
        Node modifiedTree = modifyTree(syntaxTree, symbolTable);

        // Флаг для определения наличия вещественного типа
        boolean hasFloat = hasFloatType(modifiedTree, symbolTable);

        // Дополнительный проход по дереву для конвертации целочисленных типов в вещественные
        if (hasFloat) {
            return addConversion(modifiedTree, symbolTable);
        }

        return modifiedTree;
    }

    private void checkDivisionByZero(Node node) {
        if (node.getValue().equals("/")) {
            Node right = node.getRight();
            String rightValue = getActualValue(right);
            if (rightValue.equals("0") || rightValue.equals("0.0")) {
                System.err.println("Семантическая ошибка! Деление на ноль.");
                System.exit(1);
            }
        }

        if (node.getLeft() != null) {
            checkDivisionByZero(node.getLeft());
        }
        if (node.getRight() != null) {
            checkDivisionByZero(node.getRight());
        }
    }

    private String getActualValue(Node node) {
        String value = node.getValue();
        if (value.startsWith("<CONSTANT,")) {
            // Извлекаем значение константы из строки
            String[] parts = value.split(",");
            if (parts.length > 1) {
                String constantPart = parts[1];
                return constantPart.substring(0, constantPart.indexOf(">"));
            }
        } else if (value.equals("Int2Float")) {
            return getActualValue(node.getLeft());
        } else {
            return value;
        }
        return null;
    }

    private Node modifyTree(Node node, SymbolTable symbolTable) {
        if (node.getValue().equals("+") || node.getValue().equals("-") || node.getValue().equals("*") || node.getValue().equals("/")) {
            Node left = modifyTree(node.getLeft(), symbolTable);
            Node right = modifyTree(node.getRight(), symbolTable);

            VariableType leftType = getNodeType(left, symbolTable);
            VariableType rightType = getNodeType(right, symbolTable);

            if (leftType == null || rightType == null) {
                System.err.println("Семантическая ошибка! Неизвестный тип операнда.");
                System.exit(1);
            }

            if (!leftType.equals(rightType)) {
                if (leftType.equals(VariableType.INTEGER) && rightType.equals(VariableType.FLOAT)) {
                    left = new Node("Int2Float", left, null);
                } else if (leftType.equals(VariableType.FLOAT) && rightType.equals(VariableType.INTEGER)) {
                    right = new Node("Int2Float", right, null);
                }
            }

            return new Node(node.getValue(), left, right);
        } else if (node.getValue().equals("Int2Float")) {
            // Если узел уже является Int2Float, просто возвращаем его
            return node;
        }

        return node;
    }

    private boolean hasFloatType(Node node, SymbolTable symbolTable) {
        if (node == null) {
            return false;
        }

        VariableType nodeType = getNodeType(node, symbolTable);
        if (nodeType == VariableType.FLOAT) {
            return true;
        }

        return hasFloatType(node.getLeft(), symbolTable) || hasFloatType(node.getRight(), symbolTable);
    }

    private Node addConversion(Node node, SymbolTable symbolTable) {
        if (node.getValue().equals("+") || node.getValue().equals("-") || node.getValue().equals("*") || node.getValue().equals("/")) {
            Node left = addConversion(node.getLeft(), symbolTable);
            Node right = addConversion(node.getRight(), symbolTable);

            VariableType leftType = getNodeType(left, symbolTable);
            VariableType rightType = getNodeType(right, symbolTable);

            if (leftType == null || rightType == null) {
                System.err.println("Семантическая ошибка! Неизвестный тип операнда.");
                System.exit(1);
            }

            if (leftType.equals(VariableType.INTEGER)) {
                left = new Node("Int2Float", left, null);
            }
            if (rightType.equals(VariableType.INTEGER)) {
                right = new Node("Int2Float", right, null);
            }

            return new Node(node.getValue(), left, right);
        } else if (node.getValue().equals("Int2Float")) {
            // Если узел уже является Int2Float, просто возвращаем его
            return node;
        }

        return node;
    }

    private VariableType getNodeType(Node node, SymbolTable symbolTable) {
        String value = node.getValue();

        if (value.startsWith("<IDENTIFIER,")) {
            // Извлекаем идентификатор из строки
            String[] parts = value.split(",");
            if (parts.length > 1) {
                String idPart = parts[1];
                return symbolTable.getSymbolType(Integer.parseInt(idPart));
            }
        } else if (value.startsWith("<CONSTANT,")) {
            // Извлекаем тип константы из строки
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
            // Если узел является Int2Float, возвращаем тип FLOAT
            return VariableType.FLOAT;
        } else if (isOperator(value) || isParenthesis(value)) {
            // Если узел является оператором или скобкой, возвращаем тип OPERATOR
            return VariableType.OPERATOR;
        } else {
            // Если тип узла неизвестен, выводим сообщение об ошибке
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