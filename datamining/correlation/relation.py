# -*- coding: utf-8 -*-

import scipy as sp
from scipy.stats import pearsonr
import numpy as np
from numpy import random

def main():
    dict = {"RUS":[],"AUS":[],"FRA":[],"TUR":[],"ENG":[],"GER":[],"ITA":[]}

    for line in open('gameResults.log', 'r'):
        itemList = line[:-1].split('\t')
        if len(itemList[0]) > 15:
            con = itemList[0][15:18]
            num = int(itemList[0][19:])
            if num > 1000:
                num = num - 1920
            dict[con].append(num)

    # x, y 軸の相関係数 r と有意確率 p を求める
    for key1 in dict.keys():
        for key2 in dict.keys():
            if key1 == key2:
                continue
            x = dict[key1]
            y = dict[key2]
            # print(x,y)
            r, p = pearsonr(x,y)
            print(key1, key2, r, p)

if __name__ == '__main__':
    main()