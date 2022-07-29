import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

    private static final String COMMA_DELIMITER = ",";

    public static List<List<String>> readCSVFile(String filePath, Integer limit) {

        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER, limit);
                records.add(new ArrayList(Arrays.asList(values)));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long columnsLength = records.get(0).size();
        for (int i = 1; i < records.size(); i++) {
            long lineLength = records.get(i).size();
            for (int j = 0; j < columnsLength - lineLength; j++) {
                records.get(i).add("");
            }
        }

        return records;
    }

    public static String readFile(String filePath) {

        StringBuffer sb = new StringBuffer();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

}
