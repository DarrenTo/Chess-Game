import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;

import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.InputStream;

public class Main extends Application{
    Button button;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Chess Game");
//        button = new Button();
//        button.setText("Click me");

        BorderPane layout = new BorderPane();
        StackPane overlay = new StackPane();
        overlay.setMaxSize(400,400);
        overlay.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        layout.setCenter(overlay);

//        ColumnConstraints columnConstraints = new ColumnConstraints();
//        columnConstraints.setPercentWidth(100/8);
//        RowConstraints rowConstraints = new RowConstraints();
//        rowConstraints.setPercentHeight(100/8);

        GridPane chessboard = new GridPane();
        setupChessboard(chessboard);
        GridPane chessAnnotation = new GridPane();
        GridPane chessPieces = new GridPane();
//        InputStream stream = new FileInputStream("./data/BlackPawn.png");
//        Image image = new Image(stream);
//        ImageView imageView = new ImageView();
//        imageView.setImage(image);
//        imageView.setX(10);
//        imageView.setY(10);
//        chessPieces.getColumnConstraints().add(columnConstraints);
//        chessPieces.getRowConstraints().add(rowConstraints);
//        chessPieces.add(imageView, 0,0);
//        chessPieces.add(imageView, 3,0);
        GridPane chessMoveSymbols = new GridPane();
        chessMoveSymbols.setMouseTransparent(true);
        overlay.getChildren().addAll(chessboard, chessAnnotation, chessPieces, chessMoveSymbols);
//        layout.getChildren().add(button);

        Scene scene = new Scene(layout, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupChessboard(GridPane pane) {
        int sq = 50;
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                Rectangle r = new Rectangle(sq,sq,sq,sq);
                if(y % 2 == 0) {
                    if(x % 2 == 0) {
                        r.setFill(Color.WHITE);
                    } else {
                        r.setFill(Color.GREEN);
                    }
                } else {
                    if(x % 2 == 0) {
                        r.setFill(Color.GREEN);
                    } else {
                        r.setFill(Color.WHITE);
                    }
                }

                pane.add(r, y, x);
            }
        }


    }
}