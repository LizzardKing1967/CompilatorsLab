package com.example;

import java.io.*;
import java.util.List;
import java.util.Map;

public class FileHandler {

    public static String readFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    public static void writeTokens(String filename, List<Token> tokens) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (Token token : tokens) {
            writer.write(token.toString());
            writer.newLine();
        }
        writer.close();
    }

    public static void writeSymbolTable(String filename, Map<String, Integer> symbols) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (Map.Entry<String, Integer> entry : symbols.entrySet()) {
            writer.write(entry.getValue() + " - " + entry.getKey());
            writer.newLine();
        }
        writer.close();
    }

    public static void writeSyntaxTree(String filename, Node syntaxTree) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(syntaxTree.printTree("", true));
        writer.close();
    }
}
