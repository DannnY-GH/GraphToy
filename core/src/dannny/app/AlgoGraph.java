package dannny.app;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class AlgoGraph {
    static int INF = 1000000000;
    private int[][] dist, pr;
    private boolean[] used;
    private LinkedList<Integer> tmpList, MID;
    private Graph g;

    AlgoGraph(Graph g) {
        this.g = g;
        int N = g.getNodesCount();
        dist = new int[N][N];
        pr = new int[N][N];
        used = new boolean[N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                dist[i][j] = g.vertices.get(i).get(j).cost;
                pr[i][j] = -1;
            }
        }
        tmpList = new LinkedList<Integer>();
        FloydWarshall();
        MID = GetGraphCenter();
    }

    void FloydWarshall() {
        int N = g.getNodesCount();
        for (int k = 0; k < N; k++)
            for (int i = 0; i < N; i++)
                for (int j = 0; j < N; j++)
                    if (dist[i][j] > dist[i][k] + dist[k][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        pr[i][j] = k;
                    }
    }

    public class Path {
        LinkedList<Integer> path;
        int len;

        Path(LinkedList<Integer> path, int len) {
            this.path = new LinkedList<Integer>();
            this.path.addAll(path);
            this.len = len;
        }
    }

    class LenPathCompare implements Comparator<Path> {
        public int compare(Path a, Path b) {
            return a.len - b.len;
        }
    }


    LinkedList<Path> GetAllPaths(int u, int v) {
        LinkedList<Path> paths = new LinkedList<Path>();
        Arrays.fill(used, false);
        tmpList.clear();
        AllDFS(u, v, 0, paths);
        Collections.sort(paths, new LenPathCompare());
        return paths;
    }

    LinkedList<Integer> GetGraphCenter() {
        LinkedList<Integer> ans = new LinkedList<Integer>();
        int N = g.getNodesCount();
        Integer[] e = new Integer[N];
        for (int i = 0; i < N; i++) {
            int maxDist = -INF;
            for (int j = 0; j < N; j++)
                if (j != i && dist[j][i] != INF)
                    maxDist = Math.max(maxDist, dist[j][i]);
            e[i] = Math.abs(maxDist);
        }
        int E = Collections.min(Arrays.asList(e));
        for (int i = 0; i < N; i++)
            if (e[i] == E)
                ans.add(i);
        return ans;
    }

    private void AllDFS(int u, int v, int dist, LinkedList<Path> lenPaths) {
        if (used[u])
            return;
        used[u] = true;
        tmpList.add(u);
        if (u == v) {
            lenPaths.add(new Path(tmpList, dist));
        }
        for (Node to : g.vertices.get(u))
            AllDFS(to.index, v, dist + g.vertices.get(u).get(to.index).cost, lenPaths);
        tmpList.pop();
        used[u] = false;
    }
}
