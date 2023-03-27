package com.calculator.parser;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * Парсер математических выражений с использованием польской нотации
 * @version 1.5
 */
public class MathStringExpressionParser {
    /**
     * Объект строкового выражения
     */
    private final StringExpression stringExpression;

    /**
     * Допустимые математические функции
     */
    private static final List<String> mathFunctions = List.of("tan", "sin", "cos", "pow", "min", "max");

    /**
     * Конструктор - создание нового объекта парсера с заданием объекта строкового выражения
     * @param stringExpression заполненный объект строкового выражения
     */
    public MathStringExpressionParser(StringExpression stringExpression) {
        this.stringExpression = stringExpression;
    }

    /**
     * Получение результата математического выражения
     * @return значение математического выражения
     */
    public double getExpressionResult() {
        if (stringExpression.getExpression().isEmpty()) {
            throw new ParserException(ErrorType.NO_EXPRESSION_ERROR);
        }
        String expressionInReversePolishNotation = expressionToReversePolishNotation(stringExpression.getExpression());
        return (double) Math.round(expressionInReversePolishNotationToResult(expressionInReversePolishNotation) * 100) / 100;
    }

    /**
     * Перевод математического выражения в польскую нотацию
     * @param expression математическое выражение в строковой форме
     * @return математическое выражения в польской нотации
     */
    private String expressionToReversePolishNotation (String expression) {
        String expressionInReversePolishNotation = "";
        Stack<Character> operatorsStack = new Stack<>();
        int operatorPriority;

        if (expression.charAt(0) == '-' || expression.contains("--")) {
            expression = preparingExpressionWithUnaryOperator(expression);
        }

        for (int i = 0; i < expression.length(); i++) {
            operatorPriority = getTokenPriority(expression.charAt(i));
            if (operatorPriority == 0) {
                expressionInReversePolishNotation += expression.charAt(i);
            }
            else if (operatorPriority == 1) {
                operatorsStack.push(expression.charAt(i));
            }
            else if (operatorPriority > 1) {
                expressionInReversePolishNotation += ' ';
                while (!operatorsStack.isEmpty()) {
                    if (getTokenPriority(operatorsStack.peek()) >= operatorPriority) {
                        expressionInReversePolishNotation += operatorsStack.pop();
                    }
                    else {
                        break;
                    }
                }
                operatorsStack.push(expression.charAt(i));
            }
            else if (operatorPriority == -1) {
                while (getTokenPriority(operatorsStack.peek()) != 1) {
                    expressionInReversePolishNotation += operatorsStack.pop();
                }
                operatorsStack.pop();
            }
        }
        while (!operatorsStack.empty()) {
            expressionInReversePolishNotation += operatorsStack.pop();
        }
        return expressionInReversePolishNotation;
    }

