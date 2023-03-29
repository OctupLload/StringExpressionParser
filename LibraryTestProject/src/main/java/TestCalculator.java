import com.calculator.parser.builders.LogicStringExpressionBuilder;
import com.calculator.parser.builders.MathStringExpressionBuilder;
import com.calculator.parser.entities.LogicStringExpression;
import com.calculator.parser.entities.MathStringExpression;
import com.calculator.parser.parsers.logic.FullLogicStringExpressionParser;
import com.calculator.parser.parsers.math.IntMathStringExpressionParser;

public class TestCalculator {
    public static void main(String[] args) {
        int resultMathExpression;
        boolean resultLogicExpression;

        MathStringExpression mathStringExpression = new MathStringExpressionBuilder("(cos(5) - -1) * x1 + myFun(10) + myFunTwo(2, 2)")
                                                            .setClientFunctionWithOneArgument("myFun",TestCalculator::myFun)
                                                            .setClientFunctionWithTwoArguments("myFunTwo", TestCalculator::myFunTwo)
                                                            .build();
        IntMathStringExpressionParser mathStringExpressionParser = new IntMathStringExpressionParser(mathStringExpression);
        mathStringExpressionParser.setVariablesValue(2);
        resultMathExpression = mathStringExpressionParser.getExpressionResult();

        LogicStringExpression logicStringExpression = new LogicStringExpressionBuilder("False | x1 &(True | False").build();
        FullLogicStringExpressionParser fullLogicStringExpressionParser = new FullLogicStringExpressionParser(logicStringExpression);
        fullLogicStringExpressionParser.setVariablesValue(true);
        resultLogicExpression = fullLogicStringExpressionParser.getExpressionResult();

        System.out.println("Result of math expression: " + resultMathExpression);
        System.out.println("Result of logic expression: " + resultLogicExpression);
    }

    public static double myFun (double paramOne) {
        return paramOne * 10;
    }
    public static double myFunTwo (double paramOne, double paramTwo) {
        return paramOne + paramTwo;
    }
}