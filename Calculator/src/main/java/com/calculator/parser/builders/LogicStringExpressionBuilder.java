package com.calculator.parser.builders;

import com.calculator.parser.entities.LogicStringExpression;

/**
 * Построитель логического строкового выражения
 */
public class LogicStringExpressionBuilder extends StringExpressionBuilder<LogicStringExpression> {
    /**
     * Конструктор - создание нового объекта строкового выражения
     * @param expression строковое выражение
     */
    public LogicStringExpressionBuilder(String expression) {
        stringExpression = new LogicStringExpression(expression);
    }
}