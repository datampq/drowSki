/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drowskifree;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 * http://drow.today
 *
 * @author datampq
 */
public class tile {

    public int index;
    public String type;
    public int x;
    public int y;
    public int width;
    public int height;
    public int rotation;
    public BufferedImage img;
    public int i;
    public int j;
    public boolean selected = false;
    public Dimension dim;

    public tile(String type, Dimension dim, BufferedImage img, int index, int rotation, int i, int j) {
        this.img = img;
        this.dim = dim;
        this.i = i;
        this.j = j;
        this.rotation = rotation;
        this.type = type;
        width = img.getWidth();
        height = img.getHeight();
    }

}
