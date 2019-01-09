from NetworkHandler import NetworkHandler
from Agent import Agent
from sift_features import detect_sift_features
from harris_features import detect_harris_features
from utils import remove_duplicates_matching_points

#Change base path when finished

def agents_received(agents, networkHandler):
    matching_points_sift = []
    matching_points_harris = []
    for agent in agents:
        matching_points_sift.append(detect_sift_features(agent, agents))
        matching_points_harris.append(detect_harris_features(agent, agents))
    
    match_points_sift = [item for sublist in matching_points_sift for item in sublist]
    match_points_harris = [item for sublist in matching_points_harris for item in sublist]

    match_points_sift = remove_duplicates_matching_points(match_points_sift)
    match_points_harris = remove_duplicates_matching_points(match_points_harris)

    networkHandler.send_match_points_sift(match_points_sift)
    networkHandler.send_match_points_harris(match_points_harris)


networkHandler = NetworkHandler(agents_received)