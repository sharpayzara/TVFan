package tvfan.tv.ui.gdx.widgets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.dal.HttpResponse;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.lib.ACache;
import tvfan.tv.lib.Lg;
import tvfan.tv.lib.PortalLayoutParser;
import tvfan.tv.lib.PortalLayoutParser.LayoutElement;
import tvfan.tv.lib.PortalLayoutParser.ParseListener;

import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.v4.ScrollView;

public class HorizontalIconList extends ScrollView {

	final private String TAG = "TVFAN.EPG.HorizontalIconList";
	private String _layoutFileUrl;
	private Page _page;
	private IconBuilder _iconBuilder;
	private JSONArray _dataList;
	private HorizontalIconList _me;
	private ACache mCache;

	public interface IconBuilder {
		/**
		 * @param layoutElement
		 *            当前元素x,y,width,height
		 * @param dataListItem
		 *            当前元素对应的数据节点
		 * @param layoutElementIndex
		 *            布局序号
		 * @param posOfList
		 *            当前元素相对于整个列表的位置，1:左(上)边缘 2:中间 4:右(下)边缘。如果仅有1列，那么此值为1+4
		 * @param horizontalIconList
		 *            当前元素所在的容器对象
		 **/
		public void Create(LayoutElement layoutElement,
				JSONObject dataListItem, int layoutElementIndex,
				HorizontalIconList horizontalIconList);
	}

	public HorizontalIconList(Page page, String layoutFileUrl,
			JSONArray dataList, IconBuilder iconBuilder) {
		super(page);
		mCache = ACache.get(page.getActivity());
		_page = page;
		_iconBuilder = iconBuilder;
		_layoutFileUrl = layoutFileUrl;
		_dataList = dataList;
		_me = this;
		if (_layoutFileUrl.startsWith("http://"))
			_render();
		else
			loadLayerout("");
	}

	public void refresh(JSONArray dataList, IconBuilder iconBuilder) {
		_dataList = dataList;
		_iconBuilder = iconBuilder;
		if (_layoutFileUrl.startsWith("http://"))
			_render();
		else
			loadLayerout("");
	}

	public HorizontalIconList(Page page, String layoutFileUrl,
			JSONArray dataList, IconBuilder iconBuilder, String id) {
		super(page);
		mCache = ACache.get(page.getActivity());
		_page = page;
		_iconBuilder = iconBuilder;
		_layoutFileUrl = layoutFileUrl;
		_dataList = dataList;
		_me = this;
		// String s = mCache.getAsString("epg_layoutFile_" + id);
		// if (_layoutFileUrl.startsWith("http://"))
		// _render();
		// else
		loadLayerout(id);
	}

	private void _render() {
		_loadLayerout();
	}

	// _layoutFileUrl : discovery.json
	private void loadLayerout(String id) {
		try {
			String s = mCache.getAsString("epg_layoutFile_" + id);
			JSONObject obj = new JSONObject(s);
			parserLayout(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void _loadLayerout() {
		RemoteData rd = new RemoteData(_page.getActivity());
		rd.startJsonHttpGet(_layoutFileUrl,
				new HttpResponse.Listener4JSONObject() {
					@Override
					public void onResponse(JSONObject response) {
						parserLayout(response);
						// PortalLayoutParser layp = new PortalLayoutParser(
						// response);
						// layp.startParse(new ParseListener() {
						// @Override
						// public void onLayoutElementParsed(
						// LayoutElement layoutElement,
						// int layoutElementIndex) {
						// if (layoutElementIndex >= _dataList.length())
						// return;
						// try {
						// _iconBuilder.Create(
						// layoutElement,
						// _dataList
						// .getJSONObject(layoutElementIndex),
						// layoutElementIndex, _me);
						// } catch (JSONException e) {
						// Lg.e(TAG, _layoutFileUrl);
						// e.printStackTrace();
						// }
						// }
						// });
					}

					@Override
					public void onError(String errorMessage) {
					}
				});
	}

	/**
	 * 调用方法对首页详情的数据进行解析和相关的显示
	 * @param response
	 */
	private void parserLayout(JSONObject response) {
		PortalLayoutParser layp = new PortalLayoutParser(response);
		layp.startParse(new ParseListener() {
			@Override
			public void onLayoutElementParsed(LayoutElement layoutElement,
					int layoutElementIndex) {
				if (layoutElementIndex >= _dataList.length())
					return;
				try {
					_iconBuilder.Create(layoutElement,
							_dataList.getJSONObject(layoutElementIndex),
							layoutElementIndex, _me);
				} catch (JSONException e) {
					Lg.e(TAG, _layoutFileUrl);
					e.printStackTrace();
				}
			}
		});
	}
}
