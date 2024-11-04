package com.example;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Usage: java Main LEX|SYN inputExpr.txt tokens.txt symbols.txt [syntax_tree.txt]");
            return;
        }

        String mode = args[0];
        String inputFile = args[1];
        String tokensFile = args[2];
        String symbolsFile = args[3];
        String syntaxTreeFile = mode.equals("SYN") && args.length == 5 ? args[4] : null;

        try {
            String expression = FileHandler.readFile(inputFile).replaceAll("\\s+", ""); // Удаляем пробелы
            LexicalAnalyzer analyzer = new LexicalAnalyzer();
            List<Token> tokens = analyzer.analyze(expression);

            if (tokens != null) {
                FileHandler.writeTokens(tokensFile, tokens);
                FileHandler.writeSymbolTable(symbolsFile, analyzer.getSymbolTable().getSymbols());

                if (mode.equals("SYN")) {
                    SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer();
                    Node syntaxTree = syntaxAnalyzer.analyze(tokens);

                    if (syntaxTree != null && syntaxTreeFile != null) {
                        FileHandler.writeSyntaxTree(syntaxTreeFile, syntaxTree);
                    } else {
                        System.err.println("Синтаксическая ошибка! Неверный порядок токенов.");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
