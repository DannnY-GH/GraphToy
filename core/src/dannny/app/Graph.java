package dannny.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class Node {
    int index;
    int cost;

    Node(int data, int cost) {
        this.index = data;
        this.cost = cost;
    }
}

class Graph {
    ArrayList<LinkedList<Node>> vertices;

    Graph(int N) {
        this.vertices = new ArrayList<LinkedList<Node>>();
        for (int i = 0; i < N; i++) {
            this.vertices.add(new LinkedList<Node>());
        }
    }

    void AddEdge(int u, int v, int cost) {
        vertices.get(u).add(new Node(v, cost));
    }

    int AddNode() {
        vertices.add(new LinkedList<Node>());
        return vertices.size() - 1;
    }

    void DeleteNode(int index) {
        vertices.remove(index);
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = vertices.get(i).size() - 1; j >= 0; j--) {
                Node v = vertices.get(i).get(j);
                if (v.index == index)
                    vertices.get(i).remove(v);
                if (v.index > index)
                    v.index--;
            }
        }
    }

    int getNodesCount() {
        return vertices.size();
    }

    Graph(File file) throws FileNotFoundException {
        int M, u, v, c;
        Scanner sc = new Scanner(file);
        M = sc.nextInt();
        for (int i = 0; i < M; i++) {
            u = sc.nextInt();
            v = sc.nextInt();
            c = sc.nextInt();
            AddEdge(u - 1, v - 1, c);
        }
    }

    boolean edgeBetween(int u, int v) {
        for (Node c : vertices.get(u))
            if (c.index == v)
                return true;
        for (Node c : vertices.get(v))
            if (c.index == u)
                return true;
        return false;
    }

    void RandomInit(int N, int M) {
        int u, v, c;
        Random r = new Random();
        for (int i = 0; i < M; i++) {
            u = r.nextInt(N);
            v = r.nextInt(N);
            c = r.nextInt(100);
            AddEdge(u, v, c);
        }
    }

    void Print() {
        int nodesCount = getNodesCount();
        System.out.println(nodesCount + " " + vertices.size());
        for (int i = 0; i < nodesCount; i++) {
            for (Node v : vertices.get(i))
                System.out.println(i + " " + v.index);
        }
        System.out.println();
    }
}
