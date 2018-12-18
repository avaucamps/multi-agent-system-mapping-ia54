from NetworkHandler import NetworkHandler
from Agent import Agent
from sift_features import detect_sift_features
from utils import remove_duplicates_matching_points

#Change base path when finished

def agents_received(agents, networkHandler):
    matching_points = []
    for agent in agents:
        matching_points.append(detect_sift_features(agent, agents))
    
    match_points = [item for sublist in matching_points for item in sublist]
    #match_points = remove_duplicates_matching_points(flat_list)

    networkHandler.send_match_points(match_points)


networkHandler = NetworkHandler(agents_received)