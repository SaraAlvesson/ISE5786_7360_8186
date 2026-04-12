package geometries.impl;

import geometries.api.Intersectable;
import java.util.ArrayList;
import java.util.List;
import primitives.Point;
import primitives.Ray;

/**
 * Composite class for all geometries
 */
public class Geometries extends Intersectable {
    /** List of intersectable geometries */
    private final List<Intersectable> _geometries = new ArrayList<>();

    /**
     * Constructor with varargs
     * @param geometries list of geometries
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds geometries to the list
     * @param geometries list of geometries
     */
    public void add(Intersectable... geometries) {
        if (geometries != null) {
            _geometries.addAll(List.of(geometries));
        }
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> result = null;

        // Using foreach loop to traverse geometries
        for (Intersectable item : _geometries) {
            List<Point> itemIntersections = item.findIntersections(ray);

            if (itemIntersections != null) {
                // Lazy initialization of the result list
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.addAll(itemIntersections);
            }
        }
        return result;
    }
}