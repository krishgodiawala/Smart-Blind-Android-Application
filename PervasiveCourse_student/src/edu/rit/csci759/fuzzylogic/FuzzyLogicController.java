package edu.rit.csci759.fuzzylogic;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

public class FuzzyLogicController {
	public static void main(String[] args) throws Exception {
		String filename = "FuzzyLogic/FCBlind.fcl";
		FIS fis = FIS.load(filename, true);

		if (fis == null) {
			System.err.println("Can't load file: '" + filename + "'");
			System.exit(1);
		}

		// Get default function block
		FunctionBlock fb = fis.getFunctionBlock(null);

		// Set inputs
		fb.setVariable("temperature", 52);
		fb.setVariable("ambient", 61);

		// Evaluate
		fb.evaluate();

		// Show output variable's chart
		fb.getVariable("blind").defuzzify();

		// Print ruleSet
		System.out.println(fb);
		System.out.println("blind" + fb.getVariable("blind").getValue());

	}
}
