//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright © 2009-2020 uniCenta
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.config;

import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.Session;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.DriverWrapper;
import com.openbravo.pos.util.AltEncrypter;
import com.openbravo.pos.util.DirectoryEvent;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;

/**
 * @author Jack Gerrard
 * @author adrianromero
 */
@Slf4j
public class JPanelConfigDatabase extends javax.swing.JPanel implements PanelConfig {
    
    private final DirtyManager dirty = new DirtyManager();
    
    /** Creates new form JPanelConfigDatabase */
    public JPanelConfigDatabase() {
        
        initComponents();
        
        jtxtDbDriverLib.getDocument().addDocumentListener(dirty);
        jtxtDbDriver.getDocument().addDocumentListener(dirty);
        jbtnDbDriverLib.addActionListener(new DirectoryEvent(jtxtDbDriverLib));
        jcboDBDriver.addActionListener(dirty);
        jcboDBDriver.addItem("MySQL");
        jcboDBDriver.setSelectedIndex(0);
        multiDB.addActionListener(dirty);
        
// primary DB        
        jtxtDbName.getDocument().addDocumentListener(dirty);
        jtxtDbURL.getDocument().addDocumentListener(dirty);
        jtxtDbSchema.getDocument().addDocumentListener(dirty);        
        jtxtDbOptions.getDocument().addDocumentListener(dirty);
        jtxtDbPassword.getDocument().addDocumentListener(dirty);
        jtxtDbUser.getDocument().addDocumentListener(dirty);        
        jCBSchema.addActionListener(dirty);         
        
// secondary DB        
        jtxtDbName1.getDocument().addDocumentListener(dirty);        
        jtxtDbURL1.getDocument().addDocumentListener(dirty);
        jtxtDbSchema1.getDocument().addDocumentListener(dirty);                
        jtxtDbOptions1.getDocument().addDocumentListener(dirty);
        jtxtDbPassword1.getDocument().addDocumentListener(dirty);
        jtxtDbUser1.getDocument().addDocumentListener(dirty);      
        jCBSchema1.addActionListener(dirty);         
        
        jPanel1.setVisible(false);         
        jLblAlert.setVisible(true);
        jLblDBServerversion.setVisible(false);        
    }

    /**
     *
     * @return
     */
    @Override
    public boolean hasChanged() {
        return dirty.isDirty();
    }
    
    /**
     *
     * @return
     */
    @Override
    public Component getConfigComponent() {
        return this;
    }
   
    /**
     *
     * @param config
     */
    @Override
    public void loadProperties(AppConfig config) {
        
        multiDB.setSelected(Boolean.parseBoolean(config.getProperty("db.multi")));                

        jcboDBDriver.setSelectedItem(config.getProperty("db.engine"));
        jtxtDbDriverLib.setText(config.getProperty("db.driverlib"));
        jtxtDbDriver.setText(config.getProperty("db.driver"));

// primary DB              
        jtxtDbName.setText(config.getProperty("db.name"));
        jtxtDbURL.setText("jdbc:mysql://localhost:3306/");
        jtxtDbURL.setText(config.getProperty("db.URL"));    
        jtxtDbSchema.setText(config.getProperty("db.schema"));        
        jtxtDbOptions.setText(config.getProperty("db.options"));
        String sDBUser = config.getProperty("db.user");
        String sDBPassword = config.getProperty("db.password");        

        if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser);
            sDBPassword = cypher.decrypt(sDBPassword.substring(6));
        }        
        jtxtDbUser.setText(sDBUser);
        jtxtDbPassword.setText(sDBPassword);   

