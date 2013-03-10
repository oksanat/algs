import java.util.ArrayList;
import java.util.List;

public class KdTree {

    private static final double XMIN = 0;
    private static final double YMIN = 0;
    private static final double XMAX = 1;
    private static final double YMAX = 1;

    private Node root;
    private int size = 0;

    private static class Node {
        private Point2D p;
        private RectHV rect;
        private Node lb;
        private Node rt;

        public Node(Point2D p, RectHV rect) {
            this.p = p;
            this.rect = rect;
        }
    }

    public KdTree() {
        root = null;
        size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }
    // number of points in the set
    public int size() {
        return size;
    }
    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (isEmpty()) {
            RectHV rect = new RectHV(XMIN, YMIN, XMAX, YMAX);
            root = new Node(p, rect);
            size++;
            return;
        } else if (!contains(p)) {
            put(root, p, true);
            size++;
        } else {
            StdOut.println("Contains: " + p.toString());
        }
    }

    private void put(Node node, Point2D p, boolean byX) {
        RectHV rect = null;
        int cmp = 0;
        if (byX) {
            cmp = Point2D.X_ORDER.compare(p, node.p);
        } else {
            cmp = Point2D.Y_ORDER.compare(p, node.p);
        }
        // less than
        if (cmp < 0) {
            if (node.lb == null) {
                if (byX) {
                    rect = new RectHV(node.rect.xmin(), node.rect.ymin(), node.p.x(), node.rect.ymax());
                } else {
                    rect = new RectHV(node.rect.xmin(), node.rect.ymin(), node.rect.xmax(), node.p.y());
                }
                node.lb = new Node(p, rect);
            } else {
                put(node.lb, p, !byX);
            }
        } else {
            if (node.rt == null) {
                if (byX) {
                    rect = new RectHV(node.p.x(), node.rect.ymin(), node.rect.xmax(), node.rect.ymax());
                } else {
                    rect = new RectHV(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.rect.ymax());
                }
                node.rt = new Node(p, rect);
            } else {
                put(node.rt, p, !byX);
            }
        }
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        return get(root, p, true) != null;
    }

    private Point2D get(Node x, Point2D p, boolean byX) {
        if (p.equals(x.p)) {
            return x.p;
        }
        int cmp = 0;
        if (byX) {
            cmp = Point2D.X_ORDER.compare(p, x.p);
        } else {
            cmp = Point2D.Y_ORDER.compare(p, x.p);
        }
        if (cmp < 0) {
            if (x.lb != null) {
                get(x.lb, p, !byX);
            }
        } else if (cmp > 0) {
            if (x.rt != null) {
                get(x.rt, p, !byX);
            }
        }
        return null;
    }

    // draw all of the points to standard draw
    public void draw() {
        if(isEmpty()) {
            return;
        }
        root.p.draw();
        drawNode(root, true);
    }

    private void drawNode(Node node, boolean byX)
    {
        if(node.lb != null) {
            drawNode(node.lb, !byX);
        }

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(10);
        node.p.draw();

        StdDraw.setPenRadius();
        if(byX) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.p.x(), node.rect.ymin(),
                         node.p.x(), node.rect.ymax());
        } else { // by Y
            StdDraw.setPenColor(StdDraw.BLUE);

            StdDraw.line(node.rect.xmin(), node.p.y(),
                         node.rect.xmax(), node.p.y());
        }

        if(node.rt != null) {
            drawNode(node.rt, !byX);
        }
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        List<Point2D> range = new ArrayList<Point2D>(this.size());
        if(isEmpty()) {
            return range;
        }
        //root intersects return all points
        if (rect.intersects(root.rect)) {
            add(range, root, rect);
        }
        return range;
    }


    private void add(List<Point2D> range, Node node, RectHV rect)
    {
        if (node.lb != null && rect.intersects(node.lb.rect)) {
            add(range, node.lb, rect);
        }
        if (rect.contains(node.p)) {
            range.add(node.p);

        }
        if (node.rt != null && rect.intersects(node.rt.rect)) {
            add(range, node.rt, rect);
        }
    }

    public Point2D nearest(Point2D p) {
        if(isEmpty()) {
            return null;
        }
        if (size() == 1) {
            return root.p;
        }
        return nearest(root, root.p, p, true);
    }

    private Point2D nearest(Node node, Point2D cp, Point2D p, boolean byX)
    {
        Point2D nrl = cp, nrr = cp;
        if (node.lb != null) {
            if (greater(node.lb, cp, p)) {
                nrl = nearest(node.lb, node.lb.p, p, !byX);
            }
        }
        if (node.rt != null) {
            if (greater(node.rt, cp, p)) {
                nrr = nearest(node.rt, node.rt.p, p, !byX);
            }
        }
        if (nrl.distanceTo(p) > nrr.distanceTo(p)) {
            return nrr;
        } else {
            return nrl;
        }

    }

    private boolean greater(Node node, Point2D cp, Point2D p) {
        return cp.distanceTo(p) > node.p.distanceTo(p);
    }

    private Point2D searchNearest(Node node, Point2D p, boolean byX, double nearestDistance)
    {
        int cmp = 0;
        if (byX) {
            cmp = Point2D.X_ORDER.compare(p, node.p);
        } else {
            cmp = Point2D.Y_ORDER.compare(p, node.p);
        }
        if (cmp < 0) {
            if (node.lb != null) {
                double distance = node.lb.rect.distanceSquaredTo(p);
                if (distance > nearestDistance) {
                    searchNearest(node.lb, p, !byX, distance);
                }
            }
            return node.p;
        } else if (cmp > 0) {
            if (node.rt != null) {
                double distance = node.rt.rect.distanceSquaredTo(p);
                if (distance > nearestDistance) {
                    searchNearest(node.rt, p, !byX, distance);
                }
            }
            return node.p;
        } else {
            return node.p;
        }

    }

}
