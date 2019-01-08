public enum FeatureMatchingType {
    sift ("SIFT"),
    harris ("HARRIS");

    private final String name;

    FeatureMatchingType(String type) {
        name = type;
    }

    public String toString() {
        return name;
    }
}