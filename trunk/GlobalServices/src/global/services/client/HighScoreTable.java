package global.services.client;

import global.services.client.rpc.AppScoreService;
import global.services.client.rpc.AppScoreServiceAsync;
import global.services.client.rpc.HighScoreService;
import global.services.client.rpc.HighScoreServiceAsync;
import global.services.shared.AppScore;
import global.services.shared.HighScore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

public class HighScoreTable {

	private String userId_ = null;
	private Long appId_ = null;
	private String appName_ = null;
	private AbsolutePanel headerTblPanel = new AbsolutePanel();
	private AbsolutePanel tableGamesCtrPanel = new AbsolutePanel();
	private CellTable<HighScore> scoreCellTable = new CellTable<HighScore>();
	private List<Long> selectedScores = new ArrayList<Long>();
	private VerticalPanel mainContent = new VerticalPanel();
	private List<HighScore> listScore = null;
	private Label lblAppInfo = new Label("Highscore table of ... application.");
	Anchor myAppLink = new Anchor("<< My applications");
	static HighScoreServiceAsync scoreSvc = GWT.create(HighScoreService.class);
	static AppScoreServiceAsync appSvc = GWT.create(AppScoreService.class);

	public HighScoreTable(String userId, Long appId) {
		userId_ = userId;
		appId_ = appId;
		appSvc.SelectApp(userId, appId, new AsyncCallback<AppScore>() {
			public void onFailure(Throwable caught) {
				// TODO: Do something
				// with
				// errors.
			}

			public void onSuccess(AppScore result) {

				appName_ = result.getAppName();
				lblAppInfo.setText("Highscore table of " + appName_
						+ " application.");
			}
		});
		RefreshHighScoreTbl();
	}

