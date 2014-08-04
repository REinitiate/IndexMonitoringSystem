package fnguide.index.monitoring.utility.math;


/**
 * @author REinitiate
 *
 */
public class Stat
{
	public enum FreqType
	{
		Monthly,
		Weekly,
		Daily
	}
	
	public static Double GetAvg(Double[] x){
		double sum = 0.0;
		for(int i=0; i<x.length; i++){
			sum = sum + x[i];
		}
		return sum / (double)x.length; 		
	}
	
	/**
	 * 기능 : 		시계열의 Geometric return 계산
	 * @param x		시계열
	 * @param type	데이터 주기 타입(월별, 주별, 일별)
	 * @return		Double, geometric return
	 */
	public static Double GetGeometricReturn(Double[] x, FreqType type){
		double result = 1.0;		
		for(int i=0; i<x.length; i++)
			result = result * (1 + x[i]);		
		if(type == FreqType.Monthly){
			result = Math.pow(result, 12/(double)x.length);
		}
		return result - 1;
	}
	
    public static double ManualGetTValue(double[] x, double[] y)
    {
        double beta = GetBetaManual(x, y);
        double sse = GetSumOfResidual(x, y, beta);
        double s_2 = sse / (x.length - 1);

        double x_2 = 0.0;
        for (int i = 0; i < x.length; i++)
            x_2 += x[i] * x[i];

        return beta/Math.sqrt(s_2 / x_2);
    }

    public static double GetBetaManual(double[] x, double[] y)
    {
        double x_2 = 0.0;
        double xy = 0.0;
        for (int i = 0; i < x.length; i++)
        {
            x_2 += x[i] * x[i];
            xy += x[i] * y[i];
        }
        return xy / x_2;
    }

    public static double GetSumOfResidual(double[] x, double[] y, double beta)
    {
        double sum = 0.0;
        for (int i = 0; i < x.length; i++)
        {
            sum += Math.pow(y[i] - x[i] * beta, 2);
        }
        return sum;
    }
    
    public static double GetSkew(Double[] x)
    {
        double mean_x = GetAvg(x);
        double sum = 0.0;

        for (int i = 0; i < x.length; i++)
            sum += (x[i] - mean_x) * (x[i] - mean_x) * (x[i] - mean_x);

        sum = sum / (double)(x.length);
        double var = GetVar(x);
        double result = sum / Math.sqrt(Math.pow(var, 3));
        return result;
    }
    
    public static double GetVar(Double[] x)
    {
        double mean = GetAvg(x);
        double result = 0.0;
        for (int i = 0; i < x.length; i++)
        {
            result += Math.pow(x[i] - mean, 2);
        }
        return result / (double)(x.length - 1);
    }
    
    public static Double GetStd(Double[] x)
    {
        Double var = GetVar(x);
        return Math.sqrt(var);
    }

    public static double GetCov(Double[] x1, Double[] x2)
    {
    	try {    		
        if (x1.length != x2.length)
        	throw new Exception("두 벡터의 길이가 같지 않습니다.");
    	else if (x1.length == 0)
            throw new Exception("빈 행렬입니다.");	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        double result = 0.0;
        double mean_x1 = GetAvg(x1);
        double mean_x2 = GetAvg(x2);

        for (int i = 0; i < x1.length; i++)
        {
            result += (x1[i] - mean_x1) * (x2[i] - mean_x2);
        }
        return result / (double)(x1.length - 1);
    }

    /**
     * 기능 : 
     * @param yield			포트폴리오 수익률
     * @param yield_market	시장 수익률
     * @param rf			무위험 이자율
     * @param beta			베타
     * @return
     */
    public static Double GetAlpha(Double[] yield, Double[] yield_market, Double[] rf, Double beta)
    {
    	Integer size = yield.length;
    	Double[] residual = new Double[size];
    	
    	for(int i=0; i<size; i++){
    		residual[i] = yield[i] - (beta * (yield_market[i] - rf[i]) + rf[i]);
    	}
    	return Stat.GetAvg(residual); // 월별
    }
    
    public static Double GetTe(Double[] yield, Double[] yield_market, Double[] rf, Double beta)
    {
    	Integer size = yield.length;
    	Double[] residual = new Double[size];
    	
    	for(int i=0; i<size; i++){
    		residual[i] = yield[i] - (beta * (yield_market[i] - rf[i]) + rf[i]);
    	}
    	return Stat.GetStd(residual); // 월별
    }

    // With Intercept
    /**
     * 기능 : CAPM b0, b1 산출
     * @param x X 시계열
     * @param y Y 시계열
     * @return  double[0] : b0, double[1] : b1
     */
    public static double[] ManualGetBeta(Double[] x, Double[] y)
    {
    	double[] result = new double[2];
    	
        double denom = 0.0;
        double nom = 0.0;

        double mean_x = GetAvg(x);
        double mean_y = GetAvg(y);
        
        double b0, b1;
        for (int i = 0; i < x.length; i++)
        {
            denom += (x[i] - mean_x) * (y[i] - mean_y);
            nom += (x[i] - mean_x) * (x[i] - mean_x);
        }

        b1 = denom / nom;
        b0 = mean_y - b1 * mean_x;
        result[0] = b0;
        result[1] = b1;
        
        return result;
    }
}