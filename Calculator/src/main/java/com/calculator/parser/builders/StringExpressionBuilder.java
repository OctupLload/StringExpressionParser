package com.calculator.parser.builders;

/**
 * Построитель строкового выражения
 */
public abstract class StringExpressionBuilder<T> {
    /**
     * Объект строкового выражения
     */
    T stringExpression;

    /**
     * Сборка строкового выражения
     * @return объект строкового выражение
     */
    public T build() {
        return stringExpression;
    }
}