package org.jboss.ejb3.examples.ch04.firstejb;

import javax.ejb.EJBObject;

/**
 * EJB 2.x remote component interface of the CalculatorEJB
 */
public interface CalculatorRemote extends CalculatorCommonBusiness, EJBObject {

}
