"""
Player #0's happiness is: 485.6169574301587
Player #1's happiness is: 347.2593300457907
Player #2's happiness is: 430.62242046861536
Player #3's happiness is: 412.67360940638275
Player #4's happiness is: 652.0557110679104
Game 1 ends
After Game 1 the total scores are
Total Player #0's happiness =485.6169574301587
Total Player #1's happiness =347.2593300457907
Total Player #2's happiness =430.62242046861536
Total Player #3's happiness =412.67360940638275
Total Player #4's happiness =652.0557110679104
"""

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
pprint(scores)
