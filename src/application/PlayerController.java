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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class PlayerController {

	@FXML private Label musicname, current, lblDuration, timeDuration;
	@FXML private Slider progressBar;
	@FXML private ImageView albumImage, img;
	private String musicPath;
	private Duration resumePlayer;
	private MediaPlayer mp;
	private Media midia;
	private Timer timer;
	private boolean running = false;

	public void albumCover() {
		albumImage.setImage(new Image(new File("resources//img//nocoverart.jpg").toURI().toString()));
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

	public void play() {
		try {
			if (running == false) {
				midia = new Media(Paths.get(musicPath).toUri().toString());
				running = true;
				mp = new MediaPlayer(midia);
				mp.play();
				img.setImage(new Image(new File("./resources/img/pause.png").toURI().toString()));
				resume();
				reloop();
				currentTime();
			} else {
				resume();
				running = false;
				mp.pause();
				img.setImage(new Image(new File("./resources/img/play.png").toURI().toString()));
			}
		} catch (NullPointerException e) {
			alertMessage("É necessario escolher uma musica!");
		}
	}

	public void reloop() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					try {
						if (mp.getCurrentTime().toSeconds() == midia.getDuration().toSeconds()) {
							resumePlayer = null;
							mp.setStartTime(resumePlayer);
							play();
						}
					} catch (Exception e) {
					}
				});
			}
		}, 0, 1000);
	}

	public void currentTime() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					int time = (int) mp.getCurrentTime().toMillis();
					current.setText(calcTime(time));
				});

			}
		}, 0, 1000);
	}
	
	public void durationTotal() {
		mp.setOnReady(new Runnable() {
			@Override
			public void run() {
				int durationTime = (int) midia.getDuration().toMillis();
				timeDuration.setText(calcTime(durationTime));
			}
		});
	}

	public void progress() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					double currentSeconds = mp.getCurrentTime().toSeconds();
					double end = midia.getDuration().toSeconds();
					progressBar.setMax(end);
					progressBar.setValue(currentSeconds);
				});
				
			}
		}, 0, 1000);
	}
	
	public String calcTime(int time) {
		int minutes = (time / 60000) % 60000;
		int seconds = time % 60000 / 1000;
		String musicTime = String.format("%02d:%02d", minutes, seconds);
		return musicTime;
	}

	public void sliderTime() {
		new Thread(() -> mp.seek(Duration.seconds(progressBar.getValue()))).start();
	}

	public void resume() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					if (resumePlayer != null && running == true) {
						mp.setStartTime(resumePlayer);
					} else {
						resumePlayer = mp.getCurrentTime();
					}
				});
			}
		}, 0, 1000);
	}

	public void musicFile() {
		try {
			FileChooser fc = new FileChooser();
			fc.setTitle("Escolha uma musica");
			fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3", "*.mp3*"));
			File file = fc.showOpenDialog(null);
			if (file != null) {
				musicname.setText(file.getName().replace(".mp3", ""));
				musicPath = file.getAbsolutePath();
				play();
				albumCover();
				durationTotal();
				progress();
				changeVisible();
				resumePlayer = null;
			}
		} catch (Exception e) {
			System.out.println("Arquivo não existe!");
			System.out.println(e);
		}
	}

	public void changeVisible() {
		progressBar.setVisible(true);
		timeDuration.setVisible(true);
		lblDuration.setVisible(true);
		current.setVisible(true);
	}

	public void chooseMusic() {
		if (running == true) {
			play();
			musicFile();
		} else {
			musicFile();
		}
	}

	public void alertMessage(String m) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Atention");
		alert.setContentText(m);
		alert.setHeaderText(null);
		alert.showAndWait();
	}
}
