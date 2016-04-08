package lanchat.common.message;

import java.io.Serializable;

public class TextMessage implements Serializable{

  private static final long serialVersionUID = -7213275850124433331L;
  private String text;

  public TextMessage(String text) {
    setText(text);
  }

  public String getText() {
    return text;
  }

  private void setText(String text) {
    this.text = text;
  }
}
