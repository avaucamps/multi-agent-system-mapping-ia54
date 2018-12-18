public class MatchingPoint {
    private String agent1Id;
    private Point pointAgent1;
    private String agent2Id;
    private Point pointAgent2;

    public MatchingPoint(String agent1Id, String agent2Id, Point p1, Point p2) {
        this.agent1Id = agent1Id;
        this.pointAgent1 = p1;
        this.agent2Id = agent2Id;
        this.pointAgent2 = p2;

        System.out.println("[" + agent1Id + "]New matching point added.");
    }

    public String getAgent1Id() {
        return agent1Id;
    }

    public Point getPointAgent1() {
        return pointAgent1;
    }

    public String getAgent2Id() {
        return agent2Id;
    }

    public Point getPointAgent2() {
        return pointAgent2;
    }
}
