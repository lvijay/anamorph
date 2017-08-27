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

public class Cylinder {
    public final Point3D p0;
    public final double a;
    public final double b;
    public final double c;
    public final double d;

    public Cylinder(double radius) {
        p0 = Point3D.ORIGIN;
        a = 1;
        b = 1;
        c = 0;
        d = -1 * radius * radius;
    }

    public Point3D accept(Visitor visitor, Point3D p, Point3D v) {
        return visitor.visitCylinder(this, p, v);
    }
}
