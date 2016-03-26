
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.swing.*;


public class Calculator extends JFrame {
	/* �ν��Ͻ� ���� */
	ArrayList<String> arr = new ArrayList<String>();
	ArrayList<String> stack = new ArrayList<String>();
	JPanel basePanel = new JPanel();
	JPanel show, key;
	Labels exp, res;
	/* �׸��� ���̾ƿ��� ������ ������� ��Ʈ���� �迭 ���� */
	String[] texts = {"C", "%", "x", "��", "7", "8", "9", "-", "4", "5", "6", "+", "1", "2", "3", "()", "0", ".", "+/-", "="};
	/* ��� ��ư�� ���� �迭�� ó�� */
	Buttons[] allb = new Buttons[20];
	/* ���� */
	boolean numFlag = false;	// ������ ���� �ԷµǾ����� true, �ƴϸ� false
	boolean pmFlag = false;		// plus false minus true
	boolean operFlag = false;	// �����ڵ� �ߺ� �Է� �� ���� �ڵ鸵 / ������ ������ �ԷµǾ����� true �ƴϸ� false
	int braceFlag = 0;	// ��ȣ flag. �ƹ��͵� �ƴ� �� 0, ( ������ 1, ) ������ 2 (���� �Է� ����)
	int openCount = 0;	// ��ȣ ( ����
	int closeCount = 0;	// ��ȣ ) ����
	boolean resFlag = false;	// ����� ����� �ٷ� ���ĸ� true
	int zeroCount = 0;			// 0 ī��Ʈ
	boolean dotFlag = false;	// �Ҽ��� �÷���. ���� ��, ������ ����ģ �Ŀ� false / .�� �̹� ����� �Ŀ� true (2.12.34 ����)
	/* ��������� ��� �� ��� */
	String resStr = "";
	/* ���� �� ��������� �����ִ� �κ��� �� �г� */
	class showPanel extends JPanel {
		showPanel() {
			setLayout(new GridLayout(2, 1));
			setBackground(Color.WHITE);
			exp = new Labels("");
			res = new Labels("");
			exp.setHorizontalAlignment(SwingConstants.RIGHT);
			res.setHorizontalAlignment(SwingConstants.RIGHT);
			add(exp);
			add(res);
		}
	}
	/* Ű ��ư �Է��� �޴� �Ʒ� �κ� �г� */
	class keyPanel extends JPanel {
		keyPanel() {
			setLayout(new GridLayout(5, 4, 1, 1));
			setBackground(Color.WHITE);
			MyActionListener mAc = new MyActionListener();
			int ch;
			for (int i = 0; i < 20; i++)
			{
				allb[i] = new Buttons(texts[i]);
				ch = texts[i].charAt(0);
				add(allb[i]);
				allb[i].addActionListener(mAc);
			}
			allb[0].setBackground(new Color(255, 187, 0));
			allb[3].setBackground(new Color(255, 187, 0));
			allb[19].setBackground(new Color(255, 187, 0));
			
		}
	}
	/* ������ - ������ ���� */
	Calculator()
	{
		/* �⺻ ���� - Ÿ��Ʋ, �ݱ� ��ư */
		setTitle("���� ����");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(basePanel);
		/* ���̾ƿ� ���� - ��ġ ������ ����, ���� ��ġ */
		setLayout(null);
		/* �� �г� ���� */
		show = new showPanel();
		show.setSize(320, 160);
		show.setLocation(0, 0);
		/* �Ʒ� �г� ���� */
		key = new keyPanel();
		key.setSize(320, 400);
		key.setLocation(0, 160);
		/* �гο� ������Ʈ ���� */
		basePanel.add(show);
		basePanel.add(key);
		/* ��ü ������ ���� */
		setSize(325, 587);
		/* ȭ�� �߾ӿ� ���� */
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width-320)/2, (screenSize.height-560)/2);
		/* â ũ�� ���� */
		setResizable(false);
		/* ȭ�鿡 ������! */
		setVisible(true);
	}
	class MyActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			Buttons btn = (Buttons)arg0.getSource();
			/* showPanel�� ��Ʈ���� */
			String input = btn.getText();
			String bef = exp.getText();
			int ind = findInd(input);
			char ch;
			/* ����� ��� ��, �̾ ���� �ϰ� ���� �� */
			if (resFlag == true)
				if (ind == 1 || ind == 2 || ind == 7 || ind == 11)
					bef = res.getText().substring(1, res.getText().length());
				else
					bef = "";
			resFlag = false;
			/* ��ư �Է¿� ���� ȭ�� ��� & ��ɵ� */
			switch(ind)
			{
				/* C �϶�, �ʱ�ȭ */
				case 0 :
					exp.setText("");
					res.setText("");
					numFlag = pmFlag = operFlag = dotFlag = false;
					zeroCount = braceFlag = 0;
					return ;
				/* BackSpace�϶�, ���� �ϳ� ���� */
				case 3 : 
					exp.setText(bef.substring(0, bef.length()-1));
					return ;
				/* ������ ��� */
				case 16:	//0�� ��
					if (zeroCount == 1)
					{
						input = "";
						zeroCount = 0;
						break;
					}
				case 4: case 5: case 6: case 8: case 9: case 10: case 12: case 13: case 14:
					/* ���ΰ� �� �� ��������~ */
					if (zeroCount == 1)
					{
						bef = bef.substring(0, bef.length()-1);
						zeroCount = 2;
					}
					break;
				/* �������� ��� */
				case 1: case 2: case 7: case 11:
					if (operFlag || bef.equals(""))
						/* ������ �ߺ� �Է� ���� */
						return ;
					break;
				/* ��ȣ �Է� �� */
				case 15 :
					if (braceFlag == 0)
					{
						/* ()(), num()�� ��, ���ϱ�� ó�� */
						if ((bef.length() > 0) && (((ch=bef.charAt(bef.length()-1)) >= 48 && ch <= 57) || ch == ')'))
						{
							input = "x(";
						}
						else
							input = "(";
					}
					else
					{
						if (braceFlag == 1)
						{
							input = ")";
						}
					}
					braceFlag = (braceFlag + 1) % 3;
					System.out.println("brace : "+braceFlag);
					break;
				/* . �϶� */
				case 17 : 
					if (dotFlag == true)
						return;
					if (numFlag == true)
						input = ".";
					else
						input = "0.";
					zeroCount = 2;
					dotFlag = true;
					break;
				/* +- �϶� */
				case 18 :
					pmFlag = !pmFlag;
					if (pmFlag)
					{
						input = "-";
						if (bef.equals(""))
							break ;
						if (bef.charAt(bef.length()-1) == '+')
							bef = bef.substring(0, bef.length()-1);
						if (bef.charAt(bef.length()-1) == '-')
							bef = bef.substring(0, bef.length()-1);
					}
					else
					{
						input = "";
						if (bef.equals(""))
							break ;
						if (bef.charAt(bef.length()-1) == '-')
							bef = bef.substring(0, bef.length()-1);
					}
					break;
				/* = �϶�, ����� ���� */
				case 19 :
					if (operFlag == true)
					{
						res.setText("�߸��� ������Դϴ�");
						return ;
					}
					postfix(bef);	// ���� -> ������������� ��ȯ
					resStr = String.valueOf(calc());
					res.setText("="+resStr);	// ���� �� ��� �� ��� : res�󺧿� ���
					resFlag = true;
					resStr = "";
					for (int i=0; i < arr.size(); i++)
					{
						resStr = resStr + arr.get(i);
					}
					System.out.println(resStr);
					handleFlags(ind);
					return ;
			}
			exp.setText(bef+input);
			handleFlags(ind);
		}
	}
	int findInd(String in)
	{
		for (int ind = 0; ind < 20; ind++)
		{
				if (allb[ind].getText().equals(in))
					return ind;
		}
		return -1;
	}
	void handleFlags(int ind)
	{
		/* �����Է� flag */
		if ((ind >= 4 && ind <= 6) || (ind >= 8 && ind <= 10) || (ind >= 12 && ind <= 14) || ind == 16)
		{
			numFlag = true;
			/* 0 �� �ԷµǾ���� ���� */
			if (ind == 16)
				zeroCount++;
			else
				zeroCount = 2;
		}
		else
			numFlag = false;
		/* ������ flag */
		if (ind == 1 || ind == 2 || ind == 7 || ind == 11)
		{
			operFlag = true;
			dotFlag = false;
			zeroCount = 0;
		}
		else
			operFlag = false;
		
		if (ind != 15 && braceFlag != 1)
			braceFlag = 0;
	}
	/* ���� -> ���� */
	void postfix(String input)
	{
		arr.clear();
		stack.clear();
		char ch;
		int i;
		boolean befNumFlag = false;	// ������ ���� �־����� �÷���
		System.out.println("input : '"+input+"'");
		for (i = 0; i < input.length(); i++)
		{
			ch = input.charAt(i);
			if (ch == '(')
			{
				stack.add(0, String.valueOf(ch));	// �ε��� 0 �� ž
			}
			else
			{
				if (ch == ')')
				{
					/* )������ ���ÿ� ( ���� ������ pop */
					while (!stack.get(0).equals("("))
					{
						arr.add(stack.remove(0));
					}
					stack.remove(0);
				}
				else
				{
					/* �ǿ������� ��� �׳� arr�� ����*/
					if ((ch >= 48 && ch <= 57) || (befNumFlag == false && ch == '-'))
					{
						String nums = "";
						if (ch == '-')
						{
							nums = "-";
							i++;
						}
						while (i < input.length() && (((ch = input.charAt(i)) >= 48 && ch <= 57) || ch == '.'))
						{
							nums = nums + String.valueOf(ch);
							i++;
						}
						arr.add(nums);
						i--;
					}
					else
					{
						/* ������ �� ���*/
						if (isOper(ch) == true){
							/* ������ �Ⱥ���ְ�, top�����ڰ� �Ŀ����ں��� �켱������ ������ pop */
							while (stack.size()!= 0 && (stP(stack.get(0).charAt(0)) >= inP(ch)))
							{
								arr.add(stack.remove(0));
							}
							stack.add(0, String.valueOf(ch));
						}
						else
						{
							i++;
						}
					}
				}
			}
			/* befNumFlag�� ����� (-3)�� ���� ���������� ó�� */
			if (((ch = input.charAt(i)) >= 48 && ch <= 57) || ch == '.' || ch == ')')
				befNumFlag = true;
			else
				befNumFlag = false;
			for (int j = 0; j < arr.size(); j++)
			{
				System.out.print(arr.get(j)+" ");
			}
			System.out.print(" ||| ");
			for (int j = 0; j < stack.size(); j++)
			{
				System.out.print(stack.get(j)+" ");
			}
			System.out.println();
		}
		while (stack.size() != 0)
		{
			arr.add(stack.remove(0));
		}
		System.out.println("-------------------");
	}
	/* ���������� ������ ����� ���� */
	double calc()
	{
		double a = 0, b = 0, res = 0;
		BigDecimal ba, bb, bres;
		int under = 0;
		boolean dots = false;
		boolean minus = false;
		char ch, tmp;
		stack.clear();
		/* ���� ���� ���ڸ� �ԷµǾ��� �� */
		for (int k = 0; k < arr.get(0).length(); k++)
		{
			tmp = arr.get(0).charAt(k);
			if (tmp == '-')
			{
				minus = true;
				continue;
			}
			if (tmp == '.')
				dots = true;
			else
			{
				if (dots == true)
					under++;
				res = res*10 + (tmp-48);
			}
		}
		res = res/Math.pow(10.0f, under);
		if (minus == true)
			res = -res;
		dots = minus = false;
		under = 0;
		bres = new BigDecimal(String.valueOf(res));
		/* �������� ���� �κ� */
		for (int j = 0; j < arr.size(); j++)
		{
			a = b = 0;
			ch = arr.get(j).charAt(0);
			if (isOper(ch) && (arr.get(j).length() == 1))
			{
				for (int k = 0; k < stack.get(0).length(); k++)
				{
					tmp = stack.get(0).charAt(k);
					if (tmp == '-')
					{
						minus = true;
						continue;
					}
					if (tmp == '.')
						dots = true;
					else
					{
						if (dots == true)
							under++;
						b = b*10 + (tmp-48);
					}
				}
				b = b/(double)Math.pow(10.0f, under);
				if (minus == true)
					b = -b;
				dots = minus = false;
				under = 0;
				stack.remove(0);
				for (int k = 0; k < stack.get(0).length(); k++)
				{
					tmp = stack.get(0).charAt(k);
					if (tmp == '-')
					{
						minus = true;
						continue;
					}
					if (tmp == '.')
						dots = true;
					else
					{
						if (dots == true)
							under++;
						a = a*10 + (tmp-48);
					}
				}
				a = a/Math.pow(10.0f, under);
				if (minus == true)
					a = -a;
				dots = minus = false;
				under = 0;
				stack.remove(0);
				ba = new BigDecimal(String.valueOf(a));
				bb = new BigDecimal(String.valueOf(b));
				switch (ch)
				{
					case '+' :
						bres = ba.add(bb);
						break;
					case '-' :
						bres = ba.subtract(bb);
						break;
					case 'x' :
						bres = ba.multiply(bb);
						break;
					case '%' :
						bres = ba.divide(bb);
						break;
				}
				stack.add(0, bres.toString());
			}
			else
			{
				stack.add(0, arr.get(j));
			}
		}
		return bres.doubleValue();
	}
	/* �������ΰ� */
	boolean isOper(char ch)
	{
		switch (ch)
		{
		case '+' : case '-' : case 'x' : case '%' : case '/' : 
			return true;
		}
		return false;
	}
	/* ������ �������� �켱���� */
	int inP(char ch)
	{
		switch (ch) {
			case '(' : return 10;
			case '+' : 
			case '-' : return 2;
			case 'x' :
			case '/' :
			case '%' : return 3;
		}
		return 0;
	}
	/* ���� ž �������� �켱���� */
	int stP(char ch)
	{
		switch (ch) {
			case '(' : return 0;
			case '+' : 
			case '-' : return 2;
			case 'x' :
			case '/' :
			case '%' : return 3;
		}
		return 0;
	}
	
	public static void main(String[] args) {
		new Calculator();
	}
}
