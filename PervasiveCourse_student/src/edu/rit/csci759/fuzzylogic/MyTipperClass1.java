package edu.rit.csci759.fuzzylogic;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;
import net.sourceforge.jFuzzyLogic.rule.RuleExpression;
import net.sourceforge.jFuzzyLogic.rule.RuleTerm;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodOrMax;

public class MyTipperClass1 {
	
	public static void main(String[] args) throws Exception {
		String filename = "FuzzyLogic/SmartBlind.fcl";

		FIS fis = FIS.load(filename, true);

		if (fis == null) {
			System.err.println("Can't load file: '" + filename + "'");
			System.exit(1);
		}

		// Get default function block
		FunctionBlock fb = fis.getFunctionBlock(null);	
	
		RuleBlock ruleBlock=fb.getFuzzyRuleBlock("No1");
		Rule rule=new Rule("Rulefirst",ruleBlock);
		
		RuleTerm r1= new RuleTerm(fb.getVariable("temp"),"hot",false);
		RuleTerm r2=new RuleTerm(fb.getVariable("ambient"),"bright",false);
		RuleExpression a=new RuleExpression(r1,r2,RuleConnectionMethodOrMax.get());
		rule.setAntecedents(a);
		rule.addConsequent(fb.getVariable("blind"),"open", false);
		ruleBlock.add(rule);	
		
		//JFuzzyChart.get().chart(fis);

		// Set inputs
		fb.setVariable("temp", 85);
		fb.setVariable("ambient", 99);

		// Evaluate
		fb.evaluate();
		RuleTerm r11= new RuleTerm(fb.getVariable("temp"),"hot",false);
		RuleTerm r21=new RuleTerm(fb.getVariable("ambient"),"bright",false);
		RuleExpression a1=new RuleExpression(r1,r2,RuleConnectionMethodOrMax.get());
		rule.setAntecedents(a1);
		rule.addConsequent(fb.getVariable("blind"),"open", false);
		ruleBlock.add(rule);	
		

		// Show output variable's chart
		fb.getVariable("blind").defuzzify();
		

		// Print ruleSet
		System.out.println(fb);
		
		
		
		System.out.println("Blind: " + fb.getVariable("blind").getValue());
		
		
		
	}


}
