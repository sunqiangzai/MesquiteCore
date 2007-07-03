/*
 * EthOntos - a tool for comparative methods using ontologies
 * Copyright 2004-2005 Peter E. Midford
 * 
 * Created on Apr 24, 2007
 * Last updated on Apr 24, 2007
 * 
 */
package mesquite.diverse.DiversCategCharLikelihood;

import mesquite.diverse.DivCategCharMLCalculator.DivCategCharMLCalculator;
import mesquite.diverse.SpExtCategCharMLCalculator.SpExtCategCharMLCalculator;
import mesquite.lib.*;
import mesquite.lib.characters.CharacterDistribution;
import mesquite.lib.duties.NumberForCharAndTree;

public class DiversCategCharLikelihood extends NumberForCharAndTree {
    public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed
        EmployeeNeed e = registerEmployeeNeed(DivCategCharMLCalculator.class, getName() + "  needs a method to calculate likelihoods.",
        "The method to calculate likelihoods is arranged initially");
		e.setSuppressListing(true);
   }

    DivCategCharMLCalculator calcTask;


    MesquiteParameter r0;   //user specified diversification rate in state 0
    MesquiteParameter a0;   //user specified extinction/speciation ratio in state 0
    MesquiteParameter r1;   //user specified diversification rate in state 1
    MesquiteParameter a1;   //user specified extinction/speciation ratio in state 1
    MesquiteParameter t01;   //user specified transition rate from state 0 to state 1
    MesquiteParameter t10;   //user specifiedtransition rate from state 1 to state 0

    MesquiteParameter[] params;
    MesquiteParameter[] paramsCopy;
    boolean[] selected;

    /*.................................................................................................................*/
    public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {
        calcTask = (DivCategCharMLCalculator)hireEmployee(commandRec, DivCategCharMLCalculator.class, "Integrating Likelihood");
        if (calcTask == null)
            return sorry(commandRec, getName() + " couldn't start because no integrating likelihood calculator module obtained.");
        double def = MesquiteDouble.unassigned;
        //following is for the parameters explorer
        r0 = new MesquiteParameter("r0", "Rate of net diversification with state 0", def, 0, MesquiteDouble.infinite, 0.000, 1);
        r1 = new MesquiteParameter("r1", "Rate of net diversification with state 1", def, 0, MesquiteDouble.infinite, 0.000, 1);
        a0 = new MesquiteParameter("a0", "Extinction/Speciation ratio with state 0", def, 0, MesquiteDouble.infinite, 0.000, 1);
        a1 = new MesquiteParameter("a1", "Extinction/Speciation ratio with state 1", def, 0, MesquiteDouble.infinite, 0.000, 1);
        t01 = new MesquiteParameter("r01", "Rate of 0->1 changes", def, 0, MesquiteDouble.infinite, 0.000, 1);
        t10 = new MesquiteParameter("r10", "Rate of 1->0 changes", def, 0, MesquiteDouble.infinite, 0.000, 1);
        params = new MesquiteParameter[]{r0, r1, a0, a1, t01, t10};
        if (!MesquiteThread.isScripting()){
            showDialog();
        }
        addMenuItem("Set Parameters...", makeCommand("setParameters", this));
        return true;
    }
    /*.................................................................................................................*/
    boolean showDialog(){
        if (params == null)
            return false;
        ParametersDialog dlog = new ParametersDialog(containerOfModule(), "Parameters", params, null, 2, 2, false);
        dlog.completeAndShowDialog(true);
        boolean ok = (dlog.query()==0);
        if (ok) 
            dlog.acceptParameters();
        dlog.dispose();
        return ok;
    }
    /*.................................................................................................................*/
    public void initialize(Tree tree, CharacterDistribution charStates,CommandRecord commandRec) {
        // TODO Auto-generated method stub
    }
    
    /*.................................................................................................................*/
    public Snapshot getSnapshot(MesquiteFile file) {
        Snapshot temp = new Snapshot();

        temp.addLine("getIntegTask ", calcTask);
        String pLine = MesquiteParameter.paramsToScriptString(params);
        if (!StringUtil.blank(pLine))
            temp.addLine("setParameters " + pLine);
        return temp;
    }
    boolean setParam(MesquiteParameter p, MesquiteParameter[] params, Parser parser){
        double newValue = MesquiteDouble.fromString(parser);
        int loc = parser.getPosition();
        String token = parser.getNextToken();
        if (token != null && "=".equals(token)){
            int con = MesquiteInteger.fromString(parser);
            if (MesquiteInteger.isCombinable(con) && con>=0 && con < params.length)
                p.setConstrainedTo(params[con], false);
        }
        else
            parser.setPosition(loc);
        if ((MesquiteDouble.isUnassigned(newValue) ||  newValue >=0) && newValue != p.getValue()){
            p.setValue(newValue); //change mode
            return true;
        }
        return false;
    }

    /*.................................................................................................................*/
    /*  the main command handling method.  */
    public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {
        // Should be removed when debugged
        if (checker.compare(getClass(), "Sets rate parameters", "[double double double double double double]", commandName, "setParameters")) {
            if (StringUtil.blank(arguments)){
                if (!MesquiteThread.isScripting() && showDialog())
                    parametersChanged(null, commandRec);
            }
            else {
                parser.setString(arguments);
                boolean changed =  setParam(r0, params, parser);
                boolean more = setParam(r1, params, parser);
                changed = changed || more;
                more = setParam(a0, params, parser);
                changed = changed || more;
                more = setParam(a1, params, parser);
                changed = changed || more;
                more = setParam(t01, params, parser);
                changed = changed || more;
                more = setParam(t10, params, parser);
                changed = changed || more;
                if (changed && !MesquiteThread.isScripting())
                    parametersChanged(null, commandRec); //this tells employer module that things changed, and recalculation should be requested
            }
        }
        else if (checker.compare(getClass(), "Returns integrating module", null, commandName, "getIntegTask")) {
            return calcTask;
        }
        else
            return super.doCommand(commandName, arguments, commandRec, checker);
        return null;
    }


    
    public void calculateNumber(Tree tree, CharacterDistribution charStates, MesquiteNumber result, MesquiteString resultString, CommandRecord commandRec) {
        if (result == null)
            return;
       	clearResultAndLastResult(result);

        if (tree == null || charStates == null)
            return;
        paramsCopy = MesquiteParameter.cloneArray(params, paramsCopy);
        calcTask.calculateLogProbability(tree, charStates, paramsCopy, result, resultString, commandRec);
		saveLastResult(result);
		saveLastResultString(resultString);
   }

    /*------------------------------------------------------------------------------------------*/


    public String getName() {
        return "BiSSE Net Diversification Likelihood";
    }

    public String getAuthors() {
        return "Peter E. Midford & Wayne P. Maddison";
    }

    public String getVersion() {
        return "0.1";
    }

    public String getExplanation(){
        return "Calculates likelihoods using a diversification model (r,a) whose probabilities depend on the state of a single categorical character";
    }

    public boolean isPrerelease(){
        return true;
    }


}