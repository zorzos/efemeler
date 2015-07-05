/*
 * Input.java
 *
 * Created on 16 May 2007, 11:25
 * updated on 13 August 2014
 * Author: Christian Wagner
 * Copyright 2006 Christian Wagner All Rights Reserved.
 */

package generic;

import java.io.Serializable;
import java.util.ArrayList;

import type1.sets.T1MF_Gauangle;
import type1.sets.T1MF_Gaussian;
import type1.sets.T1MF_Interface;
import type1.sets.T1MF_Singleton;
import type1.sets.T1MF_Trapezoidal;
import type1.sets.T1MF_Triangular;

/**
 * The Input class allows the management and updating of one input, for example
 * as part of fuzzy membership functions.
 * @author Christian Wagner
 * @author Amandine Pailloux
 */
public class Input implements Serializable
{
    protected double x;
    private String name;
    private Tuple domain;
    private MF_Interface inputMF;
    private String scale;
    

    public Input(String name, Tuple domain)
    {
            this.name = name;
            this.domain = domain;
            this.x = 0;     
            this.inputMF = new T1MF_Singleton(x);
    }	
    public Input(String name, Tuple domain, double x)
    {
            this.name = name;
            this.domain = domain;        
            this.x=x;
            this.inputMF = new T1MF_Singleton(x); //If there is any precision in input the inputMF is by default a singleton
    }

    public Input(String name, Tuple domain, T1MF_Interface inputMF)
    {
            this.name = name;
            this.domain = domain; 
            this.inputMF = inputMF;
            this.x = inputMF.getPeak();
            
    }
    
    public Input(String name, Tuple domain, String scale) {
    	this.name = name;
    	this.domain = domain;
    	this.scale = scale;
    }
    
    public Input(){}   //no args constructor for serialization

    public String getName()
    {
        return name;
    }
    public void setName(String name) 
    {
        this.name = name;
    }   
    public double getInput()
    {
        return x;
    }
    public Tuple getDomain()
    {
        return domain;
    }
    public void setDomain(Tuple domain) 
    {
        this.domain = domain;
    }
    
    public String getScale() {
    	return scale;
    }
    
    public void setScale(String scale) {
    	this.scale = scale;
    }

    /**
     * Set the numeric input value x for this input and change its membership function
     * @param x The numeric value
     */
    public void setInput(double x)
    {
        if(domain.contains(x)) {
            this.x = x;
            MF_Interface inMF = this.inputMF;
            String nameMF = inMF.getName();
            if (inMF instanceof T1MF_Interface) {
	            if (inMF instanceof T1MF_Singleton) {
	            	this.inputMF = new T1MF_Singleton(x);
	            } else if (inMF instanceof T1MF_Gaussian) {
	            	double spread = ((T1MF_Gaussian) inMF).getSpread();
	            	this.inputMF = new T1MF_Gaussian(nameMF,x,spread);
	            } else if (inMF instanceof T1MF_Gauangle) {
	            	double start = ((T1MF_Gauangle) inMF).getStart();
	            	double end = ((T1MF_Gauangle) inMF).getEnd();
	            	double mean = ((T1MF_Gauangle) inMF).getMean();
	            	this.inputMF = new T1MF_Gauangle(nameMF,start+(x-mean),x,end+(x-mean));
	            } else if (inMF instanceof T1MF_Triangular) {
	            	double start = ((T1MF_Triangular) inMF).getStart();
	            	double end = ((T1MF_Triangular) inMF).getEnd();
	            	double mean = ((T1MF_Triangular) inMF).getPeak();
	            	this.inputMF = new T1MF_Triangular(nameMF,start+(x-mean),x,end+(x-mean));
	            } else if (inMF instanceof T1MF_Trapezoidal) {
	            	double[] params = new double[4];
	            	params[0] = ((T1MF_Trapezoidal) inMF).getA();
	            	params[1] = ((T1MF_Trapezoidal) inMF).getB();
	            	params[2] = ((T1MF_Trapezoidal) inMF).getC();
	            	params[3] = ((T1MF_Trapezoidal) inMF).getD();
	            	double mid = (params[1]+params[2])/2;
	            	double d = x-mid;
	            	params[0] = params[0] + d;
	            	params[1] = params[1] + d;
	            	params[2] = params[2] + d;
	            	params[3] = params[3] + d;
	            	this.inputMF = new T1MF_Trapezoidal(nameMF,params);
	            }
            } 
        	
        } else {
            throw new BadParameterException("The input value "+x+" was rejected "
                    + "as it is outside of the domain for this input: "
                    + "["+domain.getLeft()+", "+domain.getRight()+"].");
        }
    }    
    
    
    public MF_Interface getInputMF() {
		return inputMF;
	}
    
	public void setInputMF(T1MF_Interface inputMF) {
		if(domain.contains(inputMF.getPeak())) {
            this.x = inputMF.getPeak();
            this.inputMF = inputMF;
        	
        } else {
            throw new BadParameterException("The inputMF was rejected "
                    + "as it is outside of the domain for this input: "
                    + "["+domain.getLeft()+", "+domain.getRight()+"].");
        }
	}
	
	public String toString()
    {
        return "Input: '"+name+"' with value: "+x;
    }
	
}