// secondary DB        
        jtxtDbName1.setText(config.getProperty("db1.name"));
        jtxtDbURL1.setText(config.getProperty("db1.URL"));
        jtxtDbSchema1.setText(config.getProperty("db1.schema"));                
        jtxtDbOptions1.setText(config.getProperty("db1.options"));        
        String sDBUser1 = config.getProperty("db1.user");
        String sDBPassword1 = config.getProperty("db1.password");        

        if (sDBUser1 != null && sDBPassword1 != null && sDBPassword1.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser1);
            sDBPassword1 = cypher.decrypt(sDBPassword1.substring(6));
        }        
        jtxtDbUser1.setText(sDBUser1);
        jtxtDbPassword1.setText(sDBPassword1);          
        
        dirty.setDirty(false);
    }
   
    /**
     *
     * @param config
     */
    @Override
    public void saveProperties(AppConfig config) {

// multi-db        
        config.setProperty("db.multi",Boolean.toString(multiDB.isSelected()));
        
        config.setProperty("db.engine", comboValue(jcboDBDriver.getSelectedItem()));
        config.setProperty("db.driverlib", jtxtDbDriverLib.getText());
        config.setProperty("db.driver", jtxtDbDriver.getText());

// primary DB
        config.setProperty("db.name", jtxtDbName.getText());
        config.setProperty("db.URL", jtxtDbURL.getText());
        config.setProperty("db.schema", jtxtDbSchema.getText());        
        config.setProperty("db.options", jtxtDbOptions.getText());
        config.setProperty("db.user", jtxtDbUser.getText());
        AltEncrypter cypher = new AltEncrypter("cypherkey" + jtxtDbUser.getText());       
        config.setProperty("db.password", "crypt:" + 
                cypher.encrypt(new String(jtxtDbPassword.getPassword())));

// secondary DB        
        config.setProperty("db1.name", jtxtDbName1.getText());        
        config.setProperty("db1.URL", jtxtDbURL1.getText());
        config.setProperty("db1.schema", jtxtDbSchema1.getText());                
        config.setProperty("db1.options", jtxtDbOptions1.getText());
        config.setProperty("db1.user", jtxtDbUser1.getText());
        cypher = new AltEncrypter("cypherkey" + jtxtDbUser1.getText());       
        config.setProperty("db1.password", "crypt:" + 
                cypher.encrypt(new String(jtxtDbPassword1.getPassword())));        

        dirty.setDirty(false);
    }

    private String comboValue(Object value) {
        return value == null ? "" : value.toString();
    }
    
    public void fillSchema() {
        /* Use existing session credentials but declare new session and connection 
         * to keep separated from current session instance as database could
         * be a different server
        */

        if (jCBSchema.getItemCount() >= 1 ) {
            jCBSchema.removeAllItems();
        }        
    
        try {
            String driverlib = jtxtDbDriverLib.getText();
            String driver = jtxtDbDriver.getText();
            String url = jtxtDbURL.getText();
            String user = jtxtDbUser.getText();
            String password = new String(jtxtDbPassword.getPassword());

            ClassLoader cloader = new URLClassLoader(new URL[]{new File(driverlib).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(driver, true, cloader).newInstance()));

            Session session1 =  new Session(url, user, password);
            Connection connection1 = session1.getConnection();
            ResultSet rs = connection1.getMetaData().getCatalogs();

            while (rs.next()) {
                jCBSchema.addItem(rs.getString("TABLE_CAT"));
            }
            
            jCBSchema.setEnabled(true);
            jCBSchema.setSelectedIndex(0);
            
        } catch (MalformedURLException | ClassNotFoundException | SQLException 
                | InstantiationException | IllegalAccessException ex) {
            log.error(ex.getMessage());
        }        
    }
    public void fillSchema1() {
        /* Use existing session credentials but declare new session and connection 
         * to keep separated from current session instance as database could
         * be a different server
        */

        if (jCBSchema1.getItemCount() >= 1 ) {
            jCBSchema1.removeAllItems();
        }        
        
        try {
            String driverlib = jtxtDbDriverLib.getText();
            String driver = jtxtDbDriver.getText();
            String url = jtxtDbURL1.getText();
            String user = jtxtDbUser1.getText();
            String password = new String(jtxtDbPassword1.getPassword());

            ClassLoader cloader = new URLClassLoader(new URL[]{new File(driverlib).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(driver, true, cloader).newInstance()));

            Session session1 =  new Session(url, user, password);
            Connection connection1 = session1.getConnection();
            ResultSet rs1 = connection1.getMetaData().getCatalogs();

            while (rs1.next()) {
                jCBSchema1.addItem(rs1.getString("TABLE_CAT"));
            }

            jCBSchema1.setEnabled(true);
            jCBSchema1.setSelectedIndex(0);            

        } catch (MalformedURLException | ClassNotFoundException | SQLException 
                | InstantiationException | IllegalAccessException ex) {
            log.error(ex.getMessage());
        }
    }    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        webPopOver1 = new com.alee.extended.window.WebPopOver();
        jLabel6 = new javax.swing.JLabel();
        jcboDBDriver = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        jtxtDbDriverLib = new javax.swing.JTextField();
        jbtnDbDriverLib = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jtxtDbDriver = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtDbURL = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtxtDbUser = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jtxtDbPassword = new javax.swing.JPasswordField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jLblDBName = new javax.swing.JLabel();
        jtxtDbName = new javax.swing.JTextField();
        LblMultiDB = new com.alee.laf.label.WebLabel();
        multiDB = new com.alee.extended.button.WebSwitch();
        jLblAlert = new javax.swing.JLabel();
        jPanelDB2 = new javax.swing.JPanel();
        jLblDbName1 = new javax.swing.JLabel();
        jtxtDbName1 = new javax.swing.JTextField();
        jLblDbURL1 = new javax.swing.JLabel();
        jtxtDbURL1 = new javax.swing.JTextField();
        jtxtDbUser1 = new javax.swing.JTextField();
        jLblDbUser1 = new javax.swing.JLabel();
        jLblDbPassword1 = new javax.swing.JLabel();
        jtxtDbPassword1 = new javax.swing.JPasswordField();
        jbtnConnect1 = new javax.swing.JButton();
        jbtnReset1 = new javax.swing.JButton();
        jLblDBServerversion1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jCBSchema1 = new javax.swing.JComboBox<>();
        jbtnSetDB1 = new javax.swing.JButton();
        jLblDbSchema1 = new javax.swing.JLabel();
        jLblDbOptions1 = new javax.swing.JLabel();
        jtxtDbSchema1 = new javax.swing.JTextField();
        jtxtDbOptions1 = new javax.swing.JTextField();
        jLblDBServerversion = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jCBSchema = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jtxtDbOptions = new javax.swing.JTextField();
        jbtnCreateDB = new javax.swing.JButton();
        jtxtDbSchema = new javax.swing.JTextField();
        jbtnSetDB = new javax.swing.JButton();
        jbtnConnect = new javax.swing.JButton();
        jbtnReset = new javax.swing.JButton();

        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(900, 500));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jLabel6.setText(bundle.getString("label.Database")); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(125, 30));

        jcboDBDriver.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jcboDBDriver.setToolTipText(bundle.getString("tooltip.config.db.dbtype")); // NOI18N
        jcboDBDriver.setPreferredSize(new java.awt.Dimension(160, 30));
        jcboDBDriver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboDBDriverActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel18.setText(AppLocal.getIntString("label.dbdriverlib")); // NOI18N
        jLabel18.setPreferredSize(new java.awt.Dimension(125, 30));

        jtxtDbDriverLib.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbDriverLib.setToolTipText(bundle.getString("tooltip.config.db.driverlib")); // NOI18N
        jtxtDbDriverLib.setPreferredSize(new java.awt.Dimension(500, 30));

        jbtnDbDriverLib.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/fileopen.png"))); // NOI18N
        jbtnDbDriverLib.setText("  ");
        jbtnDbDriverLib.setToolTipText(bundle.getString("tooltip.config.db.file")); // NOI18N
        jbtnDbDriverLib.setMaximumSize(new java.awt.Dimension(64, 32));
        jbtnDbDriverLib.setMinimumSize(new java.awt.Dimension(64, 32));
        jbtnDbDriverLib.setPreferredSize(new java.awt.Dimension(80, 30));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("label.DbDriver")); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(125, 30));

        jtxtDbDriver.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbDriver.setToolTipText(bundle.getString("tooltip.config.db.driverclass")); // NOI18N
        jtxtDbDriver.setPreferredSize(new java.awt.Dimension(150, 30));
        jtxtDbDriver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtDbDriverActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText(AppLocal.getIntString("label.DbURL")); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(125, 30));

        jtxtDbURL.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbURL.setToolTipText(bundle.getString("tooltip.config.db.url")); // NOI18N
        jtxtDbURL.setPreferredSize(new java.awt.Dimension(320, 30));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText(AppLocal.getIntString("label.DbUser")); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(125, 30));

        jtxtDbUser.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbUser.setText(bundle.getString("tooltip.config.db.user")); // NOI18N
        jtxtDbUser.setToolTipText(bundle.getString("tooltip.config.db.user")); // NOI18N
        jtxtDbUser.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText(AppLocal.getIntString("label.DbPassword")); // NOI18N
        jLabel4.setPreferredSize(new java.awt.Dimension(125, 30));

        jtxtDbPassword.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbPassword.setToolTipText(bundle.getString("tooltip.config.db.password")); // NOI18N
        jtxtDbPassword.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/uniCenta_logo_vert_100.png"))); // NOI18N
        jLabel5.setText(bundle.getString("message.DBDefault")); // NOI18N
        jLabel5.setToolTipText("");
        jLabel5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel5.setPreferredSize(new java.awt.Dimension(889, 120));
        jLabel5.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLblDBName.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLblDBName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLblDBName.setText(AppLocal.getIntString("label.DbName")); // NOI18N
        jLblDBName.setPreferredSize(new java.awt.Dimension(125, 30));

        jtxtDbName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbName.setToolTipText(bundle.getString("tooltip.config.db.name")); // NOI18N
        jtxtDbName.setPreferredSize(new java.awt.Dimension(160, 30));

        LblMultiDB.setText(AppLocal.getIntString("label.multidb")); // NOI18N
        LblMultiDB.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        LblMultiDB.setPreferredSize(new java.awt.Dimension(125, 30));

        multiDB.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        multiDB.setPreferredSize(new java.awt.Dimension(80, 30));
        multiDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multiDBActionPerformed(evt);
            }
        });

        jLblAlert.setBackground(new java.awt.Color(255, 0, 51));
        jLblAlert.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        jLblAlert.setForeground(new java.awt.Color(255, 255, 255));
        jLblAlert.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLblAlert.setText(bundle.getString("message.dbalert")); // NOI18N
        jLblAlert.setOpaque(true);
        jLblAlert.setPreferredSize(new java.awt.Dimension(570, 30));

        jLblDbName1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLblDbName1.setText(AppLocal.getIntString("label.DbName1")); // NOI18N
        jLblDbName1.setEnabled(false);
        jLblDbName1.setPreferredSize(new java.awt.Dimension(125, 30));

        jtxtDbName1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbName1.setToolTipText(bundle.getString("tooltip.config.db.name1")); // NOI18N
        jtxtDbName1.setEnabled(false);
        jtxtDbName1.setPreferredSize(new java.awt.Dimension(160, 30));

        jLblDbURL1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblDbURL1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLblDbURL1.setText(AppLocal.getIntString("label.DbURL")); // NOI18N
        jLblDbURL1.setEnabled(false);
        jLblDbURL1.setPreferredSize(new java.awt.Dimension(125, 30));

        jtxtDbURL1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbURL1.setToolTipText(bundle.getString("tooltip.config.db.url1")); // NOI18N
        jtxtDbURL1.setEnabled(false);
        jtxtDbURL1.setPreferredSize(new java.awt.Dimension(320, 30));
        jtxtDbURL1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtDbURL1ActionPerformed(evt);
            }
        });

        jtxtDbUser1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbUser1.setToolTipText(bundle.getString("tooltip.config.db.user1")); // NOI18N
        jtxtDbUser1.setEnabled(false);
        jtxtDbUser1.setPreferredSize(new java.awt.Dimension(160, 30));

        jLblDbUser1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblDbUser1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLblDbUser1.setText(AppLocal.getIntString("label.DbUser")); // NOI18N
        jLblDbUser1.setEnabled(false);
        jLblDbUser1.setPreferredSize(new java.awt.Dimension(125, 30));

        jLblDbPassword1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblDbPassword1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLblDbPassword1.setText(AppLocal.getIntString("label.DbPassword")); // NOI18N
        jLblDbPassword1.setEnabled(false);
        jLblDbPassword1.setPreferredSize(new java.awt.Dimension(125, 30));

        jtxtDbPassword1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbPassword1.setToolTipText(bundle.getString("tooltip.config.db.password1")); // NOI18N
        jtxtDbPassword1.setEnabled(false);
        jtxtDbPassword1.setPreferredSize(new java.awt.Dimension(160, 30));

        jbtnConnect1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnConnect1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btn1.png"))); // NOI18N
        jbtnConnect1.setText(bundle.getString("button.connect")); // NOI18N
        jbtnConnect1.setToolTipText(bundle.getString("tooltip.config.db.connect")); // NOI18N
        jbtnConnect1.setActionCommand(bundle.getString("Button.Test")); // NOI18N
        jbtnConnect1.setEnabled(false);
        jbtnConnect1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbtnConnect1.setPreferredSize(new java.awt.Dimension(160, 45));
        jbtnConnect1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnConnect1ActionPerformed(evt);
            }
        });

        jbtnReset1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnReset1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/reload.png"))); // NOI18N
        jbtnReset1.setToolTipText(AppLocal.getIntString("tooltip.config.db.reset1")); // NOI18N
        jbtnReset1.setEnabled(false);
        jbtnReset1.setPreferredSize(new java.awt.Dimension(80, 45));
        jbtnReset1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnReset1ActionPerformed(evt);
            }
        });

        jLblDBServerversion1.setBackground(new java.awt.Color(51, 204, 255));
        jLblDBServerversion1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLblDBServerversion1.setForeground(new java.awt.Color(255, 255, 255));
        jLblDBServerversion1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLblDBServerversion1.setOpaque(true);
        jLblDBServerversion1.setPreferredSize(new java.awt.Dimension(170, 30));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(850, 104));

        jCBSchema1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jCBSchema1.setToolTipText(bundle.getString("tooltip.config.db.schema1")); // NOI18N
        jCBSchema1.setPreferredSize(new java.awt.Dimension(160, 30));
        jCBSchema1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBSchema1ActionPerformed(evt);
            }
        });

        jbtnSetDB1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jbtnSetDB1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btn2.png"))); // NOI18N
        jbtnSetDB1.setText("SET");
        jbtnSetDB1.setToolTipText(bundle.getString("tooltip.config.db.databaseset1")); // NOI18N
        jbtnSetDB1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbtnSetDB1.setPreferredSize(new java.awt.Dimension(160, 45));
        jbtnSetDB1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSetDB1ActionPerformed(evt);
            }
        });

        jLblDbSchema1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblDbSchema1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLblDbSchema1.setText(AppLocal.getIntString("label.DBName")); // NOI18N
        jLblDbSchema1.setEnabled(false);
        jLblDbSchema1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLblDbSchema1.setPreferredSize(new java.awt.Dimension(125, 30));

        jLblDbOptions1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblDbOptions1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLblDbOptions1.setText(AppLocal.getIntString("label.DbOptions")); // NOI18N
        jLblDbOptions1.setEnabled(false);
        jLblDbOptions1.setPreferredSize(new java.awt.Dimension(125, 30));

        jtxtDbSchema1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbSchema1.setEnabled(false);
        jtxtDbSchema1.setPreferredSize(new java.awt.Dimension(250, 30));

        jtxtDbOptions1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbOptions1.setToolTipText(bundle.getString("tooltip.config.db.options")); // NOI18N
        jtxtDbOptions1.setEnabled(false);
        jtxtDbOptions1.setPreferredSize(new java.awt.Dimension(330, 30));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLblDbSchema1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCBSchema1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnSetDB1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLblDbOptions1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtDbOptions1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDbSchema1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(88, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCBSchema1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                        .addComponent(jbtnSetDB1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLblDbSchema1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLblDbOptions1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jtxtDbOptions1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtDbSchema1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelDB2Layout = new javax.swing.GroupLayout(jPanelDB2);
        jPanelDB2.setLayout(jPanelDB2Layout);
        jPanelDB2Layout.setHorizontalGroup(
            jPanelDB2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDB2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDB2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelDB2Layout.createSequentialGroup()
                        .addComponent(jLblDbName1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtDbName1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLblDbURL1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtDbURL1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLblDBServerversion1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelDB2Layout.createSequentialGroup()
                        .addComponent(jLblDbUser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtDbUser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLblDbPassword1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtDbPassword1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnConnect1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnReset1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanelDB2Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelDB2Layout.setVerticalGroup(
            jPanelDB2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDB2Layout.createSequentialGroup()
                .addGroup(jPanelDB2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLblDbName1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDbName1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelDB2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLblDbURL1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtDbURL1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLblDBServerversion1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelDB2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtDbPassword1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLblDbPassword1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnConnect1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnReset1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDbUser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLblDbUser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLblDBServerversion.setBackground(new java.awt.Color(51, 204, 255));
        jLblDBServerversion.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLblDBServerversion.setForeground(new java.awt.Color(255, 255, 255));
        jLblDBServerversion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLblDBServerversion.setOpaque(true);
        jLblDBServerversion.setPreferredSize(new java.awt.Dimension(170, 30));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText(AppLocal.getIntString("label.DBName")); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(125, 30));

        jCBSchema.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jCBSchema.setToolTipText(bundle.getString("tooltip.config.db.schema")); // NOI18N
        jCBSchema.setPreferredSize(new java.awt.Dimension(160, 30));
        jCBSchema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBSchemaActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText(AppLocal.getIntString("label.DbOptions")); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(125, 30));

        jtxtDbOptions.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbOptions.setToolTipText(bundle.getString("tooltip.config.db.options")); // NOI18N
        jtxtDbOptions.setPreferredSize(new java.awt.Dimension(330, 30));

        jbtnCreateDB.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnCreateDB.setText("CREATE DEFAULT");
        jbtnCreateDB.setToolTipText(bundle.getString("message.databasecreate")); // NOI18N
        jbtnCreateDB.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbtnCreateDB.setPreferredSize(new java.awt.Dimension(160, 45));
        jbtnCreateDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCreateDBActionPerformed(evt);
            }
        });

        jtxtDbSchema.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbSchema.setEnabled(false);
        jtxtDbSchema.setPreferredSize(new java.awt.Dimension(250, 30));

        jbtnSetDB.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jbtnSetDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btn2.png"))); // NOI18N
        jbtnSetDB.setText("SET");
        jbtnSetDB.setToolTipText(bundle.getString("tooltip.config.db.databaseset")); // NOI18N
        jbtnSetDB.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbtnSetDB.setPreferredSize(new java.awt.Dimension(160, 45));
        jbtnSetDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSetDBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jCBSchema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jbtnSetDB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jtxtDbSchema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jbtnCreateDB, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jtxtDbOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBSchema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDbOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnCreateDB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDbSchema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnSetDB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jbtnConnect.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jbtnConnect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/btn1.png"))); // NOI18N
        jbtnConnect.setText(bundle.getString("button.connect")); // NOI18N
        jbtnConnect.setToolTipText(bundle.getString("tooltip.config.db.connect")); // NOI18N
        jbtnConnect.setActionCommand(bundle.getString("Button.Test")); // NOI18N
        jbtnConnect.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbtnConnect.setPreferredSize(new java.awt.Dimension(160, 45));
        jbtnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnConnectActionPerformed(evt);
            }
        });

        jbtnReset.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/reload.png"))); // NOI18N
        jbtnReset.setToolTipText(AppLocal.getIntString("tooltip.config.db.reset")); // NOI18N
        jbtnReset.setPreferredSize(new java.awt.Dimension(80, 45));
        jbtnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnResetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelDB2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 880, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 880, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(LblMultiDB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(multiDB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jcboDBDriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxtDbUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxtDbPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtnConnect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtnReset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxtDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtnDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxtDbDriver, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLblDBName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtDbName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtDbURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLblDBServerversion, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLblAlert, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcboDBDriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jtxtDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jbtnDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jtxtDbDriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLblDBName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtDbName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtDbURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLblDBServerversion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLblAlert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtDbUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtDbPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtnConnect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LblMultiDB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(multiDB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelDB2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jbtnReset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtDbDriverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtDbDriverActionPerformed

    }//GEN-LAST:event_jtxtDbDriverActionPerformed

    private void jcboDBDriverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboDBDriverActionPerformed

        String dirname = System.getProperty("dirname.path");
        dirname = dirname == null ? "./" : dirname;
       
        if ("PostgreSQL".equals(jcboDBDriver.getSelectedItem())) {
            jtxtDbDriverLib.setText(new File(new File(dirname), "lib/postgresql-9.4-1208.jdbc4.jar").getAbsolutePath());
            jtxtDbDriver.setText("org.postgresql.Driver");
            jtxtDbURL.setText("jdbc:postgresql://localhost:5432/");            
            jtxtDbSchema.setText("unicentaopos");
            jtxtDbOptions.setText("");
        } else {
            jtxtDbDriverLib.setText(new File(new File(dirname), "lib/mysql-connector-java-5.1.39.jar").getAbsolutePath());
            jtxtDbDriver.setText("com.mysql.jdbc.Driver");            
            jtxtDbURL.setText("jdbc:mysql://localhost:3306/");
            jtxtDbSchema.setText("unicentaopos");                                    
            jtxtDbOptions.setText("?zeroDateTimeBehavior=convertToNull");
        }    
    }//GEN-LAST:event_jcboDBDriverActionPerformed

    private void jbtnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnConnectActionPerformed
        try {
            String driverlib = jtxtDbDriverLib.getText();
            String driver = jtxtDbDriver.getText();
            String url = jtxtDbURL.getText();
            String user = jtxtDbUser.getText();
            String password = new String(jtxtDbPassword.getPassword());

            ClassLoader cloader = new URLClassLoader(new URL[]{new File(driverlib).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(driver, true, cloader).newInstance()));

            Session session =  new Session(url, user, password);
            Connection connection = session.getConnection();

            boolean isValid;
            isValid = (connection == null) ? false : connection.isValid(1000);

            if (isValid) {
                JOptionPane.showMessageDialog(this, 
                        AppLocal.getIntString("message.databasesuccess"), 
                        "Connection Test", JOptionPane.INFORMATION_MESSAGE);
                fillSchema();
                jLblAlert.setVisible(false);
                jPanel1.setVisible(true);       
            } else {
                jLblAlert.setVisible(true);   
                jPanel1.setVisible(false);                
                JMessageDialog.showMessage(this, 
                        new MessageInf(MessageInf.SGN_WARNING, "Connection Error"));
            }
            
            ResultSet rs = connection.getMetaData().getCatalogs();            
            String SQL="SELECT LEFT(VERSION(),3)  ";                                   
            Statement stmt = (Statement) connection.createStatement();
                rs = stmt.executeQuery(SQL);
                rs.next();
            jLblDBServerversion.setVisible(true);        
            jLblDBServerversion.setText(" MySQL Server : " + rs.getString(1));
            
            if (!rs.getString(1).equals("5.7")) {
                jLblDBServerversion.setBackground(Color.RED);
                JOptionPane.showMessageDialog(this, 
                AppLocal.getIntString("message.databasefail"), 
                "Connection Test", JOptionPane.WARNING_MESSAGE);
            }

        } catch (InstantiationException | IllegalAccessException | MalformedURLException | ClassNotFoundException e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_WARNING, 
                            AppLocal.getIntString("message.databasedrivererror"), e));
        } catch (SQLException e) {
            jLblAlert.setVisible(true);   
            jLblDBServerversion.setText("");                
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_WARNING, 
                            AppLocal.getIntString("message.databaseconnectionerror"), e));            
        } catch (Exception e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_WARNING, "Unknown exception", e));
        }
    }//GEN-LAST:event_jbtnConnectActionPerformed

    private void jbtnConnect1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnConnect1ActionPerformed
