import time
import sys
import os

fileList = os.listdir()
DELAY_TIME = 0.07
PREFIX = '> '

def cmd_print(list = []):
	for fileName in list:
		sys.stdout.write(PREFIX)
		
		for word in fileName:
			sys.stdout.write(word)
			sys.stdout.flush()
			time.sleep(DELAY_TIME)
		
		sys.stdout.write('\n')
		

if __name__ == '__main__':
	cmd_print(list = fileList)