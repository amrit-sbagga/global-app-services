package global.services.client;

import global.services.client.rpc.AdvertisementService;
import global.services.client.rpc.AdvertisementServiceAsync;
import global.services.client.rpc.AppScoreService;
import global.services.client.rpc.AppScoreServiceAsync;
import global.services.client.rpc.FileService;
import global.services.client.rpc.FileServiceAsync;
import global.services.client.rpc.HighScoreService;
import global.services.client.rpc.HighScoreServiceAsync;
import global.services.client.rpc.LoginService;
import global.services.client.rpc.LoginServiceAsync;
import global.services.client.rpc.NotificationService;
import global.services.client.rpc.NotificationServiceAsync;
import global.services.shared.FileInfo;
import global.services.shared.Advertisement;
import global.services.shared.AppScore;
import global.services.shared.HighScore;
import global.services.shared.LoginInfo;
import global.services.shared.Notification;
import gwtupload.client.IUploader;
import gwtupload.client.SingleUploader;
import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import java.util.ArrayList;
import java.util.List;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;

import com.google.gwt.user.client.History;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class GlobalServices implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to conktact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */

	/**
	 * This is the entry point method.
	 */
	static LoginInfo loginInfo = null;
	private VerticalPanel loginPanel = new VerticalPanel();
	private Label loginLabel = new Label(
			"Please sign in to your Google Account to access the StockWatcher application.");
	private Anchor signInLink = new Anchor("Sign In");
	private Anchor signOutLink = new Anchor("Sign Out");

	private RootPanel rootPanel;
	public static DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.PX);
	public static TabLayoutPanel servicesTabPanel = new TabLayoutPanel(1.5,
			Unit.EM);
	private CellTable<AppScore> gamesCellTable = new CellTable<AppScore>();
	private CellTable<Advertisement> advsCellTable = new CellTable<Advertisement>();
	private CellTable<Notification> notesCellTable = new CellTable<Notification>();
	private CellTable<FileInfo> filesCellTable = new CellTable<FileInfo>();

	public static HorizontalPanel headerPanel = new HorizontalPanel();
	private HorizontalPanel accountPanel = new HorizontalPanel();
	public static HorizontalPanel footerPanel = new HorizontalPanel();
	private VerticalPanel scoresPanel = new VerticalPanel();
	private VerticalPanel gamesPanel = new VerticalPanel();

	static AppScoreServiceAsync appScoreSvc = GWT.create(AppScoreService.class);
	static AdvertisementServiceAsync advSvc = GWT
			.create(AdvertisementService.class);

	static FileServiceAsync fileSvc = GWT.create(FileService.class);
	static NotificationServiceAsync noteSvc = GWT
			.create(NotificationService.class);
	static HighScoreServiceAsync scoreSvc = GWT.create(HighScoreService.class);
	// static HighScoreServiceAsync scoreSvc =
	// GWT.create(HighScoreService.class);

	static Label lblAppRemaining = new Label(
			"Calculating number apps remaining...");
	static Label lblAdvRemaining = new Label(
			"Calculating number advertisements remaining...");
	static Label lblNoteRemaining = new Label(
			"Calculating number notes remaining...");
	static Label lblFileRemaining = new Label(
			"Calculating number files remaining...");
	static int numAppRemaining = 0;
	static int numAdvRemaining = 0;
	static int numNoteRemaining = 0;
	static int numFileRemaining = 0;

	static List<AppScore> listApp;
	static List<Advertisement> listAdvs;
	static List<Notification> listNotes;
	static List<FileInfo> listFiles;

	private List<Long> selectedApps = new ArrayList<Long>();
	private List<Long> selectedAdvs = new ArrayList<Long>();
	private List<Long> selectedNotes = new ArrayList<Long>();
	private List<Long> selectedFiles = new ArrayList<Long>();
	/*
	 * static String rootToken = "Homepage"; static String appScoreToken =
	 * "Create-app-score"; static String advertisementToken =
	 * "Create-advertesment"; static String highScoreToken = "Highscore"; static
	 * String notificationToken = "Notification"; static String
	 * createHighScoreToken = "Create-highscore";
	 */
	// private String strEntryNum = "...";

	static SingleUploader fileUploader;

	public void onModuleLoad() {

		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(),
				new AsyncCallback<LoginInfo>() {
					public void onFailure(Throwable error) {
					}

					public void onSuccess(LoginInfo result) {
						loginInfo = result;
						if (loginInfo.isLoggedIn()) {
							moduleLoad();
						} else {
							loadLogin();
						}
					}
				});
		servicesTabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				// TODO Auto-generated method stub

				switch (event.getSelectedItem()) {
				case 0:
					History.newItem("root-application");
					break;
				case 1:
					History.newItem("root-advertisement");
					break;
				case 2:
					History.newItem("root-file");
					break;

				}
			}
		});
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			public void onValueChange(ValueChangeEvent<String> event) {

				String historyToken = event.getValue();
				String[] tokenList = historyToken.split("-");

				// Parse the history token
				try {
					if (tokenList[0].equals("root")) {
						if (tokenList[1].equals("application"))
							servicesTabPanel.selectTab(0);
						if (tokenList[1].equals("advertisement"))
							servicesTabPanel.selectTab(1);
						if (tokenList[1].equals("file"))
							servicesTabPanel.selectTab(2);
						GlobalServices.ComebackHome(true);
					}
					if (tokenList[0].equals("application")) {
						if (tokenList[1] != null) {
							CreateAppScoresPage(Long.parseLong(tokenList[1]));
						} else {
							CreateAppScoresPage(null);
						}

					}

					if (tokenList[0].equals("advertisement")) {
						if (tokenList[1] != null) {
							CreateAdvertisementPage(Long
									.parseLong(tokenList[1]));
						} else {
							CreateAdvertisementPage(null);
						}
					}

					if (tokenList[0].equals("highscore")) {
						if (tokenList[2] != null) {
							UpdateHighscorePage(Long.parseLong(tokenList[2]));
						} else

						{
							if (tokenList[1] != null) {

								CreateHighscorePage(Long
										.parseLong(tokenList[1]));

							}
						}
					}

					if (tokenList[0].equals("notification")) {
						if (tokenList[2] != null) {
							UpdateNotificationPage(Long.parseLong(tokenList[2]));
						} else {
							if (tokenList[1] != null) {
								CreateNotificationPage(Long
										.parseLong(tokenList[1]));
							}
						}
					}

					if (tokenList[0].equals("scorelist")) {
						if (tokenList[1] != null) {
							HighScoreListPage(Long.parseLong(tokenList[1]));
						}
					}

					if (tokenList[0].equals("notelist")) {
						if (tokenList[1] != null) {
							NotificationListPage(Long.parseLong(tokenList[1]));
						}
					}

				} catch (IndexOutOfBoundsException e) {

				}
			}
		});

	}

	private void loadLogin() {
		// Assemble login panel.
		// Window.open(loginInfo.getLoginUrl(),null, null);
		Window.Location.assign(loginInfo.getLoginUrl());
		/*
		 * signInLink.setHref(loginInfo.getLoginUrl());
		 * 
		 * loginPanel.add(loginLabel); loginPanel.add(signInLink);
		 * RootPanel.get("content").add(loginPanel);
		 */
	}

	private void moduleLoad() {
		// Create header panel
		Image logo = new Image("images/GlobalAppServices.png");
		headerPanel.add(logo);
		// Create account panel
		accountPanel.add(new Label(loginInfo.getNickname()));
		accountPanel.add(new Label(" | "));
		signOutLink.setHref(loginInfo.getLogoutUrl());
		accountPanel.add(signOutLink);
		accountPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		headerPanel.add(accountPanel);

		// Create footer panel
		footerPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		footerPanel.add(new Label("Copyright 2011 Global App Services"));

		// Building services tabs
		// HighScore tab
		CreateAppScorePanel();

		// Advertisement tab
		CreateAdvertisementPanel();

		// Notification tab
		// CreateNotificationPanel();
		// File server tab
		CreateFileServerPanel();

		// Add panels to RootLayout
		// GlobalServices.ComebackHome(false);
		/*
		 * mainPanel.addNorth(headerPanel, 50); mainPanel.addSouth(footerPanel,
		 * 50); mainPanel.add(servicesTabPanel);
		 */
		rootPanel = RootPanel.get("content");

		RootLayoutPanel.get().add(mainPanel);
		// History.newItem(rootToken);
		History.newItem("root-application");
	}

	public void CreateAppScorePanel() {
		VerticalPanel highScorePanel = new VerticalPanel();
		highScorePanel.setStyleName("tabBackgroud");

		HorizontalPanel tableGamesHeaderPanel = new HorizontalPanel();

		VerticalPanel tableGamesFooterPanel = new VerticalPanel();
		HorizontalPanel tableGamesInfoPanel = new HorizontalPanel();
		tableGamesInfoPanel.add(lblAppRemaining);
		HorizontalPanel tableGamesCtrPanel = new HorizontalPanel();
		tableGamesCtrPanel.add(new Button("Create app", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (numAppRemaining <= 0) {
					Window.alert("You can not create more appplication.");
				} else {
					CreateAppScoresPage(null);

				}

			}
		}));

		tableGamesCtrPanel.add(new Button("Delete app", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (selectedApps.size() == 0) {
					Window.alert("You have to chose at least an application to delete.");
				} else {
					if (Window.confirm("Would you want to delete applications")) {
						AppScoreServiceAsync appScoreService = GWT
								.create(AppScoreService.class);
						appScoreService.DeleteApps(loginInfo.getEmailAddress(),
								selectedApps, new AsyncCallback<Integer>() {
									public void onFailure(Throwable caught) {
									}

									public void onSuccess(Integer result) {
										Window.alert(result
												+ " Applications have been deleted successful.");
										RefreshAppScoreTbl();
									}
								});
						selectedApps.clear();
					}
				}

			}
		}));
		tableGamesFooterPanel.add(tableGamesInfoPanel);
		tableGamesFooterPanel.add(tableGamesCtrPanel);

		highScorePanel.add(tableGamesHeaderPanel);

		final SelectionModel<AppScore> selectionAppModel = new MultiSelectionModel<AppScore>(
				AppScore.KEY_PROVIDER);
		gamesCellTable
				.setSelectionModel(selectionAppModel,
						DefaultSelectionEventManager
								.<AppScore> createCheckboxManager());

		Column<AppScore, Boolean> checkColumn = new Column<AppScore, Boolean>(
				new CheckboxCell(true, false)) {

			@Override
			public Boolean getValue(AppScore app) {
				// TODO Auto-generated method stub
				if (selectionAppModel.isSelected(app)) {
					if (!selectedApps.contains(app.getId()))
						selectedApps.add(app.getId());
				} else {
					if (selectedApps.contains(app.getId()))
						selectedApps.remove(app.getId());
				}
				return selectionAppModel.isSelected(app);
			}
		};
		gamesCellTable.addColumn(checkColumn,
				SafeHtmlUtils.fromSafeConstant("<br/>"));
		gamesCellTable.setColumnWidth(checkColumn, 40, Unit.PX);

		// Create appId column.
		Column<AppScore, String> appIdColumn = new Column<AppScore, String>(
				new ClickableTextCell()) {

			@Override
			public void render(Context context, AppScore object,
					SafeHtmlBuilder sb) {
				// TODO Auto-generated method stub
				super.render(context, object, sb);
				if (object != null) {
					sb.appendHtmlConstant("<div class=\"clickableanchor\">");
					sb.appendEscaped(String.valueOf(object.getId()));
					sb.appendHtmlConstant("</div>");
				}
			}

			@Override
			public void onBrowserEvent(Context context, Element elem,
					AppScore object, NativeEvent event) {
				// TODO Auto-generated method stub
				super.onBrowserEvent(context, elem, object, event);
				if (event.getType().equals("click")) {
					// CreateAppScoresPage(object.getId());
					History.newItem("application-" + object.getId());

				}

			}

			@Override
			public String getValue(AppScore object) {
				// TODO Auto-generated method stub
				return null;
			}

		};
		appIdColumn.setSortable(true);
		gamesCellTable.addColumn(appIdColumn, "AppId");

		// Create appName column.
		TextColumn<AppScore> appNameColumn = new TextColumn<AppScore>() {
			@Override
			public String getValue(AppScore app) {
				return app.getAppName();
			}
		};
		appNameColumn.setSortable(true);
		gamesCellTable.addColumn(appNameColumn, "App Name");

		// Create appTittle column.
		TextColumn<AppScore> appTittleColumn = new TextColumn<AppScore>() {
			@Override
			public String getValue(AppScore app) {
				return app.getAppTittle();
			}
		};
		appTittleColumn.setSortable(true);
		gamesCellTable.addColumn(appTittleColumn, "Description");

		// Create Score entries column
		Column<AppScore, String> scoreEntriesColumn = new Column<AppScore, String>(
				new ClickableTextCell()) {

			@Override
			public void render(Context context, AppScore object,
					SafeHtmlBuilder sb) {
				// TODO Auto-generated method stub
				super.render(context, object, sb);
				if (object != null) {
					sb.appendHtmlConstant("<div class=\"clickableanchor\">");
					sb.appendEscaped(String.valueOf(object.getScoreEntries()));
					sb.appendHtmlConstant("</div>");
				}
			}

			@Override
			public void onBrowserEvent(Context context, Element elem,
					AppScore object, NativeEvent event) {
				// TODO Auto-generated method stub
				super.onBrowserEvent(context, elem, object, event);
				if (event.getType().equals("click")) {
					appId = object.getId();
					// HighScoreListPage(object.getId());
					History.newItem("scorelist-" + object.getId());
				}

			}

			@Override
			public String getValue(AppScore object) {
				// TODO Auto-generated method stub
				return null;
			}

		};
		scoreEntriesColumn.setSortable(true);
		gamesCellTable.addColumn(scoreEntriesColumn, "Score entries");

		// Create notification entries column
		Column<AppScore, String> noteEntriesColumn = new Column<AppScore, String>(
				new ClickableTextCell()) {

			@Override
			public void render(Context context, AppScore object,
					SafeHtmlBuilder sb) {
				// TODO Auto-generated method stub
				super.render(context, object, sb);
				if (object != null) {
					sb.appendHtmlConstant("<div class=\"clickableanchor\">");
					sb.appendEscaped(String.valueOf(object.getNoteEntries()));
					sb.appendHtmlConstant("</div>");
				}
			}

			@Override
			public void onBrowserEvent(Context context, Element elem,
					AppScore object, NativeEvent event) {
				// TODO Auto-generated method stub
				super.onBrowserEvent(context, elem, object, event);
				if (event.getType().equals("click")) {
					appId = object.getId();
					// NotificationListPage(object.getId());
					History.newItem("notelist-" + object.getId());
				}

			}

			@Override
			public String getValue(AppScore object) {
				// TODO Auto-generated method stub
				return null;
			}

		};
		noteEntriesColumn.setSortable(true);
		gamesCellTable.addColumn(noteEntriesColumn, "Note entries");

		// Create a data provider.
		ListDataProvider<AppScore> dataProvider = new ListDataProvider<AppScore>();

		// Connect the table to the data provider.
		dataProvider.addDataDisplay(gamesCellTable);

		// Add the data to the data provider, which automatically pushes it to
		// the
		// widget.
		listApp = dataProvider.getList();
		RefreshAppScoreTbl();

		highScorePanel.add(gamesCellTable);
		highScorePanel.add(tableGamesFooterPanel);
		servicesTabPanel.add(highScorePanel, "HighScores");
	}

	public void CreateAdvertisementPanel() {
		VerticalPanel advsPanel = new VerticalPanel();
		advsPanel.setStyleName("tabBackgroud");

		HorizontalPanel tableAdvsHeaderPanel = new HorizontalPanel();

		VerticalPanel tableAdvsFooterPanel = new VerticalPanel();
		HorizontalPanel tableAdvsInfoPanel = new HorizontalPanel();
		tableAdvsInfoPanel.add(lblAdvRemaining);
		HorizontalPanel tableAdvsCtrPanel = new HorizontalPanel();
		tableAdvsCtrPanel.add(new Button("Create adv", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if ((numAdvRemaining <= 0) || (numFileRemaining <= 0)) {
					Window.alert("You can not create more advertisement.");
				} else {
					// CreateAdvertisementPage(null);
					History.newItem("advertisement");

				}

			}
		}));

		tableAdvsCtrPanel.add(new Button("Delete advs", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				if (selectedAdvs.size() == 0) {
					Window.alert("You have to chose at least an advertisement to delete.");
				} else {
					if (Window
							.confirm("Would you want to delete advertisements")) {
						AdvertisementServiceAsync advertisementService = GWT
								.create(AdvertisementService.class);
						advertisementService.DeleteAdvs(
								loginInfo.getEmailAddress(), selectedAdvs,
								new AsyncCallback<Integer>() {
									public void onFailure(Throwable caught) {
									}

									public void onSuccess(Integer result) {
										// TODO Auto-generated method stub
										Window.alert(result
												+ " Advertisements have been deleted successful.");
										RefreshAdvertisementTbl();

									}
								});
						selectedAdvs.clear();
					}
				}
			}
		}));
		tableAdvsFooterPanel.add(tableAdvsInfoPanel);
		tableAdvsFooterPanel.add(tableAdvsCtrPanel);

		advsPanel.add(tableAdvsHeaderPanel);

		final SelectionModel<Advertisement> selectionAdvModel = new MultiSelectionModel<Advertisement>(
				Advertisement.KEY_PROVIDER);
		advsCellTable.setSelectionModel(selectionAdvModel,
				DefaultSelectionEventManager
						.<Advertisement> createCheckboxManager());

		Column<Advertisement, Boolean> checkColumn = new Column<Advertisement, Boolean>(
				new CheckboxCell(true, false)) {

			@Override
			public Boolean getValue(Advertisement adv) {
				// TODO Auto-generated method stub
				if (selectionAdvModel.isSelected(adv)) {
					if (!selectedAdvs.contains(adv.getId()))
						selectedAdvs.add(adv.getId());
				} else {
					if (selectedAdvs.contains(adv.getId()))
						selectedAdvs.remove(adv.getId());
				}
				return selectionAdvModel.isSelected(adv);
			}
		};
		advsCellTable.addColumn(checkColumn,
				SafeHtmlUtils.fromSafeConstant("<br/>"));
		advsCellTable.setColumnWidth(checkColumn, 40, Unit.PX);
		// Create icon column.
		/*
		 * Tam thoi gem lai xem sao Column<Advertisement, Image> iconColumn =
		 * new Column<Advertisement, Image> (null) {
		 * 
		 * @Override public Image getValue(Advertisement adv) { // TODO
		 * Auto-generated method stub Image img = new Image(adv.getIconUrl());
		 * return img; }
		 * 
		 * }; advsCellTable.addColumn(iconColumn);
		 */
		// Create appId column.
		Column<Advertisement, String> appIdColumn = new Column<Advertisement, String>(
				new ClickableTextCell()) {
			/*
			 * @Override public String getValue(AppScore app) { return
			 * app.getAppId(); }
			 */

			@Override
			public void render(Context context, Advertisement object,
					SafeHtmlBuilder sb) {
				// TODO Auto-generated method stub
				super.render(context, object, sb);
				if (object != null) {
					sb.appendHtmlConstant("<div class=\"clickableanchor\">");
					sb.appendEscaped(String.valueOf(object.getId()));
					sb.appendHtmlConstant("</div>");
				}
			}

			@Override
			public void onBrowserEvent(Context context, Element elem,
					Advertisement object, NativeEvent event) {
				// TODO Auto-generated method stub
				super.onBrowserEvent(context, elem, object, event);
				if (event.getType().equals("click")) {
					// CreateAdvertisementPage(object.getId());
					History.newItem("advertisement-" + object.getId());

				}

			}

			@Override
			public String getValue(Advertisement object) {
				// TODO Auto-generated method stub
				return null;
			}

		};
		appIdColumn.setSortable(true);
		advsCellTable.addColumn(appIdColumn, "AppId");

		// Create icon column.
		Column<Advertisement, String> iconColumn = new Column<Advertisement, String>(
				new ClickableTextCell()) {
			/*
			 * @Override public String getValue(AppScore app) { return
			 * app.getAppId(); }
			 */

			@Override
			public void render(Context context, Advertisement object,
					SafeHtmlBuilder sb) {
				// TODO Auto-generated method stub
				super.render(context, object, sb);
				if (object != null) {
					String iconUrl = "http://global-app-services.appspot.com/globalservices/fileservlet?fileid=";
					iconUrl += object.getIconFile();
					sb.appendHtmlConstant("<img src=\"");
					sb.appendEscaped(iconUrl);
					sb.appendHtmlConstant("\" width=\"64px\" height=\"64px\"/>");
				}
			}

			@Override
			public String getValue(Advertisement object) {
				// TODO Auto-generated method stub
				return null;
			}

		};
		iconColumn.setSortable(true);
		advsCellTable.addColumn(iconColumn, "Icon");
		// Create appTittle column.
		TextColumn<Advertisement> appNameColumn = new TextColumn<Advertisement>() {
			@Override
			public String getValue(Advertisement adv) {
				return adv.getAppName();
			}

		};
		appNameColumn.setSortable(true);
		advsCellTable.addColumn(appNameColumn, "App Name");

		// Create appTittle column.
		TextColumn<Advertisement> appTittleColumn = new TextColumn<Advertisement>() {
			@Override
			public String getValue(Advertisement adv) {
				return adv.getTittle();
			}
		};
		appTittleColumn.setSortable(true);
		advsCellTable.addColumn(appTittleColumn, "Tittle");

		// Create Content column.
		TextColumn<Advertisement> contentColumn = new TextColumn<Advertisement>() {
			@Override
			public String getValue(Advertisement adv) {
				return String.valueOf(adv.getContent());
			}
		};
		contentColumn.setSortable(true);
		advsCellTable.addColumn(contentColumn, "Content");

		// Create Type column.
		TextColumn<Advertisement> typeColumn = new TextColumn<Advertisement>() {
			@Override
			public String getValue(Advertisement adv) {
				return String.valueOf(adv.getType());
			}
		};
		typeColumn.setSortable(true);
		advsCellTable.addColumn(typeColumn, "Type");

		// Create a data provider.
		ListDataProvider<Advertisement> dataProvider = new ListDataProvider<Advertisement>();

		// Connect the table to the data provider.
		dataProvider.addDataDisplay(advsCellTable);

		// Add the data to the data provider, which automatically pushes it to
		// the
		// widget.
		listAdvs = dataProvider.getList();

		// Set up the callback object.
		RefreshAdvertisementTbl();

		advsPanel.add(advsCellTable);
		advsPanel.add(tableAdvsFooterPanel);
		servicesTabPanel.add(advsPanel, "Advertisement");
	}

	public void CreateFileServerPanel() {
		VerticalPanel filesPanel = new VerticalPanel();
		filesPanel.setStyleName("tabBackgroud");

		HorizontalPanel tableFilesHeaderPanel = new HorizontalPanel();

		VerticalPanel tableFilesFooterPanel = new VerticalPanel();
		HorizontalPanel tableFilesInfoPanel = new HorizontalPanel();
		tableFilesInfoPanel.add(lblFileRemaining);
		VerticalPanel tableFilesCtrPanel = new VerticalPanel();

		fileUploader = new SingleUploader(FileInputType.LABEL);
		// fileUploader.add(new Hidden("userid", loginInfo.getEmailAddress()),
		// 0);
		fileUploader.setServletPath(fileUploader.getServletPath() + "?userid="
				+ loginInfo.getEmailAddress());
		fileUploader.setAutoSubmit(true);
		fileUploader.addOnFinishUploadHandler(onFinishUploaderHandler);
		tableFilesCtrPanel.add(fileUploader);

		tableFilesCtrPanel.add(new Button("Delete files", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				if (selectedFiles.size() == 0) {
					Window.alert("You have to chose at least a file to delete.");
				} else {
					if (Window.confirm("Would you want to delete files")) {
						FileServiceAsync fileService = GWT
								.create(FileService.class);
						fileService.DeleteFiles(loginInfo.getEmailAddress(),
								selectedFiles, new AsyncCallback<Integer>() {
									public void onFailure(Throwable caught) {
									}

									public void onSuccess(Integer result) {
										// TODO Auto-generated method stub
										Window.alert(result
												+ " files have been deleted successful.");
										RefreshFileTbl();
									}
								});
						selectedFiles.clear();
					}
				}
			}
		}));
		tableFilesFooterPanel.add(tableFilesInfoPanel);
		tableFilesFooterPanel.add(tableFilesCtrPanel);

		filesPanel.add(tableFilesHeaderPanel);

		final SelectionModel<FileInfo> selectionFileModel = new MultiSelectionModel<FileInfo>(
				FileInfo.KEY_PROVIDER);
		filesCellTable
				.setSelectionModel(selectionFileModel,
						DefaultSelectionEventManager
								.<FileInfo> createCheckboxManager());

		Column<FileInfo, Boolean> checkColumn = new Column<FileInfo, Boolean>(
				new CheckboxCell(true, false)) {

			@Override
			public Boolean getValue(FileInfo file) {
				// TODO Auto-generated method stub
				if (selectionFileModel.isSelected(file)) {
					if (!selectedFiles.contains(file.getId()))
						selectedFiles.add(file.getId());
				} else {
					if (selectedFiles.contains(file.getId()))
						selectedFiles.remove(file.getId());
				}
				return selectionFileModel.isSelected(file);
			}
		};
		filesCellTable.addColumn(checkColumn,
				SafeHtmlUtils.fromSafeConstant("<br/>"));
		filesCellTable.setColumnWidth(checkColumn, 40, Unit.PX);

		// Create file id column.
		TextColumn<FileInfo> fileIdColumn = new TextColumn<FileInfo>() {
			@Override
			public String getValue(FileInfo file) {
				return String.valueOf(file.getId());
			}
		};
		fileIdColumn.setSortable(true);
		filesCellTable.addColumn(fileIdColumn, "File ID");

		// Create file name column.
		TextColumn<FileInfo> fileNameColumn = new TextColumn<FileInfo>() {
			@Override
			public String getValue(FileInfo file) {
				return file.getFileName();
			}
		};
		fileNameColumn.setSortable(true);
		filesCellTable.addColumn(fileNameColumn, "File Name");

		// Create file type column.
		TextColumn<FileInfo> fileTypeColumn = new TextColumn<FileInfo>() {
			@Override
			public String getValue(FileInfo file) {
				return file.getFileType();
			}
		};
		fileTypeColumn.setSortable(true);
		filesCellTable.addColumn(fileTypeColumn, "File type");

		// Create file size column.

		TextColumn<FileInfo> fileSizeColumn = new TextColumn<FileInfo>() {
			@Override
			public String getValue(FileInfo file) {
				return file.getFileSize();
			}
		};
		fileSizeColumn.setSortable(true);
		filesCellTable.addColumn(fileSizeColumn, "File size");

		// Create a data provider.
		ListDataProvider<FileInfo> dataProvider = new ListDataProvider<FileInfo>();

		// Connect the table to the data provider.
		dataProvider.addDataDisplay(filesCellTable);

		// Add the data to the data provider, which automatically pushes it to
		// the
		// widget.
		listFiles = dataProvider.getList();

		RefreshFileTbl();

		filesPanel.add(filesCellTable);
		filesPanel.add(tableFilesFooterPanel);
		servicesTabPanel.add(filesPanel, "File Server");

	}

	private IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
		public void onFinish(IUploader uploader) {
			if (uploader.getStatus() == Status.SUCCESS) {
				RefreshFileTbl();
			}
		}
	};

	private Long appId;

	static void RefreshAppScoreTbl() {
		appScoreSvc.SelectApps(loginInfo.getEmailAddress(),
				new AsyncCallback<List<AppScore>>() {
					public void onFailure(Throwable caught) {
						// TODO: Do something
						// with
						// errors.
					}

					public void onSuccess(List<AppScore> result) {

						listApp.clear();
						listApp.addAll(result);
						numAppRemaining = 10 - result.size();
						lblAppRemaining.setText("You have " + numAppRemaining
								+ " remaining games.");
					}
				});

	}

	static void RefreshAdvertisementTbl() {
		advSvc.SelectAdvs(loginInfo.getEmailAddress(),
				new AsyncCallback<List<Advertisement>>() {
					public void onFailure(Throwable caught) {
						// TODO: Do something
						// with
						// errors.
					}

					public void onSuccess(List<Advertisement> result) {

						listAdvs.clear();
						listAdvs.addAll(result);
						numAdvRemaining = 10 - result.size();
						lblAdvRemaining.setText("You have " + numAdvRemaining
								+ " remaining advertisements.");
					}
				});

	}

	static void RefreshFileTbl() {
		fileSvc.SelectFiles(loginInfo.getEmailAddress(),
				new AsyncCallback<List<FileInfo>>() {
					public void onFailure(Throwable caught) {
						// TODO: Do something
						// with
						// errors.
					}

					public void onSuccess(List<FileInfo> result) {
						listFiles.clear();
						listFiles.addAll(result);
						numFileRemaining = 10 - result.size();
						lblFileRemaining.setText("You have " + numFileRemaining
								+ " remaining files.");
						fileUploader.setEnabled(numFileRemaining > 0);
					}
				});
	}

	static void ComebackHome(boolean reload) {

		if (reload) {
			RefreshAppScoreTbl();
			RefreshAdvertisementTbl();
			RefreshFileTbl();
		}
		GlobalServices.mainPanel.clear();
		GlobalServices.mainPanel.addNorth(GlobalServices.headerPanel, 50);
		GlobalServices.mainPanel.addSouth(GlobalServices.footerPanel, 50);
		GlobalServices.mainPanel.add(GlobalServices.servicesTabPanel);

	}

	private void CreateAppScoresPage(Long id) {
		// TODO Auto-generated method stub
		mainPanel.clear();
		mainPanel.addNorth(headerPanel, 50);
		mainPanel.addSouth(footerPanel, 50);
		CreateAppScores createApp;
		if (id == null) {
			createApp = new CreateAppScores(loginInfo.getEmailAddress());
		} else {

			createApp = new CreateAppScores(loginInfo.getEmailAddress(), id);
		}
		mainPanel.add(createApp.Initialize());
		// History.newItem("application");

	}

	private void HighScoreListPage(Long appId) {
		mainPanel.clear();
		mainPanel.addNorth(headerPanel, 50);
		mainPanel.addSouth(footerPanel, 50);
		HighScoreTable highScore = new HighScoreTable(
				loginInfo.getEmailAddress(), appId);

		mainPanel.add(highScore.Initialize());
		// History.newItem("scorelist-" + appId);
	}

	private void NotificationListPage(Long appId) {
		mainPanel.clear();
		mainPanel.addNorth(headerPanel, 50);
		mainPanel.addSouth(footerPanel, 50);
		NotificationTable noteTable = new NotificationTable(
				loginInfo.getEmailAddress(), appId);

		mainPanel.add(noteTable.Initialize());
		// History.newItem("notelist-" + appId);
	}

	private void CreateAdvertisementPage(Long id) {
		// TODO Auto-generated method stub
		mainPanel.clear();
		mainPanel.addNorth(headerPanel, 50);
		mainPanel.addSouth(footerPanel, 50);
		CreateAdvertisement createAdv;
		if (id == null) {
			createAdv = new CreateAdvertisement(loginInfo.getEmailAddress());
		} else {
			createAdv = new CreateAdvertisement(loginInfo.getEmailAddress(), id);
		}
		mainPanel.add(createAdv.Initialize());
		// History.newItem("advertisement");
	}

	private void CreateHighscorePage(Long appId) {
		// TODO Auto-generated method stub
		mainPanel.clear();
		mainPanel.addNorth(headerPanel, 50);
		mainPanel.addSouth(footerPanel, 50);
		CreateHighScore createHighscore = null;
		if (appId != null) {
			createHighscore = new CreateHighScore(loginInfo.getEmailAddress(),
					appId);

			mainPanel.add(createHighscore.Initialize());
		}
		// History.newItem("highscore-" + id);
	}

	private void UpdateHighscorePage(Long scoreId) {
		// TODO Auto-generated method stub
		mainPanel.clear();
		mainPanel.addNorth(headerPanel, 50);
		mainPanel.addSouth(footerPanel, 50);
		scoreSvc.SelectScore(loginInfo.getEmailAddress(), scoreId,
				new AsyncCallback<HighScore>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						CreateHighScore createHighscore = null;
						if (appId != null) {
							createHighscore = new CreateHighScore(loginInfo
									.getEmailAddress(), appId);

							mainPanel.add(createHighscore.Initialize());
						}

					}

					@Override
					public void onSuccess(HighScore result) {
						// TODO Auto-generated method stub
						if (result != null) {
							CreateHighScore createHighscore = new CreateHighScore(
									result);
							mainPanel.add(createHighscore.Initialize());
						}
					}

				});

		// History.newItem("highscore-" + score.getGameID());
	}

	private void CreateNotificationPage(Long appId) {
		// TODO Auto-generated method stub
		mainPanel.clear();
		mainPanel.addNorth(headerPanel, 50);
		mainPanel.addSouth(footerPanel, 50);
		CreateNotification createNote = null;
		if (appId != null) {
			createNote = new CreateNotification(loginInfo.getEmailAddress(),
					appId);
			mainPanel.add(createNote.Initialize());
		}
		// History.newItem("notification-" + appId);
	}

	private void UpdateNotificationPage(Long noteId) {
		// TODO Auto-generated method stub
		mainPanel.clear();
		mainPanel.addNorth(headerPanel, 50);
		mainPanel.addSouth(footerPanel, 50);

		noteSvc.SelectNote(loginInfo.getEmailAddress(), noteId,
				new AsyncCallback<Notification>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						CreateHighScore createHighscore = null;
						if (appId != null) {
							createHighscore = new CreateHighScore(loginInfo
									.getEmailAddress(), appId);

							mainPanel.add(createHighscore.Initialize());
						}

					}

					@Override
					public void onSuccess(Notification result) {
						// TODO Auto-generated method stub
						if (result != null) {
							CreateNotification createNote = new CreateNotification(
									result);
							mainPanel.add(createNote.Initialize());
						}
					}

				});

		// History.newItem("notification-" + appId);
	}
	/*
	 * @Override public void onHistoryChanged(String historyToken) { // TODO
	 * Auto-generated method stub if (historyToken.equals(rootPanel)) {
	 * ComebackHome(false); return; } if (historyToken.equals(appScoreToken)) {
	 * CreateAppScoresPage(null); return; } if
	 * (historyToken.equals(advertisementToken)) {
	 * CreateAdvertisementPage(null); return; }
	 * 
	 * if (historyToken.equals(highScoreToken)) { HighScorePage(appId); return;
	 * } if (historyToken.equals(createHighScoreToken)) { return; } }
	 */
}