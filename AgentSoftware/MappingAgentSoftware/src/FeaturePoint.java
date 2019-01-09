public class FeaturePoint {

    private Point point;
    private FeatureMatchingType featureMatchingType;

    public FeaturePoint(Point point, FeatureMatchingType featureMatchingType) {
        this.point = point;
        this.featureMatchingType = featureMatchingType;
    }

    public Point getPoint() {
        return point;
    }

    public FeatureMatchingType getFeatureMatchingType() {
        return featureMatchingType;
    }
}
