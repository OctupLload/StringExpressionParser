package com.calculator.parser;

/**
 * Построитель строкового выражения
 */
public abstract class StringExpressionBuilder {
    /**
     * Объект строкового выражения
     */
    StringExpression stringExpression;

    /**
     * Сборка строкового выражения
     * @return строковое выражение
     */
    public StringExpression build() {
        return stringExpression;
    }
}