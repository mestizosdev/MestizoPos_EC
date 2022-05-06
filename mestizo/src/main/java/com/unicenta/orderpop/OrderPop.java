//  uniCenta oPOS  - Touch Friendly Point Of Sale
//  Copyright (c) 2017 uniCenta
//  https://unicenta.com
//
//  This file is part of uniCenta Remote Display
//
//  uniCenta Remote Display is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  uniCenta Remote Display is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

/* uniCenta's OrderPop is a simple utilty to list tickets sent to remote printer
 * It connects to and list uniCenta oPOS orders table rows and requires a
 * manual refresh using the button.
 * It runs independently of uniCenta oPOS and uses the provided orderpop.bat

 */

package com.unicenta.orderpop;

import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.util.AltEncrypter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class OrderPop extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  private ExecutorService databaseExecutor;
  private Future databaseSetupFuture;

  @Override
  public void init() throws Exception {
    databaseExecutor = Executors.newFixedThreadPool(
            1,
            new DatabaseThreadFactory()
    );
    DBSetupTask setup = new DBSetupTask();
    databaseSetupFuture = databaseExecutor.submit(setup);
  }

  // Pop the UI will use fxml rather than in code css to set styling later for release
  @Override
  public void start(Stage stage) throws InterruptedException, ExecutionException {

    databaseSetupFuture.get();
    final ListView<String> orderView = new ListView<>();
    final ProgressIndicator databaseActivityIndicator = new ProgressIndicator();

    Image fetchO = new Image(getClass().getResourceAsStream("/com/openbravo/images/resources.png"));
    final Button fetchOrders = new Button("Fetch", new ImageView(fetchO));
    // fetch Orders table data and fill the list
    databaseActivityIndicator.setVisible(false);
    fetchOrders.setOnAction(event ->
            fetchDBOrdersListView(
                    fetchOrders,
                    databaseActivityIndicator,
                    orderView
            ));

    Image closeO = new Image(getClass().getResourceAsStream("/com/openbravo/images/fileclose.png"));
    final Button closeOrderList = new Button("Close", new ImageView(closeO));
    closeOrderList.setOnAction(e -> {
      try {
        e.consume();
        stop();
        Platform.exit();
      } catch (Exception ex) {
        log.error(ex.getMessage());
      }
    });

    stage.setOnCloseRequest((WindowEvent e) -> {
      try {
        Platform.exit();
        stop();
        Platform.exit();
      } catch (Exception ex) {
        log.error(ex.getMessage());
      }
    });

    VBox layout = new VBox(10);
    layout.setStyle("-fx-background-color: aliceblue; -fx-padding: 5;");
    layout.getChildren().setAll(
            new HBox(10,
                    fetchOrders,
                    closeOrderList,
                    databaseActivityIndicator
            ),
            orderView
    );

    layout.setPrefHeight(300);
    layout.setPrefWidth(500);

    stage.getIcons().add(new Image("/com/openbravo/images/unicentaopos.png"));
    stage.setTitle("Orders Waiting...");
    stage.setScene(new Scene(layout));
    stage.setAlwaysOnTop(true);
    stage.show();

  }

  // shutdown the app
  //@Override
  public void istop() throws Exception {

    databaseExecutor.shutdown();
    if (!databaseExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
      log.info("DB thread time-out + 3 sec's not shut down clean");
    }

    Platform.exit();
  }

  private void fetchDBOrdersListView(
          final Button triggerButton,
          final ProgressIndicator databaseActivityIndicator,
          final ListView<String> listView) {
    final FetchOrdersTask fetchOrdersTask = new FetchOrdersTask();

    triggerButton.disableProperty().bind(
            fetchOrdersTask.runningProperty()
    );

    databaseActivityIndicator.visibleProperty().bind(
            fetchOrdersTask.runningProperty()
    );

    databaseActivityIndicator.progressProperty().bind(
            fetchOrdersTask.progressProperty()
    );

    fetchOrdersTask.setOnSucceeded(t ->
            listView.setItems(fetchOrdersTask.getValue())
    );

    listView.setStyle("-fx-font-size: 14px;");

// prep for css
//        listView.getStylesheets().add(
//                this.getClass().getResource("listStyle.css").toExternalForm());

    databaseExecutor.submit(fetchOrdersTask);
  }

  abstract class DBTask<T> extends Task<T> {
    DBTask() {
      setOnFailed(t -> log.error(getException().getMessage()));
    }
  }

  class FetchOrdersTask extends DBTask<ObservableList<String>> {
    @Override
    protected ObservableList<String> call() throws Exception {
      Thread.sleep(1000);
      try (Connection con = getConnection()) {
        return fetchOrders(con);
      }
    }

    // Using an ObservableList here but could use a table as well to give more
    // flexibility to highlight specific row/column/cells
    private ObservableList<String> fetchOrders(Connection con) throws SQLException {
      log.info("Fetch Orders from DB");
      ObservableList<String> orders = FXCollections.observableArrayList();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery("SELECT ticketid, orderid, "
//                + "DATE_FORMAT(ordertime,'%b %d %Y %h:%i %p') AS ordertime, "
              + "qty, "
              + "ordertime, "
              + "details "
              + "FROM orders "
              + "ORDER BY ordertime, orderid");

      while (rs.next()) {
        orders.add(rs.getString("ticketid") + " - "
                + rs.getString("orderid") + " - "
                + rs.getString("ordertime")
                + " - " + rs.getString("qty")
                + " * " + rs.getString("details"));
      }

      log.info("Found {} orders", orders.size());
      return orders;
    }
  }

  /*
   * Check to see if we have a connection to database and
   * and pop some data in Orders table to prime if needed
   */
  class DBSetupTask extends DBTask {
    @Override
    protected Void call() throws Exception {
      try (Connection con = getConnection()) {
        if (!schemaExists(con)) {
          //  un-comment if we need to create a new Orders table
          // createSchema(con);
          // populateDatabase(con);
        }
      }

      return null;
    }

    private boolean schemaExists(Connection con) {
      log.info("Check for Orders table");
      try {
        Statement st = con.createStatement();
        st.executeQuery("select count(*) from orders");
        log.info("Orders table exists");
      } catch (SQLException ex) {
        log.info("Create Orders table");
        return false;
      }
      return true;
    }

/* 
 * We don't need to create Orders table as should already exist
 * as created by uniCenta oPOS or unicenta_remote_display apps
    private void createSchema(Connection con) throws SQLException {
      logger.info("Add Orders table to schema if not exist");
      Statement st = con.createStatement();
      String table = "create table orders(id integer, orderid varchar(50))";
      st.executeUpdate(table);
      logger.info("Created schema");
    }
*/
    
/*
 *  Useful if we want to fill the Orders table with some sample data
 *  MySQL-createSampleData.sql - INSERT x12 rows
    private void populateDatabase(Connection con) throws SQLException {
      logger.info("Populating database");      
      Statement st = con.createStatement();      
      for (String order: SAMPLE_ORDER_DATA) {
        st.executeUpdate("insert into orders values(1,'" + order + "')");
      }
      logger.info("Populated database");
    }
*/
  }

  private Connection getConnection() throws ClassNotFoundException, SQLException {
    // use explicit connection rather than uniCenta oPOS instance session
    // better for this to be independent but use current instance's credentials

    log.info("Get DB connection");

    String url = AppConfig.getInstance().getProperty("db.URL") +
            AppConfig.getInstance().getProperty("db.schema") +
            AppConfig.getInstance().getProperty("db.options");
    String sDBUser = AppConfig.getInstance().getProperty("db.user");
    String sDBPassword = AppConfig.getInstance().getProperty("db.password");

    if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
      AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser);
      sDBPassword = cypher.decrypt(sDBPassword.substring(6));
    }

    return DriverManager.getConnection(url, sDBUser, sDBPassword);
  }

  static class DatabaseThreadFactory implements ThreadFactory {
    static final AtomicInteger poolNumber = new AtomicInteger(1);

    @Override
    public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(runnable, "Database-Connection-"
              + poolNumber.getAndIncrement() + "-thread");
      thread.setDaemon(true);
      return thread;
    }
  }
}