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
    Main.isPaused = !Main.isPaused;
    if (Main.isPaused) {
      b.setText("Play");
    }
    else {
      b.setText("Pause");
    }
  }
}
