package com.calculator.parser.builders;

import com.calculator.parser.entities.StringExpression;

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