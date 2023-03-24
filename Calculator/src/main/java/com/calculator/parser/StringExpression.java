package com.calculator.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Объект строкового выражения
 */
public class StringExpression {
    /**
     * Выражение
     */
    private String expression;

    /**
     * Клиенские функции
     */
    private Map<String, Function<Double, Double>> clientFunctions;

    /**
     * Конструктор - создание нового объекта выражения
     * @param expression выражение
     */
    public StringExpression(String expression) {
        this.expression = expression.replaceAll("\\s+", "");
        clientFunctions = new HashMap<>();
    }

    /**
     * Установить выражение
     * @param expression выражение
     */
    public void setExpression(String expression) {
        this.expression = expression.replaceAll("\\s+", "");
    }

    /**
     * Получить выражение
     * @return выражение
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Установить клиентскую функцию
     * @param functionName имя функции
     * @param function функция
     */
    public void setClientFunction(String functionName, Function<Double, Double> function) {
        this.clientFunctions.put(functionName, function);
    }

    /**
     * Получить клентские функции
     * @return клентские функции
     */
    public Map<String, Function<Double, Double>> getClientFunctions() {
        return clientFunctions;
    }
}
