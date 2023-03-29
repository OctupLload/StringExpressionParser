package com.calculator.parser.parsers.logic;

import com.calculator.parser.entities.LogicStringExpression;
import com.calculator.parser.exceptions.ErrorType;
import com.calculator.parser.exceptions.ParserException;
import com.calculator.parser.parsers.StringExpressionParser;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Парсер логических выражений с использованием коротких операторов и операндов (&, |, T, F)
 */
public class ShortLogicStringExpressionParser extends StringExpressionParser<LogicStringExpression> {
    /**
     * Конструктор - создание нового объекта парсера с заданием объекта строкового выражения
     * @param stringExpression заполненный объект строкового выражения
     */
    public ShortLogicStringExpressionParser(LogicStringExpression stringExpression) {
        super(stringExpression);
    }

    /**
     * Получение результата логического строкового выражения
     * @return результат выражения
     */
    public boolean getExpressionResult() {
        if (stringExpression.getExpression().isEmpty()) {
            throw new ParserException(ErrorType.NO_EXPRESSION_ERROR);
        }
        if (!validateOperandTypeInExpression(stringExpression.getExpression())) {
            throw new ParserException(ErrorType.INCORRECT_LOGIC_TYPE);
        }
        String expressionInReversePolishNotation = expressionToReversePolishNotation(stringExpression.getExpression());
        String result = expressionInReversePolishNotationToResult(expressionInReversePolishNotation);
        return Boolean.parseBoolean(result);
    }

    @Override
    protected String expressionInReversePolishNotationToResult(String expressionInReversePolishNotation) {
        String operand = "";
        Stack<Boolean> operandStack = new Stack<>();

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
                if (operand.toLowerCase().contains("true") || operand.toLowerCase().contains("false")) {
                    operandStack.push(Boolean.parseBoolean(operand));
                }
                else if (operand.toLowerCase().contains("t")) {
                        operandStack.push(true);
                }
                else if (operand.toLowerCase().contains("f")) {
                    operandStack.push(false);
                }
                else {
                    throw new ParserException(ErrorType.VARIABLE_DETECTED_ERROR);
                }
                operand = "";
            }
            if (getTokenPriority(expressionInReversePolishNotation.charAt(i)) > 1) {
                boolean a = operandStack.pop();
                boolean b = operandStack.pop();
                if (expressionInReversePolishNotation.charAt(i) == '&') {
                    operandStack.push(b & a);
                } else if (expressionInReversePolishNotation.charAt(i) == '|') {
                    operandStack.push(b | a);
                }
            }
        }
        return Boolean.toString(operandStack.pop());
    }

    @Override
    protected int getTokenPriority(char token) {
        if (token == '&') {
            return 3;
        }
        else if (token == '|') {
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
     * Установка переменных в выражение
     * @param variablesValue значения переменных
     */
    public void setVariablesValue(Boolean... variablesValue) {
        String expression = stringExpression.getExpression();
        String expressionWithSettedVariablesValue = "";
        String variable = "";
        Queue<Boolean> variablesValueQueue = new LinkedList<>();
        Collections.addAll(variablesValueQueue, variablesValue);

        for (int token = 0; token < expression.length(); token++) {
            char symbol = expression.charAt(token);
            if (Character.isLetter(symbol) && token != expression.length() - 1) {
                variable += symbol;
            }
            else if (Character.isDigit(symbol) && !variable.isEmpty() && token != expression.length() - 1) {
                variable += symbol;
            }
            else if (!variable.isEmpty()) {
                if (Character.isLetter(symbol)) {
                    variable += symbol;
                }
                if (variable.toLowerCase().contains("t") || variable.toLowerCase().contains("f")) {
                    expressionWithSettedVariablesValue += variable;
                }
                else if (variablesValueQueue.peek() != null) {
                    if (Character.isDigit(symbol)) {
                        symbol = ' ';
                    }
                    if (variablesValueQueue.peek()) {
                        expressionWithSettedVariablesValue += "T";
                    }
                    else {
                        expressionWithSettedVariablesValue += "F";
                    }
                    variablesValueQueue.poll();
                    if (token != expression.length() - 1) {
                        expressionWithSettedVariablesValue += symbol;
                        variable = "";
                        continue;
                    }
                }
                if (getTokenPriority(symbol) != 0 ) {
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

    @Override
    protected boolean validateOperandTypeInExpression(String expression) {
        String operand = "";
        boolean flag = true;
        char symbol;

        for (int i = 0; i < expression.length(); i++) {
            symbol = expression.charAt(i);
            if (Character.isLetter(symbol)) {
                operand += symbol;
            }
            else if (!operand.isEmpty() && Character.isDigit(symbol)) {
                operand += symbol;
            }
            else if (getTokenPriority(expression.charAt(i)) != 0) {
                if (getTokenPriority(expression.charAt(i)) == 1 || getTokenPriority(expression.charAt(i)) == -1) {
                    continue;
                }
                if (operand.toLowerCase().contains("t") || operand.toLowerCase().contains("f")) {
                    operand = "";
                }
                else {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }
}
