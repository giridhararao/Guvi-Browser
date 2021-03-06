package main.java.com.goxr3plus.javafxwebbrowser.application.controller;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import main.java.com.goxr3plus.javafxwebbrowser.application.Main;
import main.java.com.goxr3plus.javafxwebbrowser.tools.InfoTool;

public class CloseAppBox extends StackPane {
	
	
	@FXML
	private JFXButton minimize;
	
	@FXML
	private JFXButton maxOrNormalize;
	
	@FXML
	private JFXButton exitApplication;
  
	public CloseAppBox() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "CloseAppBox.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	@FXML
	private void initialize() {
		
		minimize.setOnAction(ac -> Main.window.setIconified(true));

		maxOrNormalize.setOnAction(ac -> Main.borderlessScene.maximizeStage());

		exitApplication.setOnAction(ac -> System.exit(0));
		
	}
	
}
