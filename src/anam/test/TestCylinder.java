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
package anam.test;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;

import anam.anamorph.Anamorpher;
import anam.anamorph.Reflection;
import anam.definitions.Cylinder;
import anam.definitions.Point3D;

public class TestCylinder {
    public void testIntersection() {
        double r = 10d;

        Point3D p = new Point3D(  0, 0, 4);
        Point3D v = new Point3D(100, 0, 4);
        Point3D i = new Reflection().intersections(new Cylinder(r), p, v).get(0);

        boolean check = i.x == r;

        System.out.printf("check=%s point_of_intersection=%s%n", check, i);
    }

    public void testReflection() {
        double r = 10d;
        Point3D p = new Point3D(  0,   9,  99);
        Point3D v = new Point3D(100,   0, 100);
        Cylinder cyl = new Cylinder(r);
        Point3D i = new Reflection().intersections(cyl, p, v).get(0);
        Point3D rp = cyl.accept(new Reflection(), p, v);

        System.out.printf("i=%s, rp=%s%n", i, rp);
    }

    public void testReflectPixelGrid() {
        int width = 10;
        int height = 10;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                int r = new int[]{0x00, 0xFF, 0x7F}[i % 3];
                int g = new int[]{0x7F, 0x00, 0xFF}[i % 3];
                int b = new int[]{0xFF, 0x7F, 0x00}[i % 3];
                int rgb = (r << 16) | (g << 8) | (b << 0);

                img.setRGB(i, j, rgb);
            }
        }

        Anamorpher rpg = new Anamorpher(new Cylinder(20));
        BufferedImage image = rpg.anamorph(img, new Point3D(200, 0, 550));

        System.out.printf("(%d, %d)%n", image.getWidth(), image.getHeight());

        draw(image);
    }

    /**
     * @param compute
     * @param w
     * @param l
     */
    private void draw(final BufferedImage img) {
        Frame f = new Frame("paint example");

        f.add("Center", new Canvas() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paint(Graphics g) {
                g.drawImage(img, 0, 0, Color.black, null);
            }
        });
        f.setSize(new Dimension(img.getWidth()+20, img.getHeight()+20));
        f.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        TestCylinder tc = new TestCylinder();
        Method[] methods = tc.getClass().getMethods();

        for (Method method : methods) {
            if (method.getReturnType().equals(void.class)
                    && method.getName().startsWith("test")) {
                System.out.print("Running " + method.getName() + ": ");
                method.invoke(tc);
            }
        }
    }
}
