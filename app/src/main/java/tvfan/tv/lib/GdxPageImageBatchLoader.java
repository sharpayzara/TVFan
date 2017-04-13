package tvfan.tv.lib;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;

public class GdxPageImageBatchLoader implements LoaderListener {
	final int PARALLEL_NUMBER = 100;
	final String LOAD_TYPE_STR = "GdxPageImageBatchLoader";
	HashMap<String, Image> _QUEUE = new HashMap<String, Image>();
	HashMap<String, Image> _QUEUE_LOADING = new HashMap<String, Image>();
	PageImageLoader[] _pImgLoaders;
	Stack<Integer> _PV = new Stack<Integer>();
	Page _pageContext;

	public GdxPageImageBatchLoader(Page pageContext){
		_pageContext = pageContext;
		_pImgLoaders = new PageImageLoader[PARALLEL_NUMBER];
		for(int i = 0; i < PARALLEL_NUMBER; i++){
			_pImgLoaders[i] = new PageImageLoader(_pageContext);
			_PV.push(i);
		}
	}

	public void Get(String url, Image imageObj){
		_QUEUE.put(url+(_QUEUE.containsKey(url)?"#"+String.valueOf(Math.random()):""), imageObj);
		_procQueue();
	}

	public void Get(String url, Image imageObj, int radius){
		Get(url+(_QUEUE.containsKey(url)?"#"+String.valueOf(Math.random()):"")+"#"+String.valueOf(radius)+"###", imageObj);
	}

	public void CancelAll(){
		for(int i = 0; i < PARALLEL_NUMBER; i++){
			_pImgLoaders[i].cancelLoad();
			_pImgLoaders[i].dispose();
		}
	}

	private void _procQueue(){
		if(_QUEUE.isEmpty())
			return;
        Set<Map.Entry<String, Image>> entry = _QUEUE.entrySet();
        Iterator<java.util.Map.Entry<String, Image>> it = entry.iterator();
        if(it.hasNext()) {
        	java.util.Map.Entry<String, Image> en = it.next();
    		if(!_PV.empty()) {
    			int p = _PV.pop();
    			String url = en.getKey();
    			_QUEUE_LOADING.put(url, en.getValue());
    			_QUEUE.remove(url);
    			if(url.endsWith("###")){
    				String[] v = url.split("#");
    				_pImgLoaders[p].startLoadBitmap(url, LOAD_TYPE_STR, true, Integer.parseInt(v[v.length-1]), this, p);
    			}else{
    				_pImgLoaders[p].startLoadBitmap(url, LOAD_TYPE_STR, this, p);
    			}
    		}
        }
	}

	@Override
	public void onLoadComplete(String url, TextureRegion texture, Object tag) {
		Image img = _QUEUE_LOADING.get(url);
		img.addAction(Actions.fadeOut(0));
		img.setDrawable(new TextureRegionDrawable(texture));
		img.addAction(Actions.fadeIn(0.6f));

		int p = (Integer) tag;
		if(_pImgLoaders[p]!=null)
			_pImgLoaders[p].cancelLoad();
		_pImgLoaders[p] = new PageImageLoader(_pageContext);
		_PV.push(p);
		_QUEUE_LOADING.remove(url);
		_procQueue();
	}
}
