import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    static String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
    static String source = "data.csv";

    public static void main(String[] args) {

        List<Employee> list = parseCSV(columnMapping, source);
        list.forEach(System.out::println);

        String json = listToJson(list);
        System.out.println(json);
        writeString(json);

    }

    private static List<Employee> parseCSV(String[] columnMapping, String source) {
        List<Employee> list = new ArrayList<>();
        ColumnPositionMappingStrategy<Employee> fieldSet = new ColumnPositionMappingStrategy<>();
        fieldSet.setType(Employee. class);
        fieldSet.setColumnMapping(columnMapping);

        try (CSVReader reader = new CSVReader(new FileReader(source))){
            CsvToBean<Employee> csvGetter = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(fieldSet).build();
            list = csvGetter.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String listToJson(List<Employee> list) {
        return null;
    }

    private static void writeString(String jsonString) {

    }
}
