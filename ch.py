# -*- coding: UTF-8 -*-

import sys
import os

newFileNames = []
i = 1
p = "Disc2"

fo = open("title.txt", "r", encoding="UTF-8")
fileNameList = fo.readlines()
fo.close()

for fileName in fileNameList:
	fileName = fileName.strip()
	n = fileName.split(",")
	finalString = "{j}. {name}.wav".format(j=i, name=n[1])
	newFileNames.append(finalString)
	i = i+1

fileList = os.listdir(p)
for i, name in enumerate(fileList):
	pathName = os.path.join(p, name)
	os.rename(pathName, p+"\\"+newFileNames[i])

'''
AiRi,The road we belong
Faylan,hello! new world
佐々木恵梨,On the Rails
はな,ひだまりライン
Ayumi.(Astilbe×arendsii),blooming symphony
美郷あき,floraison
Duca,リトルプリュム
中恵光城,レイル・ロマネスク
カサンドラ,未来行き☆列車
米倉千尋,明日へと
中恵光城,ロオド・ラスト
松下,思い出の結晶
'''