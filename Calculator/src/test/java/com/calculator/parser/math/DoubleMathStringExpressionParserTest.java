package com.calculator.parser.math;

import com.calculator.parser.builders.MathStringExpressionBuilder;
import com.calculator.parser.entities.MathStringExpression;
import com.calculator.parser.exceptions.ParserException;
import com.calculator.parser.parsers.math.DoubleMathStringExpressionParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class DoubleMathStringExpressionParserTest {
    @Test
    void incorrectNumberType() {
        MathStringExpression stringExpression = new MathStringExpressionBuilder("(25 +3) * x1").build();

        Throwable actualException = catchThrowable(new DoubleMathStringExpressionParser(stringExpression)::getExpressionResult);

        assertThat(actualException).as("Исключение не сгенерировано")
                                   .isNotNull()
                                   .isInstanceOf(ParserException.class)
                                   .as("Верный тип чисел в выражении")
                                   .hasMessage("Неверный тип чисел в выражении");
    }
}