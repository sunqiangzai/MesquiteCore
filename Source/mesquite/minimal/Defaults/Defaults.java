/* Mesquite source code.  Copyright 1997-2007 W. Maddison and D. Maddison.Version 2.0, September 2007.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.minimal.Defaults;/*~~  */import java.awt.*;import java.net.*;import java.util.*;import java.io.*;import mesquite.lib.*;import mesquite.lib.duties.*;/** Controls some of Mesquite's default settings (like fonts in windows). */public class Defaults extends MesquiteInit  {	public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed		EmployeeNeed e2 = registerEmployeeNeed(DefaultsAssistant.class, "Modules are used to assist with setting defaults.",		"The defaults are presented in the Defaults menu of the log window or projects window");	}	MesquiteBoolean useOtherChoices, console, askSeed, suppressXORMode,  taxonTruncTrees, tabbedWindows, debugMode, wizards, logAll, phoneHome, secondaryChoicesOnInDialogs; //, useDotPrefs	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		useOtherChoices = new MesquiteBoolean(true);		askSeed = new MesquiteBoolean(false);		console = new MesquiteBoolean(MesquiteTrunk.mesquiteTrunk.logWindow.isConsoleMode());		logAll = new MesquiteBoolean(MesquiteCommand.logEverything);		suppressXORMode = new MesquiteBoolean(false);		wizards = new MesquiteBoolean(MesquiteDialog.useWizards);		tabbedWindows = new MesquiteBoolean(MesquiteWindow.compactWindows);		taxonTruncTrees = new MesquiteBoolean(true);		debugMode = new MesquiteBoolean(false);		phoneHome = new MesquiteBoolean(MesquiteTrunk.phoneHome);		secondaryChoicesOnInDialogs = new MesquiteBoolean(false);		//useDotPrefs = new MesquiteBoolean(MesquiteModule.prefsDirectory.toString().indexOf(".Mesquite_Prefs")>=0);		loadPreferences();		EmployerEmployee.useOtherChoices = useOtherChoices.getValue();		EmployerEmployee.secondaryChoicesOnInDialogs = secondaryChoicesOnInDialogs.getValue();		RandomBetween.askSeed = askSeed.getValue();		//makeMenu("DefaultsMENU");		MesquiteTrunk.defaultsSubmenu = MesquiteTrunk.mesquiteTrunk.addSubmenu(MesquiteTrunk.fileMenu, "Defaults");		MesquiteTrunk.mesquiteTrunk.addCheckMenuItemToSubmenu(MesquiteTrunk.fileMenu, MesquiteTrunk.defaultsSubmenu,"Open Windows as Tabs", makeCommand("toggleTabbedWindows",  this), tabbedWindows);		MesquiteTrunk.mesquiteTrunk.addCheckMenuItemToSubmenu(MesquiteTrunk.fileMenu, MesquiteTrunk.defaultsSubmenu,"Use Wizard-style Dialogs", makeCommand("toggleWizards",  this), wizards);		MesquiteTrunk.mesquiteTrunk.addCheckMenuItemToSubmenu(MesquiteTrunk.fileMenu, MesquiteTrunk.defaultsSubmenu,"Check for Notices on Mesquite Web Site", makeCommand("togglePhoneHome",  this), phoneHome);		MesquiteTrunk.mesquiteTrunk.addCheckMenuItemToSubmenu(MesquiteTrunk.fileMenu, MesquiteTrunk.defaultsSubmenu,"Use Log Window for Commands", makeCommand("toggleConsoleMode",  this), console);		MesquiteTrunk.mesquiteTrunk.addCheckMenuItemToSubmenu(MesquiteTrunk.fileMenu, MesquiteTrunk.defaultsSubmenu,"Log All Commands", makeCommand("toggleLogAll",  this), logAll);		MesquiteTrunk.mesquiteTrunk.addCheckMenuItemToSubmenu(MesquiteTrunk.fileMenu, MesquiteTrunk.defaultsSubmenu,"Use \"Other Choices\" for Secondary Choices", makeCommand("toggleOtherChoices",  this), useOtherChoices);		MesquiteTrunk.mesquiteTrunk.addCheckMenuItemToSubmenu(MesquiteTrunk.fileMenu, MesquiteTrunk.defaultsSubmenu,"Secondary Choices on By Default in Dialog Boxes", makeCommand("toggleSecondaryChoicesOnInDialogs",  this), secondaryChoicesOnInDialogs);		MesquiteTrunk.mesquiteTrunk.addItemToSubmenu(MesquiteTrunk.fileMenu, MesquiteTrunk.defaultsSubmenu, "Previous Logs Saved...", makeCommand("setNumPrevLog",  this));		if (!MesquiteTrunk.isMacOSX())			MesquiteTrunk.mesquiteTrunk.addItemToSubmenu(MesquiteTrunk.fileMenu, MesquiteTrunk.defaultsSubmenu, "Forget Default Web Browser", makeCommand("forgetBrowser",  this));		MesquiteTrunk.mesquiteTrunk.addSubmenu(MesquiteTrunk.defaultsSubmenu, "Default Font", makeCommand("setDefaultFont",  this), MesquiteSubmenu.getFontList());		MesquiteTrunk.mesquiteTrunk.addSubmenu(MesquiteTrunk.defaultsSubmenu,"Default Font Size", makeCommand("setDefaultFontSize",  this), MesquiteSubmenu.getFontSizeList());				if (MesquiteTrunk.isMacOSX()  && System.getProperty("os.version").indexOf("10.4")>=0)			MesquiteTrunk.mesquiteTrunk.addCheckMenuItemToSubmenu(MesquiteTrunk.fileMenu, MesquiteTrunk.defaultsSubmenu,"Suppress Inverted Highlights", makeCommand("toggleXORMode",  this), suppressXORMode);		MesquiteTrunk.mesquiteTrunk.addCheckMenuItemToSubmenu(MesquiteTrunk.fileMenu, MesquiteTrunk.defaultsSubmenu, "Ask for Random Number Seeds", makeCommand("toggleAskSeed",  this), askSeed);		MesquiteTrunk.mesquiteTrunk.addCheckMenuItemToSubmenu(MesquiteTrunk.fileMenu, MesquiteTrunk.defaultsSubmenu, "Permit Partial Names in Tree Reading", makeCommand("togglePartNamesTrees",  this), taxonTruncTrees);			//comment out debug mode menu item for users		MesquiteTrunk.mesquiteTrunk.addCheckMenuItemToSubmenu(MesquiteTrunk.fileMenu, MesquiteTrunk.defaultsSubmenu,"Debug Mode", makeCommand("toggleDebugMode",  this), debugMode);		MesquiteTrunk.mesquiteTrunk.addMenuItem(MesquiteTrunk.fileMenu, "-", null);				hireAllEmployees(DefaultsAssistant.class);		 		return true; 	} 	 	public void endJob(){ 		storePreferences(); 		super.endJob(); 	}		/*.................................................................................................................*/	 public String getExplanation() {	return "Supervises some Mesquite-wide defaults";	 }	/*.................................................................................................................*/	 public void processPreferencesFromFile (String[] prefs) {		 if (prefs!=null && prefs.length>1) {			 int fontSize = MesquiteInteger.fromString(prefs[1]);			 if (MesquiteInteger.isCombinable(fontSize)) {				 Font fontToSet = new Font (prefs[0], MesquiteWindow.defaultFont.getStyle(), fontSize);				 if (fontToSet!= null) {					 MesquiteWindow.defaultFont = fontToSet;				 }			 }			 if (prefs.length>2 && prefs[2] !=null)				 useOtherChoices.setValue("useOther".equalsIgnoreCase(prefs[2]));			 if (prefs.length>3 && prefs[3] !=null)				 MesquiteTrunk.suggestedDirectory = prefs[3];			 if (prefs.length>4 && prefs[4] !=null)				 askSeed.setValue("askSeed".equalsIgnoreCase(prefs[4]));			 if (prefs.length>5 && prefs[5] !=null){				 suppressXORMode.setValue("suppressXORMode".equalsIgnoreCase(prefs[5]));				 GraphicsUtil.useXOR = !suppressXORMode.getValue();			 }			 if (prefs.length>6 && prefs[6] !=null){				 taxonTruncTrees.setValue("permitTruncTaxonNamesTrees".equalsIgnoreCase(prefs[6]));				 MesquiteTree.permitTruncTaxNames = taxonTruncTrees.getValue();			 }			 if (prefs.length>7 && prefs[7] !=null){				 tabbedWindows.setValue("tabbed".equalsIgnoreCase(prefs[7]));				 MesquiteWindow.compactWindows = tabbedWindows.getValue();			 }			 if (prefs.length>8 && prefs[8] !=null){				 debugMode.setValue("debug".equalsIgnoreCase(prefs[8]));				 MesquiteTrunk.debugMode = debugMode.getValue();			 }			 if (prefs.length>9 && prefs[9] !=null){				 wizards.setValue("useWizards".equalsIgnoreCase(prefs[9]));				 MesquiteDialog.useWizards = wizards.getValue();			 }			 if (prefs.length>10 && prefs[10] !=null){				 phoneHome.setValue("phoneHome".equalsIgnoreCase(prefs[10]));				 MesquiteTrunk.phoneHome = phoneHome.getValue();			 }			 if (prefs.length>11 && prefs[11] !=null)				 secondaryChoicesOnInDialogs.setValue("secondaryChoicesOnInDialogs".equalsIgnoreCase(prefs[11]));		 }	 }				public void processSingleXMLPreference (String tag, String content) {			if ("useOtherChoices".equalsIgnoreCase(tag)){				useOtherChoices.setValue(content);				EmployerEmployee.useOtherChoices = useOtherChoices.getValue();			}			else if ("askSeed".equalsIgnoreCase(tag)){				askSeed.setValue(content);				RandomBetween.askSeed = askSeed.getValue();			}			else if ("suppressXORMode".equalsIgnoreCase(tag)){				suppressXORMode.setValue(content);				GraphicsUtil.useXOR = !suppressXORMode.getValue();			}			else if ("taxonTruncTrees".equalsIgnoreCase(tag)){				taxonTruncTrees.setValue(content);				 MesquiteTree.permitTruncTaxNames = taxonTruncTrees.getValue();		}			else if ("tabbedWindows".equalsIgnoreCase(tag)){				tabbedWindows.setValue(content);				 MesquiteWindow.compactWindows = tabbedWindows.getValue();			}			else if ("debugMode".equalsIgnoreCase(tag)){				debugMode.setValue(content);				 MesquiteTrunk.debugMode = debugMode.getValue();								}			else if ("wizards".equalsIgnoreCase(tag)){				wizards.setValue(content);				 MesquiteDialog.useWizards = wizards.getValue();			}			else if ("phoneHome".equalsIgnoreCase(tag)){				phoneHome.setValue(content);				 MesquiteTrunk.phoneHome = phoneHome.getValue();			}			else if ("secondaryChoicesOnInDialogs".equalsIgnoreCase(tag)){				secondaryChoicesOnInDialogs.setValue(content);				EmployerEmployee.secondaryChoicesOnInDialogs = secondaryChoicesOnInDialogs.getValue();			}			else if ("defaultFont".equalsIgnoreCase(tag)){				String defFont = StringUtil.cleanXMLEscapeCharacters(content);				 Font fontToSet = new Font (defFont, MesquiteWindow.defaultFont.getStyle(), MesquiteWindow.defaultFont.getSize());				 if (fontToSet!= null) 					 MesquiteWindow.defaultFont = fontToSet;			}			else if ("defaultFontSize".equalsIgnoreCase(tag)) {				int defFontSize = MesquiteInteger.fromString(content);				 Font fontToSet = new Font (MesquiteWindow.defaultFont.getName(), MesquiteWindow.defaultFont.getStyle(), defFontSize);				 if (fontToSet!= null) 					 MesquiteWindow.defaultFont = fontToSet;			}			else if ("suggestedDirectory".equalsIgnoreCase(tag))				MesquiteTrunk.suggestedDirectory = StringUtil.cleanXMLEscapeCharacters(content);		}		public String preparePreferencesForXML () {			StringBuffer buffer = new StringBuffer();			StringUtil.appendXMLTag(buffer, 2, "defaultFont", MesquiteWindow.defaultFont.getName());  			StringUtil.appendXMLTag(buffer, 2, "defaultFontSize", MesquiteWindow.defaultFont.getSize());			StringUtil.appendXMLTag(buffer, 2, "useOtherChoices", useOtherChoices);   			StringUtil.appendXMLTag(buffer, 2, "suggestedDirectory", MesquiteTrunk.suggestedDirectory);			StringUtil.appendXMLTag(buffer, 2, "askSeed", askSeed);   			StringUtil.appendXMLTag(buffer, 2, "suppressXORMode", suppressXORMode);   			StringUtil.appendXMLTag(buffer, 2, "taxonTruncTrees", taxonTruncTrees);   			StringUtil.appendXMLTag(buffer, 2, "tabbedWindows", tabbedWindows);   			StringUtil.appendXMLTag(buffer, 2, "debugMode", debugMode);   			StringUtil.appendXMLTag(buffer, 2, "wizards", wizards);   			StringUtil.appendXMLTag(buffer, 2, "phoneHome", phoneHome);   			StringUtil.appendXMLTag(buffer, 2, "secondaryChoicesOnInDialogs", secondaryChoicesOnInDialogs);   			return buffer.toString();		}	 /*.................................................................................................................*	public String[] preparePreferencesForFile () {		String[] prefs= new String[12];		prefs[0] = MesquiteWindow.defaultFont.getName();		prefs[1] = Integer.toString(MesquiteWindow.defaultFont.getSize());		if (useOtherChoices.getValue())			prefs[2] = "useOther";		else			prefs[2] = "dontUseOther";		prefs[3] = MesquiteTrunk.suggestedDirectory;		if (askSeed.getValue())			prefs[4] = "askSeed";		else			prefs[4] = "dontAskSeed";		if (xorMode.getValue())			prefs[5] = "suppressXORMode";		else			prefs[5] = "useXORMode";		if (taxonTruncTrees.getValue())			prefs[6] = "permitTruncTaxonNamesTrees";		else			prefs[6] = "noTruncTaxonNamesTrees";		if (tabbedWindows.getValue())			prefs[7] = "tabbed";		else			prefs[7] = "noTabbed";		if (debugMode.getValue())			prefs[8] = "debug";		else			prefs[8] = "noDebug";		if (wizards.getValue())			prefs[9] = "useWizards";		else			prefs[9] = "noUseWizards";		if (phoneHome.getValue())			prefs[10] = "phoneHome";		else			prefs[10] = "DontPhoneHome";		if (secondaryChoicesOnInDialogs.getValue())			prefs[11] = "secondaryChoicesOnInDialogs";		else			prefs[11] = "secondaryChoicesOffInDialogs";		return prefs;	}				/*.................................................................................................................*/	/** Respond to commands sent to the window. */    	 public Object doCommand(String commandName, String arguments, CommandChecker checker) {		if (checker.compare(MesquiteWindow.class, "Sets the default font of windows", "[name of font]", commandName, "setDefaultFont")) {    	 		if (MesquiteWindow.defaultFont==null)    	 			return null;    	 		Font fontToSet = new Font (parser.getFirstToken(arguments), MesquiteWindow.defaultFont.getStyle(), MesquiteWindow.defaultFont.getSize());    	 		if (fontToSet!= null) {    	 			MesquiteWindow.defaultFont = fontToSet;		  	 	storePreferences();    	 		}    	 	}    	 	else if (checker.compare(MesquiteWindow.class, "Sets the default font size of windows", "[font size]", commandName, "setDefaultFontSize")) {    	 		if (MesquiteWindow.defaultFont==null)    	 			return null;    	 		int fontSize = MesquiteInteger.fromFirstToken(arguments, new MesquiteInteger(0));    	 		if (!MesquiteInteger.isCombinable(fontSize))    	 			fontSize = MesquiteInteger.queryInteger(containerOfModule(), "Font size", "Font size for window", MesquiteWindow.defaultFont.getSize(), 4, 256);    	 		if (!MesquiteInteger.isCombinable(fontSize))    	 			return null;    	 		Font fontToSet = new Font (MesquiteWindow.defaultFont.getName(), MesquiteWindow.defaultFont.getStyle(), fontSize);    	 		if (fontToSet!= null) {    	 			MesquiteWindow.defaultFont = fontToSet;		  	 	storePreferences();    	 		}    	 	}		else if (checker.compare(MesquiteWindow.class, "Forgets the default web browser", null, commandName, "forgetBrowser")) {    	 		browserString = null;    	 	}    	 	else if (checker.compare(MesquiteWindow.class, "Sets the number of previous logs saved", "[num logs]", commandName, "setNumPrevLog")) {    	 		int numLogs = MesquiteInteger.fromString(arguments);    	 		if (!MesquiteInteger.isCombinable(numLogs))    	 			numLogs = MesquiteInteger.queryInteger(containerOfModule(), "Number of Logs", "Number of previous log files retained", MesquiteTrunk.numPrevLogs, 2, 10000);    	 		if (!MesquiteInteger.isCombinable(numLogs) || numLogs == MesquiteTrunk.numPrevLogs)    	 			return null;	 		MesquiteTrunk.numPrevLogs = numLogs;	  	 	MesquiteTrunk.mesquiteTrunk.storePreferences();    	 	}    		else if (checker.compare(getClass(), "Sets whether to show windows of each project as tabs within a single window", null, commandName, "toggleTabbedWindows")) {    	 		tabbedWindows.toggleValue(null);			MesquiteWindow.compactWindows = tabbedWindows.getValue();			discreetAlert( "You need to close and reopen files to have the change in using tabs versus windows take effect");			storePreferences();			}		else if (checker.compare(getClass(), "Sets whether to use log window as input console for commands", null, commandName, "toggleConsoleMode")) {    	 		console.toggleValue(null);			MesquiteTrunk.mesquiteTrunk.logWindow.setConsoleMode(console.getValue());			if (!MesquiteThread.isScripting()) {				if (console.getValue()) {					logln("Command-line Mode On.  Type \"help\" for some console commands.  Note: command-line mode is experimental.  Currently it is not properly protected against simultaneous calculations, e.g. doing different modifications simultaneously of the same tree or data.");					MesquiteTrunk.mesquiteTrunk.logWindow.showPrompt();				}				else					logln("\nConsole Mode Off");			}	  	 	MesquiteTrunk.mesquiteTrunk.storePreferences();    	 	}		else if (checker.compare(getClass(), "Sets whether to log all commands", null, commandName, "toggleLogAll")) {	 		logAll.toggleValue(null);		MesquiteCommand.logEverything = logAll.getValue();		if (!MesquiteThread.isScripting()) {			if (logAll.getValue()) {				logln("Highly verbose logging on (Logging all commands).");			}			else				logln("Normal logging in effect");		}  	 	MesquiteTrunk.mesquiteTrunk.storePreferences();	 	}		else if (checker.compare(getClass(), "Sets whether to use debug mode", null, commandName, "toggleDebugMode")) {	 		debugMode.toggleValue(null); 			MesquiteTrunk.debugMode = debugMode.getValue(); 			storePreferences(); 	 		return debugMode;	 	}		else if (checker.compare(getClass(), "Sets whether to use check Mesquite web site for notices on startup", null, commandName, "togglePhoneHome")) {	 		phoneHome.toggleValue(null); 			MesquiteTrunk.phoneHome = phoneHome.getValue(); 			storePreferences(); 	 		return phoneHome;	 	}			else if (checker.compare(getClass(), "Sets whether to use wizard-style dialogs", null, commandName, "toggleWizards")) {	 		wizards.toggleValue(null); 			MesquiteDialog.useWizards = wizards.getValue(); 			storePreferences(); 	 		return wizards;	 	}		else if (checker.compare(getClass(), "Sets whether to use xor mode", null, commandName, "toggleXORMode")) {	 		suppressXORMode.toggleValue(null); 			GraphicsUtil.useXOR = !suppressXORMode.getValue(); 			resetAllMenuBars();	 		return suppressXORMode;	 	}		else if (checker.compare(getClass(), "Sets whether to permit taxon name truncation in trees", null, commandName, "togglePartNamesTrees")) {	 		taxonTruncTrees.toggleValue(null);	 		MesquiteTree.permitTruncTaxNames = taxonTruncTrees.getValue(); 			resetAllMenuBars(); 			storePreferences();	 		return taxonTruncTrees;	 	}		else if (checker.compare(getClass(), "Sets whether to place secondary choices for modules into an \"Other Choices...\" dialog box", null, commandName, "toggleOtherChoices")) {			useOtherChoices.toggleValue(null);			EmployerEmployee.useOtherChoices = useOtherChoices.getValue();			resetAllMenuBars();			storePreferences();			return useOtherChoices;		}		else if (checker.compare(getClass(), "Sets whether to have secondary choices shown by default in dialog boxes", null, commandName, "toggleSecondaryChoicesOnInDialogs")) {			secondaryChoicesOnInDialogs.toggleValue(null);			EmployerEmployee.secondaryChoicesOnInDialogs = secondaryChoicesOnInDialogs.getValue();			resetAllMenuBars();			storePreferences();			return secondaryChoicesOnInDialogs;		}		else if (checker.compare(getClass(), "Sets whether to place ask for random number seeds when calculations requested", null, commandName, "toggleAskSeed")) {    	 		askSeed.toggleValue(null);			RandomBetween.askSeed = askSeed.getValue();			storePreferences();    	 		return askSeed;    	 	}		/*		else if (checker.compare(getClass(), "Sets whether to call the preferences directory \".Mesquite_Prefs\", which makes it invisible on some operating systems.", null, commandName, "toggleDotPrefs")) {    	 		useDotPrefs.toggleValue(null);			if (useDotPrefs.getValue())				MesquiteModule.prefsDirectory.renameTo(new File(System.getProperty("user.home") + MesquiteFile.fileSeparator + ".Mesquite_Prefs"));			else				MesquiteModule.prefsDirectory.renameTo(new File(System.getProperty("user.home") + MesquiteFile.fileSeparator + "Mesquite_Prefs"));			resetAllMenuBars();    	 		return useDotPrefs;    	 	}    	 	*/    	 	else    	 		return  super.doCommand(commandName, arguments, checker);    	 	return null;   	 }	/*.................................................................................................................*/    	 public String getName() {		return "Defaults";   	 }}