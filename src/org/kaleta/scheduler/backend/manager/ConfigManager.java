package org.kaleta.scheduler.backend.manager;

import android.os.Environment;
import android.util.Log;
import org.kaleta.scheduler.frontend.MainActivity;
import org.kaleta.scheduler.backend.entity.Config;
import org.kaleta.scheduler.backend.entity.ItemType;
import org.kaleta.scheduler.service.Service;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stanislav Kaleta on 10.10.2015.
 *
 * CRUD for SCHEDULER/config.xml
 */
public class ConfigManager {
    public static final String BACKEND_TAG = "back-end-log";

    public void createConfig() throws ManagerException {
        File directory = new File(Environment.getExternalStorageDirectory(), MainActivity.SCHEDULER_DIRECTORY);
        if (!directory.exists()) {
            if (directory.mkdirs()){
                Log.i(ConfigManager.BACKEND_TAG,"Data directory successfully created.");
            } else {
                String msg = "Unable to create data directory in external storage!";
                Log.e("InternalError",msg);
                throw new InternalError(msg);
            }
        }
        for (File f : directory.listFiles()){
            if (f.getName().equals("config.xml")){
                return;
            }
        }
        try {
            DocumentBuilderFactory bFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = bFactory.newDocumentBuilder();
            Document configDoc = builder.newDocument();

            Element rootE = configDoc.createElement("config");
            configDoc.appendChild(rootE);

            Element monthsE = configDoc.createElement("month_ids");
            rootE.appendChild(monthsE);

            Element typesE = configDoc.createElement("types");
            rootE.appendChild(typesE);

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(configDoc);
            File configFile = new File(directory,"config.xml");
            StreamResult result = new StreamResult(configFile);
            transformer.transform(source, result);
            Log.i(ConfigManager.BACKEND_TAG,"File config.xml successfully created.");
        } catch (ParserConfigurationException | TransformerException e) {
            Log.e(e.getClass().getName(),e.getMessage());
            throw new ManagerException(e);
        }

    }

    public Config retrieveConfig() throws ManagerException {
        File file = new File(Environment.getExternalStorageDirectory() +"/" + MainActivity.SCHEDULER_DIRECTORY,"config.xml");
        if (!file.exists()){
            String msg = "File config.xml not found!";
            Log.e(ConfigManager.BACKEND_TAG, msg);
            throw new ManagerException(msg);
        }
        Config config = new Config();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            NodeList ids = document.getDocumentElement().getElementsByTagName("month_ids").item(0).getChildNodes();
            List<Integer> idList = new ArrayList<Integer>();
            for (int i=0;i<ids.getLength();i++){
                String value = ids.item(i).getAttributes().getNamedItem("value").getNodeValue();
                idList.add(Integer.parseInt(value));
            }
            config.getMonthIds().addAll(idList);

            NodeList types = document.getDocumentElement().getElementsByTagName("types").item(0).getChildNodes();
            List<ItemType> typeList = new ArrayList<ItemType>();
            for (int i=0;i<types.getLength();i++){
                ItemType type = new ItemType();
                Node typeNode = types.item(i);

                String name = typeNode.getAttributes().getNamedItem("name").getNodeValue();
                type.setName(name);

                String income = typeNode.getAttributes().getNamedItem("income").getNodeValue();
                type.setIncome(Boolean.parseBoolean(income));

                NodeList descriptions = ((Element)typeNode).getElementsByTagName("descriptions").item(0).getChildNodes();
                List<String> descList = new ArrayList<String>();
                for (int j=0;j<descriptions.getLength();j++){
                    String value = descriptions.item(j).getAttributes().getNamedItem("value").getNodeValue();
                    descList.add(value);
                }
                type.getPreparedDescriptions().addAll(descList);

                typeList.add(type);
            }
            config.getTypes().addAll(typeList);

            return config;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Log.e(e.getClass().getName(), e.getMessage());
            throw new ManagerException(e);
        }
    }

    public void updateConfig(Config config) throws ManagerException {
        File file = new File(Environment.getExternalStorageDirectory() +"/" + MainActivity.SCHEDULER_DIRECTORY,"config.xml");
        if (!file.exists()){
            String msg = "File config.xml not found!";
            Log.e(ConfigManager.BACKEND_TAG, msg);
            throw new ManagerException(msg);
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            Element rootE = document.getDocumentElement();

            Element idsE = (Element)rootE.getElementsByTagName("month_ids").item(0);
            NodeList idNodes = idsE.getChildNodes();
            for (int i=0;i<idNodes.getLength();i++){
                idsE.removeChild(idNodes.item(i));
            }
            for (Integer id : config.getMonthIds()){
                Element idE = document.createElement("id");

                Attr valueA = document.createAttribute("value");
                valueA.setValue(String.valueOf(id));
                idE.setAttributeNode(valueA);

                idsE.appendChild(idE);
            }

            Element typesE = (Element)rootE.getElementsByTagName("types").item(0);
            NodeList typeNodes = typesE.getChildNodes();
            for (int i=0;i<typeNodes.getLength();i++){
                typesE.removeChild(typeNodes.item(i));
            }
            for (ItemType type : config.getTypes()){
                Element typeE = document.createElement("type");

                Attr nameA = document.createAttribute("name");
                nameA.setValue(String.valueOf(type.getName()));
                typeE.setAttributeNode(nameA);

                Attr incomeA = document.createAttribute("income");
                incomeA.setValue(String.valueOf(type.getIncome()));
                typeE.setAttributeNode(incomeA);

                Element descriptionsE = document.createElement("descriptions");
                for (String description : type.getPreparedDescriptions()){
                    Element descriptionE = document.createElement("description");

                    Attr valueA = document.createAttribute("value");
                    valueA.setValue(description);
                    descriptionE.setAttributeNode(valueA);

                    descriptionsE.appendChild(descriptionE);
                }
                typeE.appendChild(descriptionsE);

                typesE.appendChild(typeE);
            }

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
            Log.i(ConfigManager.BACKEND_TAG, "File config.xml successfully updated.");
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            Log.e(e.getClass().getName(), e.getMessage());
            throw new ManagerException(e);
        }
    }

    public void deleteConfig(Config config) throws ManagerException {
        throw new ManagerException("Not possible to delete config file!");
    }
}
