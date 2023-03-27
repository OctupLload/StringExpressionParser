import com.calculator.parser.MathStringExpressionBuilder;
import com.calculator.parser.MathStringExpressionParser;
import com.calculator.parser.StringExpression;

public class TestCalculator {
    public static void main(String[] args) {
        double result;
        StringExpression stringExpression = new MathStringExpressionBuilder("(cos(5) - -1) * x1 + myFun(10) + myFunTwo(2, 2)")
                                                            .setClientFunctionWithOneArgument("myFun",TestCalculator::myFun)
                                                            .setClientFunctionWithTwoArguments("myFunTwo", TestCalculator::myFunTwo)
                                                            .build();
        MathStringExpressionParser mathStringExpressionParser = new MathStringExpressionParser(stringExpression);
        mathStringExpressionParser.setVariablesValue(2);
        result = mathStringExpressionParser.getExpressionResult();

        System.out.println(result);
    }

    public static double myFun (double paramOne) {
        return paramOne * 10;
    }
    public static double myFunTwo (double paramOne, double paramTwo) {
        return paramOne + paramTwo;
    }
}