import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

public class Labels extends JLabel{
	Labels() {
		super();
		setForeground(Color.ORANGE);
		setFont(new Font("210 상상공작소 L", Font.BOLD, 25));
	}
	Labels(String str) {
		super(str);
		setForeground(Color.ORANGE);
		setFont(new Font("210 상상공작소 L", Font.BOLD, 25));
	}
}
