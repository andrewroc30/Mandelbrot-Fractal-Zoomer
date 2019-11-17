import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class PlayPauseButton extends JButton implements ActionListener {

  public JButton b;

  public PlayPauseButton(String text) {
    b = new JButton(text);
    b.addActionListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Main.setIsPaused(!Main.getIsPaused());
    if (Main.getIsPaused()) {
      b.setText("Play");
    }
    else {
      b.setText("Pause");
    }
  }
}