/* Even though TEST & TEST1 could be consolidated into method am deliberately
 *        keeping this separate as plan is to also include alternative 
 *        posApps REST API for remote DB sync's        
*/
        try {
            String driverlib = jtxtDbDriverLib.getText();
            String driver = jtxtDbDriver.getText();
            String url = jtxtDbURL1.getText();
            String user = jtxtDbUser1.getText();
            String password = new String(jtxtDbPassword1.getPassword());

            ClassLoader cloader = new URLClassLoader(new URL[]{new File(driverlib).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(driver, true, cloader).newInstance()));

            Session session =  new Session(url, user, password);
            Connection connection = session.getConnection();

            boolean isValid;
            isValid = (connection == null) ? false : connection.isValid(1000);

            if (isValid) {
                JOptionPane.showMessageDialog(this, 
                        AppLocal.getIntString("message.databasesuccess"), 
                        "Connection Test", JOptionPane.INFORMATION_MESSAGE);
                fillSchema1();
                jLblAlert.setVisible(false);
            } else {
                jLblAlert.setVisible(true);                
                JMessageDialog.showMessage(this, 
                        new MessageInf(MessageInf.SGN_WARNING, "Connection Error"));
            }
            
            ResultSet rs = connection.getMetaData().getCatalogs();            
            String SQL="SELECT LEFT(VERSION(),3)  ";                                   
            Statement stmt = (Statement) connection.createStatement();
                rs = stmt.executeQuery(SQL);
                rs.next();
            jLblDBServerversion1.setText(" MySQL Server : " + rs.getString(1));

        } catch (InstantiationException | IllegalAccessException | MalformedURLException | ClassNotFoundException e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_WARNING, 
                            AppLocal.getIntString("message.databasedrivererror"), e));
        } catch (SQLException e) {
            jLblAlert.setVisible(true);   
            jLblDBServerversion1.setText("");                
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_WARNING, 
                            AppLocal.getIntString("message.databaseconnectionerror"), e));            
        } catch (Exception e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_WARNING, "Unknown exception", e));
        }
    }//GEN-LAST:event_jbtnConnect1ActionPerformed

    private void multiDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiDBActionPerformed
        if (multiDB.isSelected()) {
            jPanel1.setVisible(false);
            jPanel2.setVisible(true);            
            jPanelDB2.setVisible(true);
            jLblDbName1.setEnabled(true);
            jtxtDbName1.setEnabled(true);
            jLblDbOptions1.setEnabled(true);
            jtxtDbOptions1.setEnabled(true);  
            jLblDbURL1.setEnabled(true);
            jtxtDbURL1.setEnabled(true);            
            jLblDbSchema1.setEnabled(true);
//            jtxtDbSchema1.setEnabled(true);            
            jLblDbUser1.setEnabled(true);
            jtxtDbUser1.setEnabled(true);
            jLblDbPassword1.setEnabled(true);
            jtxtDbPassword1.setEnabled(true);
            jbtnConnect1.setEnabled(true);
            jbtnReset1.setEnabled(true);            
        } else {
            jPanel1.setVisible(true);            
            jPanel2.setVisible(false);                        
            jPanelDB2.setVisible(false);            
            jLblDbName1.setEnabled(false);
            jtxtDbName1.setEnabled(false);
            jLblDbOptions1.setEnabled(false);
            jtxtDbOptions1.setEnabled(false);  
            jLblDbURL1.setEnabled(false);
            jtxtDbURL1.setEnabled(false);
            jLblDbSchema1.setEnabled(false);
//            jtxtDbSchema1.setEnabled(false);              
            jLblDbUser1.setEnabled(false);
            jtxtDbUser1.setEnabled(false);
            jLblDbPassword1.setEnabled(false);
            jtxtDbPassword1.setEnabled(false);            
            jbtnConnect1.setEnabled(false);  
            jbtnReset1.setEnabled(false);
        }
    }//GEN-LAST:event_multiDBActionPerformed

    private void jbtnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnResetActionPerformed
        if (jCBSchema.getItemCount() >= 1 ) {
            jCBSchema.removeAllItems();
        } 

        String dirname = System.getProperty("dirname.path");
        dirname = dirname == null ? "./" : dirname;
        
        jtxtDbDriverLib.setText(new File(new File(dirname), "lib/mysql-connector-java-5.1.39.jar").getAbsolutePath());
        jtxtDbDriver.setText("com.mysql.jdbc.Driver");            

        jtxtDbName.setText("Main DB");
        jtxtDbURL.setText("jdbc:mysql://localhost:3306/");
        jtxtDbSchema.setText("unicentaopos");
        jtxtDbOptions.setText("?zeroDateTimeBehavior=convertToNull");
        jtxtDbUser.setText(null);        
        jtxtDbPassword.setText(null);  
        jLblDBServerversion.setText(null);
        jLblDBServerversion.setVisible(false);
        jPanel1.setVisible(false);
    }//GEN-LAST:event_jbtnResetActionPerformed

    private void jbtnReset1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnReset1ActionPerformed
        if (jCBSchema1.getItemCount() >= 1 ) {
            jCBSchema1.removeAllItems();
        } 
        
        jtxtDbName1.setText("Other DB");
        jtxtDbURL1.setText("jdbc:mysql://localhost:3306/");
        jtxtDbSchema1.setText("unicentaopos1");
        jtxtDbOptions1.setText("?zeroDateTimeBehavior=convertToNull");
        jtxtDbUser1.setText(null);        
        jtxtDbPassword1.setText(null);
        jLblDBServerversion1.setText(null);
        jPanel2.setVisible(false);        
    }//GEN-LAST:event_jbtnReset1ActionPerformed

    private void jCBSchemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBSchemaActionPerformed
