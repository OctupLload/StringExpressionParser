package com.calculator.parser.parsers.math;

import com.calculator.parser.entities.MathStringExpression;
import com.calculator.parser.exceptions.ErrorType;
import com.calculator.parser.exceptions.ParserException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Парсер чисел, которые не помещаются в long и double
 */
public class BigDecimalMathStringExpressionParser extends MathStringExpressionParser<String>{

    /**
     * Конструктор - создание нового объекта парсера с заданием объекта строкового выражения
     * @param stringExpression заполненный объект строкового выражения
     */
    public BigDecimalMathStringExpressionParser(MathStringExpression stringExpression) {
        super(stringExpression);
    }

    @Override
    public String getExpressionResult() {
        if (stringExpression.getExpression().isEmpty()) {
            throw new ParserException(ErrorType.NO_EXPRESSION_ERROR);
        }
        String expressionInReversePolishNotation = expressionToReversePolishNotation(stringExpression.getExpression());
        return expressionInReversePolishNotationToResult(expressionInReversePolishNotation);
    }

    @Override
    protected String expressionInReversePolishNotationToResult(String expressionInReversePolishNotation) {
        String operand = "";
        Stack<String> operandStack = new Stack<>();
        BigDecimal a, b;

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
                    } else if (stringExpression.getClientFunctionsWithOneArgument().containsKey(getFunctionNameFromOperand(operand)) ||
                            stringExpression.getClientFunctionsWithTwoArguments().containsKey(getFunctionNameFromOperand(operand))) {
                        operand = getClientFunctionResult(operand);
                    } else {
                        throw new ParserException(ErrorType.VARIABLE_DETECTED_ERROR);
                    }
                }
                operandStack.push(operand);
                operand = "";
            }
            if (getTokenPriority(expressionInReversePolishNotation.charAt(i)) > 1) {
                a = new BigDecimal(operandStack.pop());
                b = new BigDecimal(operandStack.pop());
                if (expressionInReversePolishNotation.charAt(i) == '+') {
                    operandStack.push(String.valueOf(b.add(a)));
                } else if (expressionInReversePolishNotation.charAt(i) == '-') {
                    operandStack.push(String.valueOf(b.subtract(a)));
                } else if (expressionInReversePolishNotation.charAt(i) == '*') {
                    operandStack.push(String.valueOf(b.multiply(a)));
                } else if (expressionInReversePolishNotation.charAt(i) == '/') {
                    if (a.compareTo(BigDecimal.valueOf(0)) == 0) {
                        throw new ParserException(ErrorType.DIVISION_BY_ZERO_ERROR);
                    } else {
                        operandStack.push(String.valueOf(b.divide(a)));
                    }
                }
            }
        }
        return operandStack.pop();
    }

    @Override
    public void setVariablesValue(String... variablesValue) {
        String expression = stringExpression.getExpression();
        String expressionWithSettedVariablesValue = "";
        String variable = "";
        Queue<String> variablesValueQueue = new LinkedList<>();
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
                    if (validateVariableValue(variablesValueQueue.peek())) {
                        expressionWithSettedVariablesValue += variablesValueQueue.poll();
                    }
                    else {
                        throw new ParserException(ErrorType.INCORRECT_VARIABLE_VALUE_TYPE);
                    }
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
     * Проверка корректности значения переменной
     * @param variableValue значение переменной
     * @return является ли значение переменной числом
     */
    private boolean validateVariableValue(String variableValue) {
        char symbol;

        for (int i = 0; i < variableValue.length(); i++) {
            symbol = variableValue.charAt(i);
            if (Character.isDigit(symbol) || symbol == '.') {
                continue;
            }
            return false;
        }

        return true;
    }
}
