import org.hibernate.Session;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.type.Type;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;


public class MappingManager {
    public static void updateClassMapping(CustomizableEntityManager entityManager) {
        try {
            Session session = HibernateUtil.getInstance().getCurrentSession();
            Class<? extends CustomizableEntity> entityClass = entityManager.getEntityClass();
            String class_name = entityClass.getSimpleName();
            String file = entityClass.getResource("resources/" + class_name + ".hbm.xml").getPath();
            file="C:/Users/Marcos/hibernate/src/resources/Contact.hbm.xml";

            Document document = XMLUtil.loadDocument(file);
            NodeList list= document.getChildNodes();
            NodeList componentTags = document.getElementsByTagName("dynamic-component");
            Node node = componentTags.item(0);
            XMLUtil.removeChildren(node);

            Iterator propertyIterator = entityManager.getCustomProperties().getPropertyIterator();
            while (propertyIterator.hasNext()) {
                Property property = (Property) propertyIterator.next();
                Element element = createPropertyElement(document, property);
                node.appendChild(element);
            }

            XMLUtil.saveDocument(document, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Element createPropertyElement(Document document, Property property) {
        Element element = document.createElement("property");
        SimpleValue simpleValue = (SimpleValue) property.getValue();
        String type = simpleValue.getTypeName();


        element.setAttribute("name", property.getName());
        element.setAttribute("column", ((Column)
                property.getColumnIterator().next()).getName());
        element.setAttribute("type",
                type);
        element.setAttribute("not-null", String.valueOf(false));

        return element;
    }

    public Document newDocumentFromString(String xmlString) {


        DocumentBuilderFactory factory = null;
        DocumentBuilder builder = null;
        Document ret = null;

        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        try {
            ret = builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
