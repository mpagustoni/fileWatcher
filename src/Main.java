import java.io.IOException;
import java.util.List;


class MyCallback implements FilewatcherCallback{
	
	@Override
	public void execute(String fileName) {	
		System.out.println(fileName);
	}
	
}

public class Main {
	public static void main(String[] args) throws IOException {
		MyCallback callback = new MyCallback();
        FileWatcher fw = new FileWatcher();
        fw.register("c:/temp");
        fw.listen(callback);
    }
}
