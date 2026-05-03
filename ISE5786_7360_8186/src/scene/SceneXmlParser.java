package scene;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import java.io.File;
import lighting.AmbientLight;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import primitives.Color;
import primitives.Point;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Parser for Scene objects from XML files.
 */
public class SceneXmlParser {

    /**
     * Parses an XML file and returns a Scene object.
     *
     * @param filePath path to the XML file
     * @return initialized Scene object
     */
    public static Scene parse(String filePath) {
        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            Element sceneElement = doc.getDocumentElement();
            Scene scene = new Scene(xmlFile.getName().replace(".xml", ""));

            // Parse Background Color
            String bgColorStr = sceneElement.getAttribute("background-color");
            if (!bgColorStr.isEmpty()) {
                scene.setBackground(parseColor(bgColorStr));
            }

            // Parse Ambient Light
            NodeList alList = sceneElement.getElementsByTagName("ambient-light");
            if (alList.getLength() > 0) {
                String alColorStr = ((Element) alList.item(0)).getAttribute("color");
                scene.setAmbientLight(new AmbientLight(parseColor(alColorStr)));
            }

            // Parse Geometries
            NodeList geoGroupList = sceneElement.getElementsByTagName("geometries");
            if (geoGroupList.getLength() > 0) {
                NodeList geometries = geoGroupList.item(0).getChildNodes();
                for (int i = 0; i < geometries.getLength(); i++) {
                    Node node = geometries.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element el = (Element) node;
                        switch (el.getTagName()) {
                            case "sphere" -> scene.geometries.add(new Sphere(
                                    parsePoint(el.getAttribute("center")),
                                    Double.parseDouble(el.getAttribute("radius"))));
                            case "triangle" -> scene.geometries.add(new Triangle(
                                    parsePoint(el.getAttribute("p0")),
                                    parsePoint(el.getAttribute("p1")),
                                    parsePoint(el.getAttribute("p2"))));
                        }
                    }
                }
            }
            return scene;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing XML scene file", e);
        }
    }

    private static Color parseColor(String str) {
        String[] parts = str.split("\\s+");
        return new Color(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
    }

    private static Point parsePoint(String str) {
        String[] parts = str.split("\\s+");
        return new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
    }
}