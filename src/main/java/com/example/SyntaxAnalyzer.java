package com.example;

import java.util.List;
import java.util.Stack;

public class SyntaxAnalyzer {

    public Node analyze(List<Token> tokens) {
        Stack<Node> nodeStack = new Stack<>();
        Stack<Character> operatorStack = new Stack<>();
        int openParentheses = 0; // Счетчик открывающих скобок

        Token previousToken = null;

        for (Token token : tokens) {
            String tokenType = token.getType();

            if (isOperator(tokenType)) {
                if (previousToken == null || isOperator(previousToken.getType()) || previousToken.getType().equals("(")) {
                    System.err.println("Синтаксическая ошибка! У операции <" + tokenType + "> на позиции " + tokens.indexOf(token) + " отсутствует операнд.");
                    return null; // Вместо System.exit
                }

            } else if (tokenType.equals(")")) {
                if (previousToken == null || previousToken.getType().equals("(")) {
                    System.err.println("Синтаксическая ошибка! Лишняя закрывающая скобка на позиции " + tokens.indexOf(token));
                    return null; // Вместо System.exit
                }
            }

            // Обработка идентификаторов и констант
            if (tokenType.equals("id") || tokenType.matches("\\d+(\\.\\d+)?")) {
                nodeStack.push(new Node(token.toString()));
            } else if (isOperator(tokenType)) {
                while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(tokenType.charAt(0))) {
                    if (nodeStack.size() < 2) {
                        System.err.println("Синтаксическая ошибка! Недостаточно операндов для завершения выражения.");
                        return null; // Вместо System.exit
                    }
                    Node right = nodeStack.pop();
                    Node left = nodeStack.pop();
                    nodeStack.push(new Node(String.valueOf(operatorStack.pop()), left, right));
                }
                operatorStack.push(tokenType.charAt(0));
            } else if (tokenType.equals("(")) {
                operatorStack.push('(');
                openParentheses++;
            } else if (tokenType.equals(")")) {
                openParentheses--;
                if (operatorStack.isEmpty()) {
                    System.err.println("Синтаксическая ошибка! Лишняя закрывающая скобка на позиции " + tokens.indexOf(token));
                    return null; // Вместо System.exit
                }
                while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                    if (nodeStack.size() < 2) {
                        System.err.println("Синтаксическая ошибка! Недостаточно операндов для завершения выражения.");
                        return null; // Вместо System.exit
                    }
                    Node right = nodeStack.pop();
                    Node left = nodeStack.pop();
                    nodeStack.push(new Node(String.valueOf(operatorStack.pop()), left, right));
                }
                if (!operatorStack.isEmpty()) {
                    operatorStack.pop(); // Убираем '('
                } else {
                    System.err.println("Синтаксическая ошибка! Лишняя закрывающая скобка.");
                    return null; // Вместо System.exit
                }
            }

            previousToken = token;
        }

        // Проверяем несбалансированные скобки
        if (openParentheses > 0) {
            System.err.println("Синтаксическая ошибка! Не хватает закрывающей скобки.");
            return null; // Вместо System.exit
        } else if (openParentheses < 0) {
            System.err.println("Синтаксическая ошибка! Лишняя закрывающая скобка.");
            return null; // Вместо System.exit
        }

        // Проверка на случай, если выражение заканчивается операцией
        if (previousToken != null && isOperator(previousToken.getType())) {
            System.err.println("Синтаксическая ошибка! Выражение заканчивается операцией <" + previousToken.getType() + ">.");
            return null; // Вместо System.exit
        }

        // Обрабатываем оставшиеся операторы
        while (!operatorStack.isEmpty()) {
            if (nodeStack.size() < 2) {
                System.err.println("Синтаксическая ошибка! Недостаточно операндов для завершения выражения.");
                return null; // Вместо System.exit
            }
            Node right = nodeStack.pop();
            Node left = nodeStack.pop();
            nodeStack.push(new Node(String.valueOf(operatorStack.pop()), left, right));
        }

        return nodeStack.isEmpty() ? null : nodeStack.pop();
    }

    private boolean isOperator(String token) {
        return "+-*/".contains(token);
    }

    private int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return 0;
        }
    }
}