    /**
     * Получение значения строкового выражения, представленного в форме польской нотации
     * @param expressionInReversePolishNotation строковое выражение в польской нотации
     * @return значение строкового выражения в польской нотации
     */
    private double expressionInReversePolishNotationToResult(String expressionInReversePolishNotation) {
        String operand = "";
        Stack<Double> operandStack = new Stack<>();

        for (int i = 0; i < expressionInReversePolishNotation.length(); i++) {
            if (expressionInReversePolishNotation.charAt(i) == ' ') {
                continue;
            }
            if (getTokenPriority(expressionInReversePolishNotation.charAt(i)) == 0) {
                while (expressionInReversePolishNotation.charAt(i) != ' ' &&
                        getTokenPriority(expressionInReversePolishNotation.charAt(i)) == 0) {
                    operand += expressionInReversePolishNotation.charAt(i++);
                    if (i == expressionInReversePolishNotation.length()) {
                        i--;
                        break;
                    }
                }
                if (Character.isLetter(operand.charAt(0))) {
                    if (mathFunctions.contains(getFunctionNameFromOperand(operand))) {
                        operand = getMathFunctionResult(operand);
                    }
                    else if (stringExpression.getClientFunctionsWithOneArgument().containsKey(getFunctionNameFromOperand(operand)) ||
                             stringExpression.getClientFunctionsWithTwoArguments().containsKey(getFunctionNameFromOperand(operand))) {
                        operand = getClientFunctionResult(operand);
                    }
                    else {
                        throw new ParserException(ErrorType.VARIABLE_DETECTED_ERROR);
                    }
                }
                operandStack.push(Double.parseDouble(operand));
                operand = "";
            }
            if (getTokenPriority(expressionInReversePolishNotation.charAt(i)) > 1) {
                double a = operandStack.pop();
                double b = operandStack.pop();
                if (expressionInReversePolishNotation.charAt(i) == '+') {
                    operandStack.push(b + a);
                }
                else if (expressionInReversePolishNotation.charAt(i) == '-') {
                    operandStack.push(b - a);
                }
                else if (expressionInReversePolishNotation.charAt(i) == '*') {
                    operandStack.push(b * a);
                }
                else if (expressionInReversePolishNotation.charAt(i) == '/') {
                    if (a == 0) {
                        throw new ParserException(ErrorType.DIVISION_BY_ZERO_ERROR);
                    }
                    else {
                        operandStack.push(b / a);
                    }
                }
            }
        }
        return operandStack.pop();
    }

    /**
     * Получить приоритет символа
     * @param token символ
     * @return приоритет символа
     */
    private int getTokenPriority(char token) {
        if (token == '*' || token == '/') {
            return 3;
        }
        else if (token == '+' || token == '-') {
            return 2;
        }
        else if (token == '(') {
            return 1;
        }
        else if (token == ')') {
            return -1;
        }
        else {
            return 0;
        }
    }

    /**
     * Установка значений переменных в выражение
     * @param variablesValue значения переменных
     */
    public void setVariablesValue(Integer... variablesValue) {
        String expression = stringExpression.getExpression();
        String expressionWithSettedVariablesValue = "";
        String variable = "";
        Queue<Integer> variablesValueQueue = new LinkedList<>();
        Collections.addAll(variablesValueQueue, variablesValue);

        for (int token = 0; token < expression.length(); token++) {
            char symbol = expression.charAt(token);
            if (Character.isLetter(symbol)) {
                variable += symbol;
            }
            else if (Character.isDigit(symbol) && !variable.isEmpty() && token != expression.length() - 1) {
                variable += symbol;
            }
            else if (!variable.isEmpty()) {
                if (mathFunctions.contains(variable) ||
                    stringExpression.getClientFunctionsWithOneArgument().containsKey(variable) ||
                    stringExpression.getClientFunctionsWithTwoArguments().containsKey(variable)) {
                    expressionWithSettedVariablesValue += variable;
                }
                else if (variablesValueQueue.peek() != null) {
                    expressionWithSettedVariablesValue += Integer.toString(variablesValueQueue.poll());
                    if (token != expression.length() - 1) {
                        expressionWithSettedVariablesValue += symbol;
                    }
                }
                if (getTokenPriority(symbol) == 1 || getTokenPriority(symbol) == -1) {
                    expressionWithSettedVariablesValue += symbol;
                }
                variable = "";
            }
            else {
                expressionWithSettedVariablesValue += symbol;
                variable = "";
            }
        }
        if (!variablesValueQueue.isEmpty()) {
            throw new ParserException(ErrorType.INCORRECT_VARIABLES_QUANTITY_ERROR);
        }
        stringExpression.setExpression(expressionWithSettedVariablesValue);
    }

