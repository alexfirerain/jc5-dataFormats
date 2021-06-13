import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    static String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
    static String source = "data.csv";
    static ColumnPositionMappingStrategy<Employee> fieldSet = new ColumnPositionMappingStrategy<>();
    static {
        fieldSet.setType(Employee. class);
        fieldSet.setColumnMapping(columnMapping);
    }

    public static void main(String[] args) {


        try (CSVReader reader = new CSVReader(new FileReader(source))){
//            List<String[]> lines = reader.readAll();
//            for (String[] row : lines)
//                System.out.println(Arrays.toString(row));

            CsvToBean<Employee> csvGetter = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(fieldSet).build();

            List<Employee> employees = csvGetter.parse();
            employees.forEach(System.out::println);


        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
