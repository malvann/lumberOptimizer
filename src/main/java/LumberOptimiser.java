import java.io.*;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_16;

public class LumberOptimiser{
    private final Map<LumberType, Map<Integer, Integer>> lumberMap;
    private final int maxElementLength;
    private final float stock;

    LumberOptimiser(File[] lumberFiles, int maxElementLength, int stockPers){
        this.lumberMap = new TreeMap<>(LumberType::compareTo);
        this.maxElementLength = maxElementLength;
        stock = (float) (1 + stockPers / 100.0);
        if (lumberFiles.length != 0) fileWalker(lumberFiles);
    }

        private void fileWalker(File[] files){
            for (File currentFile: files) {
                parseLumberLathingFiles(currentFile);
            }
        }

        private void parseLumberLathingFiles(File file){
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),UTF_16))){
                String line;
                String[] lineArr;
                LumberType lumberType;
                int length;
                int num;
                Map<Integer, Integer> lengthAndNumberMap;

                while ((line = reader.readLine()) != null) {
                    if (line.matches("\\d+\\t\\d+\\t\\d+\\t\\d+|\\d+\\t\\d+\\t\\d+")) {
                        lineArr = line.split("\t");
                        lumberType = new LumberType(Integer.parseInt(lineArr[0]), Integer.parseInt(lineArr[1]));

                        //for lumber file
                        if (lineArr.length==4){
                            length = Integer.parseInt(lineArr[2]);
                            num = Integer.parseInt(lineArr[3]);
                        }

                        //for lathing file
                        else {
                            length = 6000;
                            num = (int) Math.round(Double.parseDouble(lineArr[2])/6000);
                        }

                        if (lumberMap.containsKey(lumberType)) {
                            lengthAndNumberMap = lumberMap.get(lumberType);
                            int finalNum = num;
                            lengthAndNumberMap.computeIfPresent(length, (integer, integer2) -> integer2 + finalNum);
                            lengthAndNumberMap.putIfAbsent(length, num);
                        } else {
                            lengthAndNumberMap = new TreeMap<>((o1, o2) -> o2-o1);
                            lengthAndNumberMap.put(length, num);
                        }
                        lumberMap.put(lumberType, lengthAndNumberMap);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public List<String> getOptimizeList(){
        List<String> optimizedLumberList = new ArrayList<>();
        optimizedLumberList.add("Сечение, мм\tДлина, мм\tКоличество, шт");
            
        int numberOfMaxElements;
        for (Map.Entry<LumberType, Map<Integer, Integer>> entry: lumberMap.entrySet()) {
            numberOfMaxElements = Math.round(optimization(entry.getValue(), maxElementLength) * stock);
            String row = entry.getKey().toString()+"\t\tx"+maxElementLength+"\t\t- "+numberOfMaxElements;
            optimizedLumberList.add(row);
        }

        optimizedLumberList.add("\nИспользуемый коэффициент запаса "+ stock);
        return optimizedLumberList;
    }

    private int optimization(Map<Integer, Integer> map, int maxLength) {
        int counter = 0;

        if (map.containsKey(6000)) counter = map.remove(6000);
        while (!map.keySet().isEmpty()){
            int length = maxLength;
            int len = map.keySet().iterator().next();
            while (len != 0){
                length -= len;
                map.compute(len, (key, val) -> val-1);
                if (map.get(len)==0) map.remove(len);
                int finalLength = length;
                len = map.keySet().stream().filter(key -> key<= finalLength).findFirst().orElse(0);
            }

            counter++;
        }

        return counter;
    }

    public boolean checkMaxLength(){
        return !lumberMap.isEmpty()
                && (lumberMap.values().stream().noneMatch(map -> map.keySet().stream().anyMatch(len -> len > maxElementLength)));
    }
}
