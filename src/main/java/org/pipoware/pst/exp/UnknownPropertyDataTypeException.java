/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

/**
 *
 * @author Franck Arnulfo
 */
class UnknownPropertyDataTypeException extends Exception {
  
  private final int propertyTypeValue;

  public UnknownPropertyDataTypeException(String message, int propertyTypeValue) {
    super(message);
    this.propertyTypeValue = propertyTypeValue;
  }

  public int getPropertyTypeValue() {
    return propertyTypeValue;
  }

}
