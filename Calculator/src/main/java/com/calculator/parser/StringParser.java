package com.calculator.parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Класс рекурсивно-последовательного синтаксического анализа выражения,
 * которое представлено в форме строки
 * @version 1.4
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
    private static final List<Character> delimiters = List.of('+', '-', '/', '*', '(', ')');

    /**
     * Список допустимых математических функций, которые могут находиться в выражении
     */
    private static final List<String> mathFunctions = List.of("tan", "sin", "cos");

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
     * Унарная операция
     */
    private char unaryOperator;

    /**
     * Аргументы функции
     */
    private String functionArguments;

    /**
     * Результат вычисления функции
     */
    private double functionValue;

    /**
     * Объект класса, который вызвал библиотеку
     */
    private Object classWhoCalledLibrary;

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
        while ((operator = lexeme.charAt(0)) == '*' ||
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
        if (lexeme.equals("(")) {
            getLexeme();
            result = addOrSubtractTwoTerms();
            if (!lexeme.equals(")")) {
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
        functionArguments = "";
        if (currentIndex == expression.length()) {
            lexeme = EOE;
            return;
        }
        if (isDelimiter(expression.charAt(currentIndex))) {
            if (currentIndex == 0 && expression.charAt(currentIndex) == '-') {
                unaryOperator = expression.charAt(currentIndex);
                currentIndex++;
                getLexeme();
            }
            else if (currentIndex != 0 && isDelimiter(expression.charAt(currentIndex - 1)) &&
                     expression.charAt(currentIndex) == '-') {
                unaryOperator = expression.charAt(currentIndex);
                currentIndex++;
                getLexeme();
            }
            else {
                lexeme += expression.charAt(currentIndex);
                currentIndex++;
                lexemeType = DELIMITER;
            }
        }
        else if (Character.isDigit(expression.charAt(currentIndex))) {
            if (unaryOperator == '-') {
                lexeme += unaryOperator;
            }
            while (!isDelimiter(expression.charAt(currentIndex))) {
                lexeme += expression.charAt(currentIndex);
                currentIndex++;
                if (currentIndex >= expression.length()) {
                    break;
                }
            }
            lexemeType = NUMBER;
            unaryOperator = ' ';
        }
        else if (Character.isLetter(expression.charAt(currentIndex))) {
            while (!isDelimiter(expression.charAt(currentIndex))) {
                lexeme += expression.charAt(currentIndex);
                currentIndex++;
                if (currentIndex >= expression.length() -1) {
                    break;
                }
            }
            if (expression.charAt(currentIndex) == '(') {
                currentIndex++;
                while (!(expression.charAt(currentIndex) == ')')) {
                    functionArguments += expression.charAt(currentIndex);
                    currentIndex++;
                }
                if (mathFunctions.contains(lexeme)) {
                    lexeme = getResultMathFunction(lexeme, functionArguments);
                } else {
                    lexeme = getResultClientFunction(lexeme, functionArguments);
                }
                lexemeType = NUMBER;
            }
            else {
                lexemeType = VARIABLE;
            }
        }
        else {
            lexeme = EOE;
        }
    }

    /**
     * Замена функции в выражении на результат ее вычесления
     * @param lexeme функция для вычисления и замены
     * @return результат вычисления функции
     */
    private String getResultMathFunction(String lexeme, String functionArguments) {
        String lexemeWithFunctionArguments;
        switch (lexeme) {
            case "tan" -> functionValue = Math.tan(Double.parseDouble(functionArguments));
            case "sin" -> functionValue = Math.sin(Double.parseDouble(functionArguments));
            case "cos" -> functionValue = Math.cos(Double.parseDouble(functionArguments));
        }

        functionValue = (double) Math.round(functionValue * 100) / 100;
        lexemeWithFunctionArguments = lexeme + "(" + functionArguments + ")";
        this.expression = expression.replace(lexemeWithFunctionArguments, Double.toString(functionValue));

        this.currentIndex = currentIndex - (lexemeWithFunctionArguments.length() - Double.toString(functionValue).length()) + 1;

        return Double.toString(functionValue);
    }
    /**
     * Замена пользовательской функции на результат ее вычисления
     * @param lexeme функция для вычисления и замены
     * @return результат вычисления пользовательской функции
     */
    private String getResultClientFunction (String lexeme, String functionArguments) {
        String resultClientFunction = "";
        String[] splitedFunctionArguments;
        Method clientFunction = null;
        Method[] clientMethods;
        Object[] clientFunctionArgs;
        String lexemeWithFunctionArguments;
        if (functionArguments.length() == 0 || classWhoCalledLibrary == null) {
            return resultClientFunction = lexeme;
        }
        else {
            splitedFunctionArguments = functionArguments.split(",");
            clientFunctionArgs = new Object[splitedFunctionArguments.length];
        }

        for (int i = 0; i < splitedFunctionArguments.length; i++) {
            clientFunctionArgs[i] = Integer.parseInt(splitedFunctionArguments[i]);
        }

        clientMethods = classWhoCalledLibrary.getClass().getMethods();

        for (Method method: clientMethods) {
            if (method.getName().equals(lexeme) &&
                    method.getParameterTypes().length == clientFunctionArgs.length) {
                clientFunction = method;
            }
        }

        if (clientFunction != null) {
            try {
                resultClientFunction = Integer.toString((Integer) clientFunction.invoke(classWhoCalledLibrary, clientFunctionArgs));
            } catch (IllegalAccessException | InvocationTargetException exception) {
                System.out.println("Ошибка при получении или вычислении пользовательской функции.");
            }
            lexemeWithFunctionArguments = lexeme + "(" + functionArguments + ")";
            this.expression = expression.replace(lexemeWithFunctionArguments, resultClientFunction);
            this.currentIndex = currentIndex - (lexemeWithFunctionArguments.length() - resultClientFunction.length()) + 1;
        }
        else {
            resultClientFunction = lexeme;
        }
        return resultClientFunction;
    }

    /**
     * Установка объекта класса в котором вызывается библиотека
     * @param classWhoCalledLibrary объект класса в котором вызывается библиотека
     */
    public void setClassWhoCalledLibrary (Object classWhoCalledLibrary) {
        this.classWhoCalledLibrary = classWhoCalledLibrary;
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

        while (true) {
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
                    unaryOperator = ' ';
                    break;
                }
                else {
                    handleError(INCORRECT_VARIABLES_QUANTITY_ERROR);
                }
            }
        }
    }
}