package tvfan.tv;

import android.app.Activity;
import android.os.Bundle;

import com.luxtone.lib.gdx.Page;

public class EPG extends BasePage{
	private static EPG _epg = null;
	public static EPG GetInstance(){
		if(_epg==null)
			_epg = new EPG();
		return _epg;
	}

	public boolean startGdxPage(Class<? extends Page> page, Bundle bundle){
		startPage(page, bundle);
		return true;
	}
	
	public boolean startGdxPage(Class<? extends Page> page){
		startPage(page);
		return true;
	}

	public boolean startAndrPage(Class<? extends Activity> page, Bundle bundle){
		getActivity().startActivity(null);
		return true;
	}
	public void exit(){
		getActivity().finish();
	}
}
