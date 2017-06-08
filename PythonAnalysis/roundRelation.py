# -*- coding: utf-8 -*-

import scipy as sp
from scipy.stats import pearsonr
import numpy as np
from numpy import random

def main():
    dict = {1901:{"RUS":[],"AUS":[],"FRA":[],"TUR":[],"ENG":[],"GER":[],"ITA":[]},
            1902: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            1903: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            1904: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            1905: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            1906: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            1907: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            1908: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            1909: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            1910: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            1911: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            1912: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            1913: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            1914: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            1915: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            1916: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            1917: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            1918: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            1919: {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []},
            "result": {"RUS": [], "AUS": [], "FRA": [], "TUR": [], "ENG": [], "GER": [], "ITA": []}
            }

    oldyear = 0
    for line in open('roundResults.log', 'r'):
        itemList = line[:-1].split('\t')
        if itemList[0][0:4].isdigit():
            year = int(itemList[0][0:4])
            # if oldyear < year and oldyear != 0:
            #     dict["result"]=dict[oldyear] #最終結果の保存
            oldyear = year
        else:
            con = itemList[0][0:3]
            num = int(itemList[0][5:])
            if num > 1000:
                num = num - 1920
            dict[year][con].append(num)

    # x, y 軸の相関係数 r と有意確率 p を求める
    for key1 in dict["result"].keys():
        for key2 in dict["result"].keys():
            if key1 == key2:
                continue
            for i in range(1901,1919):
                x = dict[i][key1]
                y = dict[i][key2]
                r, p = pearsonr(x,y)
                print(i, key1, key2, r, p)

if __name__ == '__main__':
    main()