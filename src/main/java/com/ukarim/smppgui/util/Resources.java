package com.ukarim.smppgui.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import javax.swing.ImageIcon;

public final class Resources {

  private Resources() {}

  private static byte[] load(String file) {
    try (var in = Resources.class.getResourceAsStream(file)) {
      var out = new ByteArrayOutputStream();
      byte[] buf = new byte[1024];
      int c;
      while ((c = in.read(buf)) != -1) {
        out.write(buf, 0, c);
      }
      return out.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("Cannot load resource file: " + file, e);
    }
  }

  public static String loadStr(String file) {
    byte[] b = load(file);
    return new String(b, StandardCharsets.UTF_8);
  }

  public static ImageIcon loadIcon(String file) {
    byte[] bytes = load(file);
    return new ImageIcon(bytes);
  }

  public static Image loadImage(String file) {
    byte[] bytes = load(file);
    return Toolkit.getDefaultToolkit().createImage(bytes);
  }
}
