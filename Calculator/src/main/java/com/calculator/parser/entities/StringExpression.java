package com.calculator.parser.entities;

/**
 * Объект строкового выражения
 */
public abstract class StringExpression {

    /**
     * Выражение
     */
    private String expression;

    /**
     * Конструктор - создание нового объекта выражения
     * @param expression выражение
     */
    public StringExpression(String expression) {
        this.expression = expression.replaceAll("\\s+", "");
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
}
