def remove_duplicates_matching_points(matching_points):
    new_match_points = []
    for match_point in matching_points:
        if (
            match_point.get_agent1_id() == match_point.get_agent2_id()
            and match_point.get_x1() == match_point.get_x2()
            and match_point.get_y1() == match_point.get_y2()
        ):
            continue
        else:
            new_match_points.append(match_point)

    return new_match_points

def get_neighbors_object(agents, neighbors_id):
    neighbors = []
    for neighbor_id in neighbors_id:
        for a in agents:
            if a.get_id() == neighbor_id:
                neighbors.append(a)
                break
    
    return neighbors