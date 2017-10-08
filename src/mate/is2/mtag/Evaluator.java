package mate.is2.mtag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map.Entry;

import mate.is2.data.SentenceData09;
import mate.is2.io.CONLLReader09;


public class Evaluator {

	public static void evaluate (String act_file,  String pred_file, String format) throws Exception {

		CONLLReader09 goldReader = new CONLLReader09(act_file);//DependencyReader.createDependencyReader();
	//	boolean labeled = goldReader.startReading(act_file);

		CONLLReader09 predictedReader = new CONLLReader09();
		 predictedReader.startReading(pred_file);

//		if (labeled != predLabeled)
//			System.out.println("Gold file and predicted file appear to differ on whether or not they are labeled. Expect problems!!!");


		int total = 0, totalP=0,corr = 0, corrL = 0, corrT=0,totalX=0;
		int totalD=0, corrD=0,err=0;
		int numsent = 0, corrsent = 0, corrsentL = 0;
		SentenceData09 goldInstance = goldReader.getNext();
		SentenceData09 predInstance = predictedReader.getNext();

		Hashtable<String,Integer> errors = new Hashtable<String,Integer>();
		Hashtable<String,StringBuffer> words = new Hashtable<String,StringBuffer>();

		
		while(goldInstance != null) {

			int instanceLength = goldInstance.length();

			if (instanceLength != predInstance.length())
				System.out.println("Lengths do not match on sentence "+numsent);

		
			String gold[] = goldInstance.ofeats;
			String pred[] = predInstance.pfeats;

			boolean whole = true;
			boolean wholeL = true;

			// NOTE: the first item is the root info added during nextInstance(), so we skip it.

			for (int i = 1; i < instanceLength; i++) {
				if (gold[i].equals(pred[i])||(gold[i].equals("_")&&pred[i]==null)) corrT++;
				else {
			//		System.out.println("gold:"+goldFeats[i]+" pred:"+predFeats[i]+" "+goldInstance.forms[i]+" snt "+numsent+" i:"+i);
					//for (int k = 1; k < instanceLength; k++) {
						
				//		System.out.print(goldInstance.forms[k]+":"+goldInstance.gpos[k]);
				//		if (k==i)  	System.out.print(":"+predInstance.gpos[k]);
				//		System.out.print(" ");
						
				//	}
					//System.out.println();
					String key = "gold: '"+gold[i]+"' pred: '"+pred[i]+"'";
					Integer cnt = errors.get(key);
					StringBuffer errWrd = words.get(key);
					if (cnt==null) {
						errors.put(key,1);
						words.put(key, new StringBuffer().append(goldInstance.forms[i]));
					}
					else {
						errors.put(key,cnt+1);		
						errWrd.append(" "+goldInstance.forms[i]);
					}
					err++;

				}
				String[] gf = gold[i].split("|");
				int eq=0;
					
				if (pred[i]!=null) {
					String[] pf = pred[i].split("|");
					totalP +=pf.length;

					if (pf.length>gf.length) totalX +=pf.length;
					else totalX+=gf.length;
					
					for(String g : gf) {
						for(String p : pf) {
							if (g.equals(p)) {eq++;break;}
					}		
				}
				} else totalX+=gf.length;
				totalD +=gf.length;
				corrD +=eq;
			}
			total += instanceLength - 1; // Subtract one to not score fake root token

			if(whole) corrsent++;
			if(wholeL) corrsentL++;
			numsent++;

			goldInstance = goldReader.getNext();
			predInstance = predictedReader.getNext();
		}

		ArrayList<Entry<String, Integer>> opsl = new ArrayList<Entry<String, Integer>>();
		for(Entry<String, Integer> e : errors.entrySet()) {
			opsl.add(e);
		}
		
		Collections.sort(opsl, new Comparator<Entry<String, Integer>>(){

			@Override
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				
				return o1.getValue()==o2.getValue()?0:o1.getValue()>o2.getValue()?-1:1;
			}
			
			
		});
			
		
		int cnt=0;
		System.out.println("10 top most errors:");
		for(Entry<String, Integer> e : opsl) {
			cnt++;
		//	System.out.println(e.getKey()+"  "+e.getValue()+" context: "+words.get(e.getKey()));
		}

		
		System.out.println("Tokens: " + total+" Correct: " + corrT+" "+(float)corrT/total+" R "+((float)corrD/totalD)+" tP "+totalP+" tG "+totalD+" P "+(float)corrD/totalP);
		System.out.println("err: " + err+" total "+total+" corr "+corrT);
//		System.out.println("Unlabeled Complete Correct: " + ((double)corrsent/numsent));

	}

	public static void main (String[] args) throws Exception {
		String format = "CONLL";
		if (args.length > 2)
			format = args[2];

		evaluate(args[0], args[1], format);
	}

}
