# -*- coding: UTF-8 -*-
import win32api
import win32con
import win32gui

title = '未命名 - 記事本'
hwnd = win32gui.FindWindow(None, title)

Ehwnd = win32gui.FindWindowEx(hwnd, None, "Edit", None)  # 找尋Edit類子視窗

# win32gui.SendMessage(hwnd, win32con.WM_COMMAND, win32con.IDOK, None)  # 直接對確定按鈕點擊
# win32gui.SendMessage(hwnd, win32con.WM_SETTEXT, None, '機掰我的記事本壞了')  # 設定Handle的標題文字
