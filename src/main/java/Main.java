import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {


    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String CSVSource = "data.csv";
        String XMLSource = "data.xml";

        // преобразование csv → json
        List<Employee> listFromCSV = parseCSV(columnMapping, CSVSource);
        String json = listToJson(listFromCSV);
        writeString(json, "data.json");
        filePrintOut("data.json");


        // преобразование xml → json
        List<Employee> listFromXML = new ArrayList<>();
        try {
            listFromXML = parseXML(XMLSource);
        } catch (ParserConfigurationException | SAXException | IOException | NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        writeString(listToJson(listFromXML), "data2.json");
        filePrintOut("data2.json");




    }

    private static List<Employee> parseCSV(String[] columnMapping, String source) {
        List<Employee> list = new ArrayList<>();
        ColumnPositionMappingStrategy<Employee> fieldSet = new ColumnPositionMappingStrategy<>();
        fieldSet.setType(Employee.class);
        fieldSet.setColumnMapping(columnMapping);

        try (CSVReader reader = new CSVReader(new FileReader(source))){
            CsvToBean<Employee> CSVExtractor = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(fieldSet).build();
            list = CSVExtractor.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType(); // непонятно, что происходит!

        return new GsonBuilder().setPrettyPrinting().create().toJson(list, listType);
    }

    private static List<Employee> parseXML(String source)
            throws ParserConfigurationException, SAXException, IOException, NumberFormatException {
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory enterprise = DocumentBuilderFactory.newInstance();
        Document XMLObject = enterprise.newDocumentBuilder().parse(new File(source));

        NodeList content = XMLObject.getDocumentElement().getChildNodes();
        for (int i = 0; i < content.getLength(); i++) {
            Node node = content.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element employee = (Element) node;
                Employee next = new Employee();

                next.id = Long.parseLong(extractProperty(employee, "id"));
                next.firstName = extractProperty(employee, "firstName");
                next.lastName = extractProperty(employee, "lastName");
                next.country = extractProperty(employee, "country");
                next.age = Integer.parseInt(extractProperty(employee, "age"));

                list.add(next);
            }
        }
        return list;
    }



    private static void writeString(String jsonString, String destination) {
        try(FileWriter writer = new FileWriter(destination, false)) {
            writer.write(jsonString);
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void filePrintOut(String source) {
        try (FileReader reader = new FileReader(source)) {
            int ch;
            while ((ch = reader.read()) != -1)
                System.out.print((char) ch);
            System.out.println();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String extractProperty(Element element, String field) {
        return element.getElementsByTagName(field).item(0).getTextContent();
    }
}