	public Widget Initialize() {

		mainContent.setStyleName("contentBackgroud");
		
		tableGamesCtrPanel.setStyleName("header-footer");
		headerTblPanel.setStyleName("header-footer");
		
		myAppLink.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				History.newItem("root-application");
			}
		});
		
		headerTblPanel.add(lblAppInfo);
		headerTblPanel.add(myAppLink);
		
		mainContent.add(headerTblPanel);

		final SelectionModel<HighScore> selectionAppModel = new MultiSelectionModel<HighScore>(
				HighScore.KEY_PROVIDER);
		scoreCellTable.setSelectionModel(selectionAppModel,
				DefaultSelectionEventManager
						.<HighScore> createCheckboxManager());

		Column<HighScore, Boolean> checkColumn = new Column<HighScore, Boolean>(
				new CheckboxCell(true, false)) {

			@Override
			public Boolean getValue(HighScore score) {
				// TODO Auto-generated method stub
				if (selectionAppModel.isSelected(score)) {
					if (!selectedScores.contains(score.getId()))
						selectedScores.add(score.getId());
				} else {
					if (selectedScores.contains(score.getId()))
						selectedScores.remove(score.getId());
				}
				return selectionAppModel.isSelected(score);
			}
		};
		scoreCellTable.addColumn(checkColumn, SafeHtmlUtils
				.fromSafeConstant("<br/>"));
		scoreCellTable.setColumnWidth(checkColumn, 40, Unit.PX);

		// Create scoreId column.
		Column<HighScore, String> scoreIdColumn = new Column<HighScore, String>(
				new ClickableTextCell()) {

			@Override
			public void render(Context context, HighScore object,
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
					HighScore object, NativeEvent event) {
				// TODO Auto-generated method stub
				super.onBrowserEvent(context, elem, object, event);
				if (event.getType().equals("click")) {
					/*
					CreateHighScore createScore = new CreateHighScore(object);
					mainContent.clear();
					createScore.setMainContent(mainContent);
					createScore.Initialize();
					*/
					History.newItem("highscore-" + object.getGameID()+ "-" + object.getId());
				}
				
			}

			@Override
			public String getValue(HighScore object) {
				// TODO Auto-generated method stub
				return null;
			}

		};
		scoreIdColumn.setSortable(true);
		scoreCellTable.addColumn(scoreIdColumn, "Score Id");

		// Create subBoard column.
		TextColumn<HighScore> levelColumn = new TextColumn<HighScore>() {
			@Override
			public String getValue(HighScore score) {
				return score.getSubBoard();
			}
		};
		levelColumn.setSortable(true);
		scoreCellTable.addColumn(levelColumn, "Level");

		// Create player column.
		TextColumn<HighScore> playerColumn = new TextColumn<HighScore>() {
			@Override
			public String getValue(HighScore score) {
				// TODO Auto-generated method stub
				return score.getPlayer();
			}

		};
		playerColumn.setSortable(true);
		scoreCellTable.addColumn(playerColumn, "Player");

		// Create score column.
		TextColumn<HighScore> scoreColumn = new TextColumn<HighScore>() {
			@Override
			public String getValue(HighScore score) {
				// TODO Auto-generated method stub
				return String.valueOf(score.getHighScore());
			}

		};
		scoreColumn.setSortable(true);
		scoreCellTable.addColumn(scoreColumn, "Score");

		// Create during column.
		TextColumn<HighScore> duringColumn = new TextColumn<HighScore>() {
			@Override
			public String getValue(HighScore score) {
				// TODO Auto-generated method stub
				Long time = score.getDuring();
				StringBuilder sb = new StringBuilder();
				int hour = (int) (time / 3600000);
				if (hour > 1) {
					sb.append(hour + ":");
					time = time % 3600000;
				}
				int minute = (int) (time / 60000);
				if (minute > 1) {
					if (minute < 10)
						sb.append("0");
					sb.append(minute + ":");
					time = time % 60000;
				} else {
					sb.append("00:");
				}
				int second = (int) (time / 1000);
				if (second > 1) {
					if (second < 10)
						sb.append("0");
					sb.append(second);
				} else {
					sb.append("00");
				}
				return sb.toString();
			}

		};
		duringColumn.setSortable(true);
		scoreCellTable.addColumn(duringColumn, "During");

		// Create location column.
		TextColumn<HighScore> locationColumn = new TextColumn<HighScore>() {
			@Override
			public String getValue(HighScore score) {
				// TODO Auto-generated method stub
				return String.valueOf(score.getLocation());
			}

		};
		locationColumn.setSortable(true);
		scoreCellTable.addColumn(locationColumn, "Location");

		// Create comment column.
		TextColumn<HighScore> commentColumn = new TextColumn<HighScore>() {
			@Override
			public String getValue(HighScore score) {
				// TODO Auto-generated method stub
				return score.getComment();
			}

		};
		commentColumn.setSortable(true);
		scoreCellTable.addColumn(commentColumn, "Comment");
		// Create date column.
		TextColumn<HighScore> dateColumn = new TextColumn<HighScore>() {
			@Override
			public String getValue(HighScore score) {
				// TODO Auto-generated method stub
				return new Date(score.getDate()).toString();
			}

		};
		dateColumn.setSortable(true);
		scoreCellTable.addColumn(dateColumn, "Date");

		// Create a data provider.
		ListDataProvider<HighScore> dataProvider = new ListDataProvider<HighScore>();

		// Connect the table to the data provider.
		dataProvider.addDataDisplay(scoreCellTable);

		// Add the data to the data provider, which automatically pushes it to
		// the
		// widget.
		listScore = dataProvider.getList();
		RefreshHighScoreTbl();

		mainContent.add(scoreCellTable);

		//HorizontalPanel tableGamesCtrPanel = new HorizontalPanel();
		Button crtScore = new Button("Create score", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				/*
				CreateHighScore createScore = new CreateHighScore(userId_,
						appId_);
				mainContent.clear();
				createScore.setMainContent(mainContent);
				createScore.Initialize();
				*/
				History.newItem("highscore-" + appId_);
			}
		});

		Button delScore = new Button("Delete score", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				if (selectedScores.size() == 0) {
					Window
							.alert("You have to chose at least an entry to delete.");
				} else {
					if (Window.confirm("Would you want to delete entries")) {
						HighScoreServiceAsync scoreService = GWT
								.create(HighScoreService.class);
						scoreService.DeleteScores(userId_, selectedScores,
								new AsyncCallback<Integer>() {
									public void onFailure(Throwable caught) {
									}

									public void onSuccess(Integer result) {
										// TODO Auto-generated method stub
										Window
												.alert(result
														+ " Scores have been deleted successful.");
										RefreshHighScoreTbl();
									}
								});
						selectedScores.clear();
					}
				}

			}
		});
		tableGamesCtrPanel.add(crtScore);
		tableGamesCtrPanel.add(delScore);
		tableGamesCtrPanel.setWidgetPosition(delScore, crtScore.getOffsetWidth() + 5, 0);
		tableGamesCtrPanel.add(myAppLink);
		mainContent.add(tableGamesCtrPanel);

		return mainContent;

	}

	private void RefreshHighScoreTbl() {
		// TODO Auto-generated method stub
		scoreSvc.SelectScores(userId_, appId_,
				new AsyncCallback<List<HighScore>>() {
					public void onFailure(Throwable caught) {
						// TODO: Do something
						// with
						// errors.
					}

					public void onSuccess(List<HighScore> result) {

						listScore.clear();
						listScore.addAll(result);
						headerTblPanel.setWidth(scoreCellTable.getOffsetWidth() + "");
						headerTblPanel.setWidgetPosition(myAppLink, headerTblPanel.getOffsetWidth()-myAppLink.getOffsetWidth(), 0);
						tableGamesCtrPanel.setWidth(scoreCellTable.getOffsetWidth() + "");
						tableGamesCtrPanel.setWidgetPosition(myAppLink, tableGamesCtrPanel.getOffsetWidth()-myAppLink.getOffsetWidth(), 0);

					}
				});
	}

	public void setAppName(String appName) {
		this.appName_ = appName;
	}

	public String getAppName() {
		return appName_;
	}

}
