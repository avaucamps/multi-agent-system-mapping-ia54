def remove_duplicates_matching_points(matching_points):
    new_match_points = []
    for match_point in matching_points:
        if (
            match_point.get_image1() == match_point.get_image2()
            and match_point.get_x1() == match_point.get_x2()
            and match_point.get_y1() == match_point.get_y2()
        ):
            continue
        else:
            new_match_points.append(match_point)

    return new_match_points