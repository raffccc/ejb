package org.jboss.ejb3.examples.ch04.firstejb;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * The function of this class is to bring everything together and
 * define the EJB metadata.
 * 
 *  This is an SLSB, noted by the @Stateless annotation
 *  We're exposing a no-interface view, new to EJB 3.1, using @LocalBean.
 *  
 *  This could be configured using xml.
 *  This class can be treated as a POJO, then unit tested.
 */
@Stateless
@LocalBean
public class NoInterfaceViewCalculatorBean extends CalculatorBeanBase {

}