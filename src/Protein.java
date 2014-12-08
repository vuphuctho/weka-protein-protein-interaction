import java.util.Vector;

public class Protein {
	// variables
	private String name;
	private String protein_expr;
	private Vector<Double> gene_expr;
	private Vector<String> GO;
	private Vector<String> DIP;
	
	public Protein() {
		this("");
	}
	
	public Protein(String _name) {
		this.name = _name;
		protein_expr = "";
		gene_expr = new Vector<Double>();
		for (int i=0; i<80; i++) {
			gene_expr.add(0.0);
		}
		GO = new Vector<String>();
		DIP = new Vector<String>();
	}
	
	public String name() {
		return this.name;
	}
	
	public Vector<Double> geneExpr() {
		return this.gene_expr;
	}
	
	public String proteinExpr() {
		return this.protein_expr;
	}
	
	public Vector<String> GO() {
		return this.GO;
	}
	
	public Vector<String> DIP() {
		return this.DIP;
	}
	
	public void setGeneExpr(Vector<Double> _v) {
		for (int i=0; i< _v.size(); i++) {
			gene_expr.set(i, _v.get(i));
		}
	}
	
	public void setProteinExpr(String _v) {
		this.protein_expr = _v;
	}
	
	public void setGO(Vector<String> _v) {
		this.GO = _v;
	}
	
	public void setDIP(Vector<String> _v) {
		this.DIP = _v;
	}
	
	public void addDip(String _s) {
		this.DIP.add(_s);
	}
	
	public String print() {
		String infor = String.format("%s, %d, %d, %d", name, protein_expr.length(), 
													gene_expr.size(), DIP.size());
		return infor;
	}
	
	public static double ProteinExprSim(Protein p1, Protein p2) {
		Vector<Double> triad1 = Protein.computeProteinTriadFreq(p1);
		Vector<Double> triad2 = Protein.computeProteinTriadFreq(p2);
		
		return Similarity.PearsonCorrelation(triad1, triad2);
	}
	
	public static double GeneExprSim(Protein p1, Protein p2) {
		try {
		Vector<Double> gene_expr1 = p1.geneExpr();
		Vector<Double> gene_expr2 = p2.geneExpr();
		
		return Similarity.PearsonCorrelation(gene_expr1, gene_expr2);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.format("stuck at handle %s and %s \n", p1.name(), p2.name());
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public static double GOSim(Protein p1, Protein p2) {
		int unionSize = Similarity.Complement(p1.GO(), p2.GO()).size();
		int minSize = (p1.GO().size()<p2.GO().size())? p1.GO().size() : p2.GO().size();
		
		if (minSize==0) return -1;
		return (double)unionSize/(double)minSize;
	}
	
	public static double DIPSim(Protein p1, Protein p2) {
		Vector<String> DIP1 = p1.DIP();
		Vector<String> DIP2 = p2.DIP();
		
		return Similarity.CommonNeighborSim(DIP1, DIP2);
	}
	
	public static Vector<Double> computeProteinTriadFreq(Protein p) {
		Vector<Double> triad_freq = new Vector<Double>();
		int triad_size = 7*7*7;	// no of triads
		for (int i=0; i<triad_size; i++) {
			double freq = Protein.computeProteinTriadFreq(p, i);
			triad_freq.add(freq);
		}
		return triad_freq;
	}
	
	public static double computeProteinTriadFreq(Protein p, int triad_index) {
		String protein_expr = p.proteinExpr();
		int size = protein_expr.length();
		int count = 0;
		for (int i=0; i<size; i+=3) {
			if (i+3<size) {
				String triad = protein_expr.substring(i, i+3);
				int index = Protein.getTriadIndex(triad);
				if (index==triad_index) count++;
			}
		}
		if (count==0) return 0;
		return (double) (count * 3)/(double) size;
	}
	
	// pre-condition: triad has size = 3
	public static int getTriadIndex(String triad) {
		int index = 0;
		for (int i=0; i<3; i++) {
			char amino_acid = triad.charAt(i);
			switch (amino_acid) {
				case 'A':
				case 'G':
				case 'V':
				{
					index += Math.pow(7, i) * 0;
					break;
				}
				case 'I':
				case 'L':
				case 'F':
				case 'P':
				{
					index += Math.pow(7, i) * 1;
					break;
				}
				case 'Y':
				case 'M':
				case 'T':
				case 'S':
				{
					index += Math.pow(7, i) * 2;
					break;
				}	
				case 'H':
				case 'N':
				case 'Q':
				case 'W':
				{
					index += Math.pow(7, i) * 3;
					break;
				}	
				case 'R':
				case 'K':
				{
					index += Math.pow(7, i) * 4;
					break;
				}	
				case 'D':
				case 'E':
				{
					index += Math.pow(7, i) * 5;
					break;
				}	
				case 'C':
				{
					index += Math.pow(7, i) * 6;
					break;
				}
				default:
					break;
			}
		}
		return index;
	}
 }