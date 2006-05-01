/* Mesquite source code.  Copyright 1997-2005 W. Maddison and D. Maddison. Version 1.06, August 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.trees.BasicTreeDrawCoordinator;/*~~  */import java.util.*;import java.awt.*;import java.io.*;import java.awt.image.*;import mesquite.lib.*;import mesquite.lib.duties.*;/** Coordinates the drawing of trees in windows (e.g., used in the Tree Window and other places) */public class BasicTreeDrawCoordinator extends DrawTreeCoordinator {	private DrawTree treeDrawTask;	private DrawNamesTreeDisplay terminalNamesTask;	MesquiteString treeDrawName, bgColorName, brColorName;	public Color bgColor=Color.white;	public Color brColor=Color.black;	boolean suppression = false;	MesquiteCommand tdC;	static String defaultDrawer = null;	MesquiteBoolean showNodeNumbers, labelBranchLengths;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		loadPreferences();		//addMenuItem("-", null);		makeMenu("Drawing");		if (defaultDrawer !=null && (condition == null || !(condition instanceof MesquiteBoolean) || ((MesquiteBoolean)condition).getValue() )) {			treeDrawTask= (DrawTree)hireNamedEmployee(commandRec, DrawTree.class, defaultDrawer);			if (treeDrawTask == null)				treeDrawTask= (DrawTree)hireEmployee(commandRec, DrawTree.class, null);		}		else			treeDrawTask= (DrawTree)hireEmployee(commandRec, DrawTree.class, null);		if (treeDrawTask==null)			return sorry(commandRec, getName() + " couldn't start because no tree drawing module was obtained");		setAutoSaveMacros(true);		treeDrawName = new MesquiteString(treeDrawTask.getName());		bgColorName = new MesquiteString("White");		brColorName = new MesquiteString("Black");		terminalNamesTask = (DrawNamesTreeDisplay)hireEmployee(commandRec, DrawNamesTreeDisplay.class, null);		//TODO: if choice of terminalNamesTask, use setHriingCommand		tdC = makeCommand("setTreeDrawer",  this);		treeDrawTask.setHiringCommand(tdC);		MesquiteSubmenuSpec mmis = addSubmenu(null, "Tree Form", tdC);		addMenuItem("Set Current Form as Default", makeCommand("setFormToDefault",  this));		mmis.setList(DrawTree.class);		mmis.setSelected(treeDrawName);		mmis = addSubmenu(null, "Background Color", makeCommand("setBackground",  this));		mmis.setList(ColorDistribution.standardColorNames);		mmis.setSelected(bgColorName);		mmis = addSubmenu(null, "Branch Color", makeCommand("setBranchColor",  this));		mmis.setList(ColorDistribution.standardColorNames);		mmis.setSelected(brColorName);		showNodeNumbers = new MesquiteBoolean(false);		labelBranchLengths = new MesquiteBoolean(false);		addCheckMenuItem(null, "Show Node Numbers", MesquiteModule.makeCommand("showNodeNumbers",  this), showNodeNumbers);		addCheckMenuItem(null, "Label Branch Lengths", MesquiteModule.makeCommand("labelBranchLengths",  this), labelBranchLengths);				return true;  	 }  	   	 public Dimension getPreferredSize(){  	 	return treeDrawTask.getPreferredSize();  	 }	/*.................................................................................................................*/	public void processPreferencesFromFile (String[] prefs) {		if (prefs!=null && prefs.length>0) {			defaultDrawer = prefs[0];		}	}	/*.................................................................................................................*/	public String[] preparePreferencesForFile () {		String[] prefs= new String[1];		prefs[0] = defaultDrawer;		return prefs;	}	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();  	 	temp.addLine("suppress");   	 	  	 	temp.addLine("setTreeDrawer " , treeDrawTask);  	 	if (bgColor !=null) {  	 		String bName = ColorDistribution.getStandardColorName(bgColor);  	 		if (bName!=null)  	 			temp.addLine("setBackground " + StringUtil.tokenize(bName));//quote  	 	}  	 	if (brColor !=null) {  	 		String bName = ColorDistribution.getStandardColorName(brColor);  	 		if (bName!=null)  	 			temp.addLine("setBranchColor " + StringUtil.tokenize(bName));//quote  	 	}   	 	temp.addLine("showNodeNumbers " + showNodeNumbers.toOffOnString());   	 	temp.addLine("labelBranchLengths " + labelBranchLengths.toOffOnString());  	 	temp.addLine("desuppress");  	 	return temp;  	 }  	    	 public DrawNamesTreeDisplay getNamesTask(){   	 	return terminalNamesTask;   	 	}	/*.................................................................................................................*/ 	public void setBranchColor(Color c) { 		brColor = c; 	}	/*.................................................................................................................*/ 	public TreeDisplay createOneTreeDisplay(Taxa taxa, MesquiteWindow window, CommandRecord commandRec) {	  	treeDisplay = new BasicTreeDisplay(this, taxa);		treeDisplay.setTreeDrawing(treeDrawTask.createTreeDrawing(treeDisplay, taxa.getNumTaxa()), commandRec);		treeDisplay.setDrawTaxonNames(terminalNamesTask);		treeDisplay.suppressDrawing(suppression);	  	return treeDisplay;   	 }	/*.................................................................................................................*/ 	public TreeDisplay[] createTreeDisplays(int numDisplays, Taxa taxa, MesquiteWindow window, CommandRecord commandRec) { 		treeDisplays = new BasicTreeDisplay[numDisplays]; 		this.numDisplays=numDisplays; 		for (int i=0; i<numDisplays; i++) {	  		treeDisplays[i] = new BasicTreeDisplay(this, taxa);			treeDisplays[i].setDrawTaxonNames(terminalNamesTask);		  	treeDisplays[i].setTreeDrawing(treeDrawTask.createTreeDrawing(treeDisplays[i], taxa.getNumTaxa()), commandRec);			treeDisplays[i].suppressDrawing(suppression);		} 		return treeDisplays;   	 }	/*.................................................................................................................*/ 	public TreeDisplay[] createTreeDisplays(int numDisplays, Taxa[] taxas, MesquiteWindow window, CommandRecord commandRec) { 		treeDisplays = new BasicTreeDisplay[numDisplays]; 		this.numDisplays=numDisplays; 		for (int i=0; i<numDisplays; i++) {	  		treeDisplays[i] = new BasicTreeDisplay(this, taxas[i]);		  	treeDisplays[i].setTreeDrawing(treeDrawTask.createTreeDrawing(treeDisplays[i], taxas[i].getNumTaxa()), commandRec);			treeDisplays[i].suppressDrawing(suppression);		} 		return treeDisplays;   	 }	/*................................................................................................................. 	public void endJob() { 		if (MesquiteTrunk.trackActivity) logln ("MesquiteModule " + getName() + "  closing down ");		closeDownAllEmployees (this); 		employees.removeElement(treeDrawTask); 		if (treeDisplay != null) 			treeDisplay.suppressDrawing(true); 		treeDisplay = null;   	 }	/*.................................................................................................................*/ long progress   = 0;   	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "Sets the current tree form to be the default", null, commandName, "setFormToDefault")) {    	 		defaultDrawer = " #" + MesquiteModule.getShortClassName(treeDrawTask.getClass());	  	 	storePreferences();    	 	}    	 	else if (checker.compare(this.getClass(), "Sets the module to be used to draw the tree", "[name of tree draw module]", commandName, "setTreeDrawer")) {			incrementMenuResetSuppression();			DrawTree temp = null;			if (treeDisplay != null) {				boolean vis = true;								while (treeDisplay.getDrawingInProcess()) {					;						}				vis = treeDisplay.isVisible();				treeDisplay.setVisible(false);				treeDisplay.suppressDrawing(true);				treeDisplay.getTreeDrawing().dispose();				treeDisplay.setTreeDrawing(null, commandRec);	    	 		temp = (DrawTree)replaceEmployee(commandRec, DrawTree.class, arguments, "Form of tree?", treeDrawTask);		 		if (temp!=null) {		 			treeDrawTask = temp;			 		treeDrawName.setValue(treeDrawTask.getName());			 		treeDrawTask.setHiringCommand(tdC);		 		}				treeDisplay.setTreeDrawing(treeDrawTask.createTreeDrawing(treeDisplay, treeDisplay.getTaxa().getNumTaxa()), commandRec); 				treeDisplay.suppressDrawing(suppression);				if (!suppression)					treeDisplay.pleaseUpdate(true, commandRec);				treeDisplay.setVisible(vis);			}			else if (treeDisplays != null) { //many tree displays				boolean[] vis = new boolean[numDisplays];		 		for (int i=0; i<numDisplays; i++) {					while (treeDisplays[i].getDrawingInProcess())						;							vis[i] = treeDisplays[i].isVisible();					treeDisplays[i].setVisible(false);			  		treeDisplays[i].suppressDrawing(true);					treeDisplays[i].getTreeDrawing().dispose();					treeDisplays[i].setTreeDrawing(null, commandRec);				}	    	 		temp = (DrawTree)replaceEmployee(commandRec, DrawTree.class, arguments, "Form of tree?", treeDrawTask);		 		if (temp!=null) {		 			treeDrawTask = temp;			 		treeDrawName.setValue(treeDrawTask.getName());			 		treeDrawTask.setHiringCommand(tdC);		 		}		 		for (int i=0; i<numDisplays; i++) {				  	treeDisplays[i].setTreeDrawing(treeDrawTask.createTreeDrawing(treeDisplays[i], treeDisplays[i].getTaxa().getNumTaxa()), commandRec);			  	}		 		for (int i=0; i<numDisplays; i++) {			  		treeDisplays[i].suppressDrawing(suppression);					if (!suppression)				  		treeDisplays[i].repaint();					treeDisplays[i].setVisible(vis[i]);			  	}			}					 					decrementMenuResetSuppression();			if (temp == null)				return null;			else {				if (!commandRec.scripting())					parametersChanged(null, commandRec);				return treeDrawTask;			}    	 	}    	 	else if (checker.compare(this.getClass(), "Suppresses tree drawing", null, commandName, "suppress")) {    	 		suppression=true;	 		if (treeDisplay != null) { 				treeDisplay.suppressDrawing(suppression);			}			else if (treeDisplays != null) {		 		for (int i=0; i<numDisplays; i++) {			  		treeDisplays[i].suppressDrawing(suppression);			  	}			}    	 	}    	 	else if (checker.compare(this.getClass(), "Removes suppression of tree drawing", null, commandName, "desuppress")) {    	 		suppression=false;	 		if (treeDisplay != null) { 				treeDisplay.suppressDrawing(suppression);				treeDisplay.pleaseUpdate(true, commandRec);			}			else if (treeDisplays != null) {		 		for (int i=0; i<numDisplays; i++) {			  		treeDisplays[i].suppressDrawing(suppression);				  	treeDisplays[i].pleaseUpdate(true, commandRec);			  	}			}    	 	}    	 	else if (checker.compare(this.getClass(), "Sets background color of tree display", "[name of color]", commandName, "setBackground")) {    	 		String token = ParseUtil.getFirstToken(arguments, stringPos);    	 		Color bc = ColorDistribution.getStandardColor(token);			if (bc == null)				return null;			bgColor = bc;			bgColorName.setValue(token);			if (treeDisplay != null) {				while (treeDisplay.getDrawingInProcess())					;						treeDisplay.setBackground(bc);				Container c = treeDisplay.getParent();				if (c!=null)					c.setBackground(bc);				terminalNamesTask.invalidateNames(treeDisplay);				if (!suppression)					treeDisplay.repaintAll();			}			else if (treeDisplays != null) {		 		for (int i=0; i<numDisplays; i++) {					while (treeDisplays[i].getDrawingInProcess())						;							treeDisplays[i].setBackground(bc);					Container c = treeDisplays[i].getParent();					if (c!=null)						c.setBackground(bc);					terminalNamesTask.invalidateNames(treeDisplays[i]);					if (!suppression)						treeDisplays[i].repaintAll();				}			}			if (!commandRec.scripting())				parametersChanged(null, commandRec);    	 	}    	 	else if (checker.compare(this.getClass(), "Sets default color of branches of tree in tree display", "[name of color]", commandName, "setBranchColor")) {    	 		String token = ParseUtil.getFirstToken(arguments, stringPos);    	 		Color bc = ColorDistribution.getStandardColor(token);			if (bc == null)				return null;    	 		Color bcD = ColorDistribution.getStandardColorDimmed(ColorDistribution.getStandardColorNumber(bc));			brColor = bc;			brColorName.setValue(token);			if (treeDisplay != null) {				while (treeDisplay.getDrawingInProcess())					;						treeDisplay.branchColor = bc;				treeDisplay.branchColorDimmed = bcD;				Container c = treeDisplay.getParent();				if (c!=null)					c.setBackground(bc);				if (!suppression)					treeDisplay.pleaseUpdate(false, commandRec);			}			else if (treeDisplays != null) {		 		for (int i=0; i<numDisplays; i++) {					while (treeDisplays[i].getDrawingInProcess())						;							treeDisplays[i].branchColor = bc;					treeDisplays[i].branchColorDimmed = bcD;					if (!suppression)						treeDisplays[i].pleaseUpdate(false, commandRec);				}			}    	 	}    	 	else if (checker.compare(this.getClass(), "Shows node numbers on tree", "[on or off]", commandName, "showNodeNumbers")) {    	 		showNodeNumbers.toggleValue(arguments);			if (treeDisplay != null) {				while (treeDisplay.getDrawingInProcess())					;						if (!suppression)					treeDisplay.pleaseUpdate(false, commandRec);			}			else if (treeDisplays != null) {		 		for (int i=0; i<numDisplays; i++) {					while (treeDisplays[i].getDrawingInProcess())						;							if (!suppression)						treeDisplays[i].pleaseUpdate(false, commandRec);				}			}			    	 	}    	 	else if (checker.compare(this.getClass(), "Shows branch lengths on tree", "[on or off]", commandName, "labelBranchLengths")) {    	 		labelBranchLengths.toggleValue(arguments);			if (treeDisplay != null) {				while (treeDisplay.getDrawingInProcess())					;						if (!suppression)					treeDisplay.pleaseUpdate(false, commandRec);			}			else if (treeDisplays != null) {		 		for (int i=0; i<numDisplays; i++) {					while (treeDisplays[i].getDrawingInProcess())						;							if (!suppression)						treeDisplays[i].pleaseUpdate(false, commandRec);				}			}			    	 	}    	 	else if (checker.compare(this.getClass(), "Returns the tree drawing module in use", null, commandName, "getTreeDrawer")) {    	 		return treeDrawTask;    	 	}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);		return null;   	 }   	 public void endJob(){   	 	treeDrawTask=null;   	 	terminalNamesTask=null; 		if (treeDisplay != null) {			treeDisplay=null;		}		else if (treeDisplays != null) {	 		for (int i=0; i<treeDisplays.length; i++) {			  	treeDisplays[i]=null;		  	}		}   	 	super.endJob();   	 }	/*.................................................................................................................*/ 	public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification, CommandRecord commandRec) { 		if (commandRec.scripting()) 			return; 		if (treeDisplay != null) {			((BasicTreeDisplay)treeDisplay).pleaseUpdate(true, commandRec);		}		else if (treeDisplays != null) {	 		for (int i=0; i<numDisplays; i++) {		  		((BasicTreeDisplay)treeDisplays[i]).pleaseUpdate(true, commandRec);		  	}		}	}	/*.................................................................................................................*/    	 public String getName() {		return "Basic Tree Draw Coordinator";   	 }	/*.................................................................................................................*/    	 public String getNameForMenuItem() {		return "Tree Drawing";   	 }	/*.................................................................................................................*/   	  	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Coordinates the drawing of a tree by maintaining the basic TreeDisplay and by hiring a DrawTree module." ;   	 }   	 }/* ======================================================================== */class BasicTreeDisplay extends TreeDisplay  {	private int foundBranch;	boolean showPixels = false;//for debugging	BasicTreeDrawCoordinator ownerDrawModule;	public BasicTreeDisplay (BasicTreeDrawCoordinator ownerModule, Taxa taxa) {		super(ownerModule, taxa);		ownerDrawModule = ownerModule;		suppress = true;		setBackground(Color.white);	}	public void setTree(Tree tree, CommandRecord commandRec) {	   	if (ownerModule.isDoomed())	   		return;		boolean wasNull = (this.tree == null);		((DrawTreeCoordinator)ownerModule).getNamesTask().setTree(tree, commandRec);	 	super.setTree(tree, commandRec);//here ask for nodelocs to be calculated	 	if (wasNull)	 		repaint();	 		}	/**/	public void forceRepaint(){	   	if (ownerModule.isDoomed())	   		return;		repaintsPending = 0;		repaint();	}	static int cr = 0;	public void repaint(boolean resetTree, CommandRecord commandRec) {  //TODO: this whole system needs revamping.  	   	if (ownerModule.isDoomed())	   		return;		repaintRequests++;		if (repaintRequests>1000){			repaintRequests = 0;			MesquiteMessage.warnProgrammer("more than 1000 repaint requests in Tree Display");			MesquiteMessage.printStackTrace("more than 1000 repaint requests in Tree Display");		}		if (tree!=null && resetTree) {			recalculatePositions(commandRec);		}		super.repaint();	}	public void repaint(){	   	if (ownerModule == null || ownerModule.isDoomed())	   		return;		repaintRequests++;		if (repaintRequests>1000){			repaintRequests = 0;			MesquiteMessage.warnProgrammer("more than 1000 repaint requests in Tree Display");			MesquiteMessage.printStackTrace("more than 1000 repaint requests in Tree Display");		}		super.repaint();	}	/*_________________________________________________*/	long repaintRequests = 0;	int retry =0;	/*_________________________________________________*/	   public void paint(Graphics g) {	   	if (ownerModule == null || ownerModule.isDoomed())	   		return;	   	if (MesquiteWindow.checkDoomed(this)) 	   		return;		setDrawingInProcess(true, CommandRecord.getRecNSIfNull());	   	int initialPending = repaintsPending;		which =0;		if (bailOut(initialPending)) return;		if (getParent().getBackground()!=getBackground())			getParent().setBackground(getBackground());		if (bailOut(initialPending)) return;	   	if (getFieldWidth()==0 || getFieldHeight()==0)	   		setFieldSize(getBounds().width, getBounds().height);		if (bailOut(initialPending)) return;	   	if (getTipsMargin()<0 && getTreeDrawing()!=null && tree !=null)	   		getTreeDrawing().recalculatePositions(tree, CommandRecord.getRecNSIfNull());		if (bailOut(initialPending)) return;	   	if (getTipsMargin()<0)	   		setTipsMargin(0);		if (bailOut(initialPending)) return;	   	super.paint(g);		if (bailOut(initialPending)) return;		Tree tempTree = getTree();		if (bailOut(initialPending)) return;		if (tree==null) {	   		//repaint();	   	}		else if (getTreeDrawing()==null) {	   		repaint();	   	}		else if (suppress) {			 	if (retry>500)			 		System.out.println("Error: retried " + retry + " times to draw tree; remains suppressed");			 	else {			 		retry++;			 		repaint();			 	}		}		else if (!tree.isLocked() && tree.isDefined()) {		   	int stage = -1;			try {				if (tree == null || tree.getTaxa().isDoomed()) {					setDrawingInProcess(false, CommandRecord.getRecNSIfNull());					MesquiteWindow.uncheckDoomed(this);					return;				}			   	if (bailOut(initialPending)) return;				retry = 0;				if (showPixels){					for (int h=0; h<getFieldWidth() &&  h<getFieldHeight(); h += 50) {						g.setColor(Color.red);						g.drawString(Integer.toString(h), h, h);					}				}				int dRoot = getTreeDrawing().getDrawnRoot();				if (!tree.nodeExists(dRoot))					dRoot = tree.getRoot();			  	//getTreeDrawing().setHighlightsOn(tree.anySelectedInClade(dRoot));			   	if (bailOut(initialPending)) return;			   	stage = 0;				drawAllBackgroundExtras(tree, dRoot, g);				stage = 1;								   	if (bailOut(initialPending)) return;				getTreeDrawing().drawTree(tree, dRoot, g); //ALLOW other drawnRoots!								//showNodeLocations(tree, g, tree.getRoot());				stage = 2;							   	if (bailOut(initialPending)) return;				drawAllExtras(tree, dRoot, g);			   	if (bailOut(initialPending)) return;			   	stage = 3;			   	if (ownerDrawModule.labelBranchLengths.getValue())			   		drawBranchLengthsOnTree(tree, dRoot, g);			   	stage = 4;			   	if (ownerDrawModule.showNodeNumbers.getValue())			   		drawNodeNumbersOnTree(tree, dRoot, g);			   	stage = 5;			   	if (bailOut(initialPending)) return;			   					if (!suppressNames && ownerModule!=null && ((DrawTreeCoordinator)ownerModule).getNamesTask()!=null)					((DrawTreeCoordinator)ownerModule).getNamesTask().drawNames(this, tree, dRoot, g);				stage = 6;			   	if (bailOut(initialPending)) return;				if (getTreeDrawing()!=null && tree !=null && getHighlightedBranch() > 0) 					getTreeDrawing().fillBranchInverted(tree, getHighlightedBranch(),g); 				stage = 7;			   	if (bailOut(initialPending)) return;		   	}		   	catch (Throwable e){		   		MesquiteMessage.println("Error or Exception in tree drawing (stage " + stage +") (" + e.toString() + ")");//		   		MesquiteMessage.println("Error or Exception in tree drawing (stage " + stage +")");				PrintWriter pw = MesquiteFile.getLogWriter();				if (pw!=null)					e.printStackTrace(pw);		   	}	   	}		setDrawingInProcess(false, CommandRecord.getRecNSIfNull());	   	if (tempTree != tree) {	   		repaint();	   	}		else if (bailOut(initialPending))			return;	   	else if (!isVisible())	   		repaint();	   	else	   		repaintsPending = 0;		repaintRequests = 0;		MesquiteWindow.uncheckDoomed(this);		setInvalid(false);	   }	   public void update(Graphics g){	   	super.update(g);	   }	  private int which = 0;	  	  private boolean bailOut(int initialPending){	  	which++;		if (repaintsPending>initialPending){			setDrawingInProcess(false, CommandRecord.getRecNSIfNull());		   	repaintsPending  = 0;		 	repaint();			return true;		}		return false;	  }	/*_________________________________________________*/	private   void showNodeLocations(Tree tree, Graphics g, int N) {		if (tree.nodeExists(N)) {			g.setColor(Color.red);			g.fillOval(getTreeDrawing().x[N], getTreeDrawing().y[N], 4, 4);			g.setColor(branchColor);			for (int d = tree.firstDaughterOfNode(N); tree.nodeExists(d); d = tree.nextSisterOfNode(d))				showNodeLocations( tree, g, d);		}	}	/*_________________________________________________*/	   public void print(Graphics g) {		   printAll(g);	   }	/*_________________________________________________*/	   public void printAll(Graphics g) {	   	if (getFieldWidth()==0 || getFieldHeight()==0)	   		setFieldSize(getBounds().width, getBounds().height);	   	//super.paint(g);		if (tree==null)	   		MesquiteMessage.warnProgrammer("tree NULL in tree draw coord");		else if ((!suppress) && (!tree.isLocked())) {		   	repaintsPending = 0;		   	/* NEEDS TO DRAW BACKGROUND EXTRAS */			int dRoot = getTreeDrawing().getDrawnRoot();			if (!tree.nodeExists(dRoot))				dRoot = tree.getRoot();			printAllBackgroundExtras(tree, dRoot, g);			getTreeDrawing().drawTree(tree, dRoot, g); //OTHER ROOTS			printAllExtras(tree, dRoot, g);		   	if (ownerDrawModule.labelBranchLengths.getValue())		   		drawBranchLengthsOnTree(tree, dRoot, g);		   	if (ownerDrawModule.showNodeNumbers.getValue())		   		drawNodeNumbersOnTree(tree, dRoot, g);			if (!suppressNames && ownerModule!=null && ((DrawTreeCoordinator)ownerModule).getNamesTask()!=null)				((DrawTreeCoordinator)ownerModule).getNamesTask().drawNames(this, tree, dRoot, g);			printComponents(g);	   	}	   	else MesquiteMessage.warnProgrammer("tree drawing suppressed");	   }	private int spotsize = 18;	/*_________________________________________________*/	private   void drawSpot(TreeDisplay treeDisplay, Tree tree, Graphics g, int N) {		if (tree.nodeExists(N)) {			if (tree.nodeIsInternal(N) || true){  //replace true by show terminal				int i=0;				int j=2;				String s = Integer.toString(N);				FontMetrics fm = g.getFontMetrics(g.getFont());				int width = fm.stringWidth(s) + 6;				int height = fm.getAscent()+fm.getDescent() + 6;				if (spotsize>width)					width = spotsize;				if (spotsize>height)					height = spotsize;		        	g.setColor(Color.white);				g.fillOval(treeDisplay.getTreeDrawing().x[N] +i - width/2 , treeDisplay.getTreeDrawing().y[N] +i - height/2, width-i-i, height-i-i);		        	g.setColor(Color.black);		        					g.drawOval(treeDisplay.getTreeDrawing().x[N] +i - width/2 , treeDisplay.getTreeDrawing().y[N] +i - height/2, width-i-i, height-i-i);		        					g.drawString(Integer.toString(N), treeDisplay.getTreeDrawing().x[N]+2- width/2, treeDisplay.getTreeDrawing().y[N]-4+ height/2);			}			for (int d = tree.firstDaughterOfNode(N); tree.nodeExists(d); d = tree.nextSisterOfNode(d))				drawSpot(treeDisplay, tree, g, d);		}	}	/*_________________________________________________*/	public   void drawSpots(TreeDisplay treeDisplay, Tree tree, int drawnRoot, Graphics g) {	        if (MesquiteTree.OK(tree)) {	       	 	drawSpot(treeDisplay, tree, g, drawnRoot);  	       	 }	   }	/*.................................................................................................................*/	public   void drawNodeNumbersOnTree(Tree tree, int drawnRoot, Graphics g) {		drawSpots(this, tree, drawnRoot, g);	}	/*.................................................................................................................*/	public void writeLengthAtNode(Graphics g, int N,  Tree tree) {		for (int d = tree.firstDaughterOfNode(N); tree.nodeExists(d); d = tree.nextSisterOfNode(d))				writeLengthAtNode(g, d, tree);						int nodeX = getTreeDrawing().x[N];		int nodeY = getTreeDrawing().y[N];		if (getOrientation() == TreeDisplay.UP) {			nodeY+=10;			//nodeX+=10;		}		else if (getOrientation() == TreeDisplay.DOWN) {			nodeY-=10;			//nodeX+=10;		}		else if (getOrientation() == TreeDisplay.RIGHT) {			//nodeY=20;			nodeX-=10;		}		else if (getOrientation() == TreeDisplay.LEFT) {			//nodeY+=20;			nodeX+=10;		}		StringUtil.highlightString(g, MesquiteDouble.toString(tree.getBranchLength(N)), nodeX, nodeY, Color.blue, Color.white);	}	/*.................................................................................................................*/	public   void drawBranchLengthsOnTree(Tree tree, int drawnRoot, Graphics g) {		if (tree!=null) {			g.setColor(Color.blue);			writeLengthAtNode(g, drawnRoot, tree);			g.setColor(Color.black);		}	}	/*_________________________________________________*/	public void fillTaxon(Graphics g, int M) {		((DrawTreeCoordinator)ownerModule).getNamesTask().fillTaxon(g, M);	}	/*_________________________________________________*/	private boolean responseOK(){   		return (!getDrawingInProcess() && (tree!=null) && (!tree.isLocked()) && ownerModule!=null &&  (ownerModule.getEmployer() instanceof TreeDisplayActive));	}	/*_________________________________________________*/	public void mouseMoved(int modifiers, int x, int y, MesquiteTool tool) {	   	if (MesquiteWindow.checkDoomed(this))	   		return;   		if (responseOK()) {   			Graphics g = getGraphics();   			boolean dummy = ((TreeDisplayActive)ownerModule.getEmployer()).mouseMoveInTreeDisplay(modifiers,x,y,this, g);   			if (g!=null)   				g.dispose();   		}		MesquiteWindow.uncheckDoomed(this);		super.mouseMoved(modifiers,x,y, tool);	}	/*_________________________________________________*/	   public void mouseDown(int modifiers, int clickCount, long when, int x, int y, MesquiteTool tool) {	   	if (MesquiteWindow.checkDoomed(this))	   		return;		boolean somethingTouched = false;   		if (responseOK()) {   			Graphics g = getGraphics();   			somethingTouched = ((TreeDisplayActive)ownerModule.getEmployer()).mouseDownInTreeDisplay(modifiers,x,y,this, g);   			if (g!=null)   				g.dispose();		}		if (!somethingTouched)			super.panelTouched(modifiers, x,y, true);		MesquiteWindow.uncheckDoomed(this);	   }	/*_________________________________________________*/	public void mouseDrag(int modifiers, int x, int y, MesquiteTool tool) {	   	if (MesquiteWindow.checkDoomed(this))	   		return;   		if (responseOK()) {    			Graphics g = getGraphics();  			boolean dummy = ((TreeDisplayActive)ownerModule.getEmployer()).mouseDragInTreeDisplay(modifiers,x,y,this, g);   			if (g!=null)   				g.dispose();		}		MesquiteWindow.uncheckDoomed(this);		super.mouseDrag(modifiers,x,y, tool);	}	/*_________________________________________________*/	public void mouseUp(int modifiers, int x, int y, MesquiteTool tool) {	   	if (MesquiteWindow.checkDoomed(this))	   		return;   		if (responseOK()) {    			Graphics g = getGraphics();   			boolean dummy = ((TreeDisplayActive)ownerModule.getEmployer()).mouseUpInTreeDisplay(modifiers,x,y,this, g);   			if (g!=null)   				g.dispose();		}		MesquiteWindow.uncheckDoomed(this);		super.mouseUp(modifiers,x,y, tool);	}}