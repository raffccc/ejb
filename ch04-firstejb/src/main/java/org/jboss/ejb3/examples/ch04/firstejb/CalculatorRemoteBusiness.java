package org.jboss.ejb3.examples.ch04.firstejb;

/**
 * Extending the Common interface will come in handy when we want to add more
 * views exposing the same method (so we don't have to rewrite its definition)
 */
public interface CalculatorRemoteBusiness extends CalculatorCommonBusiness {

}
