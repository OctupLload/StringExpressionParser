package com.calculator.parser.logic;

import com.calculator.parser.builders.LogicStringExpressionBuilder;
import com.calculator.parser.entities.LogicStringExpression;
import com.calculator.parser.exceptions.ParserException;
import com.calculator.parser.parsers.logic.ShortLogicStringExpressionParser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ShortLogicStringExpressionParserTest {
    @Nested
    class EvaluateTest {
        @Test
        void evaluateWithAndOperators() {
            LogicStringExpression stringExpression = new LogicStringExpressionBuilder("T&T&f").build();
            ShortLogicStringExpressionParser shortStringExpressionParser = new ShortLogicStringExpressionParser(stringExpression);

            boolean actualResult = shortStringExpressionParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при операторе И").isEqualTo(false);
        }

        @Test
        void evaluateWithOrOperators() {
            LogicStringExpression stringExpression = new LogicStringExpressionBuilder("t|f|f").build();
            ShortLogicStringExpressionParser shortStringExpressionParser = new ShortLogicStringExpressionParser(stringExpression);

            boolean actualResult = shortStringExpressionParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при операторе ИЛИ").isEqualTo(true);
        }

        @Test
        void evaluateWithAllOperators() {
            LogicStringExpression stringExpression = new LogicStringExpressionBuilder("T&(t|f)").build();
            ShortLogicStringExpressionParser shortStringExpressionParser = new ShortLogicStringExpressionParser(stringExpression);

            boolean actualResult = shortStringExpressionParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при наличии всех операторов").isEqualTo(true);
        }

        @Test
        void evaluateWithSettedVariable() {
            LogicStringExpression stringExpression = new LogicStringExpressionBuilder("T&f|x1").build();
            ShortLogicStringExpressionParser shortStringExpressionParser = new ShortLogicStringExpressionParser(stringExpression);

            shortStringExpressionParser.setVariablesValue(true);
            boolean actualResult = shortStringExpressionParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при подстановке значения переменной").isEqualTo(true);
        }
    }
    @Nested
    class ErrorTest {
        @Test
        void noExpressionError() {
            LogicStringExpression stringExpression = new LogicStringExpressionBuilder("").build();

            Throwable actualException = catchThrowable(new ShortLogicStringExpressionParser(stringExpression)::getExpressionResult);

            assertThat(actualException).as("Исключение не сгенерировано")
                                        .isNotNull()
                                        .isInstanceOf(ParserException.class)
                                        .as("Выражение не пустое")
                                        .hasMessage("Отсутствует выражение");
        }

        @Test
        void incorrectLogicType() {
            LogicStringExpression stringExpression = new LogicStringExpressionBuilder("v | l").build();

            Throwable actualException = catchThrowable(new ShortLogicStringExpressionParser(stringExpression)::getExpressionResult);

            assertThat(actualException).as("Исключение не сгенерировано")
                                        .isNotNull()
                                        .isInstanceOf(ParserException.class)
                                        .as("Верный тип логических значений в выражении")
                                        .hasMessage("Неверный тип логических значений в выражении");
        }
    }
}