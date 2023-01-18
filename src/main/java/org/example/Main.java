package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String fileJsonName = "data.json";
        String fileXmlName = "data.xml";
        String fileNameJsonX = "dataX.json";

        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(fileJsonName, json);

        List<Employee> listXML = parseXML(fileXmlName);
        String xml = listToJson(listXML);
        writeString(fileNameJsonX, xml);

    }

    //Метод parseCSV()
    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
// Полученный список преобразуйте в строчку в формате JSON.

    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return new Gson().toJson(list, listType);
    }

    //    запишите полученный JSON в файл с помощью метода writeString()
    public static void writeString(String path, String text) {
        File file = new File(path);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file, false)) {
                writer.write(text);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static List<Employee> parseXML(String fileName) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            Node root = doc.getDocumentElement();
            List<Employee> list = new ArrayList<>();
            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Employee employee = new Employee();
                    NodeList childNodes = node.getChildNodes();
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node cNode = childNodes.item(j);
                        if (cNode.getNodeType() == Node.ELEMENT_NODE) {
                            String content = cNode.getLastChild().getTextContent().trim();
                            switch (cNode.getNodeName()) {
                                case "id" -> employee.id = Long.parseLong(content);
                                case "firstName" -> employee.firstName = content;
                                case "lastName" -> employee.lastName = content;
                                case "country" -> employee.country = content;
                                case "age" -> employee.age = Integer.parseInt(content);
                            }
                        }
                    }
                    list.add(employee);
                }
            }

            return list;
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}

