
public class PPI {
	// variables
	private String protein1;
	private String protein2;
	private boolean is_positive;
	private double	proteinExprSim;
	private double geneExprSim;
	private double DIPSim;
	
	public PPI() {
		this("", "", false);
	}
	
	public PPI(String p1, String p2, boolean b) {
		this.protein1 = p1;
		this.protein2 = p2;
		this.is_positive = b;
		proteinExprSim = 0.0;
		geneExprSim = 0.0;
		DIPSim = 0.0;
	}
	
	public String protein1() {	return this.protein1;	}
	
	public String protein2() { 	return this.protein2;	}
	
	public boolean is_positive() { return this.is_positive;	}
	
	public double proteinExprSim() {	return this.proteinExprSim;	}
	
	public double geneExprSim() {	return this.geneExprSim;	}

	public double DIPSim() {	return this.DIPSim;	}
	
	public String print() {
		String sim1 = (proteinExprSim==0)? "?" : String.format("%.2f", proteinExprSim);   
		String sim2 = (geneExprSim==0)? "?" : String.format("%.2f", geneExprSim);
		String info = String.format("%s, %s, %s, %s, %.2f, %b\n", protein1, protein2,
									sim1, sim2, DIPSim, is_positive);
		return info;
	}
	
	public void setProteinExprSim(double val) {
		this.proteinExprSim = val;
	}
	
	public void setGeneExprSim(double val) {
		this.geneExprSim = val;
	}
	
	public void setDIPSim(double val) {
		this.DIPSim = val;
	}
}
