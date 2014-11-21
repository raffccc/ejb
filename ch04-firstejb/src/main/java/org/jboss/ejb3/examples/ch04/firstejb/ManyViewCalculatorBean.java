package org.jboss.ejb3.examples.ch04.firstejb;

import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.LocalHome;
import javax.ejb.Remote;
import javax.ejb.RemoteHome;
import javax.ejb.Stateless;

/**
 * Bean implementation class of the CalculatorEJB which exposes local 
 * and remote business and component views, as well as an EJB 3.1
 * no-interface view
 */
@Stateless
@Local(CalculatorLocalBusiness.class)
@Remote(CalculatorRemoteBusiness.class)
@LocalHome(CalculatorLocalHome.class)
@RemoteHome(CalculatorRemoteHome.class)
@LocalBean//No-interface view
public class ManyViewCalculatorBean extends CalculatorBeanBase implements CalculatorCommonBusiness {

}
