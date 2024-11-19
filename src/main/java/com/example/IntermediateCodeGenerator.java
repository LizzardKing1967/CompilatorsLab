package com.example;

import java.util.ArrayList;
import java.util.List;

public class IntermediateCodeGenerator {
    private SymbolTable symbolTable;
    private List<String> threeAddressCode;

    VariableType resultType;

    public String result;
    private int tempCounter;  // Счетчик для временных переменных

    public IntermediateCodeGenerator(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.threeAddressCode = new ArrayList<>();
        this.tempCounter = 1;  // Начинаем с T1
    }

    public SymbolTable getSymbolTable() {
        return this.symbolTable;
    }

    public List<String> generate(Node syntaxTree) {
        generateCode(syntaxTree);
        return threeAddressCode;
    }

    private void generateCode(Node node) {
        if (node == null) return;

        if (node.getValue().equals("+") || node.getValue().equals("-") || node.getValue().equals("*") || node.getValue().equals("/")) {
            // Рекурсивно генерируем код для левого и правого поддерева
            generateCode(node.getLeft());
            generateCode(node.getRight());

            String op = node.getValue();
            resultType = determineResultType(node.getLeft(), node.getRight());
            result = createTempVar(resultType);  // Создаем новую временную переменную с правильным типом

            String left = getOperand(node.getLeft());
            String right = getOperand(node.getRight());

            VariableType leftType = getNodeType(node.getLeft());
            VariableType rightType = getNodeType(node.getRight());

            // Преобразование операндов в тип результата при необходимости
            if (leftType != resultType) {
                if (resultType == VariableType.FLOAT) {
                    String leftTempVar = createTempVar(resultType);
                    threeAddressCode.add("i2f " + leftTempVar + " " + left);  // Преобразуем левый операнд в вещественное число
                    left = leftTempVar;  // Обновляем операнд на преобразованный
                }
            }

            if (rightType != resultType) {
                if (resultType == VariableType.FLOAT) {
                    String rightTempVar = createTempVar(resultType);
                    threeAddressCode.add("i2f " + rightTempVar + " " + right);  // Преобразуем правый операнд в вещественное число
                    right = rightTempVar;  // Обновляем операнд на преобразованный
                }
            }

            // Записываем операнды в трехадресный код
            if (op.equals("*")) {
                threeAddressCode.add("mul " + result + " " + left + " " + right);  // Умножение
            } else if (op.equals("+")) {
                threeAddressCode.add("add " + result + " " + left + " " + right);  // Сложение
            } else if (op.equals("-")) {
                threeAddressCode.add("sub " + result + " " + left + " " + right);  // Вычитание
            } else if (op.equals("/")) {
                threeAddressCode.add("div " + result + " " + left + " " + right);  // Деление
            }
        } else if (node.getValue().equals("Int2Float")) {
            // Преобразуем тип узла в вещественное число
            generateCode(node.getLeft());

            String result = createTempVar(VariableType.FLOAT);
            String operand = getOperand(node.getLeft());

            threeAddressCode.add("i2f " + result + " " + operand);  // Преобразуем операнд
        } else {
            // Если это листья (идентификаторы или константы)
            // Здесь ничего не делаем
        }
    }

    private VariableType determineResultType(Node left, Node right) {
        VariableType leftType = getNodeType(left);
        VariableType rightType = getNodeType(right);

        // Если хотя бы один операнд имеет тип FLOAT, то результат будет FLOAT
        if (leftType == VariableType.FLOAT || rightType == VariableType.FLOAT) {
            return VariableType.FLOAT;
        } else {
            return VariableType.INTEGER;
        }
    }

    private String createTempVar(VariableType type) {
        String tempName = "#T" + tempCounter;  // Создаем имя временной переменной, например #T1, #T2
        int tempVarId = symbolTable.addTempSymbol(tempName, type);  // Добавляем переменную и получаем ID
        tempCounter++;  // Увеличиваем счетчик для следующей временной переменной
        return "<id," + tempVarId + ">";  // Возвращаем идентификатор временной переменной
    }

    private String getOperand(Node node) {
        String value = node.getValue();

        // Проверка на временные переменные
        if (node.isTemporaryVariable()) {
            return value;
        }

        // Проверка идентификаторов и констант
        if (value.startsWith("<IDENTIFIER,") || value.startsWith("<CONSTANT,")) {
            return value;
        }

        // Преобразование для узлов с Int2Float
        if (value.equals("Int2Float")) {
            return getOperand(node.getLeft());
        }

        // Обработка некорректного значения узла
        try {
            // Если операнд некорректен, выбрасываем исключение
            throw new IllegalArgumentException("Некорректное значение узла: " + value);
        } catch (IllegalArgumentException e) {
            // Обработка исключения: создание временной переменной
            System.out.println("Ошибка: " + e.getMessage() + ". Создаём временную переменную.");

            // Создаём временную переменную вместо ошибки
            return createTempVar(resultType); // Например, создаём временную переменную
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