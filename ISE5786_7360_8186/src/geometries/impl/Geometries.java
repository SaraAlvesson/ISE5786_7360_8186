package geometries.impl;

import geometries.api.Intersectable;
import java.util.LinkedList;
import java.util.List;
import primitives.Point;
import primitives.Ray;

/**
 * Composite class for all geometries
 */
public class Geometries extends Intersectable {
    private final List<Intersectable> _geometries = new LinkedList<>();

    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

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
                if (result == null) result = new LinkedList<>();
                result.addAll(itemIntersections);
            }
        }
        return result;
    }
}