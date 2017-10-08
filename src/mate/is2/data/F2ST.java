package mate.is2.data;

import mate.is2.data.F2ST;
import mate.is2.data.IFV;

final public class F2ST extends IFV {

	final private short[] parameters;
	

	
	public int score =0;
	
	public F2ST(short[] p) {
		parameters =p;
	}
	
	@Override
	final  public void add(int i) {
		if (i>0) score += parameters[i];
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

	public float getScoreF() {
		return score;
	}

	/* (non-Javadoc)
	 * @see mate.is2.IFV#clone()
	 */
	@Override
	public IFV clone() {
		return new F2ST(this.parameters);
	}
	
}
