class MatchingPoint:
    def __init__(self, image1, image2, x1, y1, x2, y2):
        self.image1 = image1
        self.image2 = image2
        self.x1 = x1
        self.y1 = y1
        self.x2 = x2
        self.y2 = y2

    def get_image1(self):
        return self.image1

    def get_image2(self):
        return self.image2

    def get_x1(self):
        return self.x1

    def get_y1(self):
        return self.y1

    def get_x2(self):
        return self.x2

    def get_y2(self):
        return self.y2