package com.calculator.parser;

import com.calculator.parser.Builders.MathStringExpressionBuilder;
import com.calculator.parser.Entities.StringExpression;
import com.calculator.parser.Exceptions.ParserException;
import com.calculator.parser.Parsers.Math.IntMathStringExpressionParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class IntMathStringExpressionParserTest {
    @Test
    void incorrectNumberType() {
        StringExpression stringExpression = new MathStringExpressionBuilder("(2.5 +3) * x1").build();

        Throwable actualException = catchThrowable(new IntMathStringExpressionParser(stringExpression)::getExpressionResult);

        assertThat(actualException).as("Исключение не сгенерировано")
                                   .isNotNull()
                                   .isInstanceOf(ParserException.class)
                                   .as("Верный тип чисел в выражении")
                                   .hasMessage("Неверный тип чисел в выражении");
    }
}