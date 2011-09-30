/* Mesquite source code.  Copyright 1997-2011 W. Maddison and D. Maddison.Version 2.75, September 2011.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.trees.DefiniteTreeSource;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;public class DefiniteTreeSource extends TreeSourceDefinite implements NameHolder {	public String getName() {		return "Definite Tree Source";	}	public String getNameForMenuItem() {		return "Source of trees...";	}	public String getExplanation() {		return "Supplies trees from from a tree source, arranged to be a definite number";	}	public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed		EmployeeNeed e = registerEmployeeNeed(TreeSource.class, getName() + " presents a source of trees as if it had a definite number of trees.",		"The source of trees can be chosen initially, or using the Tree Source submenu");	}	/*.................................................................................................................*/	TreeSource treeSource;	MesquiteString treeSourceName;	MesquiteCommand cstC;	Taxa taxa = null;	int numTreesAssigned = MesquiteInteger.unassigned;	int defaultNumberOfItems = 100;	boolean wasDefinite = true;	boolean assigned = false;	private MesquiteMenuItemSpec numTreesItem;	MesquiteSubmenuSpec mss;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		String exp, mexp;		if (getExplanationByWhichHired()!=null) {			exp = getExplanationByWhichHired();			mexp = exp;		}		else {			exp = "Source of trees  (for " + getEmployer().getName() + ")";			mexp = "Tree Source  (for " + getEmployer().getName() + ")";		}		if (arguments != null)			treeSource = (TreeSource)hireNamedEmployee(TreeSource.class, arguments);		if (treeSource == null)			treeSource = (TreeSource)hireEmployee(TreeSource.class, exp);		if (treeSource == null)			return sorry(getName() + " couldn't start because no source of trees was obtained.");		treeSourceName = new MesquiteString(treeSource.getName());		cstC = makeCommand("setTreeSource",  this);		treeSource.setHiringCommand(cstC);		if (numModulesAvailable(TreeSource.class)>1){			mss = addSubmenu(null, mexp, cstC, TreeSource.class);			mss.setNameHolder(this);			mss.setSelected(treeSourceName);		}		return true;	}	public String getMyName(Object obj){		if (obj == mss)			return whatIsMyPurpose();		return null;	}	/** Returns the purpose for which the employee was hired (e.g., "to reconstruct ancestral states" or "for X axis").*/	public String purposeOfEmployee(MesquiteModule employee){		return whatIsMyPurpose(); //transfers info to employer, as ithis is coordinator	}	/*.................................................................................................................*/	/** Generated by an employee who quit.  The MesquiteModule should act accordingly. */	public void employeeQuit(MesquiteModule employee) {		if (employee == treeSource)  // character source quit and none rehired automatically			iQuit();	}	/*.................................................................................................................*/	public Snapshot getSnapshot(MesquiteFile file) {		Snapshot temp = new Snapshot();		temp.addLine("setTreeSource", treeSource);		if (MesquiteInteger.isCombinable(numTreesAssigned))			temp.addLine("assignNumTrees " + numTreesAssigned);		return temp;	}	/*.................................................................................................................*/	public void resetTreeSource(Taxa taxa, boolean queryPlease){		int numItems=treeSource.getNumberOfTrees(taxa, true);		if (!MesquiteInteger.isCombinable(numItems)) { //not specified; need to set			if (queryPlease || (wasDefinite && !assigned)) {				numTreesAssigned = defaultNumberOfItems;				if (!MesquiteThread.isScripting()) {					numTreesAssigned = MesquiteInteger.queryInteger(containerOfModule(), "Number of Trees", "Number of trees  (for " + getEmployer().getName() + ")", numTreesAssigned);					if (!MesquiteInteger.isCombinable(numTreesAssigned)) 						numTreesAssigned = defaultNumberOfItems;				}			}			wasDefinite = false;			assigned = true;			if (numTreesItem == null) {				numTreesItem = addMenuItem( "Number of  Trees...", makeCommand("assignNumTrees",  this));				resetContainingMenuBar();			}		}		else  {			if (numTreesItem!= null) {				deleteMenuItem(numTreesItem);				resetContainingMenuBar();				numTreesItem = null;			}			wasDefinite = true;			assigned = false;			numTreesAssigned = numItems;		}	}	/*.................................................................................................................*/	MesquiteInteger pos = new MesquiteInteger(0);	public Object doCommand(String commandName, String arguments, CommandChecker checker) {		if (checker.compare(this.getClass(), "Sets module supplying trees", "[name of module]", commandName, "setTreeSource")) {			TreeSource newtreeSource;			newtreeSource =  (TreeSource)replaceEmployee(TreeSource.class, arguments, "Source of trees", treeSource);			if (newtreeSource!=null) {				treeSource = newtreeSource;				treeSource.setHiringCommand(cstC);				treeSourceName.setValue(treeSource.getName());				parametersChanged(); 				return treeSource;			}			else {				discreetAlert( "Unable to activate tree source \"" + arguments + "\"  for use by " + employer.getName());			}		}		else if (checker.compare(this.getClass(), "Sets the number of trees", "[number of trees]", commandName, "assignNumTrees")) {			int newNum = MesquiteInteger.fromFirstToken(arguments, pos);			if (MesquiteInteger.isCombinable(newNum) && newNum>0 ) {				numTreesAssigned = newNum;				assigned = true;			}			else {				assigned = false;				resetTreeSource(taxa, true);			}			parametersChanged();		}		else			return  super.doCommand(commandName, arguments, checker);		return null;	}	/*.................................................................................................................*/	public boolean isSubstantive(){		return true;	}	/*.................................................................................................................*/	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/	public int getVersionOfFirstRelease(){		return 110;  	}	/*.................................................................................................................*/	public boolean isPrerelease(){		return false;	}	/*.................................................................................................................*/	public void setPreferredTaxa(Taxa taxa) {		this.taxa = taxa;		treeSource.setPreferredTaxa(taxa);	}	/*.................................................................................................................*/	public void initialize(Taxa taxa) {		setPreferredTaxa(taxa);		treeSource.initialize(taxa);	}	/*.................................................................................................................*/	public Tree getTree(Taxa taxa, int itree) {		setPreferredTaxa(taxa);		return treeSource.getTree(taxa, itree);	}	/*.................................................................................................................*/	public int getNumberOfTrees(Taxa taxa) {		setPreferredTaxa(taxa);		resetTreeSource(taxa, false);		return numTreesAssigned;	}	/*.................................................................................................................*/	public String getTreeNameString(Taxa taxa, int itree) {		return treeSource.getTreeNameString(taxa, itree);	}	public Selectionable getSelectionable() {		if (treeSource!=null)			return treeSource.getSelectionable();		else			return null;	}	public void setEnableWeights(boolean enable){		if (treeSource!=null)			treeSource.setEnableWeights(enable);	}	public boolean itemsHaveWeights(Taxa taxa){		if (treeSource!=null)			return treeSource.itemsHaveWeights(taxa);		return false;	}	public double getItemWeight(Taxa taxa, int ic){		if (treeSource!=null)			return treeSource.getItemWeight(taxa, ic);		return MesquiteDouble.unassigned;	}	public void prepareItemColors(Taxa taxa){		if (treeSource!=null)			treeSource.prepareItemColors(taxa);	}	public Color getItemColor(Taxa taxa, int ic){		if (treeSource!=null)			return treeSource.getItemColor(taxa, ic);		return null;	}	/*.................................................................................................................*/	public String getParameters() {		if (treeSource==null)			return null;		return treeSource.getName() + " (" + treeSource.getParameters() + ")";	}	public String getNameAndParameters() {		return treeSource.getNameAndParameters();	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return true;  	}	/*.................................................................................................................*/	public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification) {		parametersChanged(notification);	}	/*.................................................................................................................*/}