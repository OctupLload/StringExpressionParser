package com.calculator.parser;

import java.util.function.Function;

/**
 * Построитель математического стрового выражения
 */
public class MathStringExpressionBuilder extends StringExpressionBuilder {

    /**
     * Конструктор - создание нового объекта строкового выражения
     * @param expression строковое выражение
     */
    public MathStringExpressionBuilder(String expression) {
        stringExpression = new StringExpression(expression);
    }

    /**
     * Задать выражению используемые клиентские функции
     * @param functionName имя функции
     * @param function функция
     * @return MathStringExpressionBuilder
     */
    public MathStringExpressionBuilder setClientFunction(String functionName, Function<Double, Double> function) {
        stringExpression.setClientFunction(functionName, function);
        return this;
    }
}