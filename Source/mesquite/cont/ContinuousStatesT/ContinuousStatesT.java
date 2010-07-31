/* Mesquite source code.  Copyright 1997-2010 W. Maddison and D. Maddison.Version 2.73, July 2010.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.cont.ContinuousStatesT;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.coalesce.lib.Coalescer;import mesquite.cont.lib.*;/* ======================================================================== */public class ContinuousStatesT extends NumberForTaxonIncr {	public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed		EmployeeNeed e2 = registerEmployeeNeed(CharSourceCoordObed.class, getName() + " uses a source of characters for the observed states in taxa.",		"You can select the source of character initially.");	}	MesquiteNumber num;	CharSourceCoordObed characterSourceTask;	Taxa currentTaxa;	int currentChar=0;	boolean characterSet = false;	int lastCharRetrieved = -1;	ContinuousDistribution observedStates =null;	MesquiteCommand cstC;		//choice of what item to show	int currentItem=0;	MesquiteMenuItemSpec itemItem;	MesquiteCommand itemChoiceCommand;	String itemName=null;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) { 		characterSourceTask = (CharSourceCoordObed)hireCompatibleEmployee(CharSourceCoordObed.class, ContinuousState.class, "Source of characters (for Continuous States of Taxon)"); 		if (characterSourceTask == null) { 			return sorry(getName() + " couldn't start because no source of characters found"); 		}		/**/		itemChoiceCommand = MesquiteModule.makeCommand("setItem",  this);		addMenuItem("Choose Character...", new MesquiteCommand("chooseCharacter", this));		itemItem = addMenuItem("Item of Continuous State...", itemChoiceCommand); 		return true;  	 }  	 	/*.................................................................................................................*/	/** Generated by an employee who quit.  The MesquiteModule should act accordingly. */ 	public void employeeQuit(MesquiteModule employee) { 		if (employee == characterSourceTask)  // character source quit and none rehired automatically 			iQuit();	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}	/*.................................................................................................................*/ 	public long toInternal(long i){ 		return(CharacterStates.toInternal((int)i)); 	}	/*.................................................................................................................*/ 	public long toExternal(long i){ 		return(CharacterStates.toExternal((int)i)); 	}	/*.................................................................................................................*/   	 public void setCurrent(long i){ 		if (characterSourceTask==null || currentTaxa==null){ 			currentChar = (int)i;			characterSet=true; 		} 		else if ((i>=0) && (i<=characterSourceTask.getNumberOfCharacters(currentTaxa)-1)) { 			currentChar = (int)i;			characterSet=true;			//parametersChanged();		}   	 } 	public String getItemTypeName(){ 		return "Character"; 	}	/*.................................................................................................................*/   	 public long getCurrent(){   	 	return currentChar;   	 }	/*.................................................................................................................*/ 	public long getMin(){ 		return 0; 	}	/*.................................................................................................................*/ 	public long getMax(){ 		if (characterSourceTask==null || currentTaxa==null) 			return 0; 		return characterSourceTask.getNumberOfCharacters(currentTaxa)-1; 	}	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();  	 	temp.addLine( "getCharacterSource " , characterSourceTask);  	 	temp.addLine("setCharacter " + CharacterStates.toExternal(currentChar));  	 	temp.addLine("setItem " + (currentItem));    	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandChecker checker) {    	 	 if (checker.compare(this.getClass(), "Sets module supplying characters", "[name of module]", commandName, "setCharacterSource")) {//temporary, for data files using old system without coordinators 			return characterSourceTask.doCommand(commandName, arguments, checker);    	 	}    	 	else if (checker.compare(this.getClass(), "Returns module supplying characters", null, commandName, "getCharacterSource")) { 			return characterSourceTask;    	 	}    	 	else if (checker.compare(this.getClass(), "Goes to the next character", null, commandName, "nextCharacter")) {    	 		if (currentChar>=characterSourceTask.getNumberOfCharacters(currentTaxa)-1)    	 			currentChar=0;    	 		else    	 			currentChar++;				parametersChanged();    	 	}    	 	else if (checker.compare(this.getClass(), "Goes to the previous character", null, commandName, "previousCharacter")) {    	 		if (currentChar<=0)    	 			currentChar=characterSourceTask.getNumberOfCharacters(currentTaxa)-1;    	 		else    	 			currentChar--;				parametersChanged();    	 	}    	 	else if (checker.compare(this.getClass(), "Sets the item to use (in a multi-item continuous data matrix)", "[item number]", commandName, "setItem")) {    	 		int ic = MesquiteInteger.fromString(arguments, new MesquiteInteger(0));    	 		if (!MesquiteInteger.isCombinable(ic) && observedStates!=null){				ic = observedStates.userQueryItem("Select item to use for Continuous State of Taxon", this);    	 		}   			if (!MesquiteInteger.isCombinable(ic))   				return null;   			if (currentTaxa==null) {    	 			currentItem = ic;   			} 			else if (observedStates !=null && observedStates instanceof ContinuousDistribution) {	   	 		if ((ic>=0) && (ic<=observedStates.getNumItems()-1)) {	    	 			currentItem = ic;					parametersChanged();	 			} 			}    	 	}    	 	else if (checker.compare(this.getClass(), "Queries user to choose which character to use" , null, commandName, "chooseCharacter")) {    	 		int ic=characterSourceTask.queryUserChoose(currentTaxa, " for " + whatIsMyPurpose());    	 		if (MesquiteInteger.isCombinable(ic)) {	   			currentChar = ic;	 			characterSet=true;	 			parametersChanged(); //? 			}    	 	}    	 	else if (checker.compare(this.getClass(), "Sets the character to use", "[number of character]", commandName, "setCharacter")) {    	 		int ic = CharacterStates.toInternal(MesquiteInteger.fromString(arguments, new MesquiteInteger(0)));   			if (currentTaxa==null) {    	 			currentChar = ic;	 			characterSet=true;   			}    	 		if ((ic>=0) && (ic<=characterSourceTask.getNumberOfCharacters(currentTaxa)-1)) {    	 			currentChar = ic;	 			characterSet=true;				parametersChanged(); 			}    	 	}    	 	else    	 		return  super.doCommand(commandName, arguments, checker);		return null;   	 }   	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/   	public void initialize(Taxa taxa){   		currentTaxa = taxa;   		characterSourceTask.initialize(currentTaxa);   		int nums = characterSourceTask.getNumberOfCharacters(currentTaxa);		if (!MesquiteThread.isScripting() && nums>1 && MesquiteInteger.isCombinable(nums) && (!characterSet || currentChar>=nums)) {    	 		int ic=characterSourceTask.queryUserChoose(taxa, " for " + whatIsMyPurpose());    	 		if (MesquiteInteger.isCombinable(ic) && (ic>=0) && (ic<=nums-1)) {	   			currentChar = ic;	 			characterSet=true; 			}    	 	}   	}	public void calculateNumber(Taxon taxon, MesquiteNumber result, MesquiteString resultString){		if (result==null)			return;	   	clearResultAndLastResult(result);		Taxa taxa = taxon.getTaxa();		int it = taxa.whichTaxonNumber(taxon);		if (taxa != currentTaxa || currentChar != lastCharRetrieved || observedStates == null ) {			int maxnum = characterSourceTask.getNumberOfCharacters(taxa);			if (currentChar>= maxnum)				currentChar = maxnum-1;			observedStates = (ContinuousDistribution)characterSourceTask.getCharacter(taxa, currentChar);			currentTaxa = taxa;			lastCharRetrieved = currentChar;		}		if (observedStates==null)			return;		int numItems = observedStates.getNumItems();		if (numItems>1){			if (!itemItem.isEnabled()){				itemItem.setEnabled(true);				MesquiteTrunk.mesquiteTrunk.resetMenuItemEnabling();			}			if (currentItem>= numItems)				currentItem=0;			itemName = observedStates.getItemName(currentItem);					}		else {			currentItem = 0;			if (itemItem.isEnabled()){				itemItem.setEnabled(false);				MesquiteTrunk.mesquiteTrunk.resetMenuItemEnabling();			}		} 							double state = observedStates.getState(it, currentItem); 		num = new MesquiteNumber(state);		if (result!=null)			result.setValue(state);		if (resultString!=null)			resultString.setValue("State of character "+ currentChar + ": " + result.toString() + itemString());		saveLastResult(result);		saveLastResultString(resultString);	}	/*.................................................................................................................*/	private String itemString(){ 		if (itemName!=null)			return " (item " + itemName + ")"; 		else			return "";  	} 	/** returns current parameters, for logging etc..*/ 	public String getParameters() {   		if (currentTaxa != null){    			String name = characterSourceTask.getCharacterName(currentTaxa, currentChar);    			if (name != null)    				return "Character states of: " + name;    		} 		return "Character states from: " + characterSourceTask.getNameAndParameters() + itemString();     	 }	/*.................................................................................................................*/	public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification) {			observedStates = null;			parametersChanged(notification);	}	/*.................................................................................................................*/    	 public String getName() {		return "Continuous state of taxon";     	 }	/*.................................................................................................................*/    	 public String getNameAndParameters() {    		if (currentTaxa != null){    			String name = characterSourceTask.getCharacterName(currentTaxa, currentChar);    			if (name != null)    				return "Character states of: " + name;    		} 		return "Character states from: " + characterSourceTask.getNameAndParameters() + itemString();     	 }   	 	/*.................................................................................................................*/	/*.................................................................................................................*/	public boolean isPrerelease(){		return false;	}	/*.................................................................................................................*/	public boolean showCitation(){		return true;	}   	  	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "State of a continuous character." ;   	 }   	 }