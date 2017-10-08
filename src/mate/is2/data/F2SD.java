package mate.is2.data;

import mate.is2.data.F2SD;
import mate.is2.data.IFV;

final public class F2SD extends IFV {

	final private double[] parameters;
	
	public double score =0;
	
	public F2SD(double[] p) {
		parameters =p;
	}

	
	@Override
	public void add(int i) {
		if (i>0)score += parameters[i];
	}
	
	@Override
	public void clear() {
		score =0;
	}

	
	/* (non-Javadoc)
	 * @see mate.is2.IFV#getScore()
	 */
	@Override
	public double getScore() {
		return score;
	}


	/* (non-Javadoc)
	 * @see mate.is2.IFV#clone()
	 */
	@Override
	public IFV clone() {
		return new F2SD(parameters);
	}

	
}