    /**
     * Подготовка строкового выражения, которое имеет унарные операции
     * @param expressionWithUnaryOperator строковое выражение с унарными операциями
     * @return подготовленное строковое выражение без унарных операций
     */
    private String preparingExpressionWithUnaryOperator(String expressionWithUnaryOperator) {
        String preparedExpression = "";
        for (int token = 0; token < expressionWithUnaryOperator.length(); token++) {
            char symbol = expressionWithUnaryOperator.charAt(token);
            if (symbol == '-') {
                if (token == 0) {
                    preparedExpression += '0';
                }
                else if (preparedExpression.charAt(preparedExpression.length() - 1) == '-') {
                    preparedExpression = preparedExpression.substring(0, preparedExpression.length() - 1) + '+';
                    continue;
                }
            }
            preparedExpression += symbol;
        }
        return preparedExpression;
    }

    /**
     * Получить имя функции из операнда
     * @param operand операнд
     * @return имя функции
     */
    private String getFunctionNameFromOperand(String operand) {
        String functionName = "";
        char symbol;

        for (int symbolPosition = 0; symbolPosition < operand.length(); symbolPosition++) {
            symbol = operand.charAt(symbolPosition);
            if (Character.isLetter(symbol)) {
                functionName += symbol;
            }
            else {
                break;
            }
        }
        return functionName;
    }

    /**
     * Получить аргументы функции из операнда
     * @param operand операнд
     * @return аргументы функции
     */
    private String getFunctionArgumentsFromOperand(String operand) {
        String functionArguments = "";
        int startSymbolPosition = getFunctionNameFromOperand(operand).length();
        char symbol;

        for (int symbolPosition = startSymbolPosition; symbolPosition < operand.length(); symbolPosition++) {
            symbol = operand.charAt(symbolPosition);
            if (Character.isDigit(symbol) || symbol == ',') {
                functionArguments += symbol;
            }
            else {
                break;
            }
        }
        return functionArguments;
    }

    /**
     * Получить значение математической функции
     * @param operand операнд
     * @return значение математического выражения
     */
    private String getMathFunctionResult(String operand) {
        String functionName = getFunctionNameFromOperand(operand);
        String[] functionArguments = getFunctionArgumentsFromOperand(operand).split(",");
        double functionValue = 0.0;

        if (functionArguments.length == 1) {
            switch (functionName) {
                case "tan" -> functionValue = Math.tan(Double.parseDouble(functionArguments[0]));
                case "sin" -> functionValue = Math.sin(Double.parseDouble(functionArguments[0]));
                case "cos" -> functionValue = Math.cos(Double.parseDouble(functionArguments[0]));
            }
        }
        else if(functionArguments.length == 2) {
            switch (functionName) {
                case "pow" -> functionValue = Math.pow(Double.parseDouble(functionArguments[0]),
                                                       Double.parseDouble(functionArguments[1]));
                case "max" -> functionValue = Math.max(Double.parseDouble(functionArguments[0]),
                                                       Double.parseDouble(functionArguments[1]));
                case "min" -> functionValue = Math.min(Double.parseDouble(functionArguments[0]),
                                                       Double.parseDouble(functionArguments[1]));
            }
        }

        functionValue = (double) Math.round(functionValue * 100) / 100;

        return Double.toString(functionValue);
    }

    /**
     * Получение значения клиентской функции
     * @param operand операнд
     * @return значение клиентской функции
     */
    private String getClientFunctionResult(String operand) {
        String functionName = getFunctionNameFromOperand(operand);
        String[] functionArguments = getFunctionArgumentsFromOperand(operand).split(",");
        double functionValue = 0.0;

        if (functionArguments.length == 1) {
            functionValue = stringExpression.getClientFunctionsWithOneArgument()
                                            .get(functionName)
                                            .apply(Double.parseDouble(functionArguments[0]));
        }
        else if (functionArguments.length == 2) {
            functionValue = stringExpression.getClientFunctionsWithTwoArguments()
                                            .get(functionName)
                                            .apply(Double.parseDouble(functionArguments[0]),
                                                   Double.parseDouble(functionArguments[0]));
        }
        functionValue = (double) Math.round(functionValue * 100) / 100;

        return Double.toString(functionValue);
    }
}