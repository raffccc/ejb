package org.jboss.ejb3.examples.ch04.firstejb;

import javax.ejb.EJBLocalObject;

/**
 * EJB 2.x local component interface of the CalculatorEJB
 */
public interface CalculatorLocal extends CalculatorCommonBusiness, EJBLocalObject {

}
