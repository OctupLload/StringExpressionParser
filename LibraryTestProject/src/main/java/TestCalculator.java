import com.calculator.parser.StringParser;

import java.text.ParseException;

public class TestCalculator {
    public static void main(String[] args) {
        double stringResult;
        StringParser stringParser = new StringParser("(25 + 5) * 10 / 32");

        try {
            stringResult = stringParser.getExpressionResult();
            System.out.println("Результат вычисления: " + stringResult);
        } catch (ParseException parseException) {
            System.out.println("Обнаружена ошибка: " + parseException.getMessage());
        }
    }
}