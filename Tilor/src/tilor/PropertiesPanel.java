/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tilor;

import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author seanlanghi
 */
public class PropertiesPanel extends javax.swing.JPanel
    implements SaveLoadProtocol
{
    /*
     *  Human-added fields.
     */
    
    private TilorDelegate delegate;

    private int passability = 0x0;
    private static final int RIGHT_MASK = 0x1,
            BOTTOM_MASK = 0x2,
            LEFT_MASK = 0x4,
            TOP_MASK = 0x8;
    private static final String
            BLOCKED_STRING_HORIZ = "\u2501\u2716\u2501",
            BLOCKED_STRING_VERT = "\u2503\u2716\u2503",
            OPEN_STRING_HORIZ = "\u2508\u21C5\u2508",
            OPEN_STRING_VERT = "\u250A\u21C4\u250A",
            DEFAULT_ID_TEXT = "Save the tile to generate an ID.";
    private String asfxPath = null,
                   psfxPath = null;
    private double animSpeed,
                   proximityThreshold;
    private boolean proximityThresholdSet;
    
    private static FileNameExtensionFilter AudioOnly =
                new FileNameExtensionFilter("Audio Files (wav, Au, ogg, mp3, AIFF, TTA, aac)",
                                            "wav", "Au", "ogg", "mp3", "AIFF", "TTA", "aac");

    /*
     *  Human-added protocol implementation.
     */

    @Override
    public HashMap <String, Object> getData()
    {
        HashMap <String, Object> data = new HashMap<String,Object>();
        
        // Add each property to the map, if the property has been set.
        
        data.put("passability", Integer.toHexString(passability));
        
        data.put("animSpeed", String.valueOf(animSpeed));
        
        if (asfxPath != null)
            data.put("asfxPath", asfxPath);
        
        if (psfxPath != null)
            data.put("psfxPath", psfxPath);
        
        if (proximityThresholdSet)
            data.put("psfxThreshold", String.valueOf(proximityThreshold));
        
        return data;
    }
    
    @Override
    public void loadDataFromMap(HashMap <String, Object> map)
             throws MissingDataException, NumberFormatException
    {
        // If the map is missing optional properties, we should use the defaults instead.
        restoreDefaults();
        
        ArrayList<String> absenteeReport = new ArrayList<String>();
        
        // First see if the map we're loading from has what we're looking for.
        
        Object valueForKeyPassability = map.get("passability");
        if (valueForKeyPassability == null ||
                ! (valueForKeyPassability instanceof String))
        {
            absenteeReport.add("passability");
        }
        
        Object valueForKeyAnimSpeed = map.get("animSpeed");
        if (valueForKeyAnimSpeed == null ||
                ! (valueForKeyAnimSpeed instanceof String))
        {
            absenteeReport.add("animSpeed");
        }
        
        // We don't need to validate these right away.  They're optional properties.
        Object valueForKeyAsfxPath = map.get("asfxPath");
        Object valueForKeyPsfxPath = map.get("psfxPath");
        Object valueForKeyPsfxProximityThreshold = map.get("psfxThreshold");
        
        if (valueForKeyPsfxProximityThreshold != null)
        {
            proximityThresholdSet = true;
        }
        
        // If we've caught any missing (required) data, 
        if (! absenteeReport.isEmpty())
            throw new MissingDataException(absenteeReport);
        
        // Try to set the number values.
        // This might throw an exception if one of the tags contains an unparseable string.
        {
            setPassability(Integer.parseInt( (String)valueForKeyPassability, 16 ));
            updatePassabilityDisplay();

            animSpeed = Double.valueOf    ( (String)valueForKeyAnimSpeed );
            animSpeedField.setText        ( (String)valueForKeyAnimSpeed );
            
            if (proximityThresholdSet)
            {
                proximityThreshold = Double.valueOf ( (String)valueForKeyPsfxProximityThreshold );
                proximityField.setText              ( (String)valueForKeyPsfxProximityThreshold );
            }
        }
        
        // Set the audio file paths, if we have any.
        
        if (valueForKeyAsfxPath != null)
        {
            asfxPath = (String)valueForKeyAsfxPath;
            activeAudioField.setText(asfxPath);
        }
        
        if (valueForKeyPsfxPath != null)
        {
            psfxPath = (String)valueForKeyPsfxPath;
            passiveAudioField.setText(psfxPath);
        }
    }
    
    /*
     *  Human-added public instance methods.
     */
    
    public void restoreDefaults()
    {
        passability = 0;
        updatePassabilityDisplay();
        
        animSpeed = 1.0;
        animSpeedField.setText("");
        
        asfxPath = null;
        psfxPath = null;
        activeAudioField.setText("");
        passiveAudioField.setText("");
        
        proximityThreshold = 0;
        proximityThresholdSet = false;
        proximityField.setText("");
    }
    
    public boolean setPassability(int passability)
    {
        if (passability > 0xF) {
            return false;
        }

        this.passability = passability;
        return true;
    }

    public int getPassability()
    {
        return passability;
    }
    
    public void setDelegate(TilorDelegate delegate)
    {
        this.delegate = delegate;
    }
    
    public TilorDelegate getDelegate()
    {
        return delegate;
    }
    
    // Sets the text displayed in the idLabel field.
    public void setIdDisplay(long id)
    {
        if (id == TilorWindow.ID_NOT_SET)
        {
            idLabel.setText(DEFAULT_ID_TEXT);
            return;   
        }
        
        idLabel.setText(String.valueOf(id));
    }
    
    /*
     *  Human-added private instance methods.
     */
    
    // Called after a change in passability.  Modifies visual indicators to reflect stored value.
    private void updatePassabilityDisplay()
    {
        if ((passability & RIGHT_MASK) == 0) {
            rightSideBoxLabel.setText(BLOCKED_STRING_VERT);
            rightButton.setSelected(false);
        } else {
            rightSideBoxLabel.setText(OPEN_STRING_VERT);
            rightButton.setSelected(true);
        }

        if ((passability & BOTTOM_MASK) == 0) {
            bottomSideBoxLabel.setText(BLOCKED_STRING_HORIZ);
            bottomButton.setSelected(false);
        } else {
            bottomSideBoxLabel.setText(OPEN_STRING_HORIZ);
            bottomButton.setSelected(true);
        }

        if ((passability & LEFT_MASK) == 0) {
            leftSideBoxLabel.setText(BLOCKED_STRING_VERT);
            leftButton.setSelected(false);
        } else {
            leftSideBoxLabel.setText(OPEN_STRING_VERT);
            leftButton.setSelected(true);
        }

        if ((passability & TOP_MASK) == 0) {
            topSideBoxLabel.setText(BLOCKED_STRING_HORIZ);
            topButton.setSelected(false);
        } else {
            topSideBoxLabel.setText(OPEN_STRING_HORIZ);
            topButton.setSelected(true);
        }
    }

    // -------------------------------------------------------------------------------- //
    // -------------------- Auto-generated code areas begin here. --------------------- //
    // -------------------------------------------------------------------------------- //
    
    /**
     * Creates new form PropertiesPanel
     */
    public PropertiesPanel()
    {
        this.proximityThresholdSet = false;
        this.proximityThreshold = -1;
        this.animSpeed = 1.0;
        initComponents();
        updatePassabilityDisplay();
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

        jToolBar1 = new javax.swing.JToolBar();
        passabilityLabel = new javax.swing.JLabel();
        topSideBoxLabel = new javax.swing.JLabel();
        topButton = new javax.swing.JToggleButton();
        leftSideBoxLabel = new javax.swing.JLabel();
        rightSideBoxLabel = new javax.swing.JLabel();
        leftButton = new javax.swing.JToggleButton();
        rightButton = new javax.swing.JToggleButton();
        bottomSideBoxLabel = new javax.swing.JLabel();
        bottomButton = new javax.swing.JToggleButton();
        propertiesLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        animSpeedField = new javax.swing.JTextField();
        animSpeedLabel = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        activeAudioField = new javax.swing.JTextField();
        activeAudioLabel = new javax.swing.JLabel();
        activeAudioBrowseButton = new javax.swing.JButton();
        activeAudioRemoveButton = new javax.swing.JButton();
        passiveAudioLabel = new javax.swing.JLabel();
        passiveAudioField = new javax.swing.JTextField();
        passiveAudioBrowseButton = new javax.swing.JButton();
        passiveAudioRemoveButton = new javax.swing.JButton();
        animSpeedChangeButton = new javax.swing.JButton();
        proximityLabel = new javax.swing.JLabel();
        proximitySetButton = new javax.swing.JButton();
        proximityField = new javax.swing.JTextField();
        idExplanatoryLabel = new javax.swing.JLabel();
        idLabel = new javax.swing.JLabel();
        proximityRemoveButton = new javax.swing.JButton();

        jToolBar1.setRollover(true);

        setBackground(new java.awt.Color(100, 100, 200));

        passabilityLabel.setText("Passability");

        topSideBoxLabel.setText("\u2501");

        topButton.setText("Top");
        topButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                topButtonActionPerformed(evt);
            }
        });

        leftSideBoxLabel.setText("\u2503");

        rightSideBoxLabel.setText("\u2503");

        leftButton.setText("Left");
        leftButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                leftButtonActionPerformed(evt);
            }
        });

        rightButton.setText("Right");
        rightButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                rightButtonActionPerformed(evt);
            }
        });

        bottomSideBoxLabel.setText("\u2501");

        bottomButton.setText("Bottom");
        bottomButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bottomButtonActionPerformed(evt);
            }
        });

        propertiesLabel.setText("Tile Properties");

        animSpeedField.setEditable(false);
        animSpeedField.setText("1.0");

        animSpeedLabel.setText("Animation speed (fps):");

        activeAudioField.setEditable(false);

        activeAudioLabel.setText("Active audio:");

        activeAudioBrowseButton.setText("Browse...");
        activeAudioBrowseButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                activeAudioBrowseButtonActionPerformed(evt);
            }
        });

        activeAudioRemoveButton.setText("\u2718");
        activeAudioRemoveButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                activeAudioRemoveButtonActionPerformed(evt);
            }
        });

        passiveAudioLabel.setText("Passive audio:");

        passiveAudioField.setEditable(false);

        passiveAudioBrowseButton.setText("Browse...");
        passiveAudioBrowseButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                passiveAudioBrowseButtonActionPerformed(evt);
            }
        });

        passiveAudioRemoveButton.setText("\u2718");
        passiveAudioRemoveButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                passiveAudioRemoveButtonActionPerformed(evt);
            }
        });

        animSpeedChangeButton.setText("Change...");
        animSpeedChangeButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                animSpeedChangeButtonActionPerformed(evt);
            }
        });

        proximityLabel.setText("Proximity threshold for passive audio:");

        proximitySetButton.setText("Set...");
        proximitySetButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                proximitySetButtonActionPerformed(evt);
            }
        });

        idExplanatoryLabel.setText("Unique tile ID:");

        idLabel.setText(DEFAULT_ID_TEXT);

        proximityRemoveButton.setText("\u2718");
        proximityRemoveButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                proximityRemoveButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(128, 128, 128)
                        .add(passabilityLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(idExplanatoryLabel))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(113, 113, 113)
                                .add(topSideBoxLabel))
                            .add(layout.createSequentialGroup()
                                .add(113, 113, 113)
                                .add(bottomSideBoxLabel))
                            .add(layout.createSequentialGroup()
                                .add(109, 109, 109)
                                .add(bottomButton))
                            .add(layout.createSequentialGroup()
                                .add(leftButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(leftSideBoxLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(rightSideBoxLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(rightButton))
                            .add(layout.createSequentialGroup()
                                .add(54, 54, 54)
                                .add(animSpeedLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(animSpeedField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 103, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(animSpeedChangeButton)))
                        .add(0, 0, Short.MAX_VALUE)))
                .add(65, 65, 65))
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator1)
                            .add(jSeparator2)
                            .add(jSeparator3)))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(activeAudioLabel)
                                    .add(passiveAudioLabel))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(activeAudioField)
                                    .add(passiveAudioField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE))
                                .add(46, 46, 46))
                            .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(proximityLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(proximityField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(4, 4, 4)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(activeAudioBrowseButton)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(activeAudioRemoveButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .add(layout.createSequentialGroup()
                                        .add(passiveAudioBrowseButton)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(passiveAudioRemoveButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .add(layout.createSequentialGroup()
                                .add(0, 0, Short.MAX_VALUE)
                                .add(proximitySetButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(proximityRemoveButton)))
                        .add(5, 5, 5)))
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .add(120, 120, 120)
                .add(topButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(idLabel)
                .add(14, 14, 14))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(propertiesLabel)
                .add(247, 247, 247))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(8, 8, 8)
                .add(propertiesLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(1, 1, 1)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(passabilityLabel)
                    .add(idExplanatoryLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(topButton)
                    .add(idLabel))
                .add(0, 0, 0)
                .add(topSideBoxLabel)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(leftButton)
                    .add(leftSideBoxLabel)
                    .add(rightSideBoxLabel)
                    .add(rightButton))
                .add(10, 10, 10)
                .add(bottomSideBoxLabel)
                .add(8, 8, 8)
                .add(bottomButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(animSpeedField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(animSpeedLabel)
                    .add(animSpeedChangeButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(activeAudioField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(activeAudioBrowseButton)
                        .add(activeAudioRemoveButton))
                    .add(layout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(activeAudioLabel)))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(passiveAudioLabel)
                    .add(passiveAudioField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(passiveAudioBrowseButton)
                    .add(passiveAudioRemoveButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(proximityLabel)
                    .add(proximitySetButton)
                    .add(proximityField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(proximityRemoveButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void topButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_topButtonActionPerformed
    {//GEN-HEADEREND:event_topButtonActionPerformed
        passability = passability ^ TOP_MASK;
        updatePassabilityDisplay();
    }//GEN-LAST:event_topButtonActionPerformed

    private void rightButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rightButtonActionPerformed
    {//GEN-HEADEREND:event_rightButtonActionPerformed
        passability = passability ^ RIGHT_MASK;
        updatePassabilityDisplay();
    }//GEN-LAST:event_rightButtonActionPerformed

    private void bottomButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bottomButtonActionPerformed
    {//GEN-HEADEREND:event_bottomButtonActionPerformed
        passability = passability ^ BOTTOM_MASK;
        updatePassabilityDisplay();
    }//GEN-LAST:event_bottomButtonActionPerformed

    private void leftButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_leftButtonActionPerformed
    {//GEN-HEADEREND:event_leftButtonActionPerformed
        passability = passability ^ LEFT_MASK;
        updatePassabilityDisplay();
    }//GEN-LAST:event_leftButtonActionPerformed

    private void activeAudioBrowseButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_activeAudioBrowseButtonActionPerformed
    {//GEN-HEADEREND:event_activeAudioBrowseButtonActionPerformed
        // Create a file chooser that will let the user browse for a single audio file.
        JFileChooser chooser;
        if (asfxPath == null)
            chooser = new JFileChooser();
        else
        {
            String absAsfxPath = this.delegate.getAbsPathOfRelPath(asfxPath);
            chooser = new JFileChooser(new File(absAsfxPath));
        }

        // Restrict selection to audio files.
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(AudioOnly);

        // Launch the file chooser.
        int browsingResult = chooser.showDialog(this.getParent(), "Choose");
        if (browsingResult == JFileChooser.APPROVE_OPTION)
        {
            // Save the chosen file's relative path.
            asfxPath = this.delegate.getRelativePathFromProjectHomeDir(
                                        chooser.getSelectedFile().getAbsolutePath());

            // Update the GUI to reflect the change.
            activeAudioField.setText(asfxPath);
            activeAudioField.updateUI();
        }
    }//GEN-LAST:event_activeAudioBrowseButtonActionPerformed

    private void passiveAudioBrowseButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_passiveAudioBrowseButtonActionPerformed
    {//GEN-HEADEREND:event_passiveAudioBrowseButtonActionPerformed
        // Create a file chooser that will let the user browse for a single audio file.
        JFileChooser chooser;
        if (psfxPath == null)
            chooser = new JFileChooser();
        else
        {
            String absPsfxPath = this.delegate.getAbsPathOfRelPath(psfxPath);
            chooser = new JFileChooser(new File(absPsfxPath));
        }

        // Restrict selection to audio files.
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(AudioOnly);

        // Launch the file chooser.
        int browsingResult = chooser.showDialog(this.getParent(), "Choose");
        if (browsingResult == JFileChooser.APPROVE_OPTION)
        {
            // Save the chosen file's path.
            psfxPath = this.delegate.getRelativePathFromProjectHomeDir(
                                        chooser.getSelectedFile().getAbsolutePath());

            // Update the GUI to reflect the change.
            passiveAudioField.setText(psfxPath);
            passiveAudioField.updateUI();
        }
    }//GEN-LAST:event_passiveAudioBrowseButtonActionPerformed

    private void activeAudioRemoveButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_activeAudioRemoveButtonActionPerformed
    {//GEN-HEADEREND:event_activeAudioRemoveButtonActionPerformed
        asfxPath = null;
        activeAudioField.setText(null);
        activeAudioField.updateUI();
    }//GEN-LAST:event_activeAudioRemoveButtonActionPerformed

    private void passiveAudioRemoveButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_passiveAudioRemoveButtonActionPerformed
    {//GEN-HEADEREND:event_passiveAudioRemoveButtonActionPerformed
        psfxPath = null;
        passiveAudioField.setText(null);
        passiveAudioField.updateUI();
    }//GEN-LAST:event_passiveAudioRemoveButtonActionPerformed

    private void animSpeedChangeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_animSpeedChangeButtonActionPerformed
    {//GEN-HEADEREND:event_animSpeedChangeButtonActionPerformed
        // Show a popup with a text field, prompting the user to enter a new value.
        String input =
        JOptionPane.showInputDialog(this, "Please enter a new value for this tile's animation speed:",
                "Animation Speed", JOptionPane.QUESTION_MESSAGE);
        
        // Use a regex to confirm whether the new value is a non-negative decimal number.
        if (input == null)
        {
            // Do nothing on cancel.
            return;
        }
        else if (input.matches("-?(\\d*\\.)?\\d+"))
        {
            double proposedValue = Double.parseDouble(input);
            if (proposedValue >= 0)
            {
                animSpeed = proposedValue;
                animSpeedField.setText(input);
                return;
            }
            // Continue beyond this block if user entered a negative value.
        }
        
        // If the typed value is not zero or a positive decimal number, sound a beep and don't commit the change.
        Toolkit.getDefaultToolkit().beep();
    }//GEN-LAST:event_animSpeedChangeButtonActionPerformed

    private void proximitySetButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_proximitySetButtonActionPerformed
    {//GEN-HEADEREND:event_proximitySetButtonActionPerformed
        // Show a popup with a text field, prompting the user to enter a new value.
        String input =
        JOptionPane.showInputDialog(this, "Please enter a new value for this tile's passive audio proximity threshold:",
                "Passive Audio Proximity Threshold", JOptionPane.QUESTION_MESSAGE);
        
        // Use a regex to confirm whether the new value is a non-negative decimal number.
        if (input == null)
        {
            // Do nothing on cancel.
            return;
        }
        else if (input.matches("-?(\\d*\\.)?\\d+"))
        {
            double proposedValue = Double.parseDouble(input);
            if (proposedValue >= 0)
            {
                proximityThreshold = proposedValue;
                proximityThresholdSet = true;
                proximityField.setText(input);
                return;
            }
            // Continue beyond this block if user entered a negative value.
        }
        
        // If the typed value is not zero or a positive decimal number, sound a beep and don't commit the change.
        Toolkit.getDefaultToolkit().beep();
    }//GEN-LAST:event_proximitySetButtonActionPerformed

    private void proximityRemoveButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_proximityRemoveButtonActionPerformed
    {//GEN-HEADEREND:event_proximityRemoveButtonActionPerformed
        proximityThreshold = -1;
        proximityThresholdSet = false;
        proximityField.setText("");
    }//GEN-LAST:event_proximityRemoveButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton activeAudioBrowseButton;
    private javax.swing.JTextField activeAudioField;
    private javax.swing.JLabel activeAudioLabel;
    private javax.swing.JButton activeAudioRemoveButton;
    private javax.swing.JButton animSpeedChangeButton;
    private javax.swing.JTextField animSpeedField;
    private javax.swing.JLabel animSpeedLabel;
    private javax.swing.JToggleButton bottomButton;
    private javax.swing.JLabel bottomSideBoxLabel;
    private javax.swing.JLabel idExplanatoryLabel;
    private javax.swing.JLabel idLabel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToggleButton leftButton;
    private javax.swing.JLabel leftSideBoxLabel;
    private javax.swing.JLabel passabilityLabel;
    private javax.swing.JButton passiveAudioBrowseButton;
    private javax.swing.JTextField passiveAudioField;
    private javax.swing.JLabel passiveAudioLabel;
    private javax.swing.JButton passiveAudioRemoveButton;
    private javax.swing.JLabel propertiesLabel;
    private javax.swing.JTextField proximityField;
    private javax.swing.JLabel proximityLabel;
    private javax.swing.JButton proximityRemoveButton;
    private javax.swing.JButton proximitySetButton;
    private javax.swing.JToggleButton rightButton;
    private javax.swing.JLabel rightSideBoxLabel;
    private javax.swing.JToggleButton topButton;
    private javax.swing.JLabel topSideBoxLabel;
    // End of variables declaration//GEN-END:variables
}
