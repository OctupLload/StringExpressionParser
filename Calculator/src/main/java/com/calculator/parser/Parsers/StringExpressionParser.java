package com.calculator.parser.Parsers;

import com.calculator.parser.Entities.StringExpression;

import java.util.Stack;

/**
 * Парсер строковых выражений с использованием обратной польской нотации
 */
public abstract class StringExpressionParser {
    /**
     * Объект строкового выражения
     */
    protected final StringExpression stringExpression;

    /**
     * Конструктор - создание нового объекта парсера с заданием объекта строкового выражения
     * @param stringExpression заполненный объект строкового выражения
     */
    public StringExpressionParser(StringExpression stringExpression) {
        this.stringExpression = stringExpression;
    }

    /**
     * Получить приоритет оператора
     * @param token токен-символ
     * @return приоритет токена
     */
    protected abstract int getTokenPriority(char token);

    /**
     * Получить результат выражения из обратной польской нотации
     * @param expressionInReversePolishNotation выражение в обратной польской нотации
     * @return результат выражения
     */
    protected abstract double expressionInReversePolishNotationToResult(String expressionInReversePolishNotation);

    /**
     * Проверить выражение на наличие неверного типа операнда
     * @param expression выражение
     * @return логический результат валидации
     */
    protected abstract boolean validateOperandTypeInExpression(String expression);

    /**
     * Перевод математического выражения в польскую нотацию
     * @param expression математическое выражение в строковой форме
     * @return математическое выражения в польской нотации
     */
    protected String expressionToReversePolishNotation (String expression) {
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
     * Подготовка строкового выражения, которое имеет унарные операции
     * @param expressionWithUnaryOperator строковое выражение с унарными операциями
     * @return подготовленное строковое выражение без унарных операций
     */
    protected String preparingExpressionWithUnaryOperator(String expressionWithUnaryOperator) {
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
    protected String getFunctionNameFromOperand(String operand) {
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
    protected String getFunctionArgumentsFromOperand(String operand) {
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
}