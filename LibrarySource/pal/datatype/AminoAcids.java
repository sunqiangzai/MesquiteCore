// AminoAcidData.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)


package pal.datatype;


/**
 * implements DataType for amino acids
 *
 * @version $Id: AminoAcids.java,v 1.10 2001/07/13 14:39:13 korbinian Exp $
 *
 * @note The Terminate state is not part of the "true" states of this DataType. It exists for terms of translating but
					is regarded as a unknowncharacter in general. (but using getChar('*') will not return getNumStates(), but isUnknown() will classify it as unknown)
 *
 * @author Korbinian Strimmer
 * @author Alexei Drummond
 * @author Matthew Goode
 */
public class AminoAcids extends SimpleDataType
{

	public static final char TERMINATE_CHARACTER = '*';
	public static final int TERMINATE_STATE = 21;
	// Get number of amino acids
	public int getNumStates()
	{
		return 20;
	}

	// Get state corresponding to character c
	public int getState(char c)
	{
		switch (c)
		{
			case 'A':
				return 0;
			case 'C':
				return 4;
			case 'D':
				return 3;
			case 'E':
				return 6;
			case 'F':
				return 13;
			case 'G':
				return 7;
			case 'H':
				return 8;
			case 'I': 
				return 9;
			case 'K': 
				return 11;
			case 'L': 
				return 10;
			case 'M': 
				return 12;
			case 'N': 
				return 2;
			case 'P': 
				return 14;
			case 'Q': 
				return 5;
			case 'R': 
				return 1;
			case 'S':
				return 15;
			case 'T': 
				return 16;
			case 'V': 
				return 19;
			case 'W': 
				return 17;
			case 'Y':
				return 18;
			case UNKNOWN_CHARACTER:
				return 20;
			case TERMINATE_CHARACTER:
				return 21; //Terminate
			default:
				return 20;
		}
	}

	// Get character corresponding to a given state
	public char getChar(int state)
	{
		switch (state)
		{
			case 0:
				return 'A';
			case 1:
				return 'R';
			case 2:
				return 'N';
			case 3:
				return 'D';
			case 4:
				return 'C';
			case 5:
				return 'Q';
			case 6:
				return 'E';
 			case 7:
				return 'G';
			case 8:
				return 'H';
			case 9:
				return 'I';
			case 10:
				return 'L';
			case 11:
				return 'K';
			case 12:
				return 'M';
			case 13:
				return 'F';
			case 14:
				return 'P';
			case 15:
				return 'S';
			case 16:
				return 'T';
			case 17:
				return 'W';
 			case 18:
				return 'Y';
			case 19:
				return 'V';
			case 20:
				return UNKNOWN_CHARACTER;
			case 21:
				return TERMINATE_CHARACTER; //Terminate


			default:
				return UNKNOWN_CHARACTER;
		}
	}

	/**
		* @retrun true if this state is an unknown state
		*/
	public boolean isUnknownState(int state) {
		return(state>=20);
	}

	// String describing the data type
	public String getDescription()
	{
		return "Amino acid";
	}

	// Get numerical code describing the data type
	public int getTypeID()
	{
		return 1;
	}
}

