package org.jboss.ejb3.examples.ch04.firstejb;

import javax.ejb.Local;
import javax.ejb.Stateless;

/**
 * Bean implementation class of the CalculatorEJB which exposes 
 * one local business view
 */
@Stateless
@Local(CalculatorLocalBusiness.class)
public class SimpleCalculatorBean extends CalculatorBeanBase implements	CalculatorCommonBusiness {

}
