package com.calculator.parser.math;

import com.calculator.parser.builders.MathStringExpressionBuilder;
import com.calculator.parser.entities.MathStringExpression;
import com.calculator.parser.exceptions.ParserException;
import com.calculator.parser.parsers.math.BigDecimalMathStringExpressionParser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class BigDecimalMathStringExpressionParserTest {
    @Nested
    class EvaluateTest {
        @Test
        void evaluateWithAdd() {
            MathStringExpression stringExpression = new MathStringExpressionBuilder("111111111111111111111111111111.111111111111111111111111111111 +" +
                    "111111111111111111111111111111.111111111111111111111111111111").build();

            String actualResult = new BigDecimalMathStringExpressionParser(stringExpression).getExpressionResult();

            assertThat(actualResult).as("Неверный результат при сложении")
                    .isEqualTo("222222222222222222222222222222.222222222222222222222222222222");
        }

        @Test
        void evaluateWithSubstract() {
            MathStringExpression stringExpression = new MathStringExpressionBuilder("222222222222222222222222222222.222222222222222222222222222222 -" +
                    "111111111111111111111111111111.111111111111111111111111111111").build();

            String actualResult = new BigDecimalMathStringExpressionParser(stringExpression).getExpressionResult();

            assertThat(actualResult).as("Неверный результат при вычитании")
                    .isEqualTo("111111111111111111111111111111.111111111111111111111111111111");
        }

        @Test
        void evaluateWithMultiply() {
            MathStringExpression stringExpression = new MathStringExpressionBuilder("222222222222222222222222222222.222222222222222222222222222222 * 2")
                    .build();

            String actualResult = new BigDecimalMathStringExpressionParser(stringExpression).getExpressionResult();

            assertThat(actualResult).as("Неверный результат при умножении")
                    .isEqualTo("444444444444444444444444444444.444444444444444444444444444444");
        }

        @Test
        void evaluateWithDivide() {
            MathStringExpression stringExpression = new MathStringExpressionBuilder("444444444444444444444444444444.444444444444444444444444444444 / 2")
                    .build();

            String actualResult = new BigDecimalMathStringExpressionParser(stringExpression).getExpressionResult();

            assertThat(actualResult).as("Неверный результат при делении")
                    .isEqualTo("222222222222222222222222222222.222222222222222222222222222222");
        }

        @Test
        void evaluateWithVariables() {
            MathStringExpression stringExpression = new MathStringExpressionBuilder("x1 + x2").build();
            BigDecimalMathStringExpressionParser mathStringExpressionParser = new BigDecimalMathStringExpressionParser(stringExpression);
            mathStringExpressionParser.setVariablesValue("222222222222222222222222222222.222222222222222222222222222222",
                    "222222222222222222222222222222.222222222222222222222222222222");

            String actualResult = mathStringExpressionParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при наличии переменных")
                    .isEqualTo("444444444444444444444444444444.444444444444444444444444444444");
        }
    }
    @Nested
    class ErrorTest {
        @Test
        void noExpressionError() {
            MathStringExpression stringExpression = new MathStringExpressionBuilder("").build();

            Throwable actualException = catchThrowable(new BigDecimalMathStringExpressionParser(stringExpression)::getExpressionResult);

            assertThat(actualException).as("Исключение не сгенерировано")
                    .isNotNull()
                    .isInstanceOf(ParserException.class)
                    .as("Выражение не пустое")
                    .hasMessage("Отсутствует выражение");
        }

        @Test
        void divisionByZeroError() {
            MathStringExpression stringExpression = new MathStringExpressionBuilder("222222222222222222222222222222.222222222222222222222222222222 / 0")
                    .build();

            Throwable actualException = catchThrowable(new BigDecimalMathStringExpressionParser(stringExpression)::getExpressionResult);

            assertThat(actualException).as("Исключение не сгенерировано")
                    .isNotNull()
                    .isInstanceOf(ParserException.class)
                    .as("Деления на ноль не обнаружено")
                    .hasMessage("Обнаружено деление на ноль");
        }

        @Test
        void variableDetectedError() {
            MathStringExpression stringExpression = new MathStringExpressionBuilder("222222222222222222222222222222.222222222222222222222222222222 + x2")
                    .build();

            Throwable actualException = catchThrowable(new BigDecimalMathStringExpressionParser(stringExpression)::getExpressionResult);

            assertThat(actualException).as("Исключение не сгенерировано")
                    .isNotNull()
                    .isInstanceOf(ParserException.class)
                    .as("Переменных не обнаружено")
                    .hasMessage("Обнаружена переменная без значения");
        }

        @Test
        void incorrectVariableValueType() {
            MathStringExpression stringExpression = new MathStringExpressionBuilder("222222222222222222222222222222.222222222222222222222222222222 + x2")
                                                                                                                                                .build();

            BigDecimalMathStringExpressionParser mathStringExpressionParser = new BigDecimalMathStringExpressionParser(stringExpression);
            Throwable actualException = catchThrowable(() -> mathStringExpressionParser.setVariablesValue("221312bbb213123"));

            assertThat(actualException).as("Исключение не сгенерировано")
                                        .isNotNull()
                                        .isInstanceOf(ParserException.class)
                                        .as("Верный тип значения переменной")
                                        .hasMessage("Неверный тип значения переменной");
        }

        @Test
        void incorrectVariablesQuantityError() {
            MathStringExpression stringExpression = new MathStringExpressionBuilder("x1 + 25").build();
            BigDecimalMathStringExpressionParser mathStringExpressionParser = new BigDecimalMathStringExpressionParser(stringExpression);
            Throwable actualException = catchThrowable(() -> mathStringExpressionParser
                                                            .setVariablesValue("222222222222222222222222222222.222222222222222222222222222222",
                                                                               "222222222222222222222222222222.222222222222222222222222222222"));

            assertThat(actualException).as("Исключение не сгенерировано")
                                        .isNotNull()
                                        .isInstanceOf(ParserException.class)
                                        .as("Количество значений меньше количества переменных")
                                        .hasMessage("Количество значений больше количества переменных");
        }
    }
}