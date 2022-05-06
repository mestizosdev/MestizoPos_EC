/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unicenta.pos.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * HTML viewer with styles,* using the JEditorPane, HTMLTester, StyleSheet, and JFrame.
 * 
 */
public class HtmlTester
{
  public static void main(String[] args)
  {
    new HtmlTester();
  }
  
  public HtmlTester()
  {
    SwingUtilities.invokeLater(() -> {
        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(jEditorPane);
        
        HTMLEditorKit kit = new HTMLEditorKit();
        jEditorPane.setEditorKit(kit);
        
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
        styleSheet.addRule("h1 {color: blue;}");
        styleSheet.addRule("h2 {color: #ff0000;}");
        styleSheet.addRule("pre {font : 12px century gothic; color : black; background-color : #fafafa; }");
        
        String htmlString = "<html>\n"
                + "<body>\n"
                + "<h1>Welcome!</h1>\n"
                + "<h2>This is an H2 header</h2>\n"
                + "<p>This is some sample text</p>\n"
                + "<p><a href=\"https://unicenta.com/\">uniCenta site</a></p>\n"
                + "</body>\n";
        
        Document doc = kit.createDefaultDocument();
        jEditorPane.setDocument(doc);
        jEditorPane.setText(htmlString);
        
        JFrame j = new JFrame("Html Test");
        j.getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        j.setSize(new Dimension(300,200));
        
        j.setLocationRelativeTo(null);
        j.setVisible(true);
    });
  }
}
