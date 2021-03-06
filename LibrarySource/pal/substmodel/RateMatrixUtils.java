// RateMatrixUtils.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)

package pal.substmodel;

import pal.datatype.*;

/**
 * @version $Id: RateMatrixUtils.java,v 1.2 2001/07/13 14:39:13 korbinian Exp $
 *
 * @author Korbinian Strimmer
 * @author Alexei Drummond
 * @author Matthew Goode
 */

public class RateMatrixUtils {

	/**
	 * Create rate matrix given model and data type codes
	 *
	 * @param typeID  code for data type
	 * @param modelID code for model of substitution
	 * @param params      model parameters
	 * @param freq        model frequencies
	 *
	 * @return rate matrix
	 */
	public static RateMatrix getInstance(int typeID, int modelID,
		double[] params, double[] freq)
	{
		if (typeID == DataType.NUCLEOTIDES)
		{
			return NucleotideModel.getInstance(modelID, params, freq);
		}
		else if (typeID == DataType.AMINOACIDS)
		{
			return AminoAcidModel.getInstance(modelID, freq);
		}
		else if (typeID == DataType.TWOSTATES)
		{
			return TwoStateModel.getInstance(freq);
		}
		else
		{
			return NucleotideModel.getInstance(modelID, params, freq);
		}
	}

	public static final RateMatrix[] getCopy(RateMatrix[] toCopy) {
		if(toCopy==null) {
			return null;
		}
		RateMatrix[] copy = new RateMatrix[toCopy.length];
		for(int i = 0 ; i < copy.length ; i++) {
     	copy[i] = (RateMatrix)toCopy[i].clone();
		}
		return copy;
	}
}

