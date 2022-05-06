/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unicenta.pos.util;

import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class HtmlTester2 {

  public static void main(String[] a)throws Exception {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JEditorPane editorPane = new JEditorPane();

    editorPane.setPage(new URL("https://unicenta.org/splasher.html"));
    // Test LocalFile 

    frame.add(new JScrollPane(editorPane));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setSize(800, 400);
    
    frame.setVisible(true);
  }

}
