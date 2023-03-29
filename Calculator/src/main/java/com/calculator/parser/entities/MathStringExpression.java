package com.calculator.parser.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Объект математического строкового выражения
 */
public class MathStringExpression extends StringExpression {

    /**
     * Клиенские функции c одним параметром
     */
    private final Map<String, Function<Double, Double>> clientFunctionsWithOneArgument;

    /**
     * Клиентские функции с двумя параметрами
     */
    private final Map<String, BiFunction<Double, Double, Double>> clientFunctionsWithTwoArgument;

    /**
     * Конструктор - создание нового объекта выражения
     * @param expression выражение
     */
    public MathStringExpression(String expression) {
        super(expression);
        clientFunctionsWithOneArgument = new HashMap<>();
        clientFunctionsWithTwoArgument = new HashMap<>();
    }

    /**
     * Установить клиентскую функцию с одним параметром
     * @param functionName имя функции
     * @param function функция
     */
    public void setClientFunctionWithOneArgument(String functionName, Function<Double, Double> function) {
        this.clientFunctionsWithOneArgument.put(functionName, function);
    }

    /**
     * Получить клентские функции с одним параметром
     * @return клентские функции
     */
    public Map<String, Function<Double, Double>> getClientFunctionsWithOneArgument() {
        return clientFunctionsWithOneArgument;
    }

    /**
     * Установить клиентскую функцию с двумя параметрами
     * @param functionName имя функции
     * @param function функция
     */
    public void setClientFunctionWithTwoArgument(String functionName, BiFunction<Double, Double, Double> function) {
        this.clientFunctionsWithTwoArgument.put(functionName, function);
    }

    /**
     * Получить клиентские функции с двумя параметрами
     * @return клиентские функции
     */
    public Map<String, BiFunction<Double, Double, Double>> getClientFunctionsWithTwoArguments() {
        return clientFunctionsWithTwoArgument;
    }
}