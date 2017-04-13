package crack.listener;

import java.util.HashMap;

public interface JarCrackCompleteListener {
	void onJarCrackComplete(HashMap<String, String> map);
	void onCrackFailed(HashMap<String, String> map);
}
