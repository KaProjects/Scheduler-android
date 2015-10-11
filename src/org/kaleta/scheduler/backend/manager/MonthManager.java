package org.kaleta.scheduler.backend.manager;

import android.os.Environment;
import android.util.Log;
import org.kaleta.scheduler.MyActivity;
import org.kaleta.scheduler.backend.entity.Item;
import org.kaleta.scheduler.backend.entity.Month;
import org.kaleta.scheduler.backend.entity.UserType;
import org.kaleta.scheduler.backend.service.Service;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stanislav Kaleta on 10.10.2015.
 *
 * CRUD for month files
 */
public class MonthManager {

    public void createMonth(Month month) throws ManagerException {
        String fileName = "m" + month.getId() +"-" +month.getName() + ".xml";
        File file = new File(Environment.getExternalStorageDirectory() +"/" +MyActivity.SCHEDULER_DIRECTORY,fileName);
        if (file.exists()){
            String msg = "File "+fileName +" already exists!";
            Log.e(Service.BACKEND_TAG, msg);
            throw new ManagerException(msg);
        }
        try {
            DocumentBuilderFactory bFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = bFactory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element rootE = document.createElement("month");
            document.appendChild(rootE);

            Attr idA = document.createAttribute("id");
            idA.setValue(String.valueOf(month.getId()));
            rootE.setAttributeNode(idA);

            Attr nameA = document.createAttribute("name");
            nameA.setValue(month.getName());
            rootE.setAttributeNode(nameA);

            Element itemsE = document.createElement("items");
            rootE.appendChild(itemsE);

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new FileOutputStream(file));
            transformer.transform(source, result);
            Log.i(Service.BACKEND_TAG, "File " + fileName + " successfully created.");
        } catch (ParserConfigurationException e) {
            Log.e(e.getClass().getName(), e.getMessage());
            throw new ManagerException(e);
        } catch (TransformerConfigurationException e) {
            Log.e(e.getClass().getName(), e.getMessage());
            throw new ManagerException(e);
        } catch (TransformerException e) {
            Log.e(e.getClass().getName(), e.getMessage());
            throw new ManagerException(e);
        } catch (FileNotFoundException e) {
            Log.e(e.getClass().getName(), e.getMessage());
            throw new ManagerException(e);
        }
    }

    public Month retrieveMonth(Integer id) throws ManagerException {
        File dir = new File(Environment.getExternalStorageDirectory(),MyActivity.SCHEDULER_DIRECTORY);
        File file = null;
        for (File f : dir.listFiles()){
            if (f.getName().startsWith("m"+id+"-")){
                file = f;
            }
        }
        if (file == null){
            String msg = "File for month with id="+id+" not found!";
            Log.e(Service.BACKEND_TAG, msg);
            throw new ManagerException(msg);
        }
        Month month = new Month();
        try {
            DocumentBuilderFactory bFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = bFactory.newDocumentBuilder();
            Document document = builder.parse(file);

            month.setId(Integer.valueOf(document.getDocumentElement().getAttribute("id")));
            month.setName(document.getDocumentElement().getAttribute("name"));

            NodeList items = document.getDocumentElement().getElementsByTagName("items").item(0).getChildNodes();
            List<Item> itemList = new ArrayList<Item>();
            for (int i=0;i<items.getLength();i++){
                NamedNodeMap attributes = items.item(i).getAttributes();

                Item item = new Item();
                item.setId(Integer.valueOf(attributes.getNamedItem("id").getNodeValue()));
                item.setDay(Integer.valueOf(attributes.getNamedItem("day").getNodeValue()));
                item.setIncome(Boolean.valueOf(attributes.getNamedItem("income").getNodeValue()));
                item.setType(attributes.getNamedItem("type").getNodeValue());
                item.setDescription(attributes.getNamedItem("description").getNodeValue());
                item.setAmount(BigDecimal.valueOf(Double.parseDouble(attributes.getNamedItem("amount").getNodeValue())));

                itemList.add(item);
            }
            month.getItemList().addAll(itemList);

            return month;
        } catch (ParserConfigurationException e) {
            Log.e(e.getClass().getName(), e.getMessage());
            throw new ManagerException(e);
        } catch (SAXException e) {
            Log.e(e.getClass().getName(), e.getMessage());
            throw new ManagerException(e);
        } catch (IOException e) {
            Log.e(e.getClass().getName(), e.getMessage());
            throw new ManagerException(e);
        }
    }

    public void updateMonth(Month month) throws ManagerException {
        String fileName = "m" + month.getId() +"-" +month.getName() + ".xml";
        File file = new File(Environment.getExternalStorageDirectory() +"/" +MyActivity.SCHEDULER_DIRECTORY,fileName);
        if (!file.exists()){
            String msg = "File for month \""+month.getName()+"\" not found!";
            Log.e(Service.BACKEND_TAG, msg);
            throw new ManagerException(msg);
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            Element rootE = document.getDocumentElement();

            //TODO if edit month is possible then update name here

            Element itemsE = (Element)rootE.getElementsByTagName("items").item(0);
            NodeList itemNodes = itemsE.getChildNodes();
            for (int i=0;i<itemNodes.getLength();i++){
                itemsE.removeChild(itemNodes.item(i));
            }
            for (Item item : month.getItemList()){
                Element itemE = document.createElement("item");

                Attr idA = document.createAttribute("id");
                idA.setValue(String.valueOf(item.getId()));
                itemE.setAttributeNode(idA);

                Attr dayA = document.createAttribute("day");
                dayA.setValue(String.valueOf(item.getDay()));
                itemE.setAttributeNode(dayA);

                Attr incomeA = document.createAttribute("income");
                incomeA.setValue(String.valueOf(item.getIncome()));
                itemE.setAttributeNode(incomeA);

                Attr typeA = document.createAttribute("type");
                typeA.setValue(item.getType());
                itemE.setAttributeNode(typeA);

                Attr descriptionA = document.createAttribute("description");
                descriptionA.setValue(item.getDescription());
                itemE.setAttributeNode(descriptionA);

                Attr amountA = document.createAttribute("amount");
                amountA.setValue(String.valueOf(item.getAmount()));
                itemE.setAttributeNode(amountA);

                itemsE.appendChild(itemE);
            }

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new FileOutputStream(file));
            transformer.transform(source, result);
            Log.i(Service.BACKEND_TAG, "Month \""+month.getName()+"\" successfully updated.");
        } catch (ParserConfigurationException e) {
            Log.e(e.getClass().getName(), e.getMessage());
            throw new ManagerException(e);
        } catch (SAXException e) {
            Log.e(e.getClass().getName(), e.getMessage());
            throw new ManagerException(e);
        } catch (IOException e) {
            Log.e(e.getClass().getName(), e.getMessage());
            throw new ManagerException(e);
        } catch (TransformerConfigurationException e) {
            Log.e(e.getClass().getName(), e.getMessage());
            throw new ManagerException(e);
        } catch (TransformerException e) {
            Log.e(e.getClass().getName(), e.getMessage());
            throw new ManagerException(e);
        }
    }

    public void deleteMonth(Month month) throws ManagerException {
        throw new ManagerException("Method not implemented yet!");
    }
}
