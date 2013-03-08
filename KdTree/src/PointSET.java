import java.util.ArrayList;
import java.util.List;

public class PointSET {

    private SET<Point2D> rbBst;

    // construct an empty set of points
    public PointSET() {
        rbBst = new SET<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return rbBst.isEmpty();
    }
    // number of points in the set
    public int size() {
        return rbBst.size();
    }
    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
        rbBst.add(p);
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        return rbBst.contains(p);
    }

    // draw all of the points to standard draw
    public void draw() {
        for (Point2D p : rbBst)
            p.draw();
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        List<Point2D> range = new ArrayList<Point2D>(this.size());
        for (Point2D point: rbBst) {
            if (point.x() >= rect.xmin() && point.x() <= rect.xmax()
                    && point.y() >= rect.ymin() && point.y() <= rect.ymax()) {
                range.add(point);
            }
        }
        return range;
    }

    public Point2D nearest(Point2D p) {
        if (rbBst.isEmpty()) {
            return null;
        }
        Point2D nearestPoint = null;
        for (Point2D point: rbBst) {
            if (nearestPoint == null) {
                nearestPoint = point;
            } else if (p.distanceSquaredTo(point) < p.distanceSquaredTo(nearestPoint)) {
                nearestPoint = point;
            }
        }
        return nearestPoint;
    }

}
