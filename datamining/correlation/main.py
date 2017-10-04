# you can write to stdout for debugging purposes, e.g.
# print "this is a debug message"
def solution(A):
    cost = len(A) * 2
    days = set(A)

    while True:
        maxDays = 0
        x = []
        for i in range(1,31):
            j = set(range(i,i+7))
            inter = days.intersection(j)
            if (maxDays < len(inter)) and (len(inter) > 3):
                maxDays = len(inter)
                x = inter
        if maxDays == 0:
            break
        else:
            cost -= (maxDays*2 - 7)
            days.difference_update(x)

    if cost > 25:
        cost = 25
    return cost

if __name__ == '__main__':
    print(solution([1, 2, 4, 5, 7, 29, 30]))