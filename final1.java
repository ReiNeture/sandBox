import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;


public class final1 extends JFrame
{
Container c;
//介面
int number=0;//點擊次數 
JLabel lab,lab2;//生命變數
JButton rock, paper, scissor;//剪刀石頭布
JTextField txt,txt1,txt2;
JTextArea texa;//文字區域
int a,b,d;//勝負次數
int player_life =100;//玩家生命
int computer_life =100;//電腦生命

public final1()
{
	
super("test");
c=getContentPane();//ContentPane

c.setLayout(new FlowLayout());
//版面設定

lab=new JLabel("玩家生命:"+ player_life);
lab2=new JLabel("電腦生命:"+ computer_life);

rock =new JButton("石頭");
paper =new JButton("布");
scissor =new JButton("剪刀");

texa=new JTextArea(20,20);
//建立數值 介面

c.add(lab);c.add(lab2);
c.add(rock);c.add(paper);c.add(scissor);
c.add(texa);
//放置數值介面

rock.addActionListener(new ActionListener() {//出石頭
	@Override
	public void actionPerformed(ActionEvent e) {
		play(0);
		repaint();
	}
});
paper.addActionListener(new ActionListener() {//出布
	@Override
	public void actionPerformed(ActionEvent e) {
		play(1);
		repaint();
	}
});
scissor.addActionListener(new ActionListener() {//出剪刀
	@Override
	public void actionPerformed(ActionEvent e) {
		play(2);
		repaint();
	}
});
//按鈕事件
setSize(640,480);//視窗大小
setVisible(true);
}


public void play(int player){
	if(player_life ==0){
		JOptionPane.showMessageDialog(null, "連輸十次了別玩了 可憐啦");
		System.exit(0);
		//失敗訊息
	}
	else if (computer_life==0)
	{
		JOptionPane.showMessageDialog(null, "你是贏家 贏家 贏家");
		System.exit(0);
		//獲勝訊息
	}
	Random rand_num = new Random();
	int computer = rand_num.nextInt(3);
	showResult(player, computer);
	mousePressed();
	//AI亂數出拳
}

public void showResult(int player, int computer){//勝負判定區
	if(computer==player){
		texa.append("平手欸 你再出一次試試看");
		texa.append("\n");
	}else if((player == 1 && computer == 0) ){
		d = d + 1;
		computer_life = computer_life - 10;
		lab2.setText("電腦生命:" + player_life);
		texa.append("電腦出石頭你贏了" + d + "次");
		texa.append("\n");
	}
		else if(player==2 && computer == 1)
		{
			d = d + 1;
			computer_life = computer_life - 10;
			lab2.setText("電腦生命:" + player_life);
			texa.append("電腦出布你贏了" + d + "次");
			texa.append("\n");
		}
		else if(player==0 && computer == 2)
		{
			d = d + 1;
			computer_life = computer_life - 10;
			lab2.setText("電腦生命:" + player_life);
			texa.append("電腦出剪刀你贏了" + d + "次");
			texa.append("\n");
		}
	else{
		b = b + 1;
		player_life = player_life - 10;
		lab.setText("玩家生命:" + player_life);
		texa.append("挖靠你輸了" + b + "次");
		texa.append("\n");
	}
}

public void mousePressed() {//每20行清空對話框
	number++;
	if (number%20==0)
	{
		texa.setText("");
	}
}

public static void main(String args[]) //主程式
{

final1 app=new final1(); //定義名稱
app.addWindowListener(new WindowAdapter(){
	
public void windowClosing(WindowEvent e)//關閉程式
{
System.exit(0);
}
}); //關閉程式
}
}