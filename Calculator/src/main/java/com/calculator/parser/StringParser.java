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
    private final String NONE = "NONE";
    private final String DELIMITER = "DELIMITER";
    private final String NUMBER = "NUMBER";
    private final String EOE = "EOE";
    private final String VARIABLE = "VARIABLE";

    /**
     * Типы возможных ошибок
     */
    private final String SYNTAX_ERROR = "Синтаксическая ошибка";
    private final String BRACKET_ERROR = "Отсутствует скобка";
    private final String NO_EXPRESSION_ERROR = "Отсутствует выражение";
    private final String DIVISION_BY_ZERO_ERROR = "Обнаружено деление на ноль";
    private final String VARIABLE_DETECTED_ERROR = "Обнаружена переменная без значения";
    private final String INCORRECT_VALUES_QUANTITY_ERROR = "Количество значений не равно количеству переменных";

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
     * Значения переменных для подстановки
     */
    private Integer[] variablesValue;

    /**
     * Конструктор - создание нового объекта с определенным значением
     * @param expression строковое выражение
     */
    public StringParser(String expression) {
        this.expression = expression;
    }

    /**
     * Конструктор - создание нового объекта с определенным выражением,
     * которое содержит переменные, а так же с набором значений для подстановки
     * @param expression строковое выражение
     * @param variablesValue значение переменных для подстановки
     */
    public StringParser(String expression, Integer[] variablesValue) {
        this.expression = expression;
        this.variablesValue = variablesValue;
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
        if (variablesValue != null) {
            setVariablesValueToExpression(expression, variablesValue);
        }
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
        List<Character> delimiters = new ArrayList<>(Arrays.asList('+', '-', '/', '*', '(', ')'));

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
        else if(Character.isDigit(expression.charAt(currentIndex))){
            while(!isDelimiter(expression.charAt(currentIndex))){
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
     * Установка значений переменных вместо переменных в выражение
     * @param expression строковое выражение
     * @param variablesValue значение переменных для подстановки
     * @throws ParseException если количество значений не равно количеству переменных
     */
    private void setVariablesValueToExpression(String expression, Integer[] variablesValue) throws ParseException {
        String replacedExpression = expression;
        Queue<Integer> variablesValueQueue = new LinkedList<>();
        Collections.addAll(variablesValueQueue, variablesValue);

        while(true) {
            getLexeme();
            if (lexemeType.equals(VARIABLE)) {
                if (variablesValueQueue.peek() != null) {
                    replacedExpression = replacedExpression.replaceAll(lexeme, Integer.toString(variablesValueQueue.poll()));
                }
                else {
                    handleError(INCORRECT_VALUES_QUANTITY_ERROR);
                }
            }
            else if (lexeme.equals(EOE)) {
                this.expression = replacedExpression;
                lexeme = "";
                lexemeType = "";
                currentIndex = 0;
                break;
            }
        }
    }
}