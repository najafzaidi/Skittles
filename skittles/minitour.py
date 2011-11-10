# Useful result lines: Player #1's happiness is: 347.2593300457907
from pprint import pprint

num_players = 5
end_cruft = 2
skip_length = num_players + end_cruft

o = open('Results.txt','r')
lines = o.readlines()

li = []
scores = {}
for line in lines:
	if 'Player' in line:
		li = line.strip('\n').split(' ')
		player = int(li[1].strip('#').strip('\'s'))
		score = float(li[4])
		try:
			scores[player].append(score)
		except KeyError:
			scores[player] = [score]

for player in scores.keys():
	print 'P%d: %f' % (player, sum(scores[player]))

import os

os.remove('Results.txt')
