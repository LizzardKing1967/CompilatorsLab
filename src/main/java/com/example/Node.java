package com.example;


public class Node {
    private String value;
    private Node left;
    private Node right;

    public Node(String value) {
        this.value = value;
    }

    public Node(String value, Node left, Node right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public String getValue() {
        return value;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    // Метод для вывода дерева
    public String printTree(String prefix, boolean isLeft) {
        StringBuilder builder = new StringBuilder();
        if (right != null) {
            builder.append(right.printTree(prefix + (isLeft ? "│   " : "    "), false));
        }
        builder.append(prefix + (isLeft ? "└── " : "┌── ") + value + "\n");
        if (left != null) {
            builder.append(left.printTree(prefix + (isLeft ? "    " : "│   "), true));
        }
        return builder.toString();
    }
}

