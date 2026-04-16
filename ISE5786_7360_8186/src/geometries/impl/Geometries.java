package geometries.impl;

import geometries.api.Intersectable;
import java.util.ArrayList;
import java.util.List;
import primitives.Point;
import primitives.Ray;

/**
 * Composite class for all geometries in the scene.
 * This class implements the Composite design pattern.
 * @author Sara
 */
public class Geometries extends Intersectable {
    /**
     * List of intersectable geometries
     */
    private final List<Intersectable> _geometries = new ArrayList<>();

    /**
     * Default constructor, initializes an empty list of geometries.
     */
    public Geometries() {
    }

    /**
     * Constructor with varargs to initialize the list with geometries.
     * @param geometries list of geometries to add
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds geometries to the existing list.
     * @param geometries list of geometries to add
     */
    public void add(Intersectable... geometries) {
        if (geometries != null) {
            _geometries.addAll(List.of(geometries));
        }
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> result = null;

        for (Intersectable item : _geometries) {
            List<Point> itemIntersections = item.findIntersections(ray);

            if (itemIntersections != null) {
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.addAll(itemIntersections);
            }
        }
        return result;
    }
}