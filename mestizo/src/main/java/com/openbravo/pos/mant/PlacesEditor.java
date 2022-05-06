//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright © 2009-2020 uniCenta & previous Openbravo POS works
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

package com.openbravo.pos.mant;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.DataLogicSales;
import java.awt.Component;
import java.util.UUID;
import javax.swing.JPanel;

/**
 *
 * @author adrianromero
 */
public final class PlacesEditor extends JPanel implements EditorRecord {
    
    private final SentenceList m_sentfloor;
    private ComboBoxValModel m_FloorModel;
    
    private String m_sID;
    
    /** Creates new form PlacesEditor
     * @param dlSales
     * @param dirty */
    public PlacesEditor(DataLogicSales dlSales, DirtyManager dirty) {
        initComponents();
        
        m_sentfloor = dlSales.getFloorsList();
        m_FloorModel = new ComboBoxValModel();
        
        m_jName.getDocument().addDocumentListener(dirty);
        m_jSeats.getDocument().addDocumentListener(dirty);        
        m_jFloor.addActionListener(dirty);
        m_jX.getDocument().addDocumentListener(dirty);
        m_jY.getDocument().addDocumentListener(dirty);
        
        m_jWidth.getDocument().addDocumentListener(dirty);
        m_jHeight.getDocument().addDocumentListener(dirty);        
        
        writeValueEOF();
    }
    
    /**
     *
     * @throws BasicException
     */
    public void activate() throws BasicException {
        
        m_FloorModel = new ComboBoxValModel(m_sentfloor.list());
        m_jFloor.setModel(m_FloorModel);
    }
    
    /**
     *
     */
    @Override
    public void refresh() {
    }

    /**
     *
     */
    @Override
    public void writeValueEOF() {
        
        m_sID = null;
        m_jName.setText(null);
        m_jSeats.setText(null);        
        m_FloorModel.setSelectedKey(null);
        m_jX.setText(null);
        m_jY.setText(null);
        
        m_jWidth.setText(null);
        m_jHeight.setText(null);        

        m_jName.setEnabled(false);
        m_jSeats.setEnabled(false);        
        m_jFloor.setEnabled(false);
        m_jX.setEnabled(false);
        m_jY.setEnabled(false);

        m_jWidth.setEnabled(false);
        m_jHeight.setEnabled(false);        
    }

