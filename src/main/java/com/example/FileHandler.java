package com.example;

import java.io.*;
import java.util.List;
import java.util.Map;

public class FileHandler {

    public static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    public static void writeTokens(String filePath, List<Token> tokens) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Token token : tokens) {
                writer.write(token.toString());
                writer.newLine();
            }
        }
    }

    public static void writeSymbolTable(String filePath, Map<Integer, SymbolTable.Symbol> symbols) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<Integer, SymbolTable.Symbol> entry : symbols.entrySet()) {
                writer.write(entry.getKey() + " - " + entry.getValue().toString());
                writer.newLine();
            }
        }
    }

    public static void writeSyntaxTree(String filePath, Node syntaxTree) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writeNode(writer, syntaxTree, 0);
        }
    }

    private static void writeNode(BufferedWriter writer, Node node, int level) throws IOException {
        if (node == null) return;

        for (int i = 0; i < level; i++) {
            writer.write("  ");
        }
        writer.write(node.getValue());
        writer.newLine();

        writeNode(writer, node.getLeft(), level + 1);
        writeNode(writer, node.getRight(), level + 1);
    }
}