package edu.rit.csci759.fuzzylogic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.Gpr;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;
import net.sourceforge.jFuzzyLogic.rule.RuleExpression;
import net.sourceforge.jFuzzyLogic.rule.RuleTerm;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodAndMin;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodOrMax;

public class MyTipperClass {
	String filename = "FuzzyLogic/SmartBlind.fcl";
	FIS fis = FIS.load(filename, true);
	static int counter = 4;

	public static void main(String args[]) {
		MyTipperClass m = new MyTipperClass();
		m.getResults();
		 m.addRule("cold","AND", "bright","open");
		 m.addRule("freezing", null, null,"close");
		// m.deleteRule("5");

	}
//retrieves the rules
	public String getResults() {

		if (fis == null) {
			System.err.println("Can't load file: '" + filename + "'");
			System.exit(1);
		}

		// Get default function block
		FunctionBlock fb = fis.getFunctionBlock("smartblind");

		// Print ruleSet
		String rules = new String();
		RuleBlock m = fb.getFuzzyRuleBlock("No1");
		for (int i = 0; i < m.getRules().size(); i++) {
			System.out.println("saaa" + m.getRules().get(i).toString());
			rules += m.getRules().get(i).toString();
			rules = rules + "---";
		}
		System.out.println("----->>>>" + rules);
		return rules;
	}


	//adds the rules

	public void addRule(String temperature, String condition, String ambient,
			String blind) {

		// Get default function block
		FunctionBlock fb = fis.getFunctionBlock("smartblind");

		RuleBlock ruleBlock = fb.getFuzzyRuleBlock("No1");

		Rule rule = new Rule((++counter) + "", ruleBlock);

		RuleTerm r1 = new RuleTerm(fb.getVariable("temp"), temperature, false);
		RuleTerm r2 = new RuleTerm(fb.getVariable("ambient"), ambient, false);
		if (condition.equals("OR")) {
			RuleExpression a = new RuleExpression(r1, r2,
					RuleConnectionMethodOrMax.get());
			rule.setAntecedents(a);
			rule.addConsequent(fb.getVariable("blind"), blind, false);
			ruleBlock.add(rule);
			Gpr.toFile(filename,fb);
		} else {
			RuleExpression a = new RuleExpression(r1, r2,
					RuleConnectionMethodAndMin.get());
			rule.setAntecedents(a);
			rule.addConsequent(fb.getVariable("blind"), blind, false);
			ruleBlock.add(rule);
			Gpr.toFile(filename,fb);
		}

		// // Print ruleSet
		System.out.println(fb);

	}

	private boolean substringMatch(String st1, String st2) {
		return st1.substring(12).equals(st2.substring(12));
	}

	//evaluates the output given inputs

	public String fuzzLogic(int temp, int ambient) {
		FunctionBlock fb = fis.getFunctionBlock("smartblind");
		fb.setVariable("temp", temp);
		fb.setVariable("ambient", ambient);
		fb.evaluate();
		fb.getVariable("blind").defuzzify();
		double result_open = fb.getVariable("blind").getMembership("open");
		double result_close= fb.getVariable("blind").getMembership("close");
		double result_half = fb.getVariable("blind").getMembership("half");
		
		if (result_close >=result_open && result_close>=result_half) {
			return "Close";
			
		} else if (result_half >= result_open && result_half >= result_close) {
			return "Halfopen";
		} else
			return "Open";

	}


	//deletes the rule
	public void deleteRule(String name) {
		String numbers[] = name.split("---");

		Rule temp = null;

		for (int i = 0; i < numbers.length; i++) {
			FunctionBlock fb = fis.getFunctionBlock("smartblind");

			RuleBlock ruleBlock = fb.getFuzzyRuleBlock("No1");
			Iterator<Rule> rules = ruleBlock.iterator();
			while (rules.hasNext()) {
				temp = rules.next();
				// System.out.println(temp);
				if (substringMatch(temp.toString(), numbers[i])) {
					System.out.println(temp.getName());
					rules.remove();
					Gpr.toFile(filename,fb);
				}
			}
		}

	}

}
