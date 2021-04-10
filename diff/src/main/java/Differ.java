import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Differ {

    private static final String HTML_START =
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "  <meta charset=\"utf-8\">\n" +
            "  <title>The HTML5 Herald</title>\n" +
            "    <style type=\"text/css\">\n" +
            "      .split { width: 50%; position: fixed; }\n" +
            "      .left { left: 0; }\n" +
            "      .right { right: 0; }\n" +
            "      .added   { background-color: #90EE90 }\n" +
            "      .deleted { background-color: #DCDCDC }\n" +
            "      .changed { background-color: #87CEFA }\n" +
            "      .not-changed { background-color: #ffffff }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n";
    
    private static final String HTML_END = "</body>\n</html>";


    public static void getDiff(String fileName1, String fileName2, String diffFileName) {
        try (
                BufferedReader file1 = new BufferedReader(new FileReader(fileName1));
                BufferedReader file2 = new BufferedReader(new FileReader(fileName2));
                BufferedWriter diffFile = new BufferedWriter(new FileWriter(diffFileName));
        ) {
            buildDiffFile(file1, file2, diffFile);
        } catch (FileNotFoundException e) {
            System.out.println("file not found: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void buildDiffFile(BufferedReader file1, BufferedReader file2, BufferedWriter diffFile) throws IOException {
        String[] linesOld = file1.lines().toArray(String[]::new);
        String[] linesNew = file2.lines().toArray(String[]::new);

        List<int[]> indexes = getLongestCommonSubsequenceIndexes(linesOld, linesNew);
        List<LineType[]> types = getLineTypes(indexes, linesOld.length, linesNew.length);
        LineType[] typesOld = types.get(0);
        LineType[] typesNew = types.get(1);

        diffFile.append(HTML_START).append("  <div class=\"split left\">\n");

        for (int i = 0; i < linesOld.length; ++i) {

            String appendStr =
                    "    <span class=\"" +
                    typesOld[i].getType() +
                    "\">\n" +
                    "      " + linesOld[i] + "<br>" +
                    "    </span>\n";
            diffFile.append(appendStr);
        }

        diffFile.append("  </div>\n").append("  <div class=\"split right\">\n");

        for (int i = 0; i < linesNew.length; ++i) {
            String appendStr =
                    "    <span class=\"" +
                    typesNew[i].getType() +
                    "\">\n" +
                    "      " + linesNew[i] + "<br>" +
                    "    </span>\n";
            diffFile.append(appendStr);
        }

        diffFile.append("  </div>\n").append(HTML_END);
    }

    private enum LineType {
        ADDED("added"),
        DELETED("deleted"),
        CHANGED("changed"),
        NOT_CHANGED("not-changed");

        private final String type;

        public String getType() {
            return type;
        }

        LineType(String type) {
            this.type = type;
        }
    }

    private static List<LineType[]> getLineTypes(List<int[]> notChanged, int m, int n) {
        LineType[] typesOld = new LineType[m];
        LineType[] typesNew = new LineType[n];

        int oldIdx = 0;
        int newIdx = 0;
        for (int[] indexes : notChanged) {
            int notChangedLineOld = indexes[0];
            int notChangedLineNew = indexes[1];

            while (oldIdx < notChangedLineOld && newIdx < notChangedLineNew) {
                typesOld[oldIdx] = LineType.CHANGED;
                typesNew[newIdx] = LineType.CHANGED;
                oldIdx++;
                newIdx++;
            }
            while (oldIdx < notChangedLineOld) {
                typesOld[oldIdx] = LineType.DELETED;
                oldIdx++;
            }
            while (newIdx < notChangedLineNew) {
                typesNew[newIdx] = LineType.ADDED;
                newIdx++;
            }
            typesOld[oldIdx++] = LineType.NOT_CHANGED;
            typesNew[newIdx++] = LineType.NOT_CHANGED;
        }
        
        while (oldIdx < m) {
            typesOld[oldIdx] = LineType.DELETED;
            oldIdx++;
        }
        while (newIdx < n) {
            typesNew[newIdx] = LineType.ADDED;
            newIdx++;
        }

        return Arrays.asList(typesOld, typesNew);
    }

    private static List<int[]> getLongestCommonSubsequenceIndexes(String[] linesOld, String[] linesNew) {
        int m = linesOld.length;
        int n = linesNew.length;

        int[][] length = new int[m][n]; // LCS length
        for (int i = 0; i < m; ++i) {
            length[i][0] = 0;
        }
        for (int i = 0; i < n; ++i) {
            length[0][i] = 0;
        }

        int[][] prevI = new int[m][n];
        int[][] prevJ = new int[m][n];

        for (int i = 1; i < m; ++i) {
            for (int j = 1; j < n; ++j) {
                if (linesOld[i].equals(linesNew[j])) {
                    length[i][j] = length[i - 1][j - 1] + 1;
                    prevI[i][j] = i - 1;
                    prevJ[i][j] = j - 1;
                } else if (length[i - 1][j] >= length[i][j - 1]) {
                    length[i][j] = length[i - 1][j];
                    prevI[i][j] = i - 1;
                    prevJ[i][j] = j;
                } else {
                    length[i][j] = length[i][j - 1];
                    prevI[i][j] = i;
                    prevJ[i][j] = j - 1;
                }
            }
        }

        return collectIndexes(m - 1, n - 1, prevI, prevJ);
    }

    private static List<int[]> collectIndexes(int i, int j, int[][] prevI, int[][] prevJ) {
        if (i == 0 || j == 0) {
            List<int[]> res = new ArrayList<>();
            res.add(new int[] {i, j});
            return res;
        }

        if (prevI[i][j] == i - 1 && prevJ[i][j] == j - 1) {
            List<int[]> res = collectIndexes(i - 1, j - 1, prevI, prevJ);
            res.add(new int[] {i, j});
            return res;
        } else if (prevI[i][j] == i - 1 && prevJ[i][j] == j) {
            return collectIndexes(i - 1, j, prevI, prevJ);
        } else {
            return collectIndexes(i, j - 1, prevI, prevJ);
        }
    }
}
