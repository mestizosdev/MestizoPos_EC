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
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>

package com.openbravo.pos.forms;

import com.openbravo.format.Formats;
import com.openbravo.pos.instance.InstanceQuery;
import com.openbravo.pos.ticket.TicketInfo;
import lombok.extern.slf4j.Slf4j;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;

import javax.swing.*;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Locale;

@Slf4j
public class StartPOS {

  private StartPOS() {
  }

  public static boolean registerApp() {

    InstanceQuery i = null;
    try {
      i = new InstanceQuery();
      i.getAppMessage().restoreWindow();
      return false;
    } catch (RemoteException | NotBoundException e) {
      return true;
    }
  }

  public static void main(final String args[]) {

    SwingUtilities.invokeLater(() -> {
      if (!registerApp()) {
        System.exit(1);
      }

      AppConfig config = new AppConfig(args);

      config.load();

      String slang = config.getProperty("user.language");
      String scountry = config.getProperty("user.country");
      String svariant = config.getProperty("user.variant");
      if (slang != null
              && !slang.equals("")
              && scountry != null
              && svariant != null) {
        Locale.setDefault(new Locale(slang, scountry, svariant));
      }

      Formats.setIntegerPattern(config.getProperty("format.integer"));
      Formats.setDoublePattern(config.getProperty("format.double"));
      Formats.setCurrencyPattern(config.getProperty("format.currency"));
      Formats.setPercentPattern(config.getProperty("format.percent"));
      Formats.setDatePattern(config.getProperty("format.date"));
      Formats.setTimePattern(config.getProperty("format.time"));
      Formats.setDateTimePattern(config.getProperty("format.datetime"));

      // Set the look and feel.
      try {
        String lafname = config.getProperty("swing.defaultlaf");

        Object laf = Class.forName(lafname).newInstance();
        if (laf instanceof LookAndFeel) {
          UIManager.setLookAndFeel((LookAndFeel) laf);
        } else if (laf instanceof SubstanceSkin) {
          // Ultimate hack ;)
          String substanceClassName = "Substance"+lafname.split("skin.")[1].split("Skin")[0]+"LookAndFeel";
          String substancePackageName = "org.pushingpixels.substance.api.skin.";
          String substanceLookAndFeel = substancePackageName+substanceClassName;
          LookAndFeel lookAndFeel = (LookAndFeel) Class.forName(substanceLookAndFeel).newInstance();
          UIManager.setLookAndFeel(lookAndFeel);
          //SubstanceLookAndFeel.setSkin((SubstanceSkin) laf);
        }
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
        log.error("Cannot set Look and Feel", e.getMessage());
      }

      String hostname = config.getProperty("machine.hostname");
      TicketInfo.setHostname(hostname);

      String screenmode = config.getProperty("machine.screenmode");

      if ("fullscreen".equals(screenmode)) {
        JRootKiosk rootkiosk = new JRootKiosk();
        try {
          rootkiosk.initFrame(config);
        } catch (IOException ex) {
          log.error(ex.getMessage());
        }
      } else {
        JRootFrame rootframe = new JRootFrame();
        try {
          rootframe.initFrame(config);
        } catch (Exception ex) {
          log.error(ex.getMessage());
        }
      }
    });
  }
}