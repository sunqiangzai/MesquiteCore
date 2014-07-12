/* Mesquite Chromaseq source code.  Copyright 2005-2011 David Maddison and Wayne Maddison.Version 1.0   December 2011Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.basic.VoucherInfoFromFlexTable; import mesquite.lib.*;import java.util.Vector;/* ======================================================================== */public class  VoucherInfoFromFlexTable extends VoucherInfoSource {	Vector tables, paths;	boolean warned = false;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		paths = new Vector();		tables = new Vector();		return true;	}		/*.................................................................................................................*/	private String getPath(String databaseID){		String where = null;		if (databaseID.indexOf(":")<0)			where = databaseID;		else			where = databaseID.substring(databaseID.indexOf(":")+1, databaseID.length());		if (!warned && StringUtil.blank(where)) {			MesquiteMessage.warnProgrammer("Path of voucher info file is empty; databaseID = " + databaseID);			warned = true;		}		return where;	}	/*.................................................................................................................*/	private String getDBID(String databaseID){		String which = null;		if (databaseID.indexOf(":")<0)			which = databaseID;		else			which = databaseID.substring(0, databaseID.indexOf(":"));		if (!warned && StringUtil.blank(which)) {			MesquiteMessage.warnProgrammer("DBID of voucher info file is empty; databaseID = " + databaseID);			warned = true;		}		return which;	}	private int findPath(String path){		if (path == null)			return -1;		for (int i=0; i<paths.size(); i++)			if (path.equals(paths.elementAt(i)))				return i;		return -1;	}	private int findRow(String[][] table, String id){		for (int i=0; i<table.length; i++){			if (table[i] != null && table[i].length>0 && id.equalsIgnoreCase(table[i][0]))				return i;					}		return -1;	}	private VoucherInfo makeInfo(String[][] table, int row){				if (table == null || row>= table.length)			return null;		VoucherInfo vi = new VoucherInfo();		String[] line = table[0];  		// cycle through first row to get field names		for (int i = 1; i<line.length; i++)  //start at 1 as we don't want to include the code			vi.addElement(line[i]);				line = table[row];		if (line == null || line.length == 0)			return null;		for (int i = 1; i<line.length; i++)  //start at 1 as we don't want to include the code			vi.setFieldValue(i-1, line[i]);   //index is i-1 as we skipped over the first one		return vi;	}	/*.................................................................................................................*///	String voucherID, String species, String latLong, String locality, String note, String collectionDate		public VoucherInfo getVoucherInfo(String databaseID, String id){		if (!handlesDatabase(databaseID))			return null;		String path = getPath(databaseID);		if (path == null)			return null;		int i = findPath(path);		String[][] table = null;		if (i<0){			table = MesquiteFile.getTabDelimitedTextFile(path);			if (table != null){				paths.addElement(path);				tables.addElement(table);			}		}		else			table = (String[][]) tables.elementAt(i);		if (table == null)			return null;		int whichRow = findRow(table, id);		if (whichRow<0)			return null;		return makeInfo(table, whichRow);	}	/*.................................................................................................................*/	public boolean handlesDatabase(String databaseID){		if (databaseID == null)			return false;		String which = getDBID(databaseID);		return "tdt".equalsIgnoreCase(which);	}		/*.................................................................................................................*/	public String getName() {		return "Voucher Info from Flexible Table";	}	/*.................................................................................................................*/	public boolean isPrerelease(){		return false;  	}	/*.................................................................................................................*/	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/	public int getVersionOfFirstRelease(){		return -1;  	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return true;  	}		/*.................................................................................................................*/	/** returns an explanation of what the module does.*/	public String getExplanation() {		return "Supplies voucher information from a flexible tab-delimited table in a text file." ;	}}