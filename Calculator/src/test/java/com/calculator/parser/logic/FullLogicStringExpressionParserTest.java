package com.calculator.parser.logic;

import com.calculator.parser.builders.LogicStringExpressionBuilder;
import com.calculator.parser.entities.LogicStringExpression;
import com.calculator.parser.exceptions.ParserException;
import com.calculator.parser.parsers.logic.FullLogicStringExpressionParser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class FullLogicStringExpressionParserTest {
    @Nested
    class EvaluateTest {
        @Test
        void evaluateWithAndOperators() {
            LogicStringExpression stringExpression = new LogicStringExpressionBuilder("True and false").build();
            FullLogicStringExpressionParser fullStringExpressionParser = new FullLogicStringExpressionParser(stringExpression);

            boolean actualResult = fullStringExpressionParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при операторе И").isEqualTo(false);
        }

        @Test
        void evaluateWithOrOperators() {
            LogicStringExpression stringExpression = new LogicStringExpressionBuilder("true or false or false").build();
            FullLogicStringExpressionParser fullStringExpressionParser = new FullLogicStringExpressionParser(stringExpression);

            boolean actualResult = fullStringExpressionParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при операторе ИЛИ").isEqualTo(true);
        }

        @Test
        void evaluateWithAllOperators() {
            LogicStringExpression stringExpression = new LogicStringExpressionBuilder("True and (true or false)").build();
            FullLogicStringExpressionParser fullStringExpressionParser = new FullLogicStringExpressionParser(stringExpression);

            boolean actualResult = fullStringExpressionParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при наличии всех операторов").isEqualTo(true);
        }

        @Test
        void evaluateWithSettedVariable() {
            LogicStringExpression stringExpression = new LogicStringExpressionBuilder("x1 and x2 or true").build();
            FullLogicStringExpressionParser fullStringExpressionParser = new FullLogicStringExpressionParser(stringExpression);

            fullStringExpressionParser.setVariablesValue(false, true);
            boolean actualResult = fullStringExpressionParser.getExpressionResult();

            assertThat(actualResult).as("Неверный результат при подстановке значения переменной").isEqualTo(true);
        }
    }
    @Nested
    class ErrorTest {
        @Test
        void noExpressionError() {
            LogicStringExpression stringExpression = new LogicStringExpressionBuilder("").build();

            Throwable actualException = catchThrowable(new FullLogicStringExpressionParser(stringExpression)::getExpressionResult);

            assertThat(actualException).as("Исключение не сгенерировано")
                                        .isNotNull()
                                        .isInstanceOf(ParserException.class)
                                        .as("Выражение не пустое")
                                        .hasMessage("Отсутствует выражение");
        }
    }
}