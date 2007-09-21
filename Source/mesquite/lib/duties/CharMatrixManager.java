/* Mesquite source code.  Copyright 1997-2007 W. Maddison and D. Maddison.Version 2.0, September 2007.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.lib.duties;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;/* ======================================================================== *//** Manages character data matrices of particular types.  Also reads and writes parts of CHARACTERS NEXUS block.  Different datatypes have their own managers, so that the coordinating CharactersManager doesn't need to know about the details.Example modules: "Manage Categorical char. matrices" (class ManageCategoricalChars); "Manage DNA/RNA matrices" (class ManageDNARNAChars) "Manage Continuous char. matrices" (class ManageContChars)*/public abstract class CharMatrixManager extends MesquiteModule   {	boolean logVerbose = true;	public boolean getSearchableAsModule(){		return false;	}	public Class getDutyClass() {		return CharMatrixManager.class;	}	public String getDutyName() {		return "Manager of Character data matrix, including read/write CHARACTERS block";	}	/** Process format command of NEXUS CHARACTERS block. Return the resulting CharacterData object*/	public abstract mesquite.lib.characters.CharacterData processFormat(MesquiteFile file, Taxa taxa, String dataType, String formatCommand, MesquiteInteger stringPos, int numChars, String title, String fileReadingArguments);	/** Process command in CHARACTERS block other than FORMAT or MATRIX command (e.g., CHARSTATELABELS command).  Should return true if processed	successfully, false if not*/	public abstract boolean processCommand(mesquite.lib.characters.CharacterData data, String commandName, String commandString);	/** return string consisting of CHARACTERS block for NEXUS file.*/	public abstract void writeCharactersBlock(mesquite.lib.characters.CharacterData data, CharactersBlock cB, MesquiteFile file, ProgressIndicator progIndicator);	/** return true if can read the data type passed (token from NEXUS file is the string passed, e.g. "STANDARD", "CONTINUOUS") */	public abstract boolean readsWritesDataType(String dataType);	/** return true if can read the data class passed, e.g. CategoricalData.class) */	public abstract boolean readsWritesDataType(Class dataClass);	/** Return name of data class managed, for listing in menus and so on (e.g. "DNA data" .*/	public abstract String getDataClassName();	/** Returns data class managed, e.g. ContinuousData.*/	public abstract Class getDataClass();	/** return a new CharacterData object for the taxa indicated.*/	public abstract mesquite.lib.characters.CharacterData getNewData(Taxa taxa, int numChars);//	public abstract void processMatrix(Taxa taxa, CharacterData data, String matrixString, boolean nameTaxa);	/*.................................................................................................................*/	public boolean isLogVerbose() {			return logVerbose;	}	/*.................................................................................................................*/	public void setLogVerbose(boolean logVerbose) {			this.logVerbose = logVerbose;	}	/*.................................................................................................................*/	/** Process the matrix, placing data into passed CharacterData object */	public void processMatrix(Taxa taxa, mesquite.lib.characters.CharacterData data, Parser parser, int numChars, boolean nameTaxa, int firstTaxon, boolean makeNewTaxaIfNeeded) {		String charstring;		String taxonName;		if (data != null && taxa == null)			taxa = data.getTaxa();		parser.setLineEndingsDark(false);		String d = parser.getNextToken(); //eating MATRIX		if (numChars<0 || !MesquiteInteger.isCombinable(numChars))			numChars = data.getNumChars();		if (data.interleaved) {			boolean warned = false;			int[] currentCharacter = new int[taxa.getNumTaxa()];			for (int i=firstTaxon; i<taxa.getNumTaxa(); i++) currentCharacter[i] =0;			boolean done = false;			parser.setLineEndingsDark(false);			int it=firstTaxon;			while (!done && !(taxonName=parser.getNextToken()).equals(";")) {				parser.setLineEndingsDark(true);				if (it<taxa.getNumTaxa() && taxa.whichTaxonNumber(taxonName,false,false)>=0){ // this name already exists in taxon block					if (!warned)						MesquiteMessage.discreetNotifyUser("Duplicate taxon name in interleaved matrix (" + taxonName + "); file will not be properly read.  Data will be incorrect!");					else {						MesquiteMessage.warnUser("\n****  Duplicate taxon name in interleaved matrix (" + taxonName + "); file will not be properly read.  Data will be incorrect!");						MesquiteModule.showLogWindow(true);					}					warned=true;				}				if (nameTaxa && it<taxa.getNumTaxa())					taxa.setTaxonName(it,taxonName);				int whichTaxon = taxa.whichTaxonNumberRev(taxonName, false);  //use reverse order lookup in case newly added taxa with identical names as previous				if (whichTaxon == -1) {					if (!warned) {						MesquiteMessage.warnUser("Unrecognized taxon name: " + taxonName + " in matrix ");						MesquiteModule.showLogWindow(true);					}					whichTaxon = it % taxa.getNumTaxa();				}				boolean polymorphOn = false;				boolean uncertainOn = false;				int response = mesquite.lib.characters.CharacterData.OK;				while (currentCharacter[whichTaxon]<numChars && response!=mesquite.lib.characters.CharacterData.EOL) {					response = data.setState(currentCharacter[whichTaxon], whichTaxon, parser, false, null);					if (response !=mesquite.lib.characters.CharacterData.EOL)						currentCharacter[whichTaxon]++;				}				if (it==0) 					data.interleavedLength=currentCharacter[0];				done = true;				for (int ic=0; ic<currentCharacter.length; ic++) 					if (currentCharacter[ic] != numChars)						done = false;				parser.setLineEndingsDark(false);				it++;			}			parser.setLineEndingsDark(false);		}		else {			for (int it=firstTaxon; it<taxa.getNumTaxa() && !isEndLine(taxonName=parser.getNextToken()); it++) {				if (nameTaxa)					taxa.setTaxonName(it,taxonName);				int whichTaxon = taxa.whichTaxonNumberRev(taxonName, false);  //use reverse order lookup in case newly added taxa with identical names as previous				if (whichTaxon == -1) {					if (makeNewTaxaIfNeeded){						int numTaxa = taxa.getNumTaxa();						taxa.addTaxa(numTaxa-1, 1, false);						data.addTaxa(numTaxa-1, 1);												whichTaxon = numTaxa;						Taxon taxon = taxa.getTaxon(whichTaxon);						taxon.setName(taxonName);					}					else {						MesquiteMessage.warnUser("Unrecognized taxon name: " + taxonName + " in matrix ");						MesquiteModule.showLogWindow(true);						whichTaxon = it;					}				}				int ic=0;				boolean polymorphOn = false;				boolean uncertainOn = false;				while (ic<numChars) {					int response = data.setState(ic++, whichTaxon, parser, false, null);				}			}		}	}	private boolean isEndLine(String tN){		if (tN == null)			return true;		return tN.equals(";");	}	protected String getIDsCommand(mesquite.lib.characters.CharacterData data){		String s = "";		if (anyIDs(data, 0)){			s += "IDS ";			for (int it=0; it<data.getNumChars() && anyIDs(data, it); it++) {				String id = data.getUniqueID(it);				if (StringUtil.blank(id))					s += " _ ";				else					s += id + " ";			}			s += ";" + StringUtil.lineEnding();		}		if (!StringUtil.blank(data.getUniqueID()))			s += "\tBLOCKID " + data.getUniqueID() + ";" + StringUtil.lineEnding();		if (StringUtil.blank(s))			return null;		return s;	}	boolean anyIDs(mesquite.lib.characters.CharacterData data, int start){		for (int it=start; it<data.getNumChars(); it++) {			String id = data.getUniqueID(it);			if (!StringUtil.blank(id))				return true;		}		return false;	}}