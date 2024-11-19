package com.example;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Usage: java Main LEX|SYN|SEM|GEN1|GEN2 inputExpr.txt tokens.txt symbols.txt [syntax_tree.txt] [syntax_tree_mod.txt.txt]");
            return;
        }

        String mode = args[0];
        String inputFile = args[1];
        String tokensFile = args[2];
        String symbolsFile = args[3];
        String syntaxTreeFile = mode.equals("SYN") && args.length >= 5 ? args[4] : null;
        String syntaxTreeModFile = mode.equals("SEM") && args.length >= 6 ? args[5] : null;

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
                } else if (mode.equals("SEM")) {
                    SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer();
                    Node syntaxTree = syntaxAnalyzer.analyze(tokens);

                    if (syntaxTree != null) {
                        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
                        Node modifiedTree = semanticAnalyzer.analyze(syntaxTree, analyzer.getSymbolTable());

                        if (modifiedTree != null && syntaxTreeModFile != null) {
                            FileHandler.writeSyntaxTree(syntaxTreeModFile, modifiedTree);
                        }
                    } else {
                        System.err.println("Синтаксическая ошибка! Неверный порядок токенов.");
                    }
                } else if (mode.equals("GEN1")) {
                    SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer();
                    Node syntaxTree = syntaxAnalyzer.analyze(tokens);

                    if (syntaxTree != null) {
                        IntermediateCodeGenerator codeGenerator = new IntermediateCodeGenerator(analyzer.getSymbolTable());
                        List<String> threeAddressCode = codeGenerator.generate(syntaxTree);
                        FileHandler.writeThreeAddressCode("portable_code.txt", threeAddressCode);
                        FileHandler.writeSymbolTable(symbolsFile, codeGenerator.getSymbolTable().getSymbols());

                    } else {
                        System.err.println("Синтаксическая ошибка! Неверный порядок токенов.");
                    }
                } else if (mode.equals("GEN2")) {
                    SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer();
                    Node syntaxTree = syntaxAnalyzer.analyze(tokens);

                    if (syntaxTree != null) {
                        PostfixGenerator postfixGenerator = new PostfixGenerator(analyzer.getSymbolTable());
                        List<String> postfixCode = postfixGenerator.generate(syntaxTree);
                        FileHandler.writePostfixCode("postfix.txt", postfixCode);
                        //FileHandler.writeSymbolTable(symbolsFile, codeGenerator.getSymbolTable().getSymbols());
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