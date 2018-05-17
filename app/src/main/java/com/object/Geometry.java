package com.object;

/**
 * Created by txt on 2017/11/5.
 */


public class Geometry {
    public static class Point {
        public final float x, y, z;

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point TranslateY(float distance) {
            return new Point(x, y + distance, z);
        }

        public Point translate(Vector vector) {
            return new Point(
                    x + vector.x,
                    y + vector.y,
                    z + vector.z);
        }
    }

    public static class Vector {
        public final float x, y, z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float length() {
            return (float) Math.sqrt(x * x + y * y + z * z);
        }


        public Vector crossProduct(Vector other) {
            return new Vector((y * other.z) - (z * other.y), (z * other.x) - (x * other.z), (x * other.y) - (y * other.x));
        }

        public float dotProduct(Vector other) {
            return x * other.x + y * other.y + z * other.z;
        }

        public Vector scale(float f) {
            return new Vector(x * f, y * f, z * f);
        }

        public Vector add(Vector other) {
            return new Vector(x + other.x, y + other.y, z + other.z);
        }
    }

    public static class Ray {
        public final Point point;
        public final Vector vector;

        public Ray(Point point, Vector vector) {
            this.point = point;
            this.vector = vector;
        }
    }


    public static class Circle {
        public final Point center;
        public final float radius;

        public Circle(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }

        public Circle scale(float scale) {
            return new Circle(center, radius * scale);
        }
    }

    public static class Cylinder {
        public final Point center;
        public final float radius;
        public final float height;

        public Cylinder(Point center, float radius, float height) {
            this.center = center;
            this.radius = radius;
            this.height = height;
        }
    }

    public static class Sphere {
        public final Point center;
        public final float radius;

        public Sphere(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }

    public static class Plane {
        public final Point point;
        public final Vector normal;

        public Plane(Point point, Vector normal) {
            this.point = point;
            this.normal = normal;
        }
    }

    public static Vector vectorBetween(Point from, Point to) {
        return new Vector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z);
    }

    public static boolean intersects(Sphere sphere, Ray ray) {
        return distanceBetween(sphere.center, ray) < sphere.radius;
    }

    public static float distanceBetween(Point point, Ray ray) {
        Vector p1ToPoint = vectorBetween(ray.point, point);
        Vector p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point);
        float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        float lengthOfBase = ray.vector.length();
        float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
        return distanceFromPointToRay;
    }

    public static class Vector2 {
        public final float x;
        public final float y;

        public Vector2(Vector v) {
            x = v.x;
            y = v.z;
        }

        public Vector2(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public Vector2 add(Vector2 other) {
            return new Vector2(x + other.x, y + other.y);
        }

        public Vector2 scale(float f) {
            return new Vector2(x * f, y * f);
        }

        public float dotProduct(Vector2 other) {
            return x * other.x + y * other.y;
        }

        public float length() {
            return (float) Math.sqrt(x * x + y * y);
        }

        public Vector2 sub(Vector2 other) {
            return new Vector2(x - other.x, y - other.y);
        }
    }

    public static Vector reflex(Vector u1, Vector u2)//r1 is the speed , r2 is from object with speed to others
    {
        Vector2 r1 = new Vector2(u1);
        Vector2 r2 = new Vector2(u2);
        float r1cosa = r1.dotProduct(r2) / r2.length();
        Vector2 v1 = r2.scale(r1cosa / r2.length());
        Vector2 v2 = r1.sub(v1);
        Vector2 v = v2.sub(v1);
        return new Vector(v.x, u1.y, v.y);
    }

    public static Point intersectionPoint(Ray ray, Plane plane) {
        Vector rayToPlaneVector = vectorBetween(ray.point, plane.point);

        float scaleFactor = rayToPlaneVector.dotProduct(plane.normal)
                / ray.vector.dotProduct(plane.normal);

        Point intersectionPoint = ray.point.translate(ray.vector.scale(scaleFactor));
        return intersectionPoint;
    }
}
