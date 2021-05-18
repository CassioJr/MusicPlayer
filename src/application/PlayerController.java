package application;

import java.io.File;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;
public class PlayerController {
	@FXML
	private Label musicname;
	@FXML
	private Label duration;
	@FXML
	private Label current;
	@FXML
	private Slider barraProgresso;
	@FXML
	private ImageView albumImage;
	@FXML
	private Button button;
	private static String caminhomusica;
	private boolean running = false;
	private MediaPlayer mp;
	private Media midia;
	private Timer timer;
	private TimerTask task;

	public void AlbumCover() {
		File f = new File("imagens//nocoverart.jpg");
		Image img = new Image(f.toURI().toString());
		albumImage.setImage(img);

		ObservableMap<String, Object> AlbumCover = midia.getMetadata();
		AlbumCover.addListener((MapChangeListener<String, Object>) ch -> {
			if (ch.wasAdded()) {
				String key = ch.getKey();
				Object value = ch.getValueAdded();
				if (key.equals("image")) {
					albumImage.setImage((Image) value);
				}
			}
		});
	}

	public void durationTotal() {
		mp.setOnReady(new Runnable() {
			@Override
			public void run() {
				int durationTime = (int) midia.getDuration().toMillis();
				int minutes = (durationTime / 60000) % 60000;
				int seconds = durationTime % 60000 / 1000;
				String MusicTime = String.format("%02d:%02d", minutes, seconds);
				duration.setText(MusicTime);

				int Time = (int) mp.getCurrentTime().toMillis();
				int minutescurrent = (Time / 60000) % 60000;
				int secondscurrent = Time % 60000 / 1000;
				String CurrentTime = String.format("%02d:%02d", minutescurrent, secondscurrent);
				current.setText(CurrentTime);
				}
		});
	}

	public void alertMessage(String m) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Atenção");
		alert.setContentText(m);
		alert.setHeaderText(null);
		alert.showAndWait();
	}

	public void Progresso() {
	/*	timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
				running = true;
				double current = mp.getCurrentTime().toSeconds();
				double end = midia.getDuration().toSeconds();
				barraProgresso.setMax(end);
				barraProgresso.setMin(0);
				barraProgresso.setValue(current/end);
			});

			}
		}, 0, 1000);
*/
	}
	
	
	public void currentTime() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					int Time = (int) mp.getCurrentTime().toMillis();
					int minutes = (Time / 60000) % 60000;
					int seconds = Time % 60000 / 1000;
					String CurrentTime = String.format("%02d:%02d", minutes, seconds);
					current.setText(CurrentTime);
				});

			}
		}, 0, 1000);
	}

	public void play() {
		try {
			if (running == false) {
				midia = new Media(Paths.get(caminhomusica).toUri().toString());
				mp = new MediaPlayer(midia);
				mp.play();
				button.setText("Pause");
				running = true;
			    currentTime();
			} else {
				mp.pause();
				button.setText("Play");
				running = false;
			}
		} catch (NullPointerException e) {
			alertMessage("Escolha uma musica");
		}
	}


	public void MusicFile() {
		try {
			FileChooser fc = new FileChooser();
			fc.setTitle("Escolha uma musica");
			fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3", "*.mp3*"));
			File file = fc.showOpenDialog(null);
			if (file != null) {
				musicname.setText(file.getName().replace(".mp3", ""));
				caminhomusica = file.getAbsolutePath();
				play();
				AlbumCover();
				durationTotal();
				Progresso();
			}
			
		}catch(NullPointerException e) {
			
		} catch (Exception e) {
			System.out.println("Arquivo inexistente");
		}
	}

	public void Caminho() {

		if (running == true) {
			play();
			MusicFile();
		} else {
			MusicFile();

		}
	}
}
