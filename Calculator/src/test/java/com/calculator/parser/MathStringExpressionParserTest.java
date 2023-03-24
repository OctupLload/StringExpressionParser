package com.calculator.parser;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class MathStringExpressionParserTest {
    @Nested
    class EvaluateTest {
        @Test
        void evaluateWithAdd() {
            StringExpression stringExpression = new MathStringExpressionBuilder("2 +2").build();

            double actualResult = new MathStringExpressionParser(stringExpression).getExpressionResult();

            assertThat(actualResult).as("Неверный результат при сложении").isEqualTo(4);
        }

        @Test
        void evaluateWithSubstract() {
            StringExpression stringExpression = new MathStringExpressionBuilder("20- 4").build();

            double actualResult = new MathStringExpressionParser(stringExpression).getExpressionResult();

            assertThat(actualResult).as("Неверный результат при вычитании").isEqualTo(16);
        }

        @Test
        void evaluateWithAddAndSubstract() {
            StringExpression stringExpression = new MathStringExpressionBuilder("20- 4 + 5 - 10").build();

            double actualResult = new MathStringExpressionParser(stringExpression).getExpressionResult();

            assertThat(actualResult).as("Неверный результат при сложении и вычитании").isEqualTo(11);
        }

        @Test
        void evaluateWithMultiply() {
            StringExpression stringExpression = new MathStringExpressionBuilder("5 * 2 *9").build();

            double actualResult = new MathStringExpressionParser(stringExpression).getExpressionResult();

            assertThat(actualResult).as("Неверный результат при умножении").isEqualTo(90);
        }

        @Test
        void evaluateWithDivide() {
            StringExpression stringExpression = new MathStringExpressionBuilder("13 / 4 ").build();

            double actualResult = new MathStringExpressionParser(stringExpression).getExpressionResult();

            assertThat(actualResult).as("Неверный результат при делении").isEqualTo(3.25);
        }

        @Test
        void evaluateWithMultiplyAndDivide() {
            StringExpression stringExpression = new MathStringExpressionBuilder("15*20/2 ").build();

            double actualResult = new MathStringExpressionParser(stringExpression).getExpressionResult();

            assertThat(actualResult).as("Неверный результат умножении и делении").isEqualTo(150);
        }

        @Test
        void evaluateWithAllOperators() {
            StringExpression stringExpression = new MathStringExpressionBuilder("(2 + 2) * 2 / 2 - 1").build();

            double actualResult = new MathStringExpressionParser(stringExpression).getExpressionResult();

            assertThat(actualResult).as("Неправильное вычисление с наличием всех операторов и скобок")
                                    .isEqualTo(3);
        }

        @Test
        void evaluateWithVariables() {
            StringExpression stringExpression = new MathStringExpressionBuilder("x1 + 6 / x2").build();
            MathStringExpressionParser mathStringExpressionParser = new MathStringExpressionParser(stringExpression);
            mathStringExpressionParser.setVariablesValue(4, 3);

            double actualResult = mathStringExpressionParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при наличии переменных")
                                    .isEqualTo(6);
        }

        @Test
        void evaluateWithUnaryOperators() {
            StringExpression stringExpression = new MathStringExpressionBuilder("-2 - -2").build();

            double actualResult = new MathStringExpressionParser(stringExpression).getExpressionResult();

            assertThat(actualResult).as("Неправильное вычисление с наличием всех операторов и скобок")
                                    .isEqualTo(0);
        }

        @Test
        void evaluateWithMathFunctionsWhereOneArgument() {
            StringExpression stringExpression = new MathStringExpressionBuilder("cos(10) + tan(2) + sin(3)").build();

            double actualResult = new MathStringExpressionParser(stringExpression).getExpressionResult();

            assertThat(actualResult).as("Неверный результат при наличии математической функции")
                                    .isEqualTo(-2.89);
        }

        @Test
        void evaluateWithClientFunctionWhereOneArgument() {
            StringExpression stringExpression = new MathStringExpressionBuilder("10 + myFun(2)")
                                                        .setClientFunction("myFun", EvaluateTest::myFun)
                                                        .build();

            double actualResult = new MathStringExpressionParser(stringExpression).getExpressionResult();

            assertThat(actualResult).as("Неверный результат при наличии клиентской функции")
                                    .isEqualTo(20);
        }

        public static double myFun(double param) {
            return param * 5;
        }
    }

    @Nested
    class ErrorTest {
        @Test
        void noExpressionError() {
            StringExpression stringExpression = new MathStringExpressionBuilder("").build();

            Throwable actualException = catchThrowable(new MathStringExpressionParser(stringExpression)::getExpressionResult);

            assertThat(actualException).as("Исключение не сгенерировано")
                                       .isNotNull()
                                       .isInstanceOf(ParserException.class)
                                       .as("Выражение не пустое")
                                       .hasMessage("Отсутствует выражение");
        }

        @Test
        void divisionByZeroError() {
            StringExpression stringExpression = new MathStringExpressionBuilder("10 * 2 / 0").build();

            Throwable actualException = catchThrowable(new MathStringExpressionParser(stringExpression)::getExpressionResult);

            assertThat(actualException).as("Исключение не сгенерировано")
                                       .isNotNull()
                                       .isInstanceOf(ParserException.class)
                                       .as("Деления на ноль не обнаружено")
                                       .hasMessage("Обнаружено деление на ноль");
        }

        @Test
        void variableDetectedError() {
            StringExpression stringExpression = new MathStringExpressionBuilder("(25 +3) * x1").build();

            Throwable actualException = catchThrowable(new MathStringExpressionParser(stringExpression)::getExpressionResult);

            assertThat(actualException).as("Исключение не сгенерировано")
                                       .isNotNull()
                                       .isInstanceOf(ParserException.class)
                                       .as("Переменных не обнаружено")
                                       .hasMessage("Обнаружена переменная без значения");
        }

        @Test
        void incorrectVariablesQuantityError() {
            StringExpression stringExpression = new MathStringExpressionBuilder("x1 + 25 *x2").build();
            MathStringExpressionParser mathStringExpressionParser = new MathStringExpressionParser(stringExpression);
            Throwable actualException = catchThrowable(() -> mathStringExpressionParser.setVariablesValue(1, 2, 3));

            assertThat(actualException).as("Исключение не сгенерировано")
                                       .isNotNull()
                                       .isInstanceOf(ParserException.class)
                                       .as("Количество значений меньше количества переменных")
                                       .hasMessage("Количество значений больше количества переменных");
        }
    }
}