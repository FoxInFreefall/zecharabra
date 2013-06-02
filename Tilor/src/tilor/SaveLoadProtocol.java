/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tilor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author seanlanghi
 */
public interface SaveLoadProtocol
{
    // Panels will implement this method such that they return a HashMap
    // containing key-value pairs for all the properties they wish to save.
    // The property keys should be strings specifying the name of each property,
    // and the values should be strings representing each property's value.
    // When a property is a collection of strings, the value should be the collection itself.
    public HashMap <String, Object> getData();
    
    // Panels will implement this method such that they scour the HashMap for
    // keys corresponding to the panels' stored properties, and then adopt the
    // values stored in the keys if those values are suitable.
    // Panels should throw exceptions if the values found are unsuitable
    // or if the HashMap is missing required entries.
    public void loadDataFromMap(HashMap <String, Object> map)
             throws MissingDataException;
    
    // Inner class for exceptions arising from load method.
    
    public class MissingDataException extends Exception
    {
        private ArrayList<String> missingData;
        
        public ArrayList<String> getMissingData()
        {
            return this.missingData;
        }
        
        public MissingDataException(ArrayList<String> missingData)
        {
            super();
            this.missingData = missingData;
        }
    }
    
}