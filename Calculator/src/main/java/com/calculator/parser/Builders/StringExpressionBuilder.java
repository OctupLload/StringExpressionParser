package com.calculator.parser.Builders;

import com.calculator.parser.Entities.StringExpression;

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