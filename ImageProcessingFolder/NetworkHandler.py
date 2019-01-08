import socket
from Agent import Agent

class NetworkHandler:

    def __init__(self, agents_received):
        self.client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.messages = []
        self.agents = []
        self.agents_received = agents_received
        self.open_connection()

    def open_connection(self):
        self.client_socket.connect(("localhost", 9992))

        done = False
        while not done:
            data = self.client_socket.recv(512)
            string_data = data.decode("utf-8")

            if (string_data == 'Q'):
                self.close_connection()
                done = True
            elif (string_data == 'AllMessagesSent'):
                self.extract_messages_data()
                self.agents_received(self.agents, self)
                done = True
            else:
                self.messages.append(string_data)

    def send_match_points_sift(self, match_points):
        #agent1#agent2#x1#y1#x2#y2
        startText = '\x01'
        endText = '\x04'

        for match_point in match_points:
            match_point_message = '#'
            match_point_message += "SIFT"
            match_point_message += "#"
            match_point_message += match_point.get_agent1_id()
            match_point_message += '#'
            match_point_message += match_point.get_agent2_id()
            match_point_message += '#'
            match_point_message += str(match_point.get_x1())
            match_point_message += '#'
            match_point_message += str(match_point.get_y1())
            match_point_message += '#'
            match_point_message += str(match_point.get_x2())
            match_point_message += '#'
            match_point_message += str(match_point.get_y2())
            match_point_message += '#'

            self.client_socket.send(str(startText).encode())
            self.client_socket.send(match_point_message.encode())
            self.client_socket.send(str(endText).encode())

        self.client_socket.send("\x00".encode())

    def send_match_points_harris(self, match_points):
        #agent1#agent2#x1#y1#x2#y2
        startText = '\x01'
        endText = '\x04'

        for match_point in match_points:
            match_point_message = '#'
            match_point_message += "HARRIS"
            match_point_message += "#"
            match_point_message += match_point.get_agent1_id()
            match_point_message += '#'
            match_point_message += match_point.get_agent2_id()
            match_point_message += '#'
            match_point_message += str(match_point.get_x1())
            match_point_message += '#'
            match_point_message += str(match_point.get_y1())
            match_point_message += '#'
            match_point_message += str(match_point.get_x2())
            match_point_message += '#'
            match_point_message += str(match_point.get_y2())
            match_point_message += '#'

            self.client_socket.send(str(startText).encode())
            self.client_socket.send(match_point_message.encode())
            self.client_socket.send(str(endText).encode())

        self.client_socket.send("\x00".encode())

    def close_connection(self):
        self.client_socket.close()

    def extract_messages_data(self):
        for message in self.messages:
            self.process_agent_message(message)

    def process_agent_message(self, message):
        #agentid#image_path#neighbor1id#neighbor2id#neighbor3id#
        splitted_string = message.split('#')

        agent_id = splitted_string[1]
        image_path = splitted_string[2]

        neighbors_ids_list = []
        for i in range(3, len(splitted_string)):
            neighbors_ids_list.append(splitted_string[i])
        
        self.agents.append(
            Agent(agent_id, image_path, neighbors_ids_list)
        )