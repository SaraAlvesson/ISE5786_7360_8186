package geometries.impl;

import geometries.api.Intersectable;
import java.util.ArrayList;
import java.util.List;
import primitives.Ray;

/**
 * Composite class for all geometries in the scene.
 * This class implements the Composite design pattern.
 *
 * @author Sara
 */
public class Geometries extends Intersectable {
    /**
     * List of intersectable geometries
     */
    private final List<Intersectable> _geometries = new ArrayList<>();

    /**
     * Constructor with varargs to initialize the list with geometries.
     *
     * @param geometries list of geometries to add
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds geometries to the existing list.
     *
     * @param geometries list of geometries to add
     */
    public void add(Intersectable... geometries) {
        _geometries.addAll(List.of(geometries));
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        List<Intersection> result = null;

        for (Intersectable geometry : _geometries) {
            // Calling the public NVI method for each geometry [cite: 229]
            var intersections = geometry.calcIntersections(ray);

            if (intersections != null) {
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.addAll(intersections);
            }
        }
        return result;
    }
}