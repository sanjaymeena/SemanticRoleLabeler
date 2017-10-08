package mate.is2.data;

import mate.is2.data.F2SF;
import mate.is2.data.IFV;
import mate.is2.data.Long2IntInterface;

final public class F2SF extends IFV {

	final private float[] parameters;
	

	
	public float score =0;
	
	public F2SF(float[] p) {
		parameters =p;
	}
	
	@Override
	final  public void add(int i) {
		if (i>0) score += parameters[i];
	}

	  
	final  public void add(int[] i) {
		for(int k=0;k<i.length;k++) {
			if (i[k]>0) score += parameters[i[k]];
		}
	}
	
	
	final  public void sub(float[] px,int i, Long2IntInterface li) {
		
		if (i>0) {
			score -= px[li.l2i(i)];
//			score -= px[i];
			//else score -=px[];
		}
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
		return new F2SF(this.parameters);
	}

	/**
	 * @param l2i
	 */
	public void addRel(int i, float f) {
		if (i>0) score += parameters[i]*f;
		
	}
	
	public int length() {
		return this.parameters.length;
	}
	
	
}
