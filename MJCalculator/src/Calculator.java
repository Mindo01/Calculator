
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.swing.*;


public class Calculator extends JFrame {
	/* 인스턴스 변수 */
	ArrayList<String> arr = new ArrayList<String>();
	ArrayList<String> stack = new ArrayList<String>();
	JPanel basePanel = new JPanel();
	JPanel show, key;
	Labels exp, res;
	/* 그리드 레이아웃에 저장할 순서대로 스트링값 배열 생성 */
	String[] texts = {"C", "%", "x", "←", "7", "8", "9", "-", "4", "5", "6", "+", "1", "2", "3", "()", "0", ".", "+/-", "="};
	/* 모든 버튼에 대한 배열로 처리 */
	Buttons[] allb = new Buttons[20];
	/* 숫자 */
	boolean numFlag = false;	// 이전에 숫자 입력되었으면 true, 아니면 false
	boolean pmFlag = false;		// plus false minus true
	boolean operFlag = false;	// 연산자들 중복 입력 시 에러 핸들링 / 이전에 연산자 입력되었으면 true 아니면 false
	int braceFlag = 0;	// 괄호 flag. 아무것도 아닐 때 0, ( 누르면 1, ) 누르면 2 (직전 입력 기준)
	int openCount = 0;	// 괄호 ( 개수
	int closeCount = 0;	// 괄호 ) 개수
	boolean resFlag = false;	// 결과값 출력한 바로 직후면 true
	int zeroCount = 0;			// 0 카운트
	boolean dotFlag = false;	// 소수점 플래그. 숫자 뒤, 연산자 지나친 후엔 false / .을 이미 사용한 후엔 true (2.12.34 방지)
	/* 후위연산식 모드 때 사용 */
	String resStr = "";
	/* 숫자 등 연산과정을 보여주는 부분의 윗 패널 */
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
	/* 키 버튼 입력을 받는 아래 부분 패널 */
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
	/* 생성자 - 프레임 구성 */
	Calculator()
	{
		/* 기본 설정 - 타이틀, 닫기 버튼 */
		setTitle("민주 계산기");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(basePanel);
		/* 레이아웃 설정 - 배치 관리자 제거, 직접 배치 */
		setLayout(null);
		/* 위 패널 설정 */
		show = new showPanel();
		show.setSize(320, 160);
		show.setLocation(0, 0);
		/* 아래 패널 설정 */
		key = new keyPanel();
		key.setSize(320, 400);
		key.setLocation(0, 160);
		/* 패널에 컴포넌트 설정 */
		basePanel.add(show);
		basePanel.add(key);
		/* 전체 사이즈 설정 */
		setSize(325, 587);
		/* 화면 중앙에 생성 */
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width-320)/2, (screenSize.height-560)/2);
		/* 창 크기 고정 */
		setResizable(false);
		/* 화면에 보여랏! */
		setVisible(true);
	}
	class MyActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			Buttons btn = (Buttons)arg0.getSource();
			/* showPanel용 스트링값 */
			String input = btn.getText();
			String bef = exp.getText();
			int ind = findInd(input);
			char ch;
			/* 결과값 출력 후, 이어서 연산 하고 싶을 때 */
			if (resFlag == true)
				if (ind == 1 || ind == 2 || ind == 7 || ind == 11)
					bef = res.getText().substring(1, res.getText().length());
				else
					bef = "";
			resFlag = false;
			/* 버튼 입력에 따른 화면 출력 & 기능들 */
			switch(ind)
			{
				/* C 일때, 초기화 */
				case 0 :
					exp.setText("");
					res.setText("");
					numFlag = pmFlag = operFlag = dotFlag = false;
					zeroCount = braceFlag = 0;
					return ;
				/* BackSpace일때, 문자 하나 삭제 */
				case 3 : 
					exp.setText(bef.substring(0, bef.length()-1));
					return ;
				/* 숫자일 경우 */
				case 16:	//0일 때
					if (zeroCount == 1)
					{
						input = "";
						zeroCount = 0;
						break;
					}
				case 4: case 5: case 6: case 8: case 9: case 10: case 12: case 13: case 14:
					/* 제로가 한 개 쳐지나요~ */
					if (zeroCount == 1)
					{
						bef = bef.substring(0, bef.length()-1);
						zeroCount = 2;
					}
					break;
				/* 연산자일 경우 */
				case 1: case 2: case 7: case 11:
					if (operFlag || bef.equals(""))
						/* 연산자 중복 입력 방지 */
						return ;
					break;
				/* 괄호 입력 시 */
				case 15 :
					if (braceFlag == 0)
					{
						/* ()(), num()일 때, 곱하기로 처리 */
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
				/* . 일때 */
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
				/* +- 일때 */
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
				/* = 일때, 결과값 도출 */
				case 19 :
					if (operFlag == true)
					{
						res.setText("잘못된 연산식입니다");
						return ;
					}
					postfix(bef);	// 중위 -> 후위연산식으로 변환
					resStr = String.valueOf(calc());
					res.setText("="+resStr);	// 연산 후 결과 값 출력 : res라벨에 출력
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
		/* 숫자입력 flag */
		if ((ind >= 4 && ind <= 6) || (ind >= 8 && ind <= 10) || (ind >= 12 && ind <= 14) || ind == 16)
		{
			numFlag = true;
			/* 0 이 입력되었어요 ㅎㅎ */
			if (ind == 16)
				zeroCount++;
			else
				zeroCount = 2;
		}
		else
			numFlag = false;
		/* 연산자 flag */
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
	/* 중위 -> 후위 */
	void postfix(String input)
	{
		arr.clear();
		stack.clear();
		char ch;
		int i;
		boolean befNumFlag = false;	// 이전에 숫자 있었는지 플래그
		System.out.println("input : '"+input+"'");
		for (i = 0; i < input.length(); i++)
		{
			ch = input.charAt(i);
			if (ch == '(')
			{
				stack.add(0, String.valueOf(ch));	// 인덱스 0 이 탑
			}
			else
			{
				if (ch == ')')
				{
					/* )나오면 스택에 ( 나올 때까지 pop */
					while (!stack.get(0).equals("("))
					{
						arr.add(stack.remove(0));
					}
					stack.remove(0);
				}
				else
				{
					/* 피연산자일 경우 그냥 arr에 쑥쑥*/
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
						/* 연산자 일 경우*/
						if (isOper(ch) == true){
							/* 스택이 안비어있고, top연산자가 식연산자보다 우선순위가 높으면 pop */
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
			/* befNumFlag를 사용해 (-3)을 단일 음수값으로 처리 */
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
	/* 후위연산을 연산해 결과값 도출 */
	double calc()
	{
		double a = 0, b = 0, res = 0;
		BigDecimal ba, bb, bres;
		int under = 0;
		boolean dots = false;
		boolean minus = false;
		char ch, tmp;
		stack.clear();
		/* 연산 없이 숫자만 입력되었을 때 */
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
		/* 실질적인 연산 부분 */
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
	/* 연산자인가 */
	boolean isOper(char ch)
	{
		switch (ch)
		{
		case '+' : case '-' : case 'x' : case '%' : case '/' : 
			return true;
		}
		return false;
	}
	/* 들어오는 연산자의 우선순위 */
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
	/* 스택 탑 연산자의 우선순위 */
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
