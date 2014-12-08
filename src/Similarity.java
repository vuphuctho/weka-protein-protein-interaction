import java.util.Vector;

public class Similarity {
	// Pre-condition: v1 and v2 have same size
	public static double PearsonCorrelation(Vector<Double> v1, Vector<Double> v2) {
		double size = v1.size();
		double mean1 = Similarity.Mean(v1);
		double standard_derivation1 = Similarity.StandardDerivation(v1);
		double mean2 = Similarity.Mean(v2);
		double standard_derivation2 = Similarity.StandardDerivation(v2);
		
		double sum = 0;
		for (int i=0; i<size; i++) {
			sum += (v1.get(i) - mean1) * (v2.get(i) - mean2);
		}
		// avoid empty vector
		if (standard_derivation1==0 || standard_derivation2==0) return 0;
		return sum/(standard_derivation1 * standard_derivation2 * (size-1));
	} 
	
	public static double CommonNeighborSim(Vector<String> v1, Vector<String> v2) {
		int intersectSize = Similarity.Intersection(v1, v2).size();
		int complement1Size = Similarity.Complement(v1, v2).size();
		int complement2Size = Similarity.Complement(v2, v1).size();
		
		return (double)(4 * Math.pow(intersectSize, 2))/ 
			   (double)((complement1Size + 2 * intersectSize + 1) *
						(complement2Size + 2 * intersectSize + 1));
	}
	
	// pre-condition: size > 1
	public static double StandardDerivation(Vector<Double> v) {
		double mean = Similarity.Mean(v);
		double size = v.size();
		double sum = 0;
		for (Double value : v) {
			sum += Math.pow((value-mean), 2);
		}
		return Math.sqrt(sum/(size-1));
	}
	
	// pre-condition: size > 0
	public static double Mean(Vector<Double> v) {
		double size = v.size();
		double result = 0;
		for (Double value : v) {
			result += value/size;
		}
		
		return result;
	}
	
	public static Vector<String> Intersection(Vector<String> v1, Vector<String> v2) {
		Vector<String> result = new Vector<String>();
		for (int i=0; i<v1.size(); i++) {
			if (v2.contains(v1.get(i))) result.add(v1.get(i));
		}
		return result;
	}
	
	public static Vector<String> Complement(Vector<String> v1, Vector<String> v2) {
		Vector<String> result = new Vector<String>();
		for (int i=0; i<v1.size(); i++) {
			if (!v2.contains(v1.get(i))) result.add(v1.get(i));
		}
		return result;
	}
}