/*
        if (jCBSchema.getItemCount() > 0 ) {
            String selected = jCBSchema.getSelectedItem().toString();            
            if(!selected.equals(null)) {
                jtxtDbSchema.setText(selected);            
            }
        }
*/        
    }//GEN-LAST:event_jCBSchemaActionPerformed

    private void jCBSchema1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBSchema1ActionPerformed
/*        
        if (jCBSchema1.getItemCount() > 0 ) {
            String selected1 = jCBSchema1.getSelectedItem().toString();            
            if(!selected1.equals(null)) {
                jtxtDbSchema1.setText(selected1);            
            }
        }
*/
    }//GEN-LAST:event_jCBSchema1ActionPerformed

    private void jtxtDbURL1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtDbURL1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtDbURL1ActionPerformed

    private void jbtnCreateDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCreateDBActionPerformed
        try {
            String driverlib = jtxtDbDriverLib.getText();
            String driver = jtxtDbDriver.getText();
            String url = jtxtDbURL.getText();
            String user = jtxtDbUser.getText();
            String password = new String(jtxtDbPassword.getPassword());

            ClassLoader cloader = new URLClassLoader(new URL[]{new File(driverlib).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(driver, true, cloader).newInstance()));

            Session session =  new Session(url, user, password);
            Connection connection = session.getConnection();

            boolean isValid;
            isValid = (connection == null) ? false : connection.isValid(1000);

            if (isValid) {
                String SQL="CREATE DATABASE if not exists unicentaopos";                                   
                Statement stmt = (Statement) connection.createStatement();
                stmt.executeUpdate(SQL);

                fillSchema();
                jLblAlert.setVisible(false);
                jtxtDbSchema.setText("unicentaopos");
                
                JOptionPane.showMessageDialog(this,
                        AppLocal.getIntString("message.createdefaultdb"), 
                        "Create Default Database", JOptionPane.INFORMATION_MESSAGE);
            } else {
                jLblAlert.setVisible(true);
                jPanel1.setVisible(false);
                JMessageDialog.showMessage(this, 
                        new MessageInf(MessageInf.SGN_WARNING, "Connection Error"));
            }

        } catch (MalformedURLException ex) {
            log.error(ex.getMessage());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_WARNING, 
                            AppLocal.getIntString("message.databasedrivererror"), e));
        } catch (SQLException e) {
            jLblAlert.setVisible(true);   
            jLblDBServerversion.setText("");                
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_WARNING, 
                            AppLocal.getIntString("message.databaseconnectionerror"), e));            
        } catch (Exception e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_WARNING, "Unknown exception", e));
        }
    }//GEN-LAST:event_jbtnCreateDBActionPerformed

    private void jbtnSetDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSetDBActionPerformed

        if (jCBSchema.getItemCount() > 0 ) {
            String selected = jCBSchema.getSelectedItem().toString();            
            if(selected != null) {
                jtxtDbSchema.setText(selected);            
            }
        }
    }//GEN-LAST:event_jbtnSetDBActionPerformed

    private void jbtnSetDB1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSetDB1ActionPerformed

        if (jCBSchema1.getItemCount() > 0 ) {
            String selected1 = jCBSchema1.getSelectedItem().toString();            
            if(selected1 != null) {
                jtxtDbSchema1.setText(selected1);            
            }
        }
    }//GEN-LAST:event_jbtnSetDB1ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.alee.laf.label.WebLabel LblMultiDB;
    private javax.swing.JComboBox<String> jCBSchema;
    private javax.swing.JComboBox<String> jCBSchema1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLblAlert;
    private javax.swing.JLabel jLblDBName;
    private javax.swing.JLabel jLblDBServerversion;
    private javax.swing.JLabel jLblDBServerversion1;
    private javax.swing.JLabel jLblDbName1;
    private javax.swing.JLabel jLblDbOptions1;
    private javax.swing.JLabel jLblDbPassword1;
    private javax.swing.JLabel jLblDbSchema1;
    private javax.swing.JLabel jLblDbURL1;
    private javax.swing.JLabel jLblDbUser1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelDB2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton jbtnConnect;
    private javax.swing.JButton jbtnConnect1;
    private javax.swing.JButton jbtnCreateDB;
    private javax.swing.JButton jbtnDbDriverLib;
    private javax.swing.JButton jbtnReset;
    private javax.swing.JButton jbtnReset1;
    private javax.swing.JButton jbtnSetDB;
    private javax.swing.JButton jbtnSetDB1;
    private javax.swing.JComboBox jcboDBDriver;
    private javax.swing.JTextField jtxtDbDriver;
    private javax.swing.JTextField jtxtDbDriverLib;
    private javax.swing.JTextField jtxtDbName;
    private javax.swing.JTextField jtxtDbName1;
    private javax.swing.JTextField jtxtDbOptions;
    private javax.swing.JTextField jtxtDbOptions1;
    private javax.swing.JPasswordField jtxtDbPassword;
    private javax.swing.JPasswordField jtxtDbPassword1;
    private javax.swing.JTextField jtxtDbSchema;
    private javax.swing.JTextField jtxtDbSchema1;
    private javax.swing.JTextField jtxtDbURL;
    private javax.swing.JTextField jtxtDbURL1;
    private javax.swing.JTextField jtxtDbUser;
    private javax.swing.JTextField jtxtDbUser1;
    private com.alee.extended.button.WebSwitch multiDB;
    private com.alee.extended.window.WebPopOver webPopOver1;
    // End of variables declaration//GEN-END:variables
   
}