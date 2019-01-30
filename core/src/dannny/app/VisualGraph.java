package dannny.app;

import com.badlogic.gdx.math.Vector3;

import java.util.LinkedList;

import static dannny.app.Main.rnd;

class VisualGraph {
    public static final int NODE_RADIUS = 30;
    public static final int EDGE_THICK = 10;
    public static final int MAX_EDGE_RND = 100;
    public int selectedNode = -1;
    public boolean isUpdating = false;
    float K = 300;
    float T = 1000;
    float C = 0.02f;
    LinkedList<Point> points;
    Graph g;
    private Vector3 tmp = new Vector3();

    class Point {
        Vector3 pos;
        Vector3 disp;
        boolean selected = false;

        Point(Vector3 pos) {
            this.pos = pos;
            disp = new Vector3(0, 0, 0);
        }
    }

    void AddRandomPoint() {
        float RAND_SPACE = 700;
        points.add(new Point(new Vector3(rnd.nextFloat() * RAND_SPACE, rnd.nextFloat() * RAND_SPACE, 0)));
    }

    public void AddPoint(Vector3 pos) {
        points.add(new Point(new Vector3(pos)));
    }

    VisualGraph(Graph g) {
        this.g = g;
        points = new LinkedList<Point>();
        int nodesCount = g.getNodesCount();
        for (int i = 0; i < nodesCount; i++) {
            AddRandomPoint();
        }
    }

    private float Fa(float d) {
        return d * d / K;
    }

    private float Fr(float d) {
        return C * K * K / d;
    }

    void Update() {
        for (int i = 0; i < points.size(); i++) {
            Point v = points.get(i);
            v.disp.set(0, 0, 0);
            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    Point u = points.get(j);
                    tmp.set(v.pos.x - u.pos.x, v.pos.y - u.pos.y, 0);
                    float d = tmp.len();
                    float frm = Fr(d); //Repulsive
                    if (g.edgeBetween(i, j)) {
                        frm -= Fa(d);
                    }
                    tmp.scl(1 / d, 1 / d, 0);
                    v.disp.add(tmp.scl(frm));
                }
            }
        }
        for (int i = 0; i < points.size(); i++) {
            Point pt = points.get(i);
            if (selectedNode != i) {
                float len = Math.max(pt.disp.len(), 1);
                float scl = Math.min(len, T) / len * 0.1f;
                pt.pos.add(pt.disp.scl(scl, scl, 0));
            }
        }
    }
}
