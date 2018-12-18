class MatchingPoint:
    def __init__(self, agent1_id, agent2_id, x1, y1, x2, y2):
        self.agent1_id = agent1_id
        self.agent2_id = agent2_id
        self.x1 = x1
        self.y1 = y1
        self.x2 = x2
        self.y2 = y2

    def get_agent1_id(self):
        return self.agent1_id

    def get_agent2_id(self):
        return self.agent2_id

    def get_x1(self):
        return self.x1

    def get_y1(self):
        return self.y1

    def get_x2(self):
        return self.x2

    def get_y2(self):
        return self.y2