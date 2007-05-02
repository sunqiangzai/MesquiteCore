/* Mesquite source code, Treefarm package.  Copyright 1997-2006 W. Maddison, D. Maddison and P. Midford. Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.treefarm.FilterTreesOtherSrc;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;import mesquite.treefarm.lib.*;/* ======================================================================== */public class FilterTreesOtherSrc extends SourceFromTreeSource {	public String getName() {		return "Filter Trees from Other Source";	}	public String getNameForMenuItem() {		return "Filter Trees from Other Source...";	}	public String getExplanation() {		return "Filters trees from another source.";	}	public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed		super.getEmployeeNeeds();		EmployeeNeed e = registerEmployeeNeed(BooleanForTree.class, getName() + "  needs a criterion by which to filter trees.",		"The criterion to filter trees can be chosen initially or in the Filter of Trees submenu");	}	int currentTree=0;	int lastGoodTree = -1;	int lastTreeRequested = -1;	int maxAvailable = -2;	BooleanForTree booleanTask;	MesquiteString booleanName;	MesquiteCommand stC;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		if (!super.startJob(arguments, condition, commandRec, hiredByName))			return false;		booleanTask = (BooleanForTree)hireEmployee(commandRec, BooleanForTree.class, "Filter of trees");		if (booleanTask == null) {			return sorry(commandRec, getName() + " couldn't start because no tree filter was obtained.");		}		stC = makeCommand("setBoolean",  this);		booleanTask.setHiringCommand(stC);		booleanName = new MesquiteString();		booleanName.setValue(booleanTask.getName());		if (numModulesAvailable(BooleanForTree.class)>1){			MesquiteSubmenuSpec mss = addSubmenu(null, "Filter of Trees", stC, BooleanForTree.class);			mss.setSelected(booleanName);		}		return true;	}	/*.................................................................................................................*/	public void setPreferredTaxa(Taxa taxa){	}	/*.................................................................................................................*/	public boolean isPrerelease(){		return false;	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return true;  	}	/*.................................................................................................................*/	public Snapshot getSnapshot(MesquiteFile file) { 		Snapshot temp = super.getSnapshot(file);		temp.addLine("setBoolean ", booleanTask); 		return temp;	}	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {		if (checker.compare(this.getClass(), "Sets the tree filter", "[name of module]", commandName, "setBoolean")) {			BooleanForTree temp = (BooleanForTree)replaceEmployee(commandRec, BooleanForTree.class, arguments, "Filter of trees", booleanTask);			if (temp !=null){				booleanTask = temp;				booleanName.setValue(booleanTask.getName());				booleanTask.setHiringCommand(stC);				lastGoodTree = -1;				lastTreeRequested = -1;				maxAvailable = -2;				parametersChanged(null, commandRec);				return booleanTask;			}		}		else			return super.doCommand(commandName, arguments, commandRec, checker);		return null;	}	public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification, CommandRecord commandRec) {		lastGoodTree = -1;		lastTreeRequested = -1;		maxAvailable = -2;		super.employeeParametersChanged(this, source, notification, commandRec);	}	/*.................................................................................................................*/	public Tree getTree(Taxa taxa, int ic, CommandRecord commandRec) {  		int iTry = 0;		int count = -1;		if (ic>maxAvailable && maxAvailable>-2)			return null;		if (lastTreeRequested == ic)  //recently requested same; return it without checking filter (since change in filter would have reset lastTreeRequested etc.)			return getBasisTree(taxa, lastGoodTree, commandRec);		else if (lastTreeRequested >= 0 && lastTreeRequested == ic-1) {			//go from last requested			iTry = lastGoodTree+1;			count = lastTreeRequested;		}		Tree tree = null;		MesquiteBoolean result = new MesquiteBoolean(false);		TreeSource ts = getBasisTreeSource();		int numTrees = ts.getNumberOfTrees(taxa, commandRec); 		while (count<ic) {			tree = getBasisTree(taxa, iTry, commandRec);			if (tree == null)  {				maxAvailable = count;				return null;			}			booleanTask.calculateBoolean(tree, result, null, commandRec);				if (result.getValue()){				count++;			}					else				tree = null;			iTry++;		}		currentTree = ic;		lastGoodTree = iTry-1;		lastTreeRequested = ic;		return tree.cloneTree();	}	/*.................................................................................................................*/	public int getNumberOfTrees(Taxa taxa, CommandRecord commandRec) {		if (maxAvailable<-1) {			if (getBasisTreeSource().getNumberOfTrees(taxa, commandRec) == MesquiteInteger.infinite)				return MesquiteInteger.infinite;			return MesquiteInteger.finite; //don't know how many will be filtered		}		else			return maxAvailable+1;	}	/*.................................................................................................................*/	public String getTreeNameString(Taxa taxa, int itree, CommandRecord commandRec) {		return "Tree #" + (itree +1) + " filtered";	}	/*.................................................................................................................*/	public String getParameters() {		if (getBasisTreeSource() == null || booleanTask == null)			return null;		return"Filtering trees from: " + getBasisTreeSource().getNameAndParameters() + " using the filter " + booleanTask.getNameAndParameters();	}	/*.................................................................................................................*/}