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
package anam.definitions;

import java.util.stream.DoubleStream;

public class Point3D {
    public static final Point3D ORIGIN = new Point3D(0, 0, 0);
    public static final Point3D INFINITY = new Point3D(
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY);

    public final double x;
    public final double y;
    public final double z;

    /**
     * @param x
     * @param y
     * @param z
     */
    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double dot(Point3D p) {
        Point3D p1 = this;
        Point3D p2 = p;

        return (p1.x * p2.x) + (p1.y * p2.y) + (p1.z * p2.z);
    }

    public Point3D plus(Point3D p) {
        return new Point3D(x + p.x, y + p.y, z + p.z);
    }

    public Point3D minus(Point3D p) {
        return this.plus(p.times(-1));
    }

    public Point3D times(double v) {
        return new Point3D(x * v, y * v, z * v);
    }

    public Point3D withX(double v) {
        return new Point3D(v, y, z);
    }

    public Point3D withY(double v) {
        return new Point3D(x, v, z);
    }

    public Point3D withZ(double v) {
        return new Point3D(x, y, v);
    }

    public boolean isAtInfinity() {
        return DoubleStream.of(x, y, z)
                .anyMatch(v -> Double.isNaN(v) || Double.isInfinite(v));
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", x, y, z);
    }
}
