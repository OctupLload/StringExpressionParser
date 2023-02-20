package com.calculator.parser;

import java.text.ParseException;
import java.util.*;

/**
 * Класс рекурсивно-последовательного синтаксического анализа выражения,
 * которое представлено в форме строки
 * @version 1.1
 */
public class StringParser {

    /**
     * Типы лексем
     */
    private static final String NONE = "NONE";
    private static final String DELIMITER = "DELIMITER";
    private static final String NUMBER = "NUMBER";
    private static final String EOE = "EOE";
    private static final String VARIABLE = "VARIABLE";

    /**
     * Типы возможных ошибок
     */
    private static final String SYNTAX_ERROR = "Синтаксическая ошибка";
    private static final String BRACKET_ERROR = "Отсутствует скобка";
    private static final String NO_EXPRESSION_ERROR = "Отсутствует выражение";
    private static final String DIVISION_BY_ZERO_ERROR = "Обнаружено деление на ноль";
    private static final String VARIABLE_DETECTED_ERROR = "Обнаружена переменная без значения";
    private static final String INCORRECT_VALUES_QUANTITY_ERROR = "Количество значений меньше количества переменных";
    private static final String INCORRECT_VARIABLES_QUANTITY_ERROR = "Количество значений больше количества переменных";

    /**
     * Список математических символов, которые являются разделителями
     */
    private static final List<Character> delimiters = Arrays.asList('+', '-', '/', '*', '(', ')');

    /**
     * Выражение в виде строки
     */
    private String expression;

    /**
     * Позиция текущей лексемы
     */
    private int currentIndex;

    /**
     * Лексема
     */
    private String lexeme;

    /**
     * Тип лексемы
     */
    private String lexemeType;

    /**
     * Конструктор - создание нового объекта с определенным значением
     * @param expression строковое выражение
     */
    public StringParser(String expression) {
        this.expression = expression;
    }

    /**
     * Сложить или вычесть два терма
     * @return результат сложения или вычитания двух термов
     * @throws ParseException если не передано выражение, деление на ноль,
     * отсутствие скобки или другая синтаксическая ошибка
     */
    private double addOrSubtractTwoTerms() throws ParseException {
        char operator;
        double result;
        double partialResult;
        result = multiplyOrDivideTwoFactors();
        while (((operator = lexeme.charAt(0)) == '+') || operator == '-') {
            getLexeme();
            partialResult = multiplyOrDivideTwoFactors();
            result = switch (operator) {
                case '-' -> result - partialResult;
                case '+' -> result + partialResult;
                default -> result;
            };
        }
        return result;
    }

    /**
     * Умножить или разделить два фактора
     * @return результат умножения или деления двух факторов
     * @throws ParseException если не передано выражение, деление на ноль,
     * отсутствие скобки или другая синтаксическая ошибка
     */
    private double multiplyOrDivideTwoFactors() throws ParseException {
        char operator;
        double result;
        double partialResult;
        result = evaluateInsideBrackets();
        while((operator = lexeme.charAt(0)) == '*' ||
                operator == '/') {
            getLexeme();
            partialResult = evaluateInsideBrackets();
            switch (operator) {
                case '*' -> result = result * partialResult;
                case '/' -> {
                    if (partialResult == 0.0)
                        handleError(DIVISION_BY_ZERO_ERROR);
                    result = result / partialResult;
                }
            }
        }
        return result;
    }

    /**
     * Вычисление выражения в скобках
     * @return вычисленное выражение в скобках
     * @throws ParseException если не передано выражение, деление на ноль,
     * отсутствие скобки или другая синтаксическая ошибка
     */
    private double evaluateInsideBrackets() throws ParseException {
        double result;
        if(lexeme.equals("(")) {
            getLexeme();
            result = addOrSubtractTwoTerms();
            if(!lexeme.equals(")")) {
                handleError(BRACKET_ERROR);
            }
            getLexeme();
        }
        else {
            result = getNumberValue();
        }
        return result;
    }

