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

    public boolean isEqual(FeatureMatchingType type) {
        if (name.equals(type.toString())) {
            return true;
        }

        return false;
    }
}