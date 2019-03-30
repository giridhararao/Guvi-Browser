
package main.java.com.goxr3plus.javafxwebbrowser.browser;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.validator.routines.UrlValidator;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.scene.web.WebView;
import javafx.stage.StageStyle;
import main.java.com.goxr3plus.javafxwebbrowser.marquee.FXMarquee;
import main.java.com.goxr3plus.javafxwebbrowser.tools.InfoTool;
import net.sf.image4j.codec.ico.ICODecoder;
public class WebBrowserTabController extends StackPane {
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	@FXML
	private VBox errorPane;
	
	@FXML
	private JFXButton tryAgain;
	
	@FXML
	private ProgressIndicator tryAgainIndicator;
	
	@FXML
	private BorderPane borderPane;
	
	@FXML
	private JFXButton backwardButton;
	
	@FXML
	private JFXButton reloadButton;
	
	@FXML
	private JFXButton forwardButton;
	
	@FXML
	private JFXButton homeButton;
	
	@FXML
	private TextField searchBar;
	
	@FXML
	private JFXButton copyText;
	
	@FXML
	private JFXButton goButton;
	
	@FXML
	private JFXButton openInDefaultBrowser;
	
	@FXML
	private ToggleGroup searchEngineGroup;
	
	@FXML
	private CheckMenuItem movingTitleAnimation;
	
	@FXML
	private MenuItem printPage;
	
	@FXML
	private MenuItem notebookpage;
	
	@FXML
	private MenuItem findinpange;
	
	@FXML
	private MenuItem downloadPage;
	
	@FXML
	private CheckMenuItem cookieStorage;
	
	@FXML
	private WebView webView;
	WebEngine browser;
	private WebHistory history;
	private ObservableList<WebHistory.Entry> historyEntryList;
	
	private final Tab tab;
	private String firstWebSite;
	
	private final WebBrowserController webBrowserController;
	
	private final ImageView facIconImageView = new ImageView();
	public WebBrowserTabController(WebBrowserController webBrowserController, Tab tab, String firstWebSite) {
		this.webBrowserController = webBrowserController;
		this.tab = tab;
		this.firstWebSite = firstWebSite;
		this.tab.setContent(this);
    FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "WebBrowserTabController.fxml"));
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
		tryAgain.setOnAction(a -> checkForInternetConnection());
		browser = webView.getEngine();
		browser.getLoadWorker().exceptionProperty().addListener(error -> {
			checkForInternetConnection();
		});
		browser.getLoadWorker().stateProperty().addListener(new FavIconProvider());
		browser.getLoadWorker().stateProperty().addListener(new DownloadDetector());
		browser.getLoadWorker().stateProperty().addListener((observable , oldState , newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				errorPane.setVisible(false);
				
			} else if (newState == Worker.State.FAILED) {
				
				errorPane.setVisible(true);
			}
		});
		
		browser.setOnError(error -> {
			checkForInternetConnection();
		});
		setHistory(browser.getHistory());
		historyEntryList = getHistory().getEntries();
		SimpleListProperty<Entry> list = new SimpleListProperty<>(historyEntryList);
		
		tab.setTooltip(new Tooltip(""));
		tab.getTooltip().textProperty().bind(browser.titleProperty());

		StackPane stack = new StackPane();

		ProgressBar indicator = new ProgressBar();
		indicator.progressProperty().bind(browser.getLoadWorker().progressProperty());
		indicator.visibleProperty().bind(browser.getLoadWorker().runningProperty());
		indicator.setMaxSize(30, 11);

		Label label = new Label();
		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		label.setAlignment(Pos.CENTER);
		label.setStyle("-fx-background-color:#202020; -fx-font-weight:bold; -fx-text-fill: white; -fx-font-size:10;");
		label.textProperty().bind(Bindings.max(0, indicator.progressProperty()).multiply(100).asString("%.00f %%"));
		
		FXMarquee marquee = new FXMarquee();
		marquee.textProperty().bind(tab.getTooltip().textProperty());
		
		stack.getChildren().addAll(indicator, label);
		stack.setManaged(false);
		stack.setVisible(false);
		
		// stack
		indicator.visibleProperty().addListener(l -> {
			if (indicator.isVisible()) {
				stack.setManaged(true);
				stack.setVisible(true);
			} else {
				stack.setManaged(false);
				stack.setVisible(false);
			}
		});
		
		facIconImageView.setFitWidth(25);
		facIconImageView.setFitHeight(25);
		facIconImageView.setSmooth(true);
		Label iconLabel = new Label();
		iconLabel.setGraphic(facIconImageView);
		iconLabel.setStyle("-fx-background-color:#202020");
		iconLabel.visibleProperty().bind(indicator.visibleProperty().not());
		iconLabel.managedProperty().bind(facIconImageView.imageProperty().isNotNull().and(indicator.visibleProperty().not()));
		JFXButton closeButton = new JFXButton("X");
		int maxSize = 25;
		closeButton.setMinSize(maxSize, maxSize);
		closeButton.setPrefSize(maxSize, maxSize);
		closeButton.setMaxSize(maxSize, maxSize);
		closeButton.setStyle("-fx-background-radius:0; -fx-font-size:8px");
		closeButton.setOnAction(a -> this.webBrowserController.removeTab(tab));

		HBox hBox = new HBox();
		hBox.setOnMouseClicked(m -> {
			if (m.getButton() == MouseButton.MIDDLE)
				this.webBrowserController.removeTab(tab);
		});
		hBox.getChildren().addAll(iconLabel, stack, marquee, closeButton);
		tab.setGraphic(hBox);
		tab.setContextMenu(new WebBrowserTabContextMenu(this, webBrowserController));

		browser.getLoadWorker().runningProperty().addListener((observable , oldValue , newValue) -> {
			if (!newValue) // if !running
				searchBar.textProperty().unbind();
			else
				searchBar.textProperty().bind(browser.locationProperty());
		});
		searchBar.setOnAction(a -> loadWebSite(searchBar.getText()));
		searchBar.focusedProperty().addListener((observable , oldValue , newValue) -> {
			if (newValue)
				Platform.runLater(() -> searchBar.selectAll());
			
		});
		new AutoCompleteTextField().bindAutoCompletion(searchBar, 15, true, WebBrowserController.WEBSITE_PROPOSALS);
		goButton.setOnAction(searchBar.getOnAction());
		reloadButton.setOnAction(a -> reloadWebSite());
		backwardButton.setOnAction(a -> goBack());
		backwardButton.disableProperty().bind(getHistory().currentIndexProperty().isEqualTo(0));
		backwardButton.setOnMouseReleased(m -> {
			if (m.getButton() == MouseButton.MIDDLE) //Create and add it next to this tab
				webBrowserController.getTabPane().getTabs().add(webBrowserController.getTabPane().getTabs().indexOf(tab) + 1,
						webBrowserController.createNewTab(getHistory().getEntries().get(getHistory().getCurrentIndex() - 1).getUrl()).getTab());
		});
		
		forwardButton.setOnAction(a -> goForward());
		forwardButton.disableProperty().bind(getHistory().currentIndexProperty().greaterThanOrEqualTo(list.sizeProperty().subtract(1)));
		forwardButton.setOnMouseReleased(m -> {
			if (m.getButton() == MouseButton.MIDDLE) //Create and add it next to this tab
				webBrowserController.getTabPane().getTabs().add(webBrowserController.getTabPane().getTabs().indexOf(tab) + 1,
						webBrowserController.createNewTab(getHistory().getEntries().get(getHistory().getCurrentIndex() + 1).getUrl()).getTab());
		});
		

		movingTitleAnimation.selectedProperty().addListener((observable , oldValue , newValue) -> {
			marquee.checkAnimationValidity(newValue);
		});
		movingTitleAnimation.setSelected(WebBrowserController.MOVING_TITLES_ENABLED);

		loadWebSite(firstWebSite);
		
		printPage.setOnAction((e) -> {
			PrinterJob job = PrinterJob.createPrinterJob();
			if (job != null) {
				browser.print(job);
				job.endJob();
			}
		});
		cookieStorage.selectedProperty().addListener((observable , oldvalue , newvalue) -> {
			if (newvalue) {
				CookieManager manager = new CookieManager();
				manager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
				CookieHandler.setDefault(manager);
				CookieStore store = manager.getCookieStore();
				try {
					URI uriadd = new URI(getHistory().getEntries().get(getHistory().getCurrentIndex()).getUrl());
					store.add(uriadd, new HttpCookie("name", "value"));
					
				} catch (URISyntaxException e1) {
					
					e1.printStackTrace();
				}
				
				try {
					
					URI getcookie = new URI(getHistory().getEntries().get(getHistory().getCurrentIndex()).getUrl());
					store.get(getcookie);
				} catch (URISyntaxException e1) {
					
					e1.printStackTrace();
				}
				
			} else {
				CookieManager manager = new CookieManager();
				manager.setCookiePolicy(CookiePolicy.ACCEPT_NONE);
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.initStyle(StageStyle.UTILITY);
				alert.setTitle("COOKIES STATUS");
				alert.setHeaderText(null);
				alert.setContentText("Browser DISABLED COOKIES NO TRACKING");
				
				alert.showAndWait();
			}
		});
		
		downloadPage.setOnAction( ( printpage -> {
			try {
				URI u = new URI(getHistory().getEntries().get(getHistory().getCurrentIndex()).getUrl());
				//Authenticator.setDefault(new DialogAuthenticator());
				
				String new_ur = u.getHost() + u.getRawPath().replaceAll("/", ".") + "html";
				FileOutputStream fos = new FileOutputStream("D:\\" + new_ur + ".html", true);
				
				InputStream in = u.toURL().openStream();
				int c;
				while ( ( c = in.read() ) != -1) {
					fos.write(c);
					
				}
				
				in.close();
				fos.close();
			} catch (IOException | URISyntaxException e) {
				System.out.println("exception occured" + e.getMessage());
			}
		} ));
		
	}
	
	private String getHostName(String urlInput) {
		try {
			URL url = new URL(urlInput);
			return url.getProtocol() + "://" + url.getHost() + "/";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	public String getSearchEngineHomeUrl(String searchProvider) {
		switch (searchProvider.toLowerCase()) {
			case "bing":
				return "http://www.bing.com";
			case "duckduckgo":
				return "https://duckduckgo.com";
			case "yahoo":
				return "https://search.yahoo.com";
			default: //then google
				return "https://www.google.com";
		}
	}
	public String getSelectedEngineHomeUrl() {
		return getSearchEngineHomeUrl( ( (RadioMenuItem) searchEngineGroup.getSelectedToggle() ).getText());
	}
	private void loadWebSite(String webSite) {

		String load = !new UrlValidator().isValid(webSite) ? null : webSite;
		try {
			
			String finalWebsiteFristPart = ( load != null ) ? load : getSelectedEngineHomeUrl();

			String finalWebsiteSecondPart = "";
			if (searchBar.getText().isEmpty())
				finalWebsiteSecondPart = "";
			else {
				switch ( ( (RadioMenuItem) searchEngineGroup.getSelectedToggle() ).getText()) {
					case "bing":
					case "duckduckgo":
						finalWebsiteSecondPart = "//?q=" + URLEncoder.encode(searchBar.getText(), "UTF-8");
						break;
					case "yahoo":
						finalWebsiteSecondPart = "//?q=" + URLEncoder.encode(searchBar.getText(), "UTF-8");
						break;
					default: 
						finalWebsiteSecondPart = "//search?q=" + URLEncoder.encode(searchBar.getText(), "UTF-8");
						break;
				}
				
			}

			browser.load(finalWebsiteFristPart + finalWebsiteSecondPart);
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		
	}

	public void loadDefaultWebSite() {
		browser.load(getSelectedEngineHomeUrl());
	}

	public void reloadWebSite() {
		if (!getHistory().getEntries().isEmpty())
			browser.reload();
		else
			loadDefaultWebSite();
	}

	public void goBack() {
		getHistory().go(historyEntryList.size() > 1 && getHistory().getCurrentIndex() > 0 ? -1 : 0);
	}
	public void goForward() {
		getHistory().go(historyEntryList.size() > 1 && getHistory().getCurrentIndex() < historyEntryList.size() - 1 ? 1 : 0);
	}
	
	public WebView getWebView() {
		return webView;
	}
	public Tab getTab() {
		return tab;
	}
	
	public VBox getErrorPane() {
		return errorPane;
	}

	void checkForInternetConnection() {
		tryAgainIndicator.setVisible(true);

		Thread thread = new Thread(() -> {
			boolean hasInternet = InfoTool.isReachableByPing("www.google.com");
			Platform.runLater(() -> {
				errorPane.setVisible(!hasInternet);
				tryAgainIndicator.setVisible(false);
				if (hasInternet)
					reloadWebSite();
			});
		}, "Internet Connection Tester Thread");
		thread.setDaemon(true);
		thread.start();
	}

	public WebHistory getHistory() {
		return history;
	}
	
	public void setHistory(WebHistory history) {
		this.history = history;
	}
	public void setMovingTitleEnabled(boolean value) {
		movingTitleAnimation.setSelected(value);
	}

	public class FavIconProvider implements ChangeListener<State> {
		
		@Override
		public void changed(ObservableValue<? extends State> observable , State oldState , State newState) {
			if (newState == Worker.State.SUCCEEDED) {
				try {
					if ("about:blank".equals(browser.getLocation()))
						return;
					
					String favIconFullURL = getHostName(browser.getLocation()) + "favicon.ico";
					HttpURLConnection httpcon = (HttpURLConnection) new URL(favIconFullURL).openConnection();
					httpcon.addRequestProperty("User-Agent", "Mozilla/5.0");
					List<BufferedImage> image = ICODecoder.read(httpcon.getInputStream());
					
					facIconImageView.setImage(SwingFXUtils.toFXImage(image.get(0), null));
					
				} catch (Exception ex) {
					facIconImageView.setImage(null);
				}
			}
		}
	}
	
	public class DownloadDetector implements ChangeListener<State> {
		
		@Override
		public void changed(ObservableValue<? extends State> observable , State oldState , State newState) {
			if (newState == Worker.State.CANCELLED) {

				String url = browser.getLocation();
				logger.info("download url: " + url);
			
			}
		}
	}
	
}
