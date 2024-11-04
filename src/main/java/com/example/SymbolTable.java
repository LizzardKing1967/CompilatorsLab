package com.example;

import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, Integer> symbols = new LinkedHashMap<>();
    private int symbolId = 1;

    public int addSymbol(String name) {
        if (!symbols.containsKey(name)) {
            symbols.put(name, symbolId++);
        }
        return symbols.get(name);
    }

    public Map<String, Integer> getSymbols() {
        return symbols;
    }
}
