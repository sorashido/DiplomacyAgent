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

    year = 1901
    count = 0
    for line in open('roundResults.log', 'r'):
        itemList = line[:-1].split('\t')

        if itemList[0][0:4].isdigit():
            oldyear = year
            year = int(itemList[0][0:4])
            if year - oldyear != 1:
                if year >= oldyear:
                    for i in range(oldyear+1, year):
                        # print(i)
                        for con in dict["result"].keys():
                            dict[i][con].append(dict[oldyear][con][count])
                        # dict[i].update(dict[oldyear])
                else:
                    for i in range(oldyear+1, 1920):
                        for con in dict["result"].keys():
                            dict[i][con].append(dict[oldyear][con][count])

            if oldyear > year:
                count += 1
                dict["result"] = dict[oldyear-1]
        else:
            con = itemList[0][0:3]
            num = int(itemList[0][5:])

            if num == 0:
                num = -10
            dict[year][con].append(num)

    # x, y 軸の相関係数 r と有意確率 p を求める
    for key1 in dict["result"].keys():
        for key2 in dict["result"].keys():
            if key1 == key2:
                continue
            print('map.put("' + key1 + key2 + '", new HashMap<Integer, Double>());')
            for i in range(1901,1920):
                x = dict["result"][key1]
                y = dict[i][key2]
                r, p = pearsonr(x,y)
                if not r == r: #nanの場合は0
                    r = 0.00
                print('map.get'+'("'+key1+key2+'").put('+str(i)+','+str(r)+');')
                if i == 1919:
                    print('map.get' + '("' + key1 + key2 + '").put(' + str(1920) + ',' + str(r) + ');')
                    print('map.get' + '("' + key1 + key2 + '").put(' + str(1921) + ',' + str(r) + ');')

if __name__ == '__main__':
    main()