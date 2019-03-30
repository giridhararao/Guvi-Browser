
package main.java.com.goxr3plus.javafxwebbrowser.browser;

import java.io.IOException;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import main.java.com.goxr3plus.javafxwebbrowser.tools.InfoTool;

public class WebBrowserController extends StackPane {

	private final Logger logger = Logger.getLogger(getClass().getName());
	
	public static boolean MOVING_TITLES_ENABLED = true;
	
    @FXML
    private JFXTabPane tabPane;

    @FXML
    private JFXButton youtube;

    @FXML
    private JFXButton soundCloud;

    @FXML
    private JFXButton facebook;

    @FXML
    private JFXButton printerest;

    @FXML
    private JFXButton twitter;

    @FXML
    private JFXButton linkedIn;

    @FXML
    private JFXButton dropBox;

    @FXML
    private JFXButton gmail;

    @FXML
    private JFXButton googleDrive;

    @FXML
    private JFXButton googleMaps;

    @FXML
    private JFXButton addTab;

	

	public WebBrowserController() {
		
	
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "WebBrowserController.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
	}
	
	@FXML
	private void initialize() {
		
		tabPane.getTabs().clear();
		createAndAddNewTab();

		addTab.setOnAction(a -> createAndAddNewTab());

		youtube.setOnAction(a -> createTabAndSelect("https://www.youtube.com/"));
		
		soundCloud.setOnAction(a -> createTabAndSelect("https://www.soundcloud.com"));
		
		facebook.setOnAction(a -> createTabAndSelect("https://www.facebook.com"));
		
		printerest.setOnAction(a -> createTabAndSelect("https://www.pinterest.com"));
		
		twitter.setOnAction(a -> createTabAndSelect("https://www.twitter.com"));
		
		linkedIn.setOnAction(a -> createTabAndSelect("https://www.linkedin.com/"));
		
		dropBox.setOnAction(a -> createTabAndSelect("https://www.dropbox.com"));
		
		gmail.setOnAction(a -> createTabAndSelect("https://www.gmail.com"));
		
		googleDrive.setOnAction(a -> createTabAndSelect("https://www.google.com"));
		
		googleMaps.setOnAction(a -> createTabAndSelect("https://maps.google.com/"));
		
	}
	

	public void createTabAndSelect(String url) {
		tabPane.getSelectionModel().select(createAndAddNewTab(url).getTab());
	}
	

	public WebBrowserTabController createAndAddNewTab(String... webSite) {

		WebBrowserTabController webBrowserTab = createNewTab(webSite);

		tabPane.getTabs().add(webBrowserTab.getTab());
		
		return webBrowserTab;
	}
	

	public WebBrowserTabController createNewTab(String... webSite) {

		Tab tab = new Tab("");
		WebBrowserTabController webBrowserTab = new WebBrowserTabController(this, tab, webSite.length == 0 ? null : webSite[0]);
		tab.setOnClosed(c -> {
	
			if (tabPane.getTabs().isEmpty())
				createAndAddNewTab();

			webBrowserTab.browser.load("about:blank");
			
			
		});
		
		return webBrowserTab;
	}

	public void closeTabsToTheRight(Tab givenTab) {

		if (tabPane.getTabs().size() <= 1)
			return;

		int start = tabPane.getTabs().indexOf(givenTab);

		tabPane.getTabs().stream()
				.filter(tab -> tabPane.getTabs().indexOf(tab) > start)
				.collect(Collectors.toList()).forEach(this::removeTab);
		
	}
	public void closeTabsToTheLeft(Tab givenTab) {
		if (tabPane.getTabs().size() <= 1)
			return;

		int start = tabPane.getTabs().indexOf(givenTab);

		tabPane.getTabs().stream()
				.filter(tab -> tabPane.getTabs().indexOf(tab) < start)
				.collect(Collectors.toList()).forEach(this::removeTab);
		
	}

	public void removeTab(Tab tab) {
		tabPane.getTabs().remove(tab);
		tab.getOnClosed().handle(null);
	}

	public TabPane getTabPane() {
		return tabPane;
	}

	public void setMovingTitlesEnabled(boolean value) {
		MOVING_TITLES_ENABLED = value;
		tabPane.getTabs().forEach(tab -> ( (WebBrowserTabController) tab.getContent() ).setMovingTitleEnabled(value));
	}
	

	
	public static final SortedSet<String> WEBSITE_PROPOSALS = new TreeSet<>(Arrays.asList("https://www.google.com", "https://www.youtube.com","https://www.guvi.in","https://www.linkedin.com","https://www.stackoverflow.com","https://www.blogspot.com","https://www.cricbuzz.com","https://www.ebay.com","https://www.amazon.com","https://www.flipcart.com","https://www.zomato.com","https://www.swiggy.com"));
	
}
