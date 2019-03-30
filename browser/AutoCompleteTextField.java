
package main.java.com.goxr3plus.javafxwebbrowser.browser;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javafx.beans.InvalidationListener;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public final class AutoCompleteTextField {
	
	private SortedSet<String> entries = new TreeSet<>();
	
	private final ContextMenu contextMenu = new ContextMenu();
	
  private int maximumEntries = 15;
	
	private final StringBuilder stringBuilder = new StringBuilder();
	
	private int lastLength;
	
	private TextField textField;
	
	private final InvalidationListener textListener = v -> {
		if (textField.getText().length() == 0 || entries.isEmpty())
			contextMenu.hide();
		else {
			populatePopup();
			if (!contextMenu.isShowing()) {
				contextMenu.show(textField, Side.BOTTOM, 0, 0);
				if (!contextMenu.getItems().isEmpty())
					contextMenu.getSkin().getNode().lookup(".menu-item:nth-child(1)").requestFocus();
			}
		}
		
	};

	private final InvalidationListener focusListener = v -> {
		lastLength = 0;
		stringBuilder.delete(0, stringBuilder.length());
		contextMenu.hide();
	};
	
	private EventHandler<? super KeyEvent> keyHandler = key -> {
		KeyCode k = key.getCode();
		
		if (lastLength != ( textField.getLength() - textField.getSelectedText().length() ))
			lastLength = textField.getLength() - textField.getSelectedText().length();
		
		boolean pass = true;
		System.out.println(k.getName());

		if (key.isControlDown() || k == KeyCode.BACK_SPACE || k == KeyCode.RIGHT || k == KeyCode.LEFT || k == KeyCode.DELETE || k == KeyCode.HOME || k == KeyCode.END
				|| k == KeyCode.TAB) {
			pass = false;
		}
		
		if (pass) {
			IndexRange indexRange = textField.getSelection();
			stringBuilder.delete(0, stringBuilder.length());
			stringBuilder.append(textField.getText());

			try {
				stringBuilder.delete(indexRange.getStart(), stringBuilder.length());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String originalLowered = textField.getText().toLowerCase();
			for (String entry : entries)
				if (entry.toLowerCase().startsWith(originalLowered)) {
					try {
						textField.setText(entry);
					} catch (Exception e) {
						textField.setText(stringBuilder.toString());
					}
					textField.positionCaret(stringBuilder.toString().length());
					textField.selectEnd();
					break;
				}
			
			System.out.println("Passed...");
		}
	};
	
	public void bindAutoCompletion(TextField textField , int maximumEntries , boolean addKeyListener , List<String> list) {
		entries.addAll(list);
		bindAutoCompletion(textField, maximumEntries, addKeyListener);
	}
	
	public void bindAutoCompletion(TextField textField , int maximumEntries , boolean addKeyListener , SortedSet<String> sortedSet) {
		entries = sortedSet;
		bindAutoCompletion(textField, maximumEntries, addKeyListener);
	
	public void bindAutoCompletion(TextField textField , int maximumEntries , boolean addKeyListener) {
		this.textField = textField;
		this.maximumEntries = maximumEntries <= 0 ? 10 : maximumEntries;
		
		textField.textProperty().addListener(textListener);
		
		textField.focusedProperty().addListener(focusListener);
		
		
	}
	
	public void removeAutoCompletion() {
		
		textField.textProperty().removeListener(textListener);
		
		textField.focusedProperty().removeListener(focusListener);
		
		textField.removeEventHandler(KeyEvent.KEY_RELEASED, keyHandler);
	}
	
	public SortedSet<String> getEntries() {
		return entries;
	}
	

	public int getMaximumEntries() {
		return maximumEntries;
	}
	

	public void setMaximumEntries(int maximumEntries) {
		this.maximumEntries = maximumEntries;
	}

	private void populatePopup() {
		contextMenu.getItems().clear();
		
		String text = textField.getText().toLowerCase();
		

		contextMenu.getItems()
				.addAll(entries.stream().filter(string -> string.toLowerCase().contains(text.toLowerCase())).limit(maximumEntries).map(MenuItem::new).collect(Collectors.toList()));
		contextMenu.getItems().forEach(item -> item.setOnAction(a -> {
			textField.setText(item.getText());
			textField.positionCaret(textField.getLength());
		}));
	}
}
