import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;


public class final1 extends JFrame
{
Container c;
//����
int number=0;//�I������ 
JLabel lab,lab2;//�ͩR�ܼ�
JButton rock, paper, scissor;//�ŤM���Y��
JTextField txt,txt1,txt2;
JTextArea texa;//��r�ϰ�
int a,b,d;//�ӭt����
int player_life =100;//���a�ͩR
int computer_life =100;//�q���ͩR

public final1()
{
	
super("test");
c=getContentPane();//ContentPane

c.setLayout(new FlowLayout());
//�����]�w

lab=new JLabel("���a�ͩR:"+ player_life);
lab2=new JLabel("�q���ͩR:"+ computer_life);

rock =new JButton("���Y");
paper =new JButton("��");
scissor =new JButton("�ŤM");

texa=new JTextArea(20,20);
//�إ߼ƭ� ����

c.add(lab);c.add(lab2);
c.add(rock);c.add(paper);c.add(scissor);
c.add(texa);
//��m�ƭȤ���

rock.addActionListener(new ActionListener() {//�X���Y
	@Override
	public void actionPerformed(ActionEvent e) {
		play(0);
		repaint();
	}
});
paper.addActionListener(new ActionListener() {//�X��
	@Override
	public void actionPerformed(ActionEvent e) {
		play(1);
		repaint();
	}
});
scissor.addActionListener(new ActionListener() {//�X�ŤM
	@Override
	public void actionPerformed(ActionEvent e) {
		play(2);
		repaint();
	}
});
//���s�ƥ�
setSize(640,480);//�����j�p
setVisible(true);
}


public void play(int player){
	if(player_life ==0){
		JOptionPane.showMessageDialog(null, "�s��Q���F�O���F �i����");
		System.exit(0);
		//���ѰT��
	}
	else if (computer_life==0)
	{
		JOptionPane.showMessageDialog(null, "�A�OĹ�a Ĺ�a Ĺ�a");
		System.exit(0);
		//��ӰT��
	}
	Random rand_num = new Random();
	int computer = rand_num.nextInt(3);
	showResult(player, computer);
	mousePressed();
	//AI�üƥX��
}

public void showResult(int player, int computer){//�ӭt�P�w��
	if(computer==player){
		texa.append("������ �A�A�X�@���ոլ�");
		texa.append("\n");
	}else if((player == 1 && computer == 0) ){
		d = d + 1;
		computer_life = computer_life - 10;
		lab2.setText("�q���ͩR:" + player_life);
		texa.append("�q���X���Y�AĹ�F" + d + "��");
		texa.append("\n");
	}
		else if(player==2 && computer == 1)
		{
			d = d + 1;
			computer_life = computer_life - 10;
			lab2.setText("�q���ͩR:" + player_life);
			texa.append("�q���X���AĹ�F" + d + "��");
			texa.append("\n");
		}
		else if(player==0 && computer == 2)
		{
			d = d + 1;
			computer_life = computer_life - 10;
			lab2.setText("�q���ͩR:" + player_life);
			texa.append("�q���X�ŤM�AĹ�F" + d + "��");
			texa.append("\n");
		}
	else{
		b = b + 1;
		player_life = player_life - 10;
		lab.setText("���a�ͩR:" + player_life);
		texa.append("���a�A��F" + b + "��");
		texa.append("\n");
	}
}

public void mousePressed() {//�C20��M�Ź�ܮ�
	number++;
	if (number%20==0)
	{
		texa.setText("");
	}
}

public static void main(String args[]) //�D�{��
{

final1 app=new final1(); //�w�q�W��
app.addWindowListener(new WindowAdapter(){
	
public void windowClosing(WindowEvent e)//�����{��
{
System.exit(0);
}
}); //�����{��
}
}