from constants import base_path

class Agent:

    def __init__(self, id, image_path, neighbors_id_list):
        self.id = id
        self.image_path = base_path + "\\" + image_path
        self.neighbors_id_list = neighbors_id_list

    def get_id(self):
        return self.id

    def get_image_path(self):
        return self.image_path

    def get_neighbors_id(self):
        return self.neighbors_id_list