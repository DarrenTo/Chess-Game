import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;

import javafx.scene.image.Image;
import model.ChessBoard;
import model.IChessBoard;
import model.enums.CheckStatus;
import model.pieces.Piece;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;
import static model.enums.Color.WHITE;

public class Main extends Application{
    Node[][] chessAnnotationNodes = new Node[8][8];
    ImageView[][] chessPiecesNodes = new ImageView[8][8];
    Node[][] chessMoveSymbolsNodes = new Node[8][8];

    IChessBoard board = new ChessBoard();

    Image whitePawn;
    Image blackPawn;
    Image whiteRook;
    Image blackRook;
    Image whiteKnight;
    Image blackKnight;
    Image whiteBishop;
    Image blackBishop;
    Image whiteKing;
    Image blackKing;
    Image whiteQueen;
    Image blackQueen;

    Boolean selected = false;

    int selectedX;
    int selectedY;
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


        GridPane chessboard = new GridPane();
        setupChessboard(chessboard);
        GridPane chessAnnotation = new GridPane();
        setupGrid(chessAnnotation, chessAnnotationNodes);
        GridPane chessPieces = new GridPane();
        setupGrid(chessPieces, chessPiecesNodes);
        InitPieceImages();
        setUpBoard();
        GridPane chessMoveSymbols = new GridPane();
        setupGrid(chessMoveSymbols, chessMoveSymbolsNodes);
        chessMoveSymbols.setMouseTransparent(true);
        overlay.getChildren().addAll(chessboard, chessAnnotation, chessPieces, chessMoveSymbols);

        chessPieces.addEventHandler(MOUSE_CLICKED, event -> {
            int x = (int)event.getX()/50;
            int y = (int)event.getY()/50;
            CheckStatus status;

            if(selected) {
              if(board.MovePiece(selectedX, selectedY, x, y)) {
                  System.out.println("successful move from [" + selectedX + ", " + selectedY + "] to [" + x + ", " + y + "]");
//                  DrawPieceMove(selectedX, selectedY, x, y);
                  setUpBoard();
              } else {
                  System.out.println("invalid move");
              }
              selected = false;
            } else {
                if(board.getPositionStatus(x, y) != null) {
                    selectedX = x;
                    selectedY = y;
                    selected = true;
                    System.out.println("Selected\nX: " + (int) event.getX() / 50
                            + "\nY: " + (int) event.getY() / 50);
                }
            }

            status = board.FindCheckStatus();
            System.out.println("checkstatus done: " + status);
            if(status == CheckStatus.CHECKMATE) {
                GameOverScreen();
            } else if(status == CheckStatus.STALEMATE) {
                DrawScreen();
            }

        });

//        layout.getChildren().add(button);
        Scene scene = new Scene(layout, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void DrawPieceMove(int initX, int initY, int endX, int endY) {
        Image img = chessPiecesNodes[initX][initY].getImage();
        chessPiecesNodes[initX][initY].setImage(null);
        chessPiecesNodes[endX][endY].setImage(img);
    }

    private void InitPieceImages() throws FileNotFoundException {
        InputStream stream =  new FileInputStream("./data/BlackPawn.png");
        blackPawn = new Image(stream);
        stream = new FileInputStream("./data/WhitePawn.png");
        whitePawn = new Image(stream);
        stream = new FileInputStream("./data/WhiteRook.png");
        whiteRook = new Image(stream);
        stream = new FileInputStream("./data/BlackRook.png");
        blackRook = new Image(stream);
        stream = new FileInputStream("./data/WhiteKnight.png");
        whiteKnight = new Image(stream);
        stream = new FileInputStream("./data/BlackKnight.png");
        blackKnight = new Image(stream);
        stream = new FileInputStream("./data/WhiteBishop.png");
        whiteBishop = new Image(stream);
        stream = new FileInputStream("./data/BlackBishop.png");
        blackBishop = new Image(stream);
        stream = new FileInputStream("./data/WhiteKing.png");
        whiteKing = new Image(stream);
        stream = new FileInputStream("./data/BlackKing.png");
        blackKing = new Image(stream);
        stream = new FileInputStream("./data/WhiteQueen.png");
        whiteQueen = new Image(stream);
        stream = new FileInputStream("./data/BlackQueen.png");
        blackQueen = new Image(stream);
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

    private void setupGrid(GridPane pane, Node[][] list) {
//        int sq = 50;
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
//                Rectangle r = new Rectangle(sq,sq,sq,sq);
//                r.setFill(Color.TRANSPARENT);
                ImageView imageView = new ImageView();
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                list[y][x] = imageView;
                pane.add(imageView, y, x);
            }
        }

    }

    private void setUpBoard() {
        Piece[][] pieceLayout = board.getBoard();

        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                Piece piece = pieceLayout[y][x];
                drawPiece(piece, y, x);
            }
        }
    }

    private void drawPiece(Piece piece, int y, int x) {
        if(piece != null) {
            switch(piece.getName()) {
                case PAWN:
                    if(piece.getColor() == WHITE) {
                        chessPiecesNodes[x][y].setImage(whitePawn);
                    } else {
                        chessPiecesNodes[x][y].setImage(blackPawn);
                    }
                    break;
                case KING:
                    if(piece.getColor() == WHITE) {
                        chessPiecesNodes[x][y].setImage(whiteKing);
                    } else {
                        chessPiecesNodes[x][y].setImage(blackKing);
                    }
                    break;
                case QUEEN:
                    if(piece.getColor() == WHITE) {
                        chessPiecesNodes[x][y].setImage(whiteQueen);
                    } else {
                        chessPiecesNodes[x][y].setImage(blackQueen);
                    }
                    break;
                case BISHOP:
                    if(piece.getColor() == WHITE) {
                        chessPiecesNodes[x][y].setImage(whiteBishop);
                    } else {
                        chessPiecesNodes[x][y].setImage(blackBishop);
                    }
                    break;
                case KNIGHT:
                    if(piece.getColor() == WHITE) {
                        chessPiecesNodes[x][y].setImage(whiteKnight);
                    } else {
                        chessPiecesNodes[x][y].setImage(blackKnight);
                    }
                    break;
                case ROOK:
                    if(piece.getColor() == WHITE) {
                        chessPiecesNodes[x][y].setImage(whiteRook);
                    } else {
                        chessPiecesNodes[x][y].setImage(blackRook);
                    }
                    break;
            }
        } else {
            chessPiecesNodes[x][y].setImage(null);
        }
    }

    private void GameOverScreen() {
        Label label = new Label("GAMEOVER: " + (board.getActiveColor() == WHITE ? "Black Wins" : "White Wins"));
        StackPane pane = new StackPane();
        pane.getChildren().add(label);
        Scene GameOverScene = new Scene(pane, 250,250);

        Stage GameOverWindow = new Stage();
        GameOverWindow.setScene(GameOverScene);

        GameOverWindow.show();
    }

    private void DrawScreen() {
        Label label = new Label("It's a Draw");
        StackPane pane = new StackPane();
        pane.getChildren().add(label);
        Scene DrawScene = new Scene(pane, 250,250);

        Stage DrawWindow = new Stage();
        DrawWindow.setScene(DrawScene);

        DrawWindow.show();
    }


}