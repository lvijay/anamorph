/*
 * Anamorpher — generate cylindrical anamorph of a given image.
 * Copyright (C) 2014, 2017 Vijay Lakshminarayanan <lvijay@gmail.com>.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package anam.anamorph;

import static java.lang.Math.round;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

import anam.definitions.Cylinder;
import anam.definitions.Point3D;

public class Anamorpher {
    private final Cylinder c;

    public Anamorpher(Cylinder o) {
        this.c = o;
    }

    public BufferedImage anamorph(BufferedImage img, Point3D eye) {
        Point3D v = eye;

        int lowx = Integer.MAX_VALUE, lowy = Integer.MAX_VALUE;
        int maxx = Integer.MIN_VALUE, maxy = Integer.MIN_VALUE;

        int picWidth = img.getWidth();
        int picLenth = img.getHeight();
        int[][] rgb = toPixelArray(img);
        int wadjuster = picWidth / 2;
        int hadjuster = 0;
        int[][] aposx = new int[picWidth][picLenth];
        int[][] aposy = new int[picWidth][picLenth];
        Reflection r = new Reflection();

        for (int i = 0; i < picWidth; ++i) {
            for (int j = 0; j < picLenth; ++j) {
                Point3D p = new Point3D(0, i - wadjuster, j + hadjuster);
                Point3D rp = c.accept(r, p, v);

                if (rp.isAtInfinity()) {
                    continue;
                }
                aposx[i][j] = (int) round(rp.x);
                aposy[i][j] = (int) round(rp.y);

                if (aposx[i][j] < lowx)      { lowx = aposx[i][j]; }
                else if (aposx[i][j] > maxx) { maxx = aposx[i][j]; }
                if (aposy[i][j] < lowy)      { lowy = aposy[i][j]; }
                else if (aposy[i][j] > maxy) { maxy = aposy[i][j]; }
            }
        }

        // now normalize coordinates by transposing
        int xr = maxx - lowx + 1;
        int yr = maxy - lowy + 1;

        int xadj = -lowx;
        int yadj = -lowy;

        // translate result image's coordinates to origin
        for (int i = 0; i < picWidth; ++i) {
            for (int j = 0; j < picLenth; ++j) {
                aposx[i][j] += xadj;
                aposy[i][j] += yadj;
            }
        }

        BufferedImage image = new BufferedImage(xr, yr, BufferedImage.TYPE_INT_RGB);

        // set all pixels to white
        for (int i = 0; i < xr - 1; ++i) {
            for (int j = 0; j < yr - 1; ++j) {
                image.setRGB(i, j, Color.white.getRGB());
            }
        }

        Graphics g = image.getGraphics();

        // smoothen colors across the image
        for (int i = 0; i < picWidth; ++i) {
            for (int j = 0; j < picLenth; ++j) {
                if ((i == picWidth - 1)
                        || (j == picLenth - 1)) {
                    continue;
                }

                shade(aposx, aposy, i, j, rgb, g);
            }
        }

        g.dispose();

        return image;
    }

    /**
     * Returns average color of the 4 given colors. Handling needed to
     * avoid overflow and "bleed" into other colors.
     *
     * @param rgb0
     * @param rgb1
     * @param rgb2
     * @param rgb3
     * @return average color of the given colors.
     */
    public int averageColor(int rgb0, int rgb1, int rgb2, int rgb3) {
        int r0 = rgb0 & 0xFF0000;
        int r1 = rgb1 & 0xFF0000;
        int r2 = rgb2 & 0xFF0000;
        int r3 = rgb3 & 0xFF0000;

        int ar = ((r0 + r1 + r2 + r3) / 4) & 0xFF0000;

        int g0 = rgb0 & 0x00FF00;
        int g1 = rgb1 & 0x00FF00;
        int g2 = rgb2 & 0x00FF00;
        int g3 = rgb3 & 0x00FF00;

        int ag = ((g0 + g1 + g2 + g3) / 4) & 0x00FF00;

        int b0 = rgb0 & 0x0000FF;
        int b1 = rgb1 & 0x0000FF;
        int b2 = rgb2 & 0x0000FF;
        int b3 = rgb3 & 0x0000FF;

        int ab = ((b0 + b1 + b2 + b3) / 4) & 0x0000FF;

        return ar | ag | ab;
    }

    /*
     * Given point (i, j), find 3 adjacent points on the anamorphed
     * image; form a convex quadrilateral and fill it with the average
     * color of the 4 points.
     */
    private void shade(
            int[][] aposx, int[][] aposy,
            int i, int j,
            int[][] rgb,
            Graphics g)
    {
        int x0 = aposx[i][j];
        int y0 = aposy[i][j];
        int x1 = aposx[i+1][j];
        int y1 = aposy[i+1][j];
        int x2 = aposx[i+1][j+1];
        int y2 = aposy[i+1][j+1];
        int x3 = aposx[i][j+1];
        int y3 = aposy[i][j+1];

        int c0 = rgb[i][j];
        int c1 = rgb[i+1][j];
        int c2 = rgb[i][j+1];
        int c3 = rgb[i+1][j+1];
        int ac = averageColor(c0, c1, c2, c3);

        Polygon p = new Polygon();

        p.addPoint(x0, y0);
        p.addPoint(x1, y1);
        p.addPoint(x2, y2);
        p.addPoint(x3, y3);

        g.setColor(new Color(ac));
        g.drawPolygon(p);
        g.fillPolygon(p);
    }

    /**
     * Returns array representing img's pixels. Accessing array[i][j] is faster
     * than img.getRGB(i, j).
     *
     * @param img
     * @return array representing pixels of img.
     */
    public static int[][] toPixelArray(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int[][] pixels = new int[width][height];

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                pixels[i][j] = img.getRGB(width-i-1, height-j-1);
            }
        }

        return pixels;
    }
}
