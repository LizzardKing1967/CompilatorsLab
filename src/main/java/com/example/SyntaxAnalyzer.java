package com.example;

import java.util.List;
import java.util.Stack;

public class SyntaxAnalyzer {

    public Node analyze(List<Token> tokens) {
        Stack<Node> nodeStack = new Stack<>();
        Stack<Character> operatorStack = new Stack<>();
        int openParentheses = 0;

        Token previousToken = null;

        for (Token token : tokens) {
            TokenType tokenType = token.getType();

            if (tokenType == TokenType.OPERATOR) {
                if (previousToken == null || previousToken.getType() == TokenType.OPERATOR || previousToken.getType() == TokenType.OPEN_PAREN) {
                    System.err.println("Синтаксическая ошибка! Операции <" + token.getValue() + "> не хватает операнда на позиции " + tokens.indexOf(token));
                    return null;
                }
            } else if (tokenType == TokenType.CLOSE_PAREN) {
                if (previousToken == null || previousToken.getType() == TokenType.OPEN_PAREN) {
                    System.err.println("Синтаксическая ошибка! Лишняя закрывающая скобка на позиции " + tokens.indexOf(token));
                    return null;
                }
            }

            if (tokenType == TokenType.IDENTIFIER || tokenType == TokenType.CONSTANT) {
                nodeStack.push(new Node(token.toString()));
            } else if (tokenType == TokenType.OPERATOR) {
                while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(token.getValue().charAt(0))) {
                    if (!processOperator(nodeStack, operatorStack)) return null;
                }
                operatorStack.push(token.getValue().charAt(0));
            } else if (tokenType == TokenType.OPEN_PAREN) {
                operatorStack.push('(');
                openParentheses++;
            } else if (tokenType == TokenType.CLOSE_PAREN) {
                openParentheses--;
                if (!closeParenthesisHandling(nodeStack, operatorStack)) return null;
            }

            previousToken = token;
        }

        if (!finishExpression(nodeStack, operatorStack, openParentheses)) return null;

        return nodeStack.isEmpty() ? null : nodeStack.pop();
    }

    private boolean processOperator(Stack<Node> nodeStack, Stack<Character> operatorStack) {
        if (nodeStack.size() < 2) {
            System.err.println("Синтаксическая ошибка! Недостаточно операндов для завершения выражения.");
            return false;
        }
        Node right = nodeStack.pop();
        Node left = nodeStack.pop();
        nodeStack.push(new Node(String.valueOf(operatorStack.pop()), left, right));
        return true;
    }

    private boolean closeParenthesisHandling(Stack<Node> nodeStack, Stack<Character> operatorStack) {
        while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
            if (!processOperator(nodeStack, operatorStack)) return false;
        }
        if (operatorStack.isEmpty() || operatorStack.pop() != '(') {
            System.err.println("Синтаксическая ошибка! Лишняя закрывающая скобка.");
            return false;
        }
        return true;
    }

    private boolean finishExpression(Stack<Node> nodeStack, Stack<Character> operatorStack, int openParentheses) {
        if (openParentheses > 0) {
            System.err.println("Синтаксическая ошибка! Не хватает закрывающей скобки.");
            return false;
        }
        while (!operatorStack.isEmpty()) {
            if (!processOperator(nodeStack, operatorStack)) return false;
        }
        return true;
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
