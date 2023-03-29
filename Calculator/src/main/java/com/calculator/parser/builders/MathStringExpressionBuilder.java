package com.calculator.parser.builders;

import com.calculator.parser.entities.MathStringExpression;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Построитель математического стрового выражения
 */
public class MathStringExpressionBuilder extends StringExpressionBuilder<MathStringExpression> {

    /**
     * Конструктор - создание нового объекта строкового выражения
     * @param expression строковое выражение
     */
    public MathStringExpressionBuilder(String expression) {
        stringExpression = new MathStringExpression(expression);
    }

    /**
     * Задать выражению используемые клиентские функции с одним параметром
     * @param functionName имя функции
     * @param function функция
     * @return MathStringExpressionBuilder
     */
    public MathStringExpressionBuilder setClientFunctionWithOneArgument(String functionName, Function<Double, Double> function) {
        stringExpression.setClientFunctionWithOneArgument(functionName, function);
        return this;
    }

    /**
     * Задать выражению используемые клиентские функции с двумя параметрами
     * @param functionName имя функции
     * @param function функция
     * @return MathStringExpressionBuilder
     */
    public MathStringExpressionBuilder setClientFunctionWithTwoArguments(String functionName, BiFunction<Double, Double, Double> function) {
        stringExpression.setClientFunctionWithTwoArgument(functionName, function);
        return this;
    }
}