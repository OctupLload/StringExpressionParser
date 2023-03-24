package com.calculator.parser;

/**
 * Класс-перечисление возможных ошибок
 */
public enum ErrorType {
    NO_EXPRESSION_ERROR("Отсутствует выражение"),
    DIVISION_BY_ZERO_ERROR("Обнаружено деление на ноль"),
    VARIABLE_DETECTED_ERROR("Обнаружена переменная без значения"),
    INCORRECT_VARIABLES_QUANTITY_ERROR("Количество значений больше количества переменных");

    /**
     * Описание ошибки
     */
    private final String description;

    /**
     * Конструктор - создание нового типа ошибки
     * @param description описание ошибки
     */
    ErrorType (String description) {
        this.description = description;
    }

    /**
     * Получить описание ошибки
     * @return описание ошибки
     */
    public String getDescription() {
        return description;
    }
}