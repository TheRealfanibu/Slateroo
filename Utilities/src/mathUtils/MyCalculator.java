package mathUtils;

public class MyCalculator {
	/*private String rechnung;
	
	private double endErgebnis;
	
	private ArrayList <Zahl> zahlen = new ArrayList<>();
	private ArrayList <Operator> operatoren = new ArrayList<>();
	private ArrayList <Variable> variablen = new ArrayList<>();

	private char[] zeichen;
	
	private Operator prioOp = new Operator();
	
	private boolean weiterRechnen;
	private boolean fehler = false;

	public double calc (String rechnung, double x_Wert) {
		this.rechnung = rechnung;
		weiterRechnen = true;
		manageAbbreviations();
		splitString();
		
		if (!fehler) {
			while (weiterRechnen) {
				setPriority();
				calcPart(prioOp, x_Wert);
			}
		}
		return endErgebnis;
	}
		
	public double calc (String rechnung) {
		return calc(rechnung, 0);
	}
	
	private void manageAbbreviations() {
		rechnung = rechnung.replaceAll("sin", "s").
				replaceAll("cos", "c").
				replaceAll("t", "t").
				replaceAll("PI", String.valueOf(Math.PI)).
				replaceAll("E", String.valueOf(Math.E)).
				replaceAll("sqrt", "w");
		System.out.println(rechnung);
	}
	
	private void splitString () {
		zeichen = rechnung.toCharArray();
		
		zahlen = new ArrayList<>();
		operatoren = new ArrayList<>();
		variablen = new ArrayList<>();
		
		String inhalt = "";
		String lastType = "";
		String currentType = "";
		
		int stelle = 1;
		int prio = 1;
		
		boolean negate = false;
		
		for (int i = 0; i < zeichen.length; i++) {
			String einzeln = String.valueOf(zeichen[i]);
			if (einzeln.equals(" ")) {
				continue;
			}
			
			if (Pattern.matches("\\d|[.]", einzeln)) { // zahl + '.'
				currentType = "zahl";
				if (negate) {
					einzeln = "-" + einzeln;
					negate = false;
				}
			}
			else if (einzeln.equals(",")) { // ','
				currentType = "zahl";
				einzeln = ".";
			}
			else if (einzeln.equals("-") && (lastType.equals("operator") || lastType.equals(""))) {
				negate = true;
				continue;
			}
			else if (Pattern.matches("[+]|[-]|[*]|[/]|[p]|[w]|[s]|[c]|[t]", einzeln)) { // + - * / 'pow' 'sqrt' 'sin' 'cos' 'tan'
				currentType = "operator";
			}
			else if (einzeln.equals("(")) {
				prio++;
				continue;
			}
			else if (einzeln.equals(")")) {
				prio--;
				continue;
				
			}
			else if (einzeln.equals("x")) {
				if (negate) {
					currentType = "-x";
				}
				else {
					currentType = "x";
				}
				
			}
			else {
				showError();
				System.out.println("inhalt: " + inhalt);
				System.out.println("falsches Zeichen in der Gleichung!");
			}
			
			if (lastType.equals("")) {
				lastType = currentType;
			}
			
			if (currentType.equals(lastType) == false || lastType.equals("operator") && currentType.equals("operator")) {
				if (inhalt.equals("") == false) {
					setInArray(inhalt, lastType, stelle, prio);
					stelle++;
				}
				if (currentType.equals("operator")) {
					setInArray(einzeln, "operator", stelle, prio);
					stelle++;
					inhalt = "";
				}
				else 
					inhalt = einzeln;

				lastType = currentType;
			}
			else 
				inhalt += einzeln;
		}
		if (inhalt.equals("") == false) {
			setInArray(inhalt, lastType, stelle, prio);
		}
	}
	

	private void setInArray(String inhalt, String lastType, int stelle, int prio) {
		
		if (lastType.equals("zahl")) {
			Zahl neu = new Zahl();
			neu.inhalt = Double.valueOf(inhalt);
			neu.stelle = stelle;
			zahlen.add(neu);
		}
		else if (lastType.equals("operator")) {
			if (inhalt.length() > 1) {
				showError();
				System.out.println("Es kann keinen mehrstelligen Operator geben!");
			}
			else {
				Operator neu = new Operator();
				neu.inhalt = inhalt.charAt(0);
				neu.prio = prio;
				neu.stelle = stelle;
				operatoren.add(neu);
			}
		}
		else if (lastType.equals("x")) {
			Variable neu = new Variable();
			neu.stelle = stelle;
			variablen.add(neu);
		}
		else if (lastType.equals("-x")) {
			Variable neu = new Variable();
			neu.stelle = stelle;
			neu.negate = true;
			variablen.add(neu);
		}
	}
	
	private void setPriority () {
		if (operatoren.size() != 0) {
			prioOp = operatoren.get(0);
			
			for (int i = 0; i < operatoren.size(); i++) {
				if (i == 0) {
					continue;
				}
				Operator now = operatoren.get(i);
				
				if (now.prio > prioOp.prio) {
					prioOp = now;
				}
				else if (now.prio == prioOp.prio) {
					if (!String.valueOf(prioOp.inhalt).matches("[s]|[c]|[t]") && String.valueOf(now.inhalt).matches("[s]|[c]|[t]")) {
						prioOp = now;
					}
					else if (!String.valueOf(prioOp.inhalt).matches("[s]|[c]|[t]|[w]|[p]") && String.valueOf(now.inhalt).matches("[p]|[w]")) {
						prioOp = now;
					}
					else if (!String.valueOf(prioOp.inhalt).matches("[s]|[c]|[t]|[w]|[p]|[*]|[/]") && String.valueOf(now.inhalt).matches("[*]|[/]")) {
						prioOp = now;
					}
				}
			}
			
			if (Pattern.matches("[s]|[c]|[t]|[w]", String.valueOf(prioOp.inhalt))) {
				prioOp.oneNumber = true;
			}
			
		}
		else {
			prioOp = new Operator();
			prioOp.inhalt = '+';
			prioOp.stelle = 2;
		}
	}
	
	private void calcPart (Operator operator, double x) {
		double zahl1 = 0;
		double zahl2 = 0;
		double ergebnis = 0;
		
		Zahl z1 = null;
		Zahl z2 = null;
	
		for (int i = 0; i < zahlen.size(); i++) {
			Zahl now = zahlen.get(i);
			
			if (now.stelle - operator.stelle == -1) {
				z1 = now;
				zahl1 = z1.inhalt;
			}
			else if (now.stelle - operator.stelle == 1) {
				z2 = now;
				zahl2 = z2.inhalt;
			}
		}
		
		if (z1 != null) {
			zahlen.remove(z1);
		}
		if (z2 != null) {
			zahlen.remove(z2);	
		}
		
		if (z1 == null || z2 == null) {
			Variable v1 = null;
			Variable v2 = null;
			
			for (int i = 0; i < variablen.size(); i++) {
				Variable now = variablen.get(i);
				
				if (now.stelle - operator.stelle == -1) {
					v1 = now;
					if (now.negate) {
						zahl1 = -x;
					}
					else {
						zahl1 = x;
					}
					
				}
				else if (now.stelle - operator.stelle == 1) {
					v2 = now;
					if (now.negate) {
						zahl2 = -x;
					}
					else {
						zahl2 = x;
					}
				}
			}
			
			if (v1 != null) {
				variablen.remove(v1);
			}
			if (v2 != null) {
				variablen.remove(v2);
			}
			
		}
		
		switch (operator.inhalt) {
			case '+': ergebnis = zahl1 + zahl2; 			break;
			case '-': ergebnis = zahl1 - zahl2; 			break;
			case '*': ergebnis = zahl1 * zahl2; 			break;
			case '/': ergebnis = zahl1 / zahl2; 			break;
			case 'p': ergebnis = Math.pow(zahl1, zahl2);    break;
			case 'w': ergebnis = Math.sqrt(zahl2); 			break;
			case 's': ergebnis = Math.sin(zahl2); 			break;
			case 'c': ergebnis = Math.cos(zahl2);			break;
			case 't': ergebnis = Math.tan(zahl2);			break;
		}
		
		Zahl erg = new Zahl();
		erg.inhalt = ergebnis;

		if (operator.oneNumber) {
			erg.stelle = operator.stelle;
		}
		else {
			erg.stelle = operator.stelle - 1;
		}
		zahlen.add(erg);

		if (operator.oneNumber) {
			updateStellen(operator, 1);
		}
		else {
			updateStellen(operator, 2);
		}
			
		if (operatoren.size() == 0) {
			weiterRechnen = false;
			endErgebnis = ergebnis;

		}
		
	}
	
	private void updateStellen (Operator op, int abzug) {
		for (int i = 0; i < zahlen.size(); i++) {
			Zahl now = zahlen.get(i);

			if (now.stelle - op.stelle >= 2) {
				now.stelle -= abzug;
			}
		}
		
		for (int i = 0; i < operatoren.size(); i++) {
			Operator now = operatoren.get(i);
			
			if (now.stelle - op.stelle >= 2) {
				now.stelle -= abzug;
			}
		}
		
		for (int i = 0; i < variablen.size(); i++) {
			Variable now = variablen.get(i);
			
			if (now.stelle - op.stelle >= 2) {
				now.stelle -= abzug;
			}
		}
		
		operatoren.remove(op); 
	}
	
	private void showError () {
		System.out.println("Falsche Eingabe!");
		fehler = true;
	}
	
*/
}



