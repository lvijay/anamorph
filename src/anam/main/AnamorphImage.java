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
package anam.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import anam.anamorph.Anamorpher;
import anam.definitions.Cylinder;
import anam.definitions.Point3D;

public class AnamorphImage {
    private static Map<String, String> toMap(String[] args) {
        Map<String, String> map = new HashMap<String, String>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (ARGS.containsKey(arg)) {
                map.put(arg, args[i+1]);

                i++;
            } else {
                throw new IllegalArgumentException("Unknown argument " + arg);
            }
        }

        return map;
    }

    public static final Map<String, String> ARGS;
    public static final String INPUT = "-input";
    public static final String OUTPUT = "-output";
    public static final String RADIUS = "-radius";
    public static final String DIST = "-dist";
    public static final String HEIGHT = "-height";

    static {
        Map<String, String> map = new HashMap<String, String>();

        map.put(INPUT, "Input image file");
        map.put(OUTPUT, "Output image file");
        map.put(RADIUS, "Radius of the cylinder in pixels");
        map.put(DIST, "Horizontal distance of the eye from the cylinder's center");
        map.put(HEIGHT, "Vertical distance of the eye from the ground");

        ARGS = Collections.unmodifiableMap(map);
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> kwargs = null;

        try {
            kwargs = toMap(args);

            if (kwargs.isEmpty()) {
                printUsage();
                return;
            }
        } catch (NumberFormatException e) {
            printUsage();
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Missing argument for " + args[args.length - 1]);
        } catch (IllegalArgumentException e) {
            System.err.println(e);
        }

        dwim(kwargs);
    }

    private static void printUsage() {
        System.err.println("Usage:");

        for (Map.Entry<String, String> e : ARGS.entrySet()) {
            System.err.printf("    %s: %s%n", e.getKey(), e.getValue());
        }
    }

    private static void dwim(Map<String, String> kwargs) throws IOException {
        String input = kwargs.get(INPUT);
        String output = kwargs.get(OUTPUT);
        if (output == null) {
            File inf = new File(input);
            output = inf.getParent()
                + File.separator
                + "anamo_" + inf.getName();
        }
        int radius = Integer.parseInt(kwargs.get(RADIUS));
        int dist = Integer.parseInt(kwargs.get(DIST));
        int vdist = Integer.parseInt(kwargs.get(HEIGHT));

        BufferedImage img = ImageIO.read(new File(input));
        int wid = img.getWidth();
        int hei = img.getHeight();

        int ratio = (int) Math.ceil(1.0d * wid / radius);
        if (ratio > 1) {
            radius *= ratio;
            dist *= ratio;
            vdist *= ratio;
        }

        System.out.printf("ratio=%d, dist=%d, vdist=%d%n",
                ratio, dist, vdist);

        Cylinder c = new Cylinder(radius);
        Anamorpher rpg = new Anamorpher(c);
        BufferedImage opimg = rpg.anamorph(img, new Point3D(dist, 0, vdist));

        System.out.printf("Original image:   %dx%d%n", wid, hei);
        System.out.printf("Anamorphed image: %dx%d%n", opimg.getWidth(), opimg.getHeight());

        ImageIO.write(opimg, "png", new File(output));
    }
}
