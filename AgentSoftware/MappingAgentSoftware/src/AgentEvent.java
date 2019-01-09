public class AgentEvent {

    private Agent agent;
    private Event event;
    private String message;

    public AgentEvent(Agent agent, Event event) {
        this.agent = agent;
        this.event = event;
    }

    public AgentEvent(Agent agent, Event event, String message) {
        this.agent = agent;
        this.event = event;
        this.message = message;
    }

    public Event getEventType() { return event; }

    public String getMessage() { return message; }

    public enum Event {
        askNeighbors,
        getMatchingPoints;

        public final boolean equals(Event event) {
            return this.hashCode() == event.hashCode();
        }

    }
}