    /**
     * Получение значения числа
     * @return возвращает значение числа
     * @throws ParseException если не передано выражение, деление на ноль,
     * отсутствие скобки или другая синтаксическая ошибка
     */
    private double getNumberValue() throws ParseException {
        double result = 0.0;
        if (lexemeType.equals(NUMBER)) {
            try {
                result = Double.parseDouble(lexeme);
            } catch (NumberFormatException exception) {
                handleError(SYNTAX_ERROR);
            }
            getLexeme();
        }
        else if (lexemeType.equals(VARIABLE)) {
            handleError(VARIABLE_DETECTED_ERROR);
        }
        else {
            handleError(SYNTAX_ERROR);
        }
        return result;
    }

    /**
     * Является ли символ разделителем
     * @param character символ из выражения
     * @return возвращает true, если символ является разделителем
     */
    private boolean isDelimiter(char character) {
        return delimiters.contains(character);
    }

    /**
     * Отлавливание ошибок и генерация исключения
     * @param errorType тип ошибки
     * @throws ParseException если не передано выражение, деление на ноль,
     * отсутствие скобки или другая синтаксическая ошибка
     */
    private void handleError(String errorType) throws ParseException {
        throw new ParseException(errorType, 0);
    }

    /**
     * Получение лексемы
     */
    private void getLexeme(){
        lexemeType = NONE;
        lexeme = "";
        if (currentIndex == expression.length()) {
            lexeme = EOE;
            return;
        }
        if (isDelimiter(expression.charAt(currentIndex))) {
            lexeme += expression.charAt(currentIndex);
            currentIndex++;
            lexemeType = DELIMITER;
        }
        else if(Character.isDigit(expression.charAt(currentIndex))) {
            while(!isDelimiter(expression.charAt(currentIndex))) {
                lexeme += expression.charAt(currentIndex);
                currentIndex++;
                if(currentIndex >= expression.length()) {
                    break;
                }
            }
            lexemeType = NUMBER;
        }
        else if (Character.isLetter(expression.charAt(currentIndex))) {
            while(!isDelimiter(expression.charAt(currentIndex))) {
                lexeme += expression.charAt(currentIndex);
                currentIndex++;
                if(currentIndex >= expression.length()) {
                    break;
                }
            }
            lexemeType = VARIABLE;
        }
        else {
            lexeme = EOE;
        }
    }

    /**
     * Вычисление значения выражения, которое представлено в форме строки
     * @return результат вычисления выражения
     * @throws ParseException если не передано выражение, деление на ноль,
     * отсутствие скобки или другая синтаксическая ошибка
     */
    public double getExpressionResult() throws ParseException {
        double result;
        this.expression = expression.replaceAll("\\s+", "");
        getLexeme();
        if (lexeme.equals(EOE)) {
            handleError(NO_EXPRESSION_ERROR);
        }
        result = addOrSubtractTwoTerms();
        if (!lexeme.equals(EOE)) {
            handleError(SYNTAX_ERROR);
        }
        return (double) Math.round(result * 100.0) / 100.0;
    }

    /**
     * Установка переданных значений вместо переменных
     * @param variablesValue значения переменных для подстановки
     * @throws ParseException если количество значений больше или меньше количества переменных
     */
    public void setVariablesValue(Integer... variablesValue) throws ParseException {
        this.expression = expression.replaceAll("\\s+", "");
        String expressionWithSetedValues = expression;
        Queue<Integer> variablesValueQueue = new LinkedList<>();
        Collections.addAll(variablesValueQueue, variablesValue);

        while(true) {
            getLexeme();
            if (lexemeType.equals(VARIABLE)) {
                if (variablesValueQueue.peek() != null) {
                    expressionWithSetedValues = expressionWithSetedValues.replaceFirst(lexeme, Integer.toString(variablesValueQueue.poll()));
                }
                else {
                    handleError(INCORRECT_VALUES_QUANTITY_ERROR);
                }
            }
            else if (lexeme.equals(EOE)) {
                if (variablesValueQueue.peek() == null) {
                    this.expression = expressionWithSetedValues;
                    lexeme = "";
                    lexemeType = "";
                    currentIndex = 0;
                    break;
                }
                else {
                    handleError(INCORRECT_VARIABLES_QUANTITY_ERROR);
                }
            }
        }
    }
}