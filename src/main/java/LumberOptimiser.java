import java.io.*;
import java.util.*;

public class LumberOptimiser{
    private Map<LumberType, Map<Integer, Integer>> lumberMap;
    private int maxElementLength;
    private float stock;

    LumberOptimiser(File file, int maxElementLength, int stockPers){
        this.lumberMap = new TreeMap<>(LumberType::compareTo);
        this.maxElementLength = maxElementLength;
        stock = (float) (1 + stockPers / 100.0);
        parseTxtFiles(file);
    }

    private void parseTxtFiles(File file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF_16"));

            String line;
            String[] lineArr;
            LumberType lumberType;
            int length;
            int num;
            Map<Integer, Integer> lengthAndNumberMap;
            while ((line = reader.readLine()) != null) {
                if (line.matches("\\d+\\t\\d+\\t\\d+\\t\\d+")) {
                    lineArr = line.split("\t");
                    lumberType = new LumberType(Integer.parseInt(lineArr[0]), Integer.parseInt(lineArr[1]));
                    length = Integer.parseInt(lineArr[2]);
                    num = Integer.parseInt(lineArr[3]);

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

            reader.close();
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
            map = fillLength(map, maxLength);
            counter++;
        }

        return counter;
    }

    private Map<Integer, Integer> fillLength(Map<Integer, Integer> map, int length){
        int len = map.keySet().stream().findFirst().get();
        while (len != 0){
            length -= len;
            map.compute(len, (key, val) -> val-1);
            if (map.get(len)==0) map.remove(len);
            int finalLength = length;
            len = map.keySet().stream().filter(key -> key<= finalLength).findFirst().orElse(0);
        }
        return map;
    }

    public boolean checkMaxLength(){
        if (lumberMap.isEmpty()
                || (lumberMap.values().stream().anyMatch(map -> map.keySet().stream().anyMatch(len -> len > maxElementLength))))
            return false;
        return true;
    }
}
