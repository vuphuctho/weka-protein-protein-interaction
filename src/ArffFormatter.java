
public class ArffFormatter {
	public enum AttrType {
		NUMERIC, STRING, NOMINAL
	}; 
	
	public static String relation(String relation) {
		String result = String.format("@RELATION %s\n", relation);
		return result;
	}
	
	public static String attribute(String attr, AttrType attr_type) {
		String type = getType(attr_type);
		String result = String.format("@ATTRIBUTE %s %s\n", attr, type);
		return result;
	}
	
	public static String attribute(String attr, String[] choices, AttrType attr_type) {
		String result = "";
		if (attr_type==AttrType.NOMINAL) {
			String list = "{";
			for (int i=0; i<choices.length; i++) {
				list+= choices[i];
				if (i+1<choices.length) {
					list+=",";
				}
			}
			list+="}";
			result = String.format("@ATTRIBUTE %s %s\n", attr, list);
		}
		
		
		
		return result;
	}
	
	public static String data() {
		return "@DATA\n"; 
	}
	
	public static String data(PPI ppi) {
		String result = ppi.print();
		return result;
	}
	
	private static String getType(AttrType type) {
		String result = "";
		switch (type) {
			case NUMERIC: {
				result = "NUMERIC"; break;
			} 
			case STRING: {
				result = "STRING"; break;
			}
			default:
				break;
		}
		return result;
	}
}
