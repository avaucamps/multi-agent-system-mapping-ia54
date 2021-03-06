import numpy as np
import pandas as pd
import cv2
from utils import get_neighbors_object
import matplotlib.pyplot as plt
import os
from matching_point import MatchingPoint
import time

def detect_sift_features(agent, all_agents):
    neighbors_id = agent.get_neighbors_id()
    neighbors = get_neighbors_object(all_agents, neighbors_id)

    sift = cv2.xfeatures2d.SIFT_create()

    matching_points = []
    for neighbor in neighbors:
        matching_points.append(get_image_matches(
            sift, 
            agent, 
            neighbor
        ))
    
    return [item for sublist in matching_points for item in sublist]


def image_detect_and_compute(detector, img_name):
    """Detect and compute interest points and their descriptors."""
    img = cv2.imread(img_name)

    # gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)

    # gray = np.float32(gray)
    # dst = cv2.cornerHarris(gray,2,3,0.04)

    # #result is dilated for marking the corners, not important
    # dst = cv2.dilate(dst,None)

    # # # Threshold for an optimal value,it may vary depending on the image.
    # img[dst>0.01*dst.max()]=[0,0,255]

    # cv2.imshow('dst',img)
    # if cv2.waitKey(0) & 0xff == 27:
    #     cv2.destroyAllWindows()
    
    if img is None:
        print("Image could not be loaded. Trying again in 2 seconds.")
        time.sleep(2)

        img = cv2.imread(img_name)
        if img is None:
            print("Image could still not be loaded.")
            return None, None, None

    #img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    kp, des = detector.detectAndCompute(img, None)
    return img, kp, des

def get_image_matches(detector, agent1, agent2, nmatches=5):
    img1_name = agent1.get_image_path()
    img2_name = agent2.get_image_path()
    img1, kp1, des1 = image_detect_and_compute(detector, img1_name)
    img2, kp2, des2 = image_detect_and_compute(detector, img2_name)

    if des1 is None or des2 is None:
        return []
    bf = cv2.BFMatcher(cv2.NORM_L1, crossCheck=False)
    matches = bf.match(des1, des2)
    matches = sorted(matches, key = lambda x: x.distance)
    
    matching_points = []
    for mat in matches:
        img1_idx = mat.queryIdx
        img2_idx = mat.trainIdx

        (x1,y1) = kp1[img1_idx].pt
        (x2,y2) = kp2[img2_idx].pt
        match_point = MatchingPoint(agent1.get_id(), agent2.get_id(), x1, y1, x2, y2)
        matching_points.append(match_point)

    # img_matches = cv2.drawMatches(img1, kp1, img2, kp2, matches[:nmatches], img2, flags=2)
    # plt.figure(figsize=(16, 16))
    # plt.title(type(detector))
    # plt.imshow(img_matches); plt.show() 

    return matching_points       