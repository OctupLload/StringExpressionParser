package com.calculator.parser.Parsers.Math;

import com.calculator.parser.Exceptions.ErrorType;
import com.calculator.parser.Exceptions.ParserException;
import com.calculator.parser.Entities.StringExpression;
import com.calculator.parser.Parsers.StringExpressionParser;

import java.util.List;
import java.util.Stack;

/**
 * Парсер математических выражений с использованием обратной польской нотации
 */
public abstract class MathStringExpressionParser<T> extends StringExpressionParser {

    /**
     * Допустимые математические функции
     */
    protected static final List<String> mathFunctions = List.of("tan", "sin", "cos", "pow", "min", "max");

    /**
     * Конструктор - создание нового объекта парсера с заданием объекта строкового выражения
     * @param stringExpression заполненный объект строкового выражения
     */
    public MathStringExpressionParser(StringExpression stringExpression) {
        super(stringExpression);
    }

    /**
     * Получение результата математического выражения
     * @return значение математического выражения
     */
    public abstract T getExpressionResult();

    /**
     * Получение значения строкового выражения, представленного в форме обратной польской нотации
     * @param expressionInReversePolishNotation строковое выражение в обратной польской нотации
     * @return значение строкового выражения
     */
    protected double expressionInReversePolishNotationToResult(String expressionInReversePolishNotation) {
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
    protected int getTokenPriority(char token) {
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
    public abstract void setVariablesValue(T... variablesValue);

    /**
     * Получить значение математической функции
     * @param operand операнд
     * @return значение математического выражения
     */
    protected String getMathFunctionResult(String operand) {
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
    protected String getClientFunctionResult(String operand) {
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

    @Override
    protected boolean validateOperandTypeInExpression(String expression) {
        String operand = "";
        boolean flag = true;
        char symbol;

        for (int i = 0; i < expression.length(); i++) {
            symbol = expression.charAt(i);
            if (getTokenPriority(symbol) == 0) {
                if (Character.isDigit(symbol)) {
                    operand += symbol;
                }
                else if (symbol == '.' && !operand.isEmpty()) {
                    operand += symbol;
                }
            }
            else {
                if (operand.contains(".")) {
                    flag = false;
                    break;
                }
                else {
                    operand = "";
                }
            }
        }
        return flag;
    }
}