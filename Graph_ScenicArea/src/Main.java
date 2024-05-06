import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Graph graph = new Graph();
        Tourism service = new Tourism();
        Scanner scanner = new Scanner(System.in);
        int choice;
        while (true) {
            service.printMenu();
            choice = scanner.nextInt();
            switch (choice) {
                case 0:
                    System.out.println("再见!");
                    return;
                case 1:
                    service.createGraph(graph);
                    break;
                case 2:
                    service.getSpotInfo(graph);
                    break;
                case 3:
                    service.traverPath(graph);
                    break;
                case 4:
                    service.findShortPath(graph);
                    break;
                case 5:
                    service.designPath(graph);
                    break;
                default:
                    System.out.println("无效编号!请重新输入: ");
            }
        }
        //

    }


}

class Tourism {
    void createGraph(Graph graph){
        System.out.println("==== 创建景区景点图 =====");
        try{
            File file = new File("Graph_ScenicArea\\File_dir\\Vertex.txt");
            Scanner scanner = new Scanner(file);
            int count = Integer.parseInt(scanner.nextLine());
            System.out.println("顶点数目: " + count);

            for (int i = 0; i < count; i++) {
                int num = Integer.parseInt(scanner.nextLine());
                String name = scanner.nextLine();
                String desc = scanner.nextLine();
                Vertex vertex = new Vertex(num, name, desc);
                graph.insertVex(vertex);
            }
            System.out.println("---- 顶点 ----");
            graph.printVertex();
            scanner.close();

            file = new File("Graph_ScenicArea\\File_dir\\Edge.txt");
            scanner = new Scanner(file);
            System.out.println("---- 边 ----");
            while (scanner.hasNextLine()) {
                String[] nums = scanner.nextLine().split("\\s+");
                int vex1 = Integer.parseInt(nums[0]);
                int vex2 = Integer.parseInt(nums[1]);
                int weight = Integer.parseInt(nums[2]);
                Edge edge = new Edge(vex1, vex2, weight);
                graph.insertEdge(edge);
                System.out.println("< v" + vex1 + ", v" + vex2 + " > " + weight);
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void traverPath(Graph graph) {
        System.out.println("==== 旅游景点导航 ====");
        graph.printVertex();
        System.out.println("请输入起始点编号: ");
        Scanner scanner = new Scanner(System.in);
        int start = scanner.nextInt();
        System.out.println("导游路线为: ");
        String[] paths = graph.dfsTraverse(start);
        for(String s : paths) {
            System.out.println(s);
        }
    }

    void getSpotInfo(Graph graph) {
        System.out.println("==== 查询顶点信息 ====");
        graph.printVertex();
        System.out.println("请输入要查询的顶点编号: ");
        Scanner scanner = new Scanner(System.in);
        int idx = scanner.nextInt();
        Vertex vertex = graph.getVertex(idx);
        if (vertex == null) {
            System.out.println("找不到该编号的顶点信息!");
            return;
        } else {
            System.out.println(vertex);
        }
        System.out.println("---- 周边景区 ----");
        String[] surroundings = graph.findEdge(idx);
        for (String str : surroundings) {
            System.out.println(str);
        }
    }

    void findShortPath(Graph graph) {
        System.out.println("==== 搜索最短路径 ====");
        graph.printVertex();
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入起点的编号: ");
        int start = scanner.nextInt();
        System.out.println("请输入终点的编号: ");
        int destination = scanner.nextInt();
        graph.Dijkstra(start, destination);
    }

    void designPath(Graph graph) {
        System.out.println("==== 铺设电路规划 ====");
        List<Edge> path = graph.findMinTree();
        if(path == null) {
            System.out.println("无法规划!");
            return;
        }
        System.out.println("在以下两个景点之间铺设电路:");
        int sum = 0;
        for (Edge edge : path) {
            int u = edge.either();
            int v = edge.other(u);
            int weight = edge.weight;
            sum += weight;
            System.out.println(graph.vexs[u].name + " -- " + graph.vexs[v].name + "    " + weight + "m");
        }
        System.out.printf("铺设电路的总长度为: %dm\n", sum);
    }
    void printMenu(){
        System.out.println("==== 景区信息管理系统 ====");
        System.out.println("1.创建景区景点图");
        System.out.println("2.查询景点信息");
        System.out.println("3.旅游景点导航");
        System.out.println("4.搜索最短路径");
        System.out.println("5.铺设电路规划");
        System.out.println("0.退出");
        System.out.println("请输入操作编号(0 ~ 5): ");
    }

}

class Vertex{
    int num;        //景区编号
    String name;    //景点名字
    String desc;    //景点介绍
    Vertex(int num, String name, String desc) {
        this.num = num;
        this.name = name;
        this.desc = desc;
    }
    @Override
    public String toString() {
        return name + " : " + desc;
    }
}
class Edge{
    int vex1, vex2; //边的两个顶点
    int weight;
    Edge(int vex1, int vex2, int weight) {
        this.vex1 = vex1;
        this.vex2 = vex2;
        this.weight = weight;
    }

    int either() {
        return vex1;
    }
    int other(int v) {
        return v == vex1 ? vex2 : vex1;
    }
}
class Graph{

    int[][] adjMatrix;  //邻接矩阵
    Vertex[] vexs;      //顶点信息数组
    int vexNum;         //当前顶点个数
    int MAX_VEXNUM = 50;//最大顶点个数

    Graph() {
        adjMatrix = new int[MAX_VEXNUM][MAX_VEXNUM];
        vexs = new Vertex[MAX_VEXNUM];
    }

    boolean insertVex(Vertex vertex) {
        if(vexNum >= MAX_VEXNUM) {
            return false;
        }
        vexs[vexNum++] = vertex;
        return true;
    }

    boolean insertEdge(Edge edge) {
        int vex1 = edge.vex1;
        int vex2 = edge.vex2;
        if(vex1 < 0 || vex2 < 0 || vex1 >= vexNum || vex2 >= vexNum) {
            return false;
        }
        adjMatrix[vex1][vex2] = edge.weight;
        adjMatrix[vex2][vex1] = edge.weight;
        return true;
    }

    Vertex getVertex(int idx) {
        if (idx < 0 || idx >= vexNum) {
            return null;
        }
        return vexs[idx];
    }

    String[] findEdge(int idx) {
        ArrayList<String> list = new ArrayList<>();
        String name = vexs[idx].name;
        for (int i = 0; i < vexNum; i++) {
            if(adjMatrix[idx][i] != 0) {
                String str = vexs[idx].name + " -> " + name + "  " + adjMatrix[idx][i] + "m";
                list.add(str);
            }
        }
        return list.toArray(new String[0]);
    }

    void printVertex() {
        for (int i = 0; i < vexNum; i++) {
            System.out.println(vexs[i].num + " - " + vexs[i].name);
        }
    }
    ArrayList<ArrayList<Integer>> pathList = new ArrayList<>();
    String[] dfsTraverse(int start) {
        boolean[] visited = new boolean[vexNum];
        Arrays.fill(visited, false);

        ArrayList<Integer> path = new ArrayList<>();
        path.add(start);
        visited[start] = true;
        dfs(start, visited, path);
        String[] paths = new String[pathList.size()];
        for(int i = 0; i < pathList.size(); i++) {
            int j = 1;
            StringBuilder sb = new StringBuilder();
            sb.append("路线" + ( i + 1) + ": ");
            for (int v : pathList.get(i)) {
                sb.append(vexs[v].name);
                if (j != vexNum) {
                    sb.append(" -> ");
                }
                j++;
            }
            paths[i] = sb.toString();
        }
        return paths;
    }
    void dfs(int v, boolean[] visited, ArrayList<Integer> path) {
        if (path.size() == vexNum) {
            pathList.add(new ArrayList<>(path));
            return;
        }

        for (int i = 0; i < vexNum; i++) {
            if(! visited[i] && adjMatrix[v][i] != 0) {
                visited[i] = true;
                path.add(i);
                dfs(i, visited, path);
                visited[i] = false;
                path.remove(path.size() - 1);
            }
        }
    }

    void Dijkstra(int start, int end) {
        class Node implements Comparable<Node> {
            int vertex,distance;
            Node(int vertex, int distance) {
                this.vertex = vertex;
                this.distance = distance;
            }

            @Override
            public int compareTo(Node o) {
                return Integer.compare(this.distance, o.distance);
            }

        }
        int[] dist = new int[vexNum];
        int[] path = new int[vexNum];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(path, -1);
        dist[start] = 0;
        path[start] = start;
        PriorityQueue<Node> minHeap = new PriorityQueue<>();
        minHeap.offer(new Node(start, 0));

        while (!minHeap.isEmpty()) {
            Node node = minHeap.poll();
            int u = node.vertex;
            int uDist = node.distance;

            if(uDist > dist[u]) {
                continue;
            }
            for (int v = 0; v < vexNum; v++) {
                int weight = adjMatrix[u][v];
                if( weight == 0) {
                    continue;
                }
                if (dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    path[v] = u;
                    minHeap.offer(new Node(v, dist[v]));
                }
            }
        }

        int i = end;
        StringBuilder sb = new StringBuilder();
        sb.append(vexs[end].name);
        while (path[i] != i) {
            sb.insert(0, vexs[path[i]].name + " -> ");
            i = path[i];
        }
        System.out.println("最短路线为: " + sb);
        System.out.println("最短距离: " + dist[end]);
    }

    List<Edge> findMinTree() {
        boolean[] visited = new boolean[vexNum];
        PriorityQueue<Edge> pq = new PriorityQueue<>((a, b) -> a.weight - b.weight);
        List<Edge> mstEdges = new ArrayList<>();
        visited[0] = true;
        for (int i = 0; i < vexNum; i++) {
            if(adjMatrix[0][i] != 0) {
                pq.offer(new Edge(0, i, adjMatrix[0][i]));
            }
        }
        while (!pq.isEmpty()) {
            Edge edge = pq.poll();
            int u = edge.either();
            if(visited[u]) {
                u = edge.other(u);
                if(visited[u]){
                    continue;
                }
            }
            visited[u] = true;
            mstEdges.add(edge);
            for (int i = 0; i < vexNum; i++) {
                if(adjMatrix[u][i] != 0 && !visited[i]) {
                    pq.add(new Edge(u, i, adjMatrix[u][i]));
                }
            }
        }
        return mstEdges.size() == vexNum - 1 ? mstEdges : null;
    }
}