package com.calculator.parser.parsers.logic;

import com.calculator.parser.entities.LogicStringExpression;
import com.calculator.parser.exceptions.ErrorType;
import com.calculator.parser.exceptions.ParserException;

/**
 * Парсер логических выражений с использованием полных операторов и операндов (and, or, false, true)
 */
public class FullLogicStringExpressionParser extends ShortLogicStringExpressionParser {

    /**
     * Установлены ли значения переменных в выражение
     */
    private boolean isSettedVariablesValue = false;

    /**
     * Конструктор - создание нового объекта парсера с заданием объекта строкового выражения
     * @param stringExpression заполненный объект строкового выражения
     */
    public FullLogicStringExpressionParser(LogicStringExpression stringExpression) {
        super(stringExpression);
    }

    /**
     * Получение результата логического строкового выражения
     * @return результат выражения
     */
    public boolean getExpressionResult() {
        String inputExpression;
        if (stringExpression.getExpression().isEmpty()) {
            throw new ParserException(ErrorType.NO_EXPRESSION_ERROR);
        }
        if (isSettedVariablesValue) {
            inputExpression = stringExpression.getExpression();
        }
        else {
            inputExpression = convertExpressionWithFullOperatorsAndOperandsToShort(stringExpression.getExpression());
        }
        String expressionInReversePolishNotation = expressionToReversePolishNotation(inputExpression);
        String result = expressionInReversePolishNotationToResult(expressionInReversePolishNotation);
        return Boolean.parseBoolean(result);
    }

    /**
     * Установка переменных в выражение
     * @param variablesValue значения переменных
     */
    public void setVariablesValue(Boolean... variablesValue) {
        stringExpression.setExpression(convertExpressionWithFullOperatorsAndOperandsToShort(stringExpression.getExpression()));
        super.setVariablesValue(variablesValue);
        isSettedVariablesValue = true;
    }

    /**
     * Преобразование выражения в выражение с краткими операторами и операндами
     * @param expression выражение
     * @return выражение с короткими операторами и операндами
     */
    private String convertExpressionWithFullOperatorsAndOperandsToShort(String expression) {
        String preparedExpression = "";
        String lexeme = "";
        char symbol;

        for (int i = 0; i < expression.length(); i++) {
            symbol = expression.charAt(i);
            if (Character.isLetter(symbol)) {
                lexeme += symbol;
            }
            if (lexeme.toLowerCase().contains("true")) {
                preparedExpression += "T";
                lexeme = "";
            }
            else if (lexeme.toLowerCase().contains("false")) {
                preparedExpression += "F";
                lexeme = "";
            }
            else if (lexeme.toLowerCase().contains("and")) {
                preparedExpression += "&";
                lexeme = "";
            }
            else if (lexeme.toLowerCase().contains("or")) {
                preparedExpression += "|";
                lexeme = "";
            }
            else if (getTokenPriority(symbol) != 0) {
                preparedExpression += symbol;
                lexeme = "";
            }
            else if (Character.isDigit(symbol)) {
                lexeme += symbol;
                preparedExpression += lexeme;
                lexeme = "";
            }
        }

        return preparedExpression;
    }
}