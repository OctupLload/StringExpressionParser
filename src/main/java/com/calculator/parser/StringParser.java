package com.calculator.parser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Класс рекурсивно-последовательного синтаксического анализа выражения,
 * которое представлено в форме строки
 */
public class StringParser {

    /**
     * Типы лексем
     */
    private final String NONE = "NONE";
    private final String DELIMITER = "DELIMITER";
    private final String NUMBER = "NUMBER";
    private final String EOE = "EOE";

    /**
     * Типы возможных ошибок
     */
    private final String SYNTAX_ERROR = "Синтаксическая ошибка";
    private final String BRACKET_ERROR = "Отсутствует скобка";
    private final String NO_EXPRESSION_ERROR = "Отсутствует выражение";
    private final String DIVISION_BY_ZERO_ERROR ="Обнаружено деление на ноль";

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
        return result;
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
            switch (operator) {
                case '-':
                    result = result - partialResult;
                    break;
                case '+':
                    result = result + partialResult;
                    break;
            }
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
                case '*':
                    result = result * partialResult;
                    break;
                case '/':
                    if(partialResult == 0.0)
                        handleError(DIVISION_BY_ZERO_ERROR);
                    result = result / partialResult;
                    break;
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
        } else {
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
                if(currentIndex >= expression.length())
                    break;
            }
            lexemeType = NUMBER;
        }
        else {
            lexeme = EOE;
        }
    }
}