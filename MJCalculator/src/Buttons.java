import java.awt.*;
import javax.swing.JButton;

public class Buttons extends JButton {
	Buttons()
	{
		super();
		setBackground(Color.DARK_GRAY);
		setForeground(Color.WHITE);
		setFont(new Font("210 상상공작소 L", Font.BOLD, 16));
	}
	Buttons(String str)
	{
		super(str);
		setBackground(Color.DARK_GRAY);
		setForeground(Color.WHITE);
		setFont(new Font ("210 상상공작소 L", Font.BOLD, 16));
	}
	
}
