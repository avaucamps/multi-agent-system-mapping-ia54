class Agent:

    def __init__(self, id, image_path, neighbors_id_list):
        self.id = id
        self.image_path = image_path
        self.neighbors_id_list = neighbors_id_list

    def getid(self):
        return self.id