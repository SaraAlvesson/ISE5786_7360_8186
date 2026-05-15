package scene;

import geometries.api.Geometry;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import java.io.File;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Parser for Scene objects from XML files.
 * Updated for Stage 7 to support light sources and material properties.
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
                Element alElement = (Element) alList.item(0);
                String alColorStr = alElement.getAttribute("color");
                String kAStr = alElement.getAttribute("ka");
                double kA = kAStr.isEmpty() ? 1.0 : Double.parseDouble(kAStr);
                scene.setAmbientLight(new AmbientLight(parseColor(alColorStr), kA));
            }

            // Parse Geometries and Materials (Stage 7 Update)
            parseGeometries(sceneElement, scene);

            // Parse External Light Sources (Stage 7 Update)
            parseLights(sceneElement, scene);

            return scene;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing XML scene file", e);
        }
    }

    private static void parseGeometries(Element sceneElement, Scene scene) {
        NodeList geoGroupList = sceneElement.getElementsByTagName("geometries");
        if (geoGroupList.getLength() > 0) {
            NodeList geometries = geoGroupList.item(0).getChildNodes();
            for (int i = 0; i < geometries.getLength(); i++) {
                Node node = geometries.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) node;
                    Geometry geo = null;
                    switch (el.getTagName()) {
                        case "sphere" -> geo = new Sphere(
                                parsePoint(el.getAttribute("center")),
                                Double.parseDouble(el.getAttribute("radius")));
                        case "triangle" -> geo = new Triangle(
                                parsePoint(el.getAttribute("p0")),
                                parsePoint(el.getAttribute("p1")),
                                parsePoint(el.getAttribute("p2")));
                    }

                    if (geo != null) {
                        // Parse Emission
                        String emission = el.getAttribute("emission");
                        if (!emission.isEmpty()) geo.setEmission(parseColor(emission));

                        // Parse Material
                        Material mat = new Material();
                        String kD = el.getAttribute("kd");
                        if (!kD.isEmpty()) mat.setKD(Double.parseDouble(kD));
                        String kS = el.getAttribute("ks");
                        if (!kS.isEmpty()) mat.setKS(Double.parseDouble(kS));
                        String nShininess = el.getAttribute("n-shininess");
                        if (!nShininess.isEmpty()) mat.setShininess(Integer.parseInt(nShininess));
                        geo.setMaterial(mat);

                        scene.geometries.add(geo);
                    }
                }
            }
        }
    }

    private static void parseLights(Element sceneElement, Scene scene) {
        NodeList lightGroupList = sceneElement.getElementsByTagName("lights");
        if (lightGroupList.getLength() > 0) {
            NodeList lights = lightGroupList.item(0).getChildNodes();
            for (int i = 0; i < lights.getLength(); i++) {
                Node node = lights.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) node;
                    Color intensity = parseColor(el.getAttribute("color"));
                    switch (el.getTagName()) {
                        case "directional-light" -> scene.lights.add(new DirectionalLight(
                                intensity, parseVector(el.getAttribute("direction"))));
                        case "point-light" -> {
                            PointLight pl = new PointLight(intensity, parsePoint(el.getAttribute("position")));
                            String kL = el.getAttribute("kl");
                            if (!kL.isEmpty()) pl.setKl(Double.parseDouble(kL));
                            String kQ = el.getAttribute("kq");
                            if (!kQ.isEmpty()) pl.setKq(Double.parseDouble(kQ));
                            scene.lights.add(pl);
                        }
                        case "spot-light" -> {
                            SpotLight sl = new SpotLight(intensity,
                                    parsePoint(el.getAttribute("position")),
                                    parseVector(el.getAttribute("direction")));
                            String kL = el.getAttribute("kl");
                            if (!kL.isEmpty()) sl.setKl(Double.parseDouble(kL));
                            String kQ = el.getAttribute("kq");
                            if (!kQ.isEmpty()) sl.setKq(Double.parseDouble(kQ));
                            String nBeam = el.getAttribute("narrow-beam");
                            if (!nBeam.isEmpty()) sl.setNarrowBeam(Double.parseDouble(nBeam));
                            scene.lights.add(sl);
                        }
                    }
                }
            }
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

    private static Vector parseVector(String str) {
        String[] parts = str.split("\\s+");
        return new Vector(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
    }
}