import java.util.*;

public class Utils {
    private int columns;
    private int rows;
    private int[][] arr;
    private List<int[][]> track;
    private int imageAmount;

    //构造函数
    Utils(int[][] arr, List<int[][]> track) {
        this.arr = arr;
        this.track = track;
        imageAmount = Config.IMAGE_AMOUNT;
        columns = Config.COLUMNS;
        rows = Config.ROWS;
    }

    //初始化图片位置数组
    public void initArray() {
        Random random = new Random();
        Map<Integer, Integer> countMap = new HashMap<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int num = random.nextInt(imageAmount);
                arr[i][j] = num;
                countMap.put(num, countMap.getOrDefault(num, 0) + 1);  //统计每一个数字出现的次数
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int num = arr[i][j];
                if (countMap.get(num) % 2 != 0) {   //若某一个数字出现的次数为奇数，则找到另一个出现次数同为奇数的图片改为前者，使二者均出现偶数次
                    for (int m = 0; m < imageAmount; m++) {
                        if (countMap.getOrDefault(m, 0) % 2 != 0) {
                            countMap.put(m, countMap.get(m) + 1);
                            countMap.put(num, countMap.get(num) - 1);
                            arr[i][j] = m;
                            break;
                        }
                    }
                }
            }
        }
//        for (int i = 0; i < columns; i++) {
//            arr[1][i] = -1;
//        }
//        arr[0] = new int[]{-1,1,2,3,4,-1,6,7,8,9,-1,-1,-1,-1,7,5};
    }

    //重排数组
    public void rearrange(){
        Integer[] arr1 = new Integer[rows * columns];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                arr1[index++] = arr[i][j];
            }
        }
        List<Integer> list = Arrays.asList(arr1);
        Collections.shuffle(list);
        index = 0;
        for (int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                arr[i][j] = list.get(index++);
            }
        }
    }

    private boolean found = false; // 表示是否找到目标
    public boolean hint() {
        List<int[]> points = new ArrayList<>();
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}};  //用于起点遍历的三个方向  右 下 左， 一开始不往上是为了避免重复查找
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int val = arr[i][j];
                if(val != -1) {
                    points.clear();
                    points.add(new int[]{i, j});
                    found = false;
                    for (int k = 0; k < 3; k++) {
                        dfs(i + directions[k][0], j + directions[k][1], 0,0, points, i, j);
                        if(found) {        //找到后将points转化为track，两两相连
                            track.clear();
                            for (int l = 0; l < points.size() - 1; l++) {
                                track.add(new int[][]{points.get(l), points.get(l + 1)});
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 深度优先遍历寻找连通点
     * @param x         dfs所遍历的当前点的横坐标
     * @param y         dfs所遍历的当前点的纵坐标
     * @param direct    遍历的方向  0右 1下 2左 3上
     * @param turn      拐弯的次数，三条直线即turn不能大于2
     * @param points    记录起点、终点、拐点坐标
     * @param X         起点的横坐标
     * @param Y         起点的纵坐标
     */
    private void dfs(int x, int y, int direct, int turn, List<int[]> points, int X, int Y) {
        if (found || x < 0 || x >= rows || y < 0 || y >= columns || x == X && y == Y) {
            return;                 //如果已经找到或者点越界则返回
        }
        int val = arr[X][Y];
        if (arr[x][y] == val) {     //符合条件
            points.add(new int[]{x, y});
            found = true;
            return;
        }
        if (arr[x][y] != -1) {     //不连通则返回
            return;
        }

        switch (direct) {           //根据参数direct提供的方向继续递归
            case 0: // 右
                dfs(x, y + 1, direct, turn, points, X, Y);
                break;
            case 1: // 下
                dfs(x + 1, y, direct, turn, points, X, Y);
                break;
            case 2: // 左
                dfs(x, y - 1, direct, turn, points, X, Y);
                break;
            case 3: // 上
                dfs(x - 1, y, direct, turn, points, X, Y);
                break;
        }

        if (!found && turn < 2) {     //如果还没找到且拐弯次数少于2，则进行拐弯
            points.add(new int[]{x, y});

            int next_direct = (direct + 1) % 4;          //第一次拐弯
            dfs(x, y, next_direct, turn + 1, points, X, Y);

            if (!found) {
                next_direct = (direct - 1 + 4) % 4;     //第二次拐弯
                dfs(x, y, next_direct, turn + 1, points, X, Y);
            }

            if (!found) {       //若两次拐弯都没找到，则恢复现场
                points.remove(points.size() - 1);
            }
        }
    }
    //判断是否获胜
    public boolean victory(){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (arr[i][j] != -1 ){
                    return false;
                }
            }
        }
        return true;
    }

    //判断是否可以消子
    public boolean isLink(int x1, int y1, int x2, int y2) {
        track.clear();
        if (arr[x1][y1] != arr[x2][y2]) {
            return false;
        }
        //判断是否一条直接连通
        if (isDirectConnect(x1, y1, x2, y2)) {
            track.add(new int[][]{{x1, y1}, {x2, y2}});
            return true;
        }
        //判断是否同在边界上
        if (x1 == 0 && x2 == 0) {
            track.add(new int[][]{{x1, y1}, {-1, y1}});
            track.add(new int[][]{{x2, y2}, {-1, y2}});
            track.add(new int[][]{{-1, y1}, {-1, y2}});
            return true;
        }
        if (x1 == rows - 1 && x2 == rows - 1) {
            track.add(new int[][]{{x1, y1}, {rows, y1}});
            track.add(new int[][]{{x2, y2}, {rows, y2}});
            track.add(new int[][]{{rows, y1}, {rows, y2}});
            return true;
        }
        if (y1 == 0 && y2 == 0) {
            track.add(new int[][]{{x1, y1}, {x1, -1}});
            track.add(new int[][]{{x2, y2}, {x2, -1}});
            track.add(new int[][]{{x1, -1}, {x2, -1}});
            return true;
        }
        if (y1 == columns - 1 && y2 == columns - 1) {
            track.add(new int[][]{{x1, y1}, {x1, columns}});
            track.add(new int[][]{{x2, y2}, {x2, columns}});
            track.add(new int[][]{{x1, columns}, {x2, columns}});
            return true;
        }

        //判断是否两条直线连通（L型）
        if (isLConnect(x1, y1, x2, y2)) {
            return true;
        }
        //判断是否三条直线连通: 将一个图片分别在横向、竖向平移，然后判断其与另一图片是否是两条直线连通
        for (int x = 0; x < columns; x++) {
            if (x < x1 && !isDirectConnect(x - 1, y1, x1, y1) || x > x1 && !isDirectConnect(x + 1, y1, x1, y1)) {
                System.out.print("  x" + x);
                continue;
            }
            if (isLConnect(x, y1, x2, y2)) {
                track.add(new int[][]{{x1, y1}, {x, y1}});
                return true;
            }
        }
        for (int y = 0; y < rows; y++) {
            if (y < y1 && !isDirectConnect(x1, y - 1, x1, y1) || y > y1 && !isDirectConnect(x1, y + 1, x1, y1)) {
                System.out.print("  y" + y);
                continue;
            }
            if (isLConnect(x1, y, x2, y2)) {
                track.add(new int[][]{{x1, y1}, {x1, y}});
                return true;
            }
        }
        return false;
    }

    //判断两点是否一条直线连通
    private boolean isDirectConnect(int x1, int y1, int x2, int y2) {
        if (x1 == x2) {
            if (y1 > y2) {
                int t = y1;
                y1 = y2;
                y2 = t;
            }
            for (int y = y1 + 1; y < y2; y++) {
                if (arr[x1][y] != -1) {
                    return false;
                }
            }
            return true;
        }
        if (y1 == y2) {
            if (x1 > x2) {
                int t = x1;
                x1 = x2;
                x2 = t;
            }
            for (int x = x1 + 1; x < x2; x++) {
                if (arr[x][y1] != -1) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    //判断两点是否L型连通（两条直线）
    private boolean isLConnect(int x1, int y1, int x2, int y2) {
        if (x1 > x2) {
            return isLConnect(x2, y2, x1, y1);
        }
        if (y1 < y2) {
            if (isDirectConnect(x1, y1, x1, y2 + 1) && isDirectConnect(x2, y2, x1 - 1, y2)) {
                track.add(new int[][]{{x1, y1}, {x1, y2}});
                track.add(new int[][]{{x2, y2}, {x1, y2}});
                return true;
            }
            if (isDirectConnect(x1, y1, x2 + 1, y1) && isDirectConnect(x2, y2, x2, y1 - 1)) {
                track.add(new int[][]{{x1, y1}, {x2, y1}});
                track.add(new int[][]{{x2, y2}, {x2, y1}});
                return true;
            }
        } else {
            if (isDirectConnect(x1, y1, x1, y2 - 1) && isDirectConnect(x2, y2, x1 - 1, y2)) {
                track.add(new int[][]{{x1, y1}, {x1, y2}});
                track.add(new int[][]{{x2, y2}, {x1, y2}});
                return true;
            }
            if (isDirectConnect(x1, y1, x2 + 1, y1) && isDirectConnect(x2, y2, x2, y1 + 1)) {
                track.add(new int[][]{{x1, y1}, {x2, y1}});
                track.add(new int[][]{{x2, y2}, {x2, y1}});
                return true;
            }
        }
        return false;
    }

}