package com.example;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<Integer, Symbol> symbols = new HashMap<>();
    private Map<String, Integer> nameToIdMap = new HashMap<>();
    private int nextId = 1;  // Это будет использоваться для генерации уникальных ID

    // Добавление обычной переменной в таблицу символов
    public int addSymbol(String name, VariableType type) {
        symbols.put(nextId, new Symbol(name, type));  // Добавляем в таблицу по ID
        nameToIdMap.put(name, nextId);  // Добавляем имя в карту
        return nextId++;  // Возвращаем текущий ID и увеличиваем для следующего символа
    }

    // Добавление временной переменной в таблицу символов
    public int addTempSymbol(String tempName, VariableType type) {
        symbols.put(nextId, new Symbol(tempName, type));  // Добавляем в таблицу по ID
        nameToIdMap.put(tempName, nextId);  // Добавляем имя в карту
        return nextId++;  // Возвращаем ID и увеличиваем для следующего символа
    }

    // Получение типа переменной по ID
    public VariableType getSymbolType(int id) {
        return symbols.get(id).getType();
    }

    // Получение ID переменной по имени
    public int getSymbolId(String name) {
        Integer id = nameToIdMap.get(name);
        if (id == null) {
            throw new IllegalArgumentException("Symbol with name " + name + " does not exist");
        }
        return id;
    }

    // Получение всех символов в таблице
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
            return "<id," + name + "> - " + type;
        }
    }
}