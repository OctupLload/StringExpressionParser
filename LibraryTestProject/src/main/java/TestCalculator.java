import com.calculator.parser.StringParser;

import java.text.ParseException;

public class TestCalculator {
    public static void main(String[] args) {
        double stringResult;
        StringParser stringParser = new StringParser("(2 - -1) * -x1");
        try {
            stringParser.setVariablesValue(2);
            stringResult = stringParser.getExpressionResult();
            System.out.println("Результат вычисления: " + stringResult);
        } catch (ParseException parseException) {
            System.out.println("Обнаружена ошибка: " + parseException.getMessage());
        }
    }
}