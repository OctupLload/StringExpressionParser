package com.calculator.parser;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class StringParserTest {
    @Nested
    class EvaluateTest {
        @Test
        void evaluateWithAdd() throws ParseException {
            StringParser stringParser = new StringParser("2 +2");
            double actualResult = stringParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при сложении").isEqualTo(4.0);
        }

        @Test
        void evaluateWithSubstract() throws ParseException {
            StringParser stringParser = new StringParser("20- 4");
            double actualResult = stringParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при вычитании").isEqualTo(16.0);
        }

        @Test
        void evaluateWithAddAndSubstract() throws ParseException {
            StringParser stringParser = new StringParser("20- 4 + 5 - 10");
            double actualResult = stringParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при сложении и вычитании").isEqualTo(11.0);
        }

        @Test
        void evaluateWithMultiply() throws ParseException {
            StringParser stringParser = new StringParser("5 * 2 *9");
            double actualResult = stringParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при умножении").isEqualTo(90.0);
        }

        @Test
        void evaluateWithDivide() throws ParseException {
            StringParser stringParser = new StringParser("13 / 4 ");
            double actualResult = stringParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при делении").isEqualTo(3.25);
        }

        @Test
        void evaluateWithMultiplyAndDivide() throws ParseException {
            StringParser stringParser = new StringParser("15*20/2 ");
            double actualResult = stringParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат умножении и делении").isEqualTo(150.0);
        }

        @Test
        void evaluateWithBrackets() throws ParseException {
            StringParser stringParser = new StringParser("(2 + 2) * 2");
            double actualResult = stringParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при наличии скобок").isEqualTo(8.0);
        }

        @Test
        void evaluateWithAllOperators() throws ParseException {
            StringParser stringParser = new StringParser("24/ 4 *5-(17-9)");
            double actualResult = stringParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при использовании всех операторов и скобок")
                                    .isEqualTo(22.0);
        }

        @Test
        void evaluateWithVariables() throws ParseException {
            StringParser stringParser = new StringParser("x1 / 3 + (22 + x2)");
            stringParser.setVariablesValue(3, 4);
            double actualResult = stringParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при подстановке значений переменных")
                                    .isEqualTo(27.0);
        }

        @Test
        void evaluateWithUnaryOperator() throws ParseException {
            StringParser stringParser = new StringParser("-x1 * 5 * (2 - -4)");
            stringParser.setVariablesValue(2);
            double actualResult = stringParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при использовании унарного оператора")
                                    .isEqualTo(-60.0);
        }

        @Test
        void evaluateWithMathFunctions() throws ParseException {
            StringParser stringParser = new StringParser("cos(10) + 2 - sin(3)");

            double actualResult = stringParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при наличии математической функции")
                                    .isEqualTo(1.02);
        }

        @Test
        void evaluateWithClientFunction() throws ParseException {
            StringParser stringParser = new StringParser("myFunction(5) + 10");
            EvaluateTest evaluateTest = new EvaluateTest();

            stringParser.setClassWhoCalledLibrary(evaluateTest);
            double actualResult = stringParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при наличии клиентской функции")
                    .isEqualTo(35.0);
        }

        public int myFunction(int paramOne) {
            return paramOne * 5;
        }
    }

    @Nested
    class ErrorTest {
        @Test
        void noExpressionError() {
            StringParser stringParser = new StringParser("  ");
            Throwable actualException = catchThrowable(stringParser::getExpressionResult);

            assertThat(actualException).as("Исключение не сгенерировано").isNotNull()
                                       .isInstanceOf(ParseException.class)
                                       .as("Выражение не пустое")
                                       .hasMessage("Отсутствует выражение");
        }

        @Test
        void bracketError() {
            StringParser stringParser = new StringParser("2 * (2 + 2");
            Throwable actualException = catchThrowable(stringParser::getExpressionResult);

            assertThat(actualException).as("Исключение не сгенерировано").isNotNull()
                                       .isInstanceOf(ParseException.class)
                                       .as("Количество открывающих скобок равно закрывающим")
                                       .hasMessage("Отсутствует скобка");
        }

        @Test
        void divisionByZeroError() {
            StringParser stringParser = new StringParser("10 * 2 / 0");
            Throwable actualException = catchThrowable(stringParser::getExpressionResult);

            assertThat(actualException).as("Исключение не сгенерировано").isNotNull()
                                       .isInstanceOf(ParseException.class)
                                       .as("Деления на ноль не обнаружено")
                                       .hasMessage("Обнаружено деление на ноль");
        }

        @Test
        void syntaxError() {
            StringParser stringParser = new StringParser("2 ++ 4");
            Throwable actualException = catchThrowable(stringParser::getExpressionResult);

            assertThat(actualException).as("Исключение не сгенерировано").isNotNull()
                                       .isInstanceOf(ParseException.class)
                                       .as("С синтаксисом выражения все впорядке")
                                       .hasMessage("Синтаксическая ошибка");
        }

        @Test
        void variableDetectedError() {
            StringParser stringParser = new StringParser("(25 +3) * x1");
            Throwable actualException = catchThrowable(stringParser::getExpressionResult);

            assertThat(actualException).as("Исключение не сгенерировано").isNotNull()
                                       .isInstanceOf(ParseException.class)
                                       .as("Переменных не обнаружено")
                                       .hasMessage("Обнаружена переменная без значения");
        }

        @Test
        void incorrectValuesQuantityError() {
            StringParser stringParser = new StringParser("x1 + 25 *x2");
            Throwable actualException = catchThrowable(() -> stringParser.setVariablesValue(1));

            assertThat(actualException).as("Исключение не сгенерировано").isNotNull()
                                       .isInstanceOf(ParseException.class)
                                       .as("Количество значений больше количества переменных")
                                       .hasMessage("Количество значений меньше количества переменных");
        }

        @Test
        void incorrectVariablesQuantityError() {
            StringParser stringParser = new StringParser("x1 + 25 *x2");
            Throwable actualException = catchThrowable(() -> stringParser.setVariablesValue(4, 5, 6));

            assertThat(actualException).as("Исключение не сгенерировано").isNotNull()
                                       .isInstanceOf(ParseException.class)
                                       .as("Количество значений меньше количества переменных")
                                       .hasMessage("Количество значений больше количества переменных");
        }
    }
}