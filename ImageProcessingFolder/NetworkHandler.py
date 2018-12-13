import socket
from Agent import Agent

class NetworkHandler:

    def __init__(self, agents_received):
        self.client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.agents = []
        self.agents_received = agents_received
        self.open_connection()

    def open_connection(self):
        self.client_socket.connect(("localhost", 9992))

        while 1:
            data = self.client_socket.recv(1024)
            string_data = data.decode("utf-8")

            if (string_data == 'Q'):
                self.close_connection()
                break
            elif (string_data == 'AllAgentsSent'):
                self.agents_received(self.agents)
                break
            else:
                self.process_message(string_data)

    def close_connection(self):
        self.client_socket.close()

    def process_message(self, message):
        #agentid#image_path#neighbor1id#neighbor2id#neighbor3id#
        splitted_string = message.split('#')

        agent_id = splitted_string[0]
        image_path = splitted_string[1]

        neighbors_ids_list = []
        for i in range(3, len(splitted_string)):
            neighbors_ids_list.append(splitted_string[i])
        
        self.agents.append(
            Agent(agent_id, image_path, neighbors_ids_list)
        )