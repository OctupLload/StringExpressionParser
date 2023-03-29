package com.calculator.parser.parsers.math;

import com.calculator.parser.entities.MathStringExpression;
import com.calculator.parser.exceptions.ErrorType;
import com.calculator.parser.exceptions.ParserException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Парсер целочисленного математического выражения с использованием обратной польской нотации
 */
public class IntMathStringExpressionParser extends MathStringExpressionParser<Integer> {

    /**
     * Конструктор - создание нового объекта парсера с заданием объекта строкового выражения
     * @param stringExpression заполненный объект строкового выражения
     */
    public IntMathStringExpressionParser(MathStringExpression stringExpression) {
        super(stringExpression);
    }

    /**
     * Получить результат выражения
     * @return результат выражения
     */
    public Integer getExpressionResult() {
        if (stringExpression.getExpression().isEmpty()) {
            throw new ParserException(ErrorType.NO_EXPRESSION_ERROR);
        }
        if (!validateOperandTypeInExpression(stringExpression.getExpression())) {
            throw new ParserException(ErrorType.INCORRECT_NUMBER_TYPE);
        }
        String expressionInReversePolishNotation = expressionToReversePolishNotation(stringExpression.getExpression());
        return Math.toIntExact(Math.round(Double.parseDouble(expressionInReversePolishNotationToResult(expressionInReversePolishNotation))));
    }

    @Override
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
}