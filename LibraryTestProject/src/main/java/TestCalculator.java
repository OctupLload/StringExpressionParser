import com.calculator.parser.StringParser;

import java.text.ParseException;

public class TestCalculator {
    public static void main(String[] args) {
        double stringResult;
        StringParser stringParser = new StringParser("(cos(5) - -1) * -x1 + myFun(10)");
        TestCalculator testCalculator = new TestCalculator();
        stringParser.setClassWhoCalledLibrary(testCalculator);
        try {
            stringParser.setVariablesValue(2);
            stringResult = stringParser.getExpressionResult();
            System.out.println("Результат вычисления: " + stringResult);
        } catch (ParseException parseException) {
            System.out.println("Обнаружена ошибка: " + parseException.getMessage());
        }
    }

    public int myFun (int paramOne) {
        return paramOne * 10;
    }
}