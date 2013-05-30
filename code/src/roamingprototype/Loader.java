/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roamingprototype;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author Foxtrot <aeseligman@gmail.com>
 */
public abstract class Loader implements Settings
{    
    public void load(String levelPath)
    {
        InputStream input = getClass().getResourceAsStream("/resources" + levelPath);
        try 
        {
            Document doc = Jsoup.parse(input, "UTF-8", "");

            Element currentElement = null;
            if(!doc.children().isEmpty())
            {
                currentElement = doc.child(0);
            }

            while(doc != null)
            {
                processOpenTag(currentElement.tagName(), currentElement.attributes());
                processContent(currentElement.tagName(), currentElement.ownText());

                //Try to go deeper
                if(!currentElement.children().isEmpty())
                {
                    currentElement = currentElement.child(0);
                }

                //Otherwise, go to next sibling or go up
                else
                {
                    while(true)
                    {
                        //Try to go to the next sibling
                        if(currentElement.nextElementSibling() != null)
                        {
                            //Close this tag
                            processCloseTag(currentElement.tagName());

                            //Pass to next tag
                            currentElement = currentElement.nextElementSibling();
                            break;
                        }

                        //Try to go up
                        else if(currentElement.parent() != null)
                        {
                            //Close this tag
                            processCloseTag(currentElement.tagName());

                            //Go to parent, close that
                            currentElement = currentElement.parent();
                        }

                        //Otherwise, there's no where else to go
                        else
                        {
                            doc = null;
                            currentElement = null;
                            break;
                        }
                    }
                }
            }
        }
        catch (IOException ex) { Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex); }
    }
    
    public abstract void processOpenTag(String tagName, Attributes atts);
    public abstract void processContent(String tagName, String text);
    public abstract void processCloseTag(String tagName);
}
