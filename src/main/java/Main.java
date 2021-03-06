import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main(String[] args) {
        // файлы на чтение
        String CSVSource = "data.csv";
        String XMLSource = "data.xml";
        String JSONSource = "new_data.json";

        // преобразование csv → json
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> listFromCSV = parseCSV(columnMapping, CSVSource);
        String jsonRender = listToJson(listFromCSV);
        writeString(jsonRender, "data.json");
        filePrintOut("data.json");

        // преобразование xml → json
        List<Employee> listFromXML = parseXML(XMLSource);
        writeString(listToJson(listFromXML), "data2.json");
        filePrintOut("data2.json");

        // чтение объектов из json
        String newDataJsonContent = readString(JSONSource);
        List<Employee> listFromJson = jsonToList(newDataJsonContent);
        listFromJson.forEach(System.out::println);

        // то же самое одной строкой
        jsonToList(readString(JSONSource)).forEach(System.out::println);
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

    private static List<Employee> parseXML(String source) {
        List<Employee> list = new ArrayList<>();
        try {
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
        } catch (ParserConfigurationException | SAXException | IOException | NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    private static List<Employee> jsonToList(String jsonString) {
        List<Employee> list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            Object thing = parser.parse(jsonString);
            JSONArray content = (JSONArray) thing;
            for (Object o : content) {
                Employee e = new GsonBuilder().create().fromJson(String.valueOf(o), Employee.class);

                // альтернативный вариант через JSON-Simple
//                JSONObject next = (JSONObject) o;
//                Employee e = new Employee();
//                e.id = (long) next.get("id");
//                e.firstName = (String) next.get("firstName");
//                e.lastName = (String) next.get("lastName");
//                e.country = (String) next.get("country");
//                e.age = Integer.parseInt(String.valueOf(next.get("age")));    //?

                list.add(e);
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    // служебные нужности
    private static void writeString(String jsonString, String destination) {
        try(FileWriter writer = new FileWriter(destination, false)) {
            writer.write(jsonString);
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    private static String readString(String source) {
        StringBuilder string = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(source))) {
            String c;
            while ((c = reader.readLine()) != null)
                string.append(c);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return string.toString();
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
