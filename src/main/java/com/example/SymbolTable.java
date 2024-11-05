package com.example;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<Integer, Symbol> symbols = new HashMap<>();
    private int nextId = 1;

    public int addSymbol(String name, VariableType type) {
        symbols.put(nextId, new Symbol(name, type));
        return nextId++;
    }

    public VariableType getSymbolType(int id) {
        return symbols.get(id).getType();
    }

    public Map<Integer, Symbol> getSymbols() {
        return symbols;
    }

    public static class Symbol {
        private String name;
        private VariableType type;

        public Symbol(String name, VariableType type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public VariableType getType() {
            return type;
        }

        @Override
        public String toString() {
            return name + " [" + type.getDescription() + "]";
        }
    }
}