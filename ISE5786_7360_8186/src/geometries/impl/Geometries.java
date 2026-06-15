package geometries.impl;

import geometries.api.BoundingBox;
import geometries.api.Intersectable;
import java.util.ArrayList;
import java.util.Comparator;
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
     * List of intersectable geometries.
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
        updateBoundingBox();
    }

    /**
     * Returns the internal geometries list.
     * Used for BVH automatic construction and tests.
     *
     * @return list of geometries
     */
    public List<Intersectable> getGeometries() {
        return _geometries;
    }

    /**
     * Creates a bounding box that contains all child geometries.
     * If a child has no finite bounding box, it is skipped.
     *
     * @return combined bounding box, or null if no child has a finite box
     */
    @Override
    protected BoundingBox createBoundingBox() {
        BoundingBox result = null;

        for (Intersectable geometry : _geometries) {
            geometry.updateBoundingBox();
            BoundingBox childBox = geometry.getBoundingBox();

            if (childBox == null) {
                continue;
            }

            result = result == null ? childBox : result.combine(childBox);
        }

        return result;
    }

    /**
     * Enables CBR for this composite object and for all child geometries.
     *
     * @return this object
     */
    @Override
    public Intersectable enableCBR() {
        super.enableCBR();

        for (Intersectable geometry : _geometries) {
            geometry.enableCBR();
        }

        return this;
    }

    /**
     * Disables CBR for this composite object and for all child geometries.
     *
     * @return this object
     */
    @Override
    public Intersectable disableCBR() {
        super.disableCBR();

        for (Intersectable geometry : _geometries) {
            geometry.disableCBR();
        }

        return this;
    }

    /**
     * Enables automatic BVH construction.
     * The method reorganizes finite geometries into a hierarchy of Geometries objects.
     * Infinite geometries, which have no finite bounding box, stay outside the hierarchy.
     *
     * @return this Geometries object
     */
    public Geometries enableBVH() {
        buildBVH();
        enableCBR();
        return this;
    }

    /**
     * Builds an automatic BVH tree from the current flat geometries list.
     *
     * @return this Geometries object
     */
    public Geometries buildBVH() {
        List<Intersectable> finiteGeometries = new ArrayList<>();
        List<Intersectable> infiniteGeometries = new ArrayList<>();

        for (Intersectable geometry : _geometries) {
            geometry.updateBoundingBox();

            if (geometry.getBoundingBox() == null) {
                infiniteGeometries.add(geometry);
            } else {
                finiteGeometries.add(geometry);
            }
        }

        _geometries.clear();

        if (!finiteGeometries.isEmpty()) {
            Intersectable bvhRoot = buildBVHRecursive(finiteGeometries);
            _geometries.add(bvhRoot);
        }

        _geometries.addAll(infiniteGeometries);
        updateBoundingBox();

        return this;
    }

    /**
     * Recursive BVH construction.
     * Each step sorts the geometries by the longest axis and splits the list into two groups.
     *
     * @param geometries finite geometries to organize
     * @return a geometry or a composite hierarchy
     */
    private Intersectable buildBVHRecursive(List<Intersectable> geometries) {
        if (geometries.size() == 1) {
            return geometries.get(0);
        }

        int axis = longestAxis(geometries);

        geometries.sort(Comparator.comparingDouble(
                geometry -> centerByAxis(geometry.getBoundingBox(), axis)
        ));

        int middle = geometries.size() / 2;

        List<Intersectable> leftList = new ArrayList<>(geometries.subList(0, middle));
        List<Intersectable> rightList = new ArrayList<>(geometries.subList(middle, geometries.size()));

        Geometries left = new Geometries(buildBVHRecursive(leftList));
        Geometries right = new Geometries(buildBVHRecursive(rightList));

        Geometries node = new Geometries(left, right);
        node.enableCBR();

        return node;
    }

    /**
     * Finds the longest axis of the combined bounding box of a geometry list.
     *
     * @param geometries geometries list
     * @return 0 for x, 1 for y, 2 for z
     */
    private int longestAxis(List<Intersectable> geometries) {
        BoundingBox combined = null;

        for (Intersectable geometry : geometries) {
            BoundingBox box = geometry.getBoundingBox();

            if (box == null) {
                continue;
            }

            combined = combined == null ? box : combined.combine(box);
        }

        if (combined == null) {
            return 0;
        }

        double xLength = combined.getMaxX() - combined.getMinX();
        double yLength = combined.getMaxY() - combined.getMinY();
        double zLength = combined.getMaxZ() - combined.getMinZ();

        if (xLength >= yLength && xLength >= zLength) {
            return 0;
        }

        if (yLength >= xLength && yLength >= zLength) {
            return 1;
        }

        return 2;
    }

    /**
     * Gets the center coordinate of a bounding box by axis.
     *
     * @param box  bounding box
     * @param axis 0 for x, 1 for y, 2 for z
     * @return center coordinate
     */
    private double centerByAxis(BoundingBox box, int axis) {
        if (box == null) {
            return 0;
        }

        return switch (axis) {
            case 0 -> box.getCenterX();
            case 1 -> box.getCenterY();
            default -> box.getCenterZ();
        };
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> result = null;

        for (Intersectable geometry : _geometries) {
            var intersections = geometry.calcIntersections(ray, maxDistance);

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