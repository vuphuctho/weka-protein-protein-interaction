import java.util.Scanner;

public class MainController {
	public MainController() {}
	
	public static void main(String[] args) {
		MainController controller = new MainController();
		controller.run();
	}
	
	private void run() {
		Scanner scanner = null;
		try {
			scanner= new Scanner(System.in);
			System.out.print("Build arff file? (Y/n) \n");
			System.out.print("(If an arff file exists in data folder, building again is unnecessary): ");
			String choice1 = scanner.next().toLowerCase();
			while (choice1.compareTo("y")!=0 && choice1.compareTo("n")!=0) {
				System.out.printf("Not a valid input: %s\n", choice1);
				System.out.print("Build arff file? (Y/n): ");
				choice1 = scanner.next().toLowerCase();
			}
			if (choice1.compareTo("y")==0) {
				// do read and build arff file 
				DataBuilder db = new DataBuilder();
				db.buildPPIDataFile();
				
			}
			// read arff data file and start classification process
			System.out.println("Start evaluation");
			System.out.println("Choose a classification model to use (Insert number from 1 to 3): ");
			System.out.println("1. Decision tree");
			System.out.println("2. Naive Bayes");
			System.out.println("3. k-Nearest Neighbor");
			String choice2 = scanner.next();
			while (parseInt(choice2)<1 || parseInt(choice2)>3) {
				System.out.printf("Not a valid input: %s\n", choice2);
				System.out.print("Choose a classification model to use: ");
				choice2 = scanner.next();
			}
			WekaClassifier wc = new WekaClassifier(parseInt(choice2));
			wc.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (scanner!=null)
				scanner.close();
		}
	}
	
	public int parseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e){
			return -1;
		}
	}
}