    /**
     *
     */
    @Override
    public void writeValueInsert() {

        m_sID = UUID.randomUUID().toString(); 
        m_jName.setText(null);
        m_jSeats.setText(null);        
        m_FloorModel.setSelectedKey(null);
        m_jX.setText(null);
        m_jY.setText(null);
        m_jWidth.setText(null);
        m_jHeight.setText(null);         
        
        m_jName.setEnabled(true);
        m_jSeats.setEnabled(true);        
        m_jFloor.setEnabled(true);
        m_jX.setEnabled(true);
        m_jY.setEnabled(true);
        m_jWidth.setEnabled(true);
        m_jHeight.setEnabled(true);          
    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueDelete(Object value) {
        
        Object[] place = (Object[]) value;
        m_sID = Formats.STRING.formatValue(place[0]);
        m_jName.setText(Formats.STRING.formatValue(place[1]));
        m_jSeats.setText(Formats.STRING.formatValue(place[2]));        
        m_jX.setText(Formats.INT.formatValue(place[3]));
        m_jY.setText(Formats.INT.formatValue(place[4]));
        m_FloorModel.setSelectedKey(place[5]);
        m_jWidth.setText(Formats.INT.formatValue(place[6]));
        m_jHeight.setText(Formats.INT.formatValue(place[7]));        
        
        m_jName.setEnabled(false);
        m_jSeats.setEnabled(false);        
        m_jFloor.setEnabled(false);
        m_jX.setEnabled(false);
        m_jY.setEnabled(false);
        m_jWidth.setEnabled(false);
        m_jHeight.setEnabled(false);         
    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueEdit(Object value) {
        
        Object[] place = (Object[]) value;
        m_sID = Formats.STRING.formatValue(place[0]);
        m_jName.setText(Formats.STRING.formatValue(place[1]));
        m_jSeats.setText(Formats.STRING.formatValue(place[2]));        
        m_jX.setText(Formats.INT.formatValue(place[3]));
        m_jY.setText(Formats.INT.formatValue(place[4]));
        m_FloorModel.setSelectedKey(place[5]);
        m_jWidth.setText(Formats.INT.formatValue(place[6]));
        m_jHeight.setText(Formats.INT.formatValue(place[7]));         

        m_jName.setEnabled(true);
        m_jSeats.setEnabled(true);        
        m_jFloor.setEnabled(true);
        m_jX.setEnabled(true);
        m_jY.setEnabled(true);
        m_jWidth.setEnabled(true);
        m_jHeight.setEnabled(true);         
    }

    /**
     *
     * @return
     * @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {
        Object[] place = new Object[8];
        place[0] = m_sID;
        place[1] = m_jName.getText();
        place[2] = m_jSeats.getText();        
        place[3] = Formats.INT.parseValue(m_jX.getText());
        place[4] = Formats.INT.parseValue(m_jY.getText());
        place[5] = m_FloorModel.getSelectedKey();
        place[6] = Formats.INT.parseValue(m_jWidth.getText());
        place[7] = Formats.INT.parseValue(m_jHeight.getText()); 
        return place;
    }
    
    /**
     *
     * @return
     */
    @Override
    public Component getComponent() {
        return this;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        m_jFloor = new javax.swing.JComboBox();
        m_jSeats = new javax.swing.JTextField();
        m_jWidth = new javax.swing.JTextField();
        m_jHeight = new javax.swing.JTextField();
        m_jX = new javax.swing.JTextField();
        m_jY = new javax.swing.JTextField();

        setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("label.name")); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(110, 30));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("label.placefloor")); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(110, 30));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText(AppLocal.getIntString("label.placepositionx")); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(110, 30));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText("Across");
        jLabel5.setPreferredSize(new java.awt.Dimension(0, 30));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText("Down");
        jLabel3.setPreferredSize(new java.awt.Dimension(0, 30));

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(102, 102, 102));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jLabel7.setText(bundle.getString("message.places")); // NOI18N
        jLabel7.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jLabel7.setMinimumSize(new java.awt.Dimension(50, 40));
        jLabel7.setOpaque(true);
        jLabel7.setPreferredSize(new java.awt.Dimension(489, 40));

        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setText(AppLocal.getIntString("label.seats")); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(110, 30));

        jLabel9.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel9.setText(AppLocal.getIntString("label.btnwidth")); // NOI18N
        jLabel9.setPreferredSize(new java.awt.Dimension(110, 30));

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText(AppLocal.getIntString("label.placepositiony")); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(110, 30));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setText(AppLocal.getIntString("label.btnheight")); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(110, 30));

        m_jName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jName.setPreferredSize(new java.awt.Dimension(0, 30));

        m_jFloor.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jFloor.setPreferredSize(new java.awt.Dimension(0, 30));

        m_jSeats.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jSeats.setPreferredSize(new java.awt.Dimension(50, 30));

        m_jWidth.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jWidth.setPreferredSize(new java.awt.Dimension(50, 30));

        m_jHeight.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jHeight.setPreferredSize(new java.awt.Dimension(50, 30));

        m_jX.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jX.setPreferredSize(new java.awt.Dimension(50, 30));

        m_jY.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jY.setPreferredSize(new java.awt.Dimension(50, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jFloor, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_jHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jY, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(m_jX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jSeats, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jFloor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jSeats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(m_jY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(180, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JComboBox m_jFloor;
    private javax.swing.JTextField m_jHeight;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextField m_jSeats;
    private javax.swing.JTextField m_jWidth;
    private javax.swing.JTextField m_jX;
    private javax.swing.JTextField m_jY;
    // End of variables declaration//GEN-END:variables
    
}