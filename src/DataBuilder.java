import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

public class DataBuilder {
	private HashMap<String, Protein> protein_list;
	private Vector<PPI> positive_PPIs;
	private Vector<PPI> negative_PPIs;
	
	private enum DataType {
		ProteinExpr, GeneExpr, GO, DIP,
		Positive_PPI, Negative_PPI
	};
	
	public DataBuilder() {	
		protein_list = new HashMap<String, Protein>();
		positive_PPIs = new Vector<PPI>();
		negative_PPIs = new Vector<PPI>();
	}
	
	public void buildPPIDataFile() {
		// first build list of protein	
		// second, update protein's attribute values
		readProteinExprData();
		readGeneExprData();
		// readGOData();
		readDIPData();
		// third, read PPI files (both positive and negative) and calculate attribute values
		readPPIData();
		// optional : update missing data if we don't want to ignore them in data file
		updateMissingData();
		// finally, write all PPIs and their attribute values to arff file
		writePPIData();
		
	}
	
	private void readProteinExprData() {
		String file = "data\\orf_trans.19970128.fasta";
		
		readData(file, DataType.ProteinExpr);
	}
	
	private void readGeneExprData() {
		String file = "data\\GeneExpression.txt";
		
		readData(file, DataType.GeneExpr);
	}
	
	// unused method based on difficulty of reading data file
	private void readGOData() {
		String file = "data\\gene_association.sgd";
		
		readData(file, DataType.GO);
	}
	
	private void readDIPData() {
		String file = "data\\DIP.txt";
		
		readData(file, DataType.DIP);
	}
	
	private void readPPIData() {
		String positive_file = "data\\Positives.txt";
		String negative_file = "data\\Negatives.txt";
		
		readData(positive_file, DataType.Positive_PPI);
		readData(negative_file, DataType.Negative_PPI);
	}
	
	private void readData(String file, DataType type) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
						new FileReader(file));
			switch (type) {
				case ProteinExpr: {
					String cur_line;
					String name = "";
					String protein_expr = "";
					while ((cur_line = reader.readLine()) != null) {
						// find first line containing name of protein
						if (cur_line.charAt(0)=='>') {
							String[] words= cur_line.split("\\s+");
							// save found protein to protein list
							if (name.compareTo("")!=0) {
								Protein p = getProtein(name);
								protein_expr = protein_expr.substring(0, protein_expr.length()-1);
								p.setProteinExpr(protein_expr);
								protein_list.put(name, p);
							}
							// get new protein
							name = words[0].substring(1);
							protein_expr = "";
						} else {
							protein_expr += cur_line;
						}
					}
					Protein p = getProtein(name);
					protein_expr = protein_expr.substring(0, protein_expr.length()-1);
					p.setProteinExpr(protein_expr);
					protein_list.put(name, p);
					break;
				}
				case GeneExpr: {
					String cur_line;
					String name = "";
					Vector<Double> gene_expr = new Vector<Double>();
					while ((cur_line = reader.readLine()) != null) {
						gene_expr = new Vector<Double>();
						String[] words = cur_line.split("\\s+");
						name = words[0];
						for (int i=1; i<words.length; i++) {
							double val = Double.parseDouble(words[i]);
							gene_expr.add(val);
						}
						
						// save found protein to protein list
						Protein p = getProtein(name);
						p.setGeneExpr(gene_expr);
						protein_list.put(name, p);
					}
					break;
				}
				case GO:
				{
					break;
				}
				case DIP: 
				{
					String cur_line = "";
					String protein1 = "";
					String protein2 = "";
					while ((cur_line=reader.readLine())!=null) {
						String[] words = cur_line.split("\\s+");
						protein1 = words[0];
						protein2 = words[1];
						Protein p1 = getProtein(protein1);
						Protein p2 = getProtein(protein2);
						p1.addDip(protein2); p2.addDip(protein1);
						protein_list.put(protein1, p1);
						protein_list.put(protein2, p2);
					}
					break;
				}
				default: { // deal with PPI data file
					String cur_line = "";
					String protein1 = "";
					String protein2 = "";
					while ((cur_line=reader.readLine())!=null) {
						String[] words = cur_line.split("\\s+");
						protein1 = words[0];
						protein2 = words[1];
						PPI ppi1 = null; PPI ppi2 = null;
						if (type==DataType.Positive_PPI) {
							ppi1  = new PPI(protein1, protein2, true);
							ppi2  = new PPI(protein2, protein1, true);
						} else {
							ppi1  = new PPI(protein1, protein2, false);
							ppi2  = new PPI(protein2, protein1, false);
						}
						// compute and set protein expression similarity and 
						// gene expression similarity
						double proteinExprSim = ProteinExprSim(protein1, protein2);
						ppi1.setProteinExprSim(proteinExprSim);
						ppi2.setProteinExprSim(proteinExprSim);
						double geneExprSim = GeneExprSim(protein1, protein2);
						ppi1.setGeneExprSim(geneExprSim);
						ppi2.setGeneExprSim(geneExprSim);
						double DIPSim = DIPSim(protein1, protein2);
						ppi1.setDIPSim(DIPSim);
						ppi2.setDIPSim(DIPSim);
						// save to corresponding PPI vector
						if (type==DataType.Positive_PPI) {
							positive_PPIs.add(ppi1); positive_PPIs.add(ppi2);
						} else {
							negative_PPIs.add(ppi1); negative_PPIs.add(ppi2);
						}
					}
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader!=null) reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void writePPIData() {
		try {
			File file = new File("data\\ppi.arff");
			
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			// content to write in file
			bw.write(ArffFormatter.relation("PPI"));
			bw.newLine();
			bw.write(ArffFormatter.attribute("Protein1", ArffFormatter.AttrType.STRING));
			bw.write(ArffFormatter.attribute("Protein2", ArffFormatter.AttrType.STRING));
			bw.write(ArffFormatter.attribute("ProteinExpressionSim", ArffFormatter.AttrType.NUMERIC));
			bw.write(ArffFormatter.attribute("GeneExpressionSim", ArffFormatter.AttrType.NUMERIC));
			bw.write(ArffFormatter.attribute("DIPSim", ArffFormatter.AttrType.NUMERIC));
			String[] cases = {"true", "false"};
			bw.write(ArffFormatter.attribute("PositiveData", cases, ArffFormatter.AttrType.NOMINAL));
			bw.newLine();
			bw.write(ArffFormatter.data());
			for (PPI ppi : positive_PPIs) {
				bw.write(ArffFormatter.data(ppi));
			}
			for (PPI ppi : negative_PPIs) {
				bw.write(ArffFormatter.data(ppi));
			}
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void updateMissingData() {
		// get mean of each attribute in positive and negative sets
		updateMissingData(DataType.Positive_PPI);
		updateMissingData(DataType.Negative_PPI);
	}
	
	private void updateMissingData(DataType type) {
		updateMissingData(type, DataType.ProteinExpr);
		updateMissingData(type, DataType.GeneExpr);
	}
	
	private void updateMissingData(DataType setType, DataType attrType) {
		double mean = 0.0;
		int count = 0;
		if (setType==DataType.Positive_PPI) {
			for (PPI ppi : positive_PPIs) {
				if (attrType==DataType.ProteinExpr) {
					if (ppi.proteinExprSim()!=0) count++;
					mean += ppi.proteinExprSim();
				} else if (attrType==DataType.GeneExpr) {
					if (ppi.geneExprSim()!=0) count++;
					mean += ppi.geneExprSim();
				}
			}
			if (count>0) {
				mean = mean/count;
				for (int i=0; i<positive_PPIs.size(); i++) {
					PPI ppi = positive_PPIs.get(i);
					if (attrType==DataType.ProteinExpr) {
						if (ppi.proteinExprSim()==0) {
							ppi.setProteinExprSim(mean);
							positive_PPIs.set(i, ppi);
						}
					} else if (attrType==DataType.GeneExpr) {
						if (ppi.geneExprSim()==0) {
							ppi.setGeneExprSim(mean);
							positive_PPIs.set(i, ppi);
						}
					}
				}
			}
		} else if (setType==DataType.Negative_PPI) {
			for (PPI ppi : negative_PPIs) {
				if (attrType==DataType.ProteinExpr) {
					if (ppi.proteinExprSim()!=0) count++;
					mean += ppi.proteinExprSim();
				} else if (attrType==DataType.GeneExpr) {
					if (ppi.geneExprSim()!=0) count++;
					mean += ppi.geneExprSim();
				}
			}
			if (count>0) {
				mean = mean/count;
				for (int i=0; i<negative_PPIs.size(); i++) {
					PPI ppi = negative_PPIs.get(i);
					if (attrType==DataType.ProteinExpr) {
						if (ppi.proteinExprSim()==0) {
							ppi.setProteinExprSim(mean);
							negative_PPIs.set(i, ppi);
						}
					} else if (attrType==DataType.GeneExpr) {
						if (ppi.geneExprSim()==0) {
							ppi.setGeneExprSim(mean);
							negative_PPIs.set(i, ppi);
						}
					}
				}
			}
		}
	}
	
	private double ProteinExprSim(String protein1, String protein2) {
		Protein p1 = getProtein(protein1);
		Protein p2 = getProtein(protein2);
		return Protein.ProteinExprSim(p1, p2);
	}
	
	private double GeneExprSim(String protein1, String protein2) {
		Protein p1 = getProtein(protein1);
		Protein p2 = getProtein(protein2);
		return Protein.GeneExprSim(p1, p2);
	}
	
	private double DIPSim(String protein1, String protein2) {
		Protein p1 = getProtein(protein1);
		Protein p2 = getProtein(protein2);
		return Protein.DIPSim(p1, p2);
	}
	
	private Protein getProtein(String name) {
		Protein p = new Protein(name);
		if (protein_list.containsKey(name)) {
			p = protein_list.get(name);
		}	
		return p;
	}
}