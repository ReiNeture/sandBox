#-*- coding:utf-8 -*-

import numpy as np
import cv2
import json

def video2imgs(video_name, size):

	img_list = []
	cap = cv2.VideoCapture(video_name)

	while cap.isOpened():
	
		ret, frame = cap.read()

		if ret:
			gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
			img = cv2.resize(gray, size, interpolation=cv2.INTER_AREA)
			img_list.append(img.tolist())
		else:
			break
	
	cap.release()
	return img_list
	
if __name__ == "__main__":

	imgs = video2imgs("y2mate.mp4", (10, 5)) # 64, 36

	with open('data.json', 'w') as f:
		json.dump(imgs, f, ensure_ascii=False, indent=4)

	
	#assert len(imgs) > 10
