import com.calculator.parser.builders.MathStringExpressionBuilder;
import com.calculator.parser.entities.StringExpression;
import com.calculator.parser.parsers.math.IntMathStringExpressionParser;

public class TestCalculator {
    public static void main(String[] args) {
        int result;
        StringExpression stringExpression = new MathStringExpressionBuilder("(cos(5) - -1) * x1 + myFun(10) + myFunTwo(2, 2)")
                                                            .setClientFunctionWithOneArgument("myFun",TestCalculator::myFun)
                                                            .setClientFunctionWithTwoArguments("myFunTwo", TestCalculator::myFunTwo)
                                                            .build();
        IntMathStringExpressionParser mathStringExpressionParser = new IntMathStringExpressionParser(stringExpression);
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