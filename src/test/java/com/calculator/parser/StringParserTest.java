package com.calculator.parser;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class StringParserTest {
    @Nested
    class EvaluateTest {
        @Test
        public void evaluateWithAdd() throws ParseException {
            String expression = "2 +2";

            StringParser stringParser = new StringParser(expression);
            double actualResult = stringParser.getExpressionResult();
            double expectedResult = 4.0;

            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        public void evaluateWithSubstract() throws ParseException {
            String expression = "20- 4";

            StringParser stringParser = new StringParser(expression);
            double actualResult = stringParser.getExpressionResult();
            double expectedResult = 16.0;

            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        public void evaluateWithAddAndSubstract() throws ParseException {
            String expression = "20- 4 + 5 - 10";

            StringParser stringParser = new StringParser(expression);
            double actualResult = stringParser.getExpressionResult();
            double expectedResult = 11.0;

            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        public void evaluateWithMultiply() throws ParseException {
            String expression = "5 * 2 *9";

            StringParser stringParser = new StringParser(expression);
            double actualResult = stringParser.getExpressionResult();
            double expectedResult = 90.0;

            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        public void evaluateWithDivide() throws ParseException {
            String expression = "13 / 4 ";

            StringParser stringParser = new StringParser(expression);
            double actualResult = stringParser.getExpressionResult();
            double expectedResult = 3.25;

            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        public void evaluateWithMultiplyAndDivide() throws ParseException {
            String expression = "15*20/2 ";

            StringParser stringParser = new StringParser(expression);
            double actualResult = stringParser.getExpressionResult();
            double expectedResult = 150.0;

            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        public void evaluateWithBrackets() throws ParseException {
            String expression = "(2 + 2) * 2";

            StringParser stringParser = new StringParser(expression);
            double actualResult = stringParser.getExpressionResult();
            double expectedResult = 8.0;

            assertThat(actualResult).isEqualTo(expectedResult);
        }

        @Test
        public void evaluateWithAllOperators() throws ParseException {
            String expression = "24/ 4 *5-(17-9)";

            StringParser stringParser = new StringParser(expression);
            double actualResult = stringParser.getExpressionResult();
            double expectedResult = 22.0;

            assertThat(actualResult).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ErrorTest {
        @Test
        public void noExpressionError() {
            String expression = " ";

            StringParser stringParser = new StringParser(expression);
            Throwable actualException = catchThrowable(stringParser::getExpressionResult);

            assertThat(actualException).isInstanceOf(ParseException.class);
            assertThat(actualException.getMessage()).isEqualTo("Отсутствует выражение");
        }

        @Test
        public void bracketError() {
            String expression = "2 * (2 + 2";

            StringParser stringParser = new StringParser(expression);
            Throwable actualException = catchThrowable(stringParser::getExpressionResult);

            assertThat(actualException).isInstanceOf(ParseException.class);
            assertThat(actualException.getMessage()).isEqualTo("Отсутствует скобка");
        }

        @Test
        public void divisionByZeroError() {
            String expression = "10 * 2 / 0";

            StringParser stringParser = new StringParser(expression);
            Throwable actualException = catchThrowable(stringParser::getExpressionResult);

            assertThat(actualException).isInstanceOf(ParseException.class);
            assertThat(actualException.getMessage()).isEqualTo("Обнаружено деление на ноль");
        }

        @Test
        public void syntaxError() {
            String expression = "2 ++ 4";

            StringParser stringParser = new StringParser(expression);
            Throwable actualException = catchThrowable(stringParser::getExpressionResult);

            assertThat(actualException).isInstanceOf(ParseException.class);
            assertThat(actualException.getMessage()).isEqualTo("Синтаксическая ошибка");
        }
    }
}
