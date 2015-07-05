/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package similarity;

import generic.Tuple;
import type1.sets.T1MF_Interface;

/**
 * Implementation of Jaccard Similarity for Type-1, Type-2 and zGT2 Sets
 * @author Christian Wagner
 */
public class JaccardSimilarityEngine 
{
    private double numerator, denominator;
    
    public double getSimilarity(T1MF_Interface setA, T1MF_Interface setB, int numberOfDiscretisations)
    {
        double[] discValues = getDiscretisationValues(setA.getSupport(), setB.getSupport(), numberOfDiscretisations);
        numerator = 0.0; denominator = 0.0;
        for(int i=0;i<discValues.length;i++)
        {
            numerator += Math.min(setA.getFS(discValues[i]), setB.getFS(discValues[i]));
            denominator += Math.max(setA.getFS(discValues[i]), setB.getFS(discValues[i]));
        }
        return numerator/denominator;
    }  
    
    private double[] getDiscretisationValues(Tuple domainSetA, Tuple domainSetB, int numberOfDiscretisations)
    {
        Tuple domain = new Tuple(Math.min(domainSetA.getLeft(), domainSetB.getLeft()), Math.max(domainSetA.getRight(), domainSetB.getRight()));
        double discStep = domain.getSize()/(numberOfDiscretisations-1);
        double[] discValues = new double[numberOfDiscretisations];
        for(int i=0;i<numberOfDiscretisations;i++)
        {
            discValues[i] = domain.getLeft() + i * discStep;
        }
        return discValues;
    }
}
