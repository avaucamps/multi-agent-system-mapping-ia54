import numpy as np
import pandas as pd
from skimage import data
from skimage.util import img_as_float
from skimage.feature import (corner_harris, corner_subpix, corner_peaks, plot_matches)
from skimage.color import rgb2gray
from skimage.measure import ransac
import matplotlib.pyplot as plt
import os
from matching_point import MatchingPoint
import time
from skimage import io
from utils import get_neighbors_object
import math
from skimage.transform import AffineTransform

def detect_harris_features(agent, all_agents):
    neighbors_id = agent.get_neighbors_id()
    neighbors = get_neighbors_object(all_agents, neighbors_id)

    matching_points = []
    for neighbor in neighbors:
        matching_points.append(get_image_matches( 
            agent, 
            neighbor
        ))
    
    return [item for sublist in matching_points for item in sublist]

def gaussian_weights(window_ext, sigma=1):
    y, x = np.mgrid[-window_ext:window_ext+1, -window_ext:window_ext+1]
    g = np.zeros(y.shape, dtype=np.double)
    g[:] = np.exp(-0.5 * (x**2 / sigma**2 + y**2 / sigma**2))
    g /= 2 * np.pi * sigma * sigma
    return g

def show_images(src, dst, img_orig_gray, img_warped_gray):
        # estimate affine transform model using all coordinates
        model = AffineTransform()
        model.estimate(src, dst)

        # robustly estimate affine transform model with RANSAC
        model_robust, inliers = ransac((src, dst), AffineTransform, min_samples=3,
                                    residual_threshold=2, max_trials=100)
        outliers = inliers == False

        # visualize correspondence
        fig, ax = plt.subplots(nrows=2, ncols=1)

        plt.gray()

        inlier_idxs = np.nonzero(inliers)[0]
        plot_matches(ax[0], img_orig_gray, img_warped_gray, src, dst,
                    np.column_stack((inlier_idxs, inlier_idxs)), matches_color='b')
        ax[0].axis('off')
        ax[0].set_title('Correct correspondences')

        outlier_idxs = np.nonzero(outliers)[0]
        plot_matches(ax[1], img_orig_gray, img_warped_gray, src, dst,
                    np.column_stack((outlier_idxs, outlier_idxs)), matches_color='r')
        ax[1].axis('off')
        ax[1].set_title('Faulty correspondences')

        plt.show()

def match_corner(img_orig, img_warped, coord, coords_warped, coords_warped_subpix, window_ext=5):
    r, c = np.round(coord).astype(np.intp)
    window_orig = img_orig[r-window_ext:r+window_ext+1,
                           c-window_ext:c+window_ext+1, :]

    # weight pixels depending on distance to center pixel
    weights = gaussian_weights(window_ext, 3)
    weights = np.dstack((weights, weights, weights))

    # compute sum of squared differences to all corners in warped image
    SSDs = []
    for cr, cc in coords_warped:
        window_warped = img_warped[cr-window_ext:cr+window_ext+1,
                                   cc-window_ext:cc+window_ext+1, :]
       
        if window_orig.shape != window_warped.shape:
            continue

        SSD = np.sum(weights * (window_orig - window_warped)**2)
        SSDs.append(SSD)

    # use corner with minimum SSD as correspondence
    if not SSDs:
        return

    min_idx = np.argmin(SSDs)
    return coords_warped_subpix[min_idx]

def get_image_matches(agent1, agent2, nmatches=5):
    img1_name = agent1.get_image_path()
    img2_name = agent2.get_image_path()
    
    img1 = io.imread(img1_name)
    img2 = io.imread(img2_name)
    
    img1_gray = rgb2gray(img1)
    img2_gray = rgb2gray(img2)

    # extract corners using Harris' corner measure
    coords_img1 = corner_peaks(corner_harris(img1_gray), threshold_rel=0.001,
                            min_distance=5)
    coords_img2 = corner_peaks(corner_harris(img2_gray),
                                threshold_rel=0.001, min_distance=5)

    # determine sub-pixel corner position
    coords_img1_subpix = corner_subpix(img1_gray, coords_img1, window_size=9)
    coords_img2_subpix = corner_subpix(img2_gray, coords_img2,
                                        window_size=9)

    matching_points = []
    for coord in coords_img1_subpix:
        if math.isnan(coord[0]) or math.isnan(coord[1]):
            continue

        coord_warped = match_corner(img1, img2, coord, coords_img2, coords_img2_subpix)
        if coord_warped is None:
            continue

        x1 = coord[0]
        y1 = coord[1]
        x2 = coord_warped[0]
        y2 = coord_warped[1]
        match_point = MatchingPoint(agent1.get_id(), agent2.get_id(), x1, y1, x2, y2)
        matching_points.append(match_point)

    return matching_points