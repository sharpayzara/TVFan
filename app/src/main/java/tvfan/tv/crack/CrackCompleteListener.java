package tvfan.tv.crack;

import java.util.HashMap;

public interface CrackCompleteListener {
	void onCrackComplete(CrackResult result);
	void onCrackFailed(HashMap<String, String> arg0);
}
