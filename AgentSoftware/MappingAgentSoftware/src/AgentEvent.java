public class AgentEvent {

    private Agent agent;
    private Event event;
    private String message;
    private FeaturePoint featurePoint;

    public AgentEvent(Agent agent, Event event) {
        this.agent = agent;
        this.event = event;
    }

    public AgentEvent(Agent agent, Event event, String message) {
        this.agent = agent;
        this.event = event;
        this.message = message;
    }

    public AgentEvent(Agent agent, Event event, FeaturePoint featurePoint) {
        this.agent = agent;
        this.event = event;
        this.featurePoint = featurePoint;
    }

    public Agent getAgent() {
        return agent;
    }

    public Event getEventType() { return event; }

    public String getMessage() { return message; }

    public FeaturePoint getFeaturePoint() { return featurePoint; }

    public enum Event {
        askNeighbors,
        getMatchingPoint,
        getWorldPoint,
        addPointToMap;

        public final boolean equals(Event event) {
            return this.hashCode() == event.hashCode();
        }

    }
}
