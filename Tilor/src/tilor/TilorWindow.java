/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tilor;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import tilor.SaveLoadProtocol.MissingDataException;

/**
 *
 * @author seanlanghi
 */
public class TilorWindow extends javax.swing.JFrame
    implements TilorDelegate
{   
    /*
     *  Human-added public static fields.
     */
    
    public static final long ID_NOT_SET = 0;
    
    /*
     *  Human-added private fields.
     */
    
    // Receives a pseudo-unique value (System.currentTimeMillis())
    // when the editor file is first saved.
    private long tileId;
    private static final FileNameExtensionFilter xmlOnly =
            new FileNameExtensionFilter("XML files (.xml)", "xml");
    
    /*
     *  Human-added (public) TilorDelegate protocol methods.
     */
    
    @Override
    public void framepathListDidUpdate(ArrayList<String> framepaths)
    {
        this.framesPanel.setFramesWithFramepathList(framepaths);
    }
    
    @Override
    public void frameIconAtIndexDidMoveToIndex(int oldIndex, int newIndex)
    {
        // This will be implemented in the future.
    }
    
    @Override
    public String getRelativePathFromProjectHomeDir(String path)
    {
        String homeDirPath = framesPanel.getHomeDir().getAbsolutePath();
        return path.replaceFirst( (homeDirPath + "/") , "");
    }
    
    @Override
    public String getAbsPathOfRelPath(String relPath)
    {
        String homeDirPath = framesPanel.getHomeDir().getAbsolutePath();
        return homeDirPath + "/" + relPath;
    }
    
    /*
     *  Human-added private instance methods.
     */
    
    private void generateTileId()
    {
        tileId = System.currentTimeMillis();
        propertiesPanel.setIdDisplay(tileId);
    }
    
    private void unsetTileId()
    {
        tileId = ID_NOT_SET;
        propertiesPanel.setIdDisplay(ID_NOT_SET);
    }
    
    private void setupMenuShortcuts()
    {
        // Determine the accelerator key of the current OS.
        // META_MASK refers to the Command key on a Mac.
        int acceleratorKey;
        if (System.getProperty("os.name").startsWith("Mac"))
            acceleratorKey = ActionEvent.META_MASK;
        else
            acceleratorKey = ActionEvent.CTRL_MASK;

        // Set up key shortcuts for the File menu.
        {
            fileMenu.setMnemonic(KeyEvent.VK_F);

            fileMenuItemNew.setAccelerator(KeyStroke.getKeyStroke(
                            KeyEvent.VK_N,
                            acceleratorKey));
            
            fileMenuItemSave.setMnemonic(KeyEvent.VK_S);
            fileMenuItemSave.setAccelerator(KeyStroke.getKeyStroke(
                             KeyEvent.VK_S,
                             acceleratorKey));
            
            fileMenuItemOpen.setMnemonic(KeyEvent.VK_O);
            fileMenuItemOpen.setAccelerator(KeyStroke.getKeyStroke(
                             KeyEvent.VK_O,
                             acceleratorKey));
        }
        
        // Set up key shortcuts for the Edit menu.
        {
            editMenu.setMnemonic(KeyEvent.VK_E);
            
            editMenuItemAddFramepaths.setMnemonic(KeyEvent.VK_A);
            editMenuItemAddFramepaths.setAccelerator(KeyStroke.getKeyStroke(
                            KeyEvent.VK_A,
                            acceleratorKey | ActionEvent.SHIFT_MASK));
            
            editMenuItemRemoveFramepaths.setMnemonic(KeyEvent.VK_M);
            editMenuItemRemoveFramepaths.setAccelerator(
                    KeyStroke.getKeyStroke(
                        KeyEvent.VK_R,
                        acceleratorKey | ActionEvent.ALT_MASK));
        }
    }
    
    // Composes and returns a Document object representing the tile's data in XML format.
    private Document xmlDocumentFromTileData()
            throws ParserConfigurationException
    {
        HashMap <String, Object> framepathsData = framepathsPanel.getData();
        HashMap <String, Object> propertiesData = propertiesPanel.getData();
        HashMap <String, Object> data = new HashMap<String,Object>();
        data.putAll(framepathsData);
        data.putAll(propertiesData);
        
        // Code adapted from:
        // http://www.mkyong.com/java/how-to-create-xml-file-in-java-dom/

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbFactory.newDocumentBuilder();

        // Root of tree: <tiledef>
        Document doc = db.newDocument();
        Element rootElement = doc.createElement("tiledef");
        doc.appendChild(rootElement);

        // <tile> element
        Element tile = doc.createElement("tile");
        rootElement.appendChild(tile);

        // <id> element
        Element idTag = doc.createElement("id");
        idTag.appendChild(doc.createTextNode(String.valueOf(this.tileId)));
        tile.appendChild(idTag);

        // <path> elements (that is, framepaths)
        Element framepathTag;
        ArrayList<String> framepathList = (ArrayList<String>) (data.get("framepaths"));
        if (framepathList != null)
        {
            for (String framepath : framepathList)
            {
                framepathTag = doc.createElement("path");
                framepathTag.appendChild(doc.createTextNode(framepath));
                tile.appendChild(framepathTag);
            }
        }

        // <psfx> element
        String psfxPath = (String) (data.get("psfxPath"));
        if (psfxPath != null)
        {
            Element psfxTag = doc.createElement("psfx");
            psfxTag.appendChild(doc.createTextNode(psfxPath));
            tile.appendChild(psfxTag);
        }

        // <asfx> element
        String asfxPath = (String) (data.get("asfxPath"));
        if (asfxPath != null)
        {
            Element asfxTag = doc.createElement("asfx");
            asfxTag.appendChild(doc.createTextNode(asfxPath));
            tile.appendChild(asfxTag);
        }

        // <passability> element
        String passabilityString = (String) data.get("passability");
        Element passabilityTag = doc.createElement("passability");
        passabilityTag.appendChild(doc.createTextNode(passabilityString));
        tile.appendChild(passabilityTag);

        // <animspeed> element
        String animSpeedString = (String) data.get("animSpeed");
        Element animSpeedTag = doc.createElement("anim-speed");
        animSpeedTag.appendChild(doc.createTextNode(animSpeedString));
        tile.appendChild(animSpeedTag);

        // <psfxthreshold> element (if it has been set)
        String psfxThresholdString = (String) (data.get("psfxThreshold"));
        if (psfxThresholdString != null)
        {
            Element psfxThresholdTag = doc.createElement("psfx-threshold");
            psfxThresholdTag.appendChild(doc.createTextNode(psfxThresholdString));
            tile.appendChild(psfxThresholdTag);
        }

        return doc;
    }
    
    // Given a parsed XML document, return a HashMap containing the tile data stored in the document.
    private HashMap <String, Object> tileDataFromXMLDocument(Document doc)
    {
        // Establish an interface for retrieving values from the document.
        Element treeRoot = doc.getDocumentElement();

        // Clean up unnecessary line breaks in our internal representation of the document.
        // Explanation: http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        treeRoot.normalize();

        // Create the HashMap that will hold the data contained in the tree.
        HashMap <String, Object> dataMap = new HashMap<String,Object>();

        // Prepare an array of the tag names used in the tree,
        // and an array of the property names used in Tilor's internal stores.
        // (Skip framepaths for now; we'll fetch those in a separate operation.)
        String [] propertyNamesInXmlTree = {"id", "psfx", "asfx", "passability",
                                            "anim-speed", "psfx-threshold"};

        String [] propertyNamesInHashMap = {"id", "psfxPath", "asfxPath", "passability",
                                            "animSpeed", "psfxThreshold"};
        
        // Create a shorthand for the <tile> tag, since we'll be querying it for its children repeatedly.
        Element tileTag = (Element) treeRoot.getElementsByTagName("tile").item(0);
        
        // Iterate through the array of property names,
        // copying each property's stored value from the tree to the map.
        
        NodeList retrievedTagList;
        Element retrievedTag;
        
        for (int i = 0; i < propertyNamesInXmlTree.length; i++)
        {
            // Grab the list of all tags that match the property name we're looking for.
            retrievedTagList = tileTag.getElementsByTagName(
                                               propertyNamesInXmlTree[i]);
            
            // Check that we have any tags for the given property name.  If not, skip to the next p-name.
            if (retrievedTagList.getLength() == 0)
                continue;
         
            // The tags we're handling in this "for" loop have at most one instance apiece,
            // so we don't have to iterate through retrievedTagList.
            retrievedTag = (Element) retrievedTagList.item(0);
            
            // Take the string value stored in the tag,
            // and put it in the HashMap along with the appropriate key.
            dataMap.put(propertyNamesInHashMap[i], retrievedTag.getTextContent());
        }
        
        // Now fetch the framepaths.  Do this manually, since there may be multiple of them.
        
        // First, grab the list of all tags called "path".
        NodeList fpNodeList = tileTag.getElementsByTagName("path");
        
        // Now, iterate through the node list, extracting the text content of each tag and adding it to an ArrayList.
        ArrayList<String> fpArrayList = new ArrayList<String>();
        
        for (int i = 0; i < fpNodeList.getLength(); i++)
        {
            fpArrayList.add(fpNodeList.item(i).getTextContent());
        }
        
        // The ArrayList now contains all the framepaths that were stored in the tree.
        // Add it to the map along with the appropriate key.
        dataMap.put("framepaths", fpArrayList);
        
        // Everything's now in the HashMap.  We're done.
        return dataMap;
    }
    
    private void saveDataToFile(File file)
    {
        // Create the file if it doesn't yet exist.
        if (! file.exists())
        {
            try
            {
                file.createNewFile();
                
                // If we're doing a "Save As..." kind of operation,
                // the cloned file should have a different ID than the original.
                tileId = ID_NOT_SET;
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
        // If the file does exist, confirm whether we want to overwrite it.
        else
        {   
            int shouldOverwrite = JOptionPane.showConfirmDialog(this,
                    "A file already exists there with the name \"" + file.getName() + "\".\nOverwrite it?",
                    "Overwrite file?", JOptionPane.OK_CANCEL_OPTION);
            
            if (shouldOverwrite == JOptionPane.NO_OPTION)
            {
                // The user decided not to overwrite the file.  Abandon the operation.
                return;
            }
        }
        
        // Write to the file, if possible.
        if (file.canWrite())
        {
            try
            {
                // Prepare the systems that will generate a nicely formatted XML file.
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer = tf.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                
                // Set the tile ID, if necessary, before we write our XML file.
                if (tileId == ID_NOT_SET)
                    generateTileId();
                
                // Generate an XML document from the tile data.
                Document doc = xmlDocumentFromTileData();
                
                // Write the document to a file.
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(file);
                transformer.transform(source, result);

                System.out.println("File saved.");   
            }
            catch (ParserConfigurationException pce)
            {
                pce.printStackTrace();
            }
            catch (TransformerException tfe)
            {
                tfe.printStackTrace();
            }
        }
        
        // If we couldn't write to the file, say so and quit.
        else
        {
            JOptionPane.showMessageDialog(this, "Save failed.  Couldn't write to file.  Try checking permissions.",
                    "Couldn't save file", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void loadDataFromFile(File file)
    {
        // Abandon the load if the file doesn't exist.
        if (! file.exists())
        {
            JOptionPane.showMessageDialog(this,
                    "The specified file doesn't seem to exist.\nNothing has been loaded.",
                    "No such file found", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Abandon the load if we can't read the file.
        if (! file.canRead())
        {
            JOptionPane.showMessageDialog(this,
                    "Tilor doesn't have permission to read that file.\nNothing has been loaded.",
                    "Couldn't open file", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Now that we're sure the file is an existing XML document that we can read...
        try
        {
            // Prepare the utilities that parse the document.
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            // Parse the document.
            Document doc = db.parse(file);
            
            // Load the data from the document.
            HashMap <String, Object> data = tileDataFromXMLDocument(doc);
            
            // Disseminate the data to the various parts of the program.
            
            // First, take care of our own responsibility: the tile's ID.
            {
                Object valueForKeyId = data.get("id");

                // If we didn't receive an ID value from the XML file, we need to abort the load.
                if (valueForKeyId == null ||
                        ! (valueForKeyId instanceof String))
                {
                    throw new MissingDataException(null);
                }

                // Adopt the loaded ID value, and adjust the ID indicator in the Properties panel.
                try
                {
                    this.tileId = Long.valueOf((String) data.get("id"));
                }
                catch (NumberFormatException nfe)
                {
                    // If we've got a bogus String (one that isn't a valid Long number) for our ID, blow the whistle.
                    throw new MissingDataException(null);
                }

                propertiesPanel.setIdDisplay(tileId);
            }
            
            // Pass the data map to the Framepaths and Properties panels for further loading.
            // These calls will throw MissingDataExceptions if any necessary data aren't in the map.
            framepathsPanel.loadDataFromMap(data);
            propertiesPanel.loadDataFromMap(data);
            
            // All data are loaded.  We're done.
            return;
        }
        catch (MissingDataException mde)
        {
            JOptionPane.showMessageDialog(this,
                    "The XML file doesn't seem to be a complete Tilor file.\nNothing has been loaded.",
                    "Load failed", JOptionPane.WARNING_MESSAGE);
            
            for (String datum : mde.getMissingData())
                System.out.println(datum);
            
            // Drop any data already loaded.
            loadDefaults();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void loadDefaults()
    {
        unsetTileId();
        framepathsPanel.removeAllFramepaths();
        propertiesPanel.restoreDefaults();   
    }
    
    // -------------------------------------------------------------------------------- //
    // -------------------- Auto-generated code areas begin here. --------------------- //
    // -------------------------------------------------------------------------------- //

    /**
     * Creates new form TilorWindow
     */
    public TilorWindow()
    {
        this.tileId = ID_NOT_SET;
        initComponents();
        setupMenuShortcuts();
        framepathsPanel.setDelegate(this);
        framesPanel    .setDelegate(this);
        propertiesPanel.setDelegate(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        framesPanel = new tilor.FramesPanel();
        framepathsPanel = new tilor.FramepathsPanel();
        propertiesPanel = new tilor.PropertiesPanel();
        topMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        fileMenuItemNew = new javax.swing.JMenuItem();
        jSeparator = new javax.swing.JPopupMenu.Separator();
        fileMenuItemSave = new javax.swing.JMenuItem();
        fileMenuItemOpen = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        editMenuItemAddFramepaths = new javax.swing.JMenuItem();
        editMenuItemRemoveFramepaths = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        fileMenu.setText("File");

        fileMenuItemNew.setText("New");
        fileMenuItemNew.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                fileMenuItemNewActionPerformed(evt);
            }
        });
        fileMenu.add(fileMenuItemNew);
        fileMenu.add(jSeparator);

        fileMenuItemSave.setText("Save to XML...");
        fileMenuItemSave.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                fileMenuItemSaveActionPerformed(evt);
            }
        });
        fileMenu.add(fileMenuItemSave);

        fileMenuItemOpen.setText("Open from XML...");
        fileMenuItemOpen.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                fileMenuItemOpenActionPerformed(evt);
            }
        });
        fileMenu.add(fileMenuItemOpen);

        topMenuBar.add(fileMenu);

        editMenu.setText("Edit");

        editMenuItemAddFramepaths.setText("Add framepaths...");
        editMenuItemAddFramepaths.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                editMenuItemAddFramepathsActionPerformed(evt);
            }
        });
        editMenu.add(editMenuItemAddFramepaths);

        editMenuItemRemoveFramepaths.setText("Remove all framepaths");
        editMenuItemRemoveFramepaths.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                editMenuItemRemoveFramepathsActionPerformed(evt);
            }
        });
        editMenu.add(editMenuItemRemoveFramepaths);

        topMenuBar.add(editMenu);

        setJMenuBar(topMenuBar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(framepathsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(framesPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(propertiesPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(propertiesPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(0, 0, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, framesPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(framepathsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Remove all framepaths from the list.
    private void editMenuItemRemoveFramepathsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_editMenuItemRemoveFramepathsActionPerformed
    {//GEN-HEADEREND:event_editMenuItemRemoveFramepathsActionPerformed
        framepathsPanel.removeAllFramepaths();
    }//GEN-LAST:event_editMenuItemRemoveFramepathsActionPerformed

    // If the file is ready to be saved, ask for a save location, and attempt to write an XML document there.
    private void fileMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_fileMenuItemSaveActionPerformed
    {//GEN-HEADEREND:event_fileMenuItemSaveActionPerformed
        // Make sure at least one framepath is specified.
        ArrayList<String> framepathList =
                (ArrayList<String>) framepathsPanel.getData().get("framepaths");
        
        if (framepathList.isEmpty() || framepathList == null)
        {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null,
                    "Before you can save to XML, you need to specify "
                    + "at least one framepath.");
            return;
        }
        
        // Let the user specify the save location via a file chooser.
        JFileChooser saveChooser = new JFileChooser();
        saveChooser.setAcceptAllFileFilterUsed(false);
        saveChooser.setFileFilter(xmlOnly);
                
        File destinationFile;
      
        int dialogResult = saveChooser.showSaveDialog(this);
        if (dialogResult == JFileChooser.CANCEL_OPTION)
        {
            // User cancelled.  Abandon the whole operation.
            return;
        }
        
        // Get the file path entered by the user and guarantee that it ends with ".xml".
        destinationFile = saveChooser.getSelectedFile();
        String path = destinationFile.getPath();
        if (! path.endsWith(".xml"))
        {
            destinationFile = new File(path + ".xml");
        }
        
        // Save the data to the chosen destination.
        saveDataToFile(destinationFile);
    }//GEN-LAST:event_fileMenuItemSaveActionPerformed

    // Let the user browse for an XML file, and attempt to open it.
    private void fileMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_fileMenuItemOpenActionPerformed
    {//GEN-HEADEREND:event_fileMenuItemOpenActionPerformed
        // Present a file chooser of the "Open" variety.
        JFileChooser openChooser = new JFileChooser();
        openChooser.setAcceptAllFileFilterUsed(false);
        openChooser.setFileFilter(xmlOnly);
        
        File chosenFile;
        
        int dialogResult = openChooser.showOpenDialog(this);
        if (dialogResult == JFileChooser.CANCEL_OPTION)
        {
            // User cancelled.  Abandon the whole operation.
            return;
        }
        
        chosenFile = openChooser.getSelectedFile();
        
        // Load the data from the chosen file.
        loadDataFromFile(chosenFile);
    }//GEN-LAST:event_fileMenuItemOpenActionPerformed

    private void editMenuItemAddFramepathsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_editMenuItemAddFramepathsActionPerformed
    {//GEN-HEADEREND:event_editMenuItemAddFramepathsActionPerformed
        framepathsPanel.addFramepaths();
    }//GEN-LAST:event_editMenuItemAddFramepathsActionPerformed

    private void fileMenuItemNewActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_fileMenuItemNewActionPerformed
    {//GEN-HEADEREND:event_fileMenuItemNewActionPerformed
        loadDefaults();
    }//GEN-LAST:event_fileMenuItemNewActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TilorWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem editMenuItemAddFramepaths;
    private javax.swing.JMenuItem editMenuItemRemoveFramepaths;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem fileMenuItemNew;
    private javax.swing.JMenuItem fileMenuItemOpen;
    private javax.swing.JMenuItem fileMenuItemSave;
    private tilor.FramepathsPanel framepathsPanel;
    private tilor.FramesPanel framesPanel;
    private javax.swing.JPopupMenu.Separator jSeparator;
    private tilor.PropertiesPanel propertiesPanel;
    private javax.swing.JMenuBar topMenuBar;
    // End of variables declaration//GEN-END:variables
}
