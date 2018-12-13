from NetworkHandler import NetworkHandler
from Agent import Agent

def agents_received(agents):
    for agent in agents:
        if type(agent) is Agent:
            print(agent.getid())

networkHandler = NetworkHandler(agents_received)