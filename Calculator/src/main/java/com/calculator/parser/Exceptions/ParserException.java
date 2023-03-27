package com.calculator.parser.Exceptions;

/**
 * Исключение при вычислении выражения
 */
public class ParserException extends RuntimeException {

    /**
     * Конструктор - создание нового исключения
     * @param errorType тип ошибки
     */
    public ParserException(ErrorType errorType) {
        super(errorType.getDescription());
    }
}