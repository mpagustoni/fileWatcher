import java.nio.file.WatchService;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class FileWatcher {

	private MyThread processingThread;
	private WatchService watchService;
	private Path path;

	public FileWatcher() {

	}

	public void register(String pathToDir) {
		try {
			this.watchService = FileSystems.getDefault().newWatchService();
			this.path = Paths.get(pathToDir);
			this.path.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void listen(FilewatcherCallback callback) {
		this.processingThread = new MyThread(this.watchService, callback, this.path);
		this.processingThread.start();
	}

	public void shutDown() {
		if (this.processingThread != null) {
			this.processingThread.interrupt();
		}
	}

	private class MyThread extends Thread {
		private WatchService watchService;
		private FilewatcherCallback callback;
		private Path path;

		public MyThread(WatchService watchService, FilewatcherCallback callback, Path path) {
			this.watchService = watchService;
			this.callback = callback;
			this.path = path;
		}

		public void run() {
			WatchKey key = null;
			try {
				while ((key = watchService.take()) != null) {
					for (WatchEvent<?> event : key.pollEvents()) {
						String fileName = event.context().toString();
						Path filePath = Paths.get(this.path.toString() + "/" + fileName);
						File file = new File(filePath.toString());
						while (!file.renameTo(file)) {
							Thread.sleep(100);
						}
						this.callback.execute(fileName);
					}
					key.reset();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}
}
