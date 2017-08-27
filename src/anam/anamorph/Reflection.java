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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import anam.definitions.Cylinder;
import anam.definitions.Point3D;
import anam.definitions.Visitor;

public class Reflection implements Visitor {

    /**
     * Returns the point on the XY plane which represents the reflection of the
     * line from V to P. Returns {@link Point3D#INFINITY} if there's no point of
     * reflection.
     *
     * @param c the cylinder.
     * @param P point representing a pixel on the original image. Assumed to be
     *        within the object. Not verified.
     * @param V point representing the viewpoint (the eye). Assumed to be
     *        outside the object. Not verified.
     * @return the point on the XY plane which represents the reflection of the
     *         line from V to P.
     */
    @Override
    public Point3D visitCylinder(Cylinder c, Point3D P, Point3D V) {
        Point3D i = getIntersectionPoint(c, P, V);

        if (i.isAtInfinity()) {
            return Point3D.INFINITY;
        }

        Point3D r = reflectionVector(P, V, i);
        double t = i.z / r.z;

        return new Point3D(i.x - t * r.x, i.y - t * r.y, 0);
    }

    Point3D getIntersectionPoint(Cylinder c, Point3D P, Point3D V)  {
        List<Point3D> ixns = intersections(c, P, V);

        if (ixns.isEmpty()) {
            return Point3D.INFINITY;
        }

        return ixns.get(0);
    }

    /**
     * @param p1
     * @param p2
     * @return list of intersections.
     */
    /* Visible for testing */
    public List<Point3D> intersections(Cylinder c, Point3D p1, Point3D p2) {
        double A =
                  c.a * square(p2.x - p1.x)
                + c.b * square(p2.y - p1.y)
                + c.c * square(p2.z - p1.z);
        double B =
                  2 * c.a * (p1.x - c.p0.x) * (p2.x - p1.x)
                + 2 * c.b * (p1.y - c.p0.y) * (p2.y - p1.y)
                + 2 * c.c * (p1.z - c.p0.z) * (p2.z - p1.z);
        double C =
                  c.a * square(p1.x - c.p0.x)
                + c.b * square(p1.y - c.p0.y)
                + c.c * square(p1.z - c.p0.z)
                + c.d;

        // u = -b ± √(b^2-4ac)
        //          2a

        double det = square(B) - 4 * A * C;

        if (det < 0) {
            return Collections.emptyList();
        }

        double upd = (-B + Math.sqrt(det)) / (2 * A); // +ve determinant
        double und = (-B - Math.sqrt(det)) / (2 * A); // -ve determinant

        double ixp = p1.x + upd * (p2.x - p1.x);
        double ixn = p1.x + und * (p2.x - p1.x);
        double iyp = p1.y + upd * (p2.y - p1.y);
        double iyn = p1.y + und * (p2.y - p1.y);
        double izp = p1.z + upd * (p2.z - p1.z);
        double izn = p1.z + und * (p2.z - p1.z);

        Point3D i0 = new Point3D(ixp, iyp, izp);
        Point3D i1 = new Point3D(ixn, iyn, izn);

        if (det == 0) {
            return Collections.singletonList(i0);
        }

        return Arrays.asList(i0, i1);
    }

    private Point3D reflectionVector(Point3D p, Point3D V, Point3D i) {
        Point3D n = p.withZ(0.0d); //normal vector
        Point3D v = V.minus(p);
        Point3D a = n.times(n.dot(v) / n.dot(n)).minus(v);
        Point3D r = v.plus(a.times(2));

        return r;
    }

    private final double square(double x) {
        return x * x;
    }
}
