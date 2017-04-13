package tvfan.tv.lib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PortalLayoutParser {
	/**
	 * 解析监听
	 **/
	public interface ParseListener {
		/**
		 * 解析完成1个布局元素
		 * @param layoutElement 当前元素x,y,width,height
		 * @param layoutElementIndex 布局序号
		 **/
        public void onLayoutElementParsed(LayoutElement layoutElement, int layoutElementIndex);
    }

	/**
	 * 布局元素
	 **/
	public class LayoutElement {
		public int x;
		public int y;
		public int width;
		public int height;
		public float offset;
	}

	private JSONObject _layoutFile;

	public PortalLayoutParser(JSONObject layoutFile){
		_layoutFile = layoutFile;
	}

	/**
	 * _marginLeft:布局举例左边控件的距离,这里其实就是指对于屏幕左边的距离
	 * _marginBottom:布局相对于底部控件的距离
	 * spacing:每个模块之间的距离
	 * _height:布局总高度
	 * _halfHeight:如果是只有2个控件的单个控件的高度
	 * _quarterHeight:如果摆放4个控件,单个控件的高度
	 * _posX:同_marginLeft 相同
	 * _posY:同_marginBottom 相同
	 * _halfPosY:如果只有2个控件时上面控件的y坐标
	 * _quarterPosY:如果有4个控件时,第三个控件的y坐标
	 */
	int _posX, _posY, _halfPosY, _quarterPosY;
	int _blc_style, _blc_pics, _blc_width, _blc_halfWidth;
	int _spacing, _marginLeft, _marginBottom, _height, _halfHeight, _quarterHeight;
	int _sixHeight,_sixposY6,_sixposY5,_sixposY4,_sixposY3,_sixposY2,_sixposY1;
	public void startParse(ParseListener pl){
		JSONArray ary;
		JSONObject obj;
		LayoutElement le;
		int _leIdx = 0, _layoutLen;
		int [][] layoutVals;
		float offset;
		try {
			obj = _layoutFile.getJSONObject("margin");
			_marginLeft = obj.getInt("l");
			_marginBottom = obj.getInt("b");
			_spacing = _layoutFile.getInt("spacing");
			_height = _layoutFile.getInt("height");
			_halfHeight = (_height - _spacing) / 2;
			_quarterHeight = (_halfHeight - _spacing) / 2;
			_posX = _marginLeft;
			_posY = _marginBottom;
			_halfPosY = _marginBottom + _halfHeight + _spacing;
			_quarterPosY = _marginBottom + _quarterHeight + _spacing;

			//这里我需要摆放6个控件
			//获取每个控件的高度
			_sixHeight =(_height - _spacing*5)/6;
			//获取每个控件的起始坐标
			_sixposY6 = _marginBottom;
			_sixposY5 = _marginBottom + _sixHeight + _spacing;
			_sixposY4 = _marginBottom + 2*_sixHeight + 2*_spacing;
			_sixposY3 = _halfPosY;
			_sixposY2 = _halfPosY + _sixHeight + _spacing;
			_sixposY1 = _halfPosY +2*_sixHeight + 2*_spacing;
			ary = _layoutFile.getJSONArray("layout");
			_layoutLen = ary.length();
			for(int i = 0; i < _layoutLen; i++)
			{
				obj = ary.getJSONObject(i);
				_blc_pics = obj.getInt("s") / 10;
				_blc_style = obj.getInt("s") % 10;
				_blc_width = obj.getInt("w");
				_blc_halfWidth = (_blc_width - _spacing) / 2;

				layoutVals = _computeBlockLayout();
				offset = (float)i/(float)_layoutLen;
				for(int j = 0; j < _blc_pics; j++)
				{
					le = new LayoutElement();
					le.x = layoutVals[j][0];
					le.y = layoutVals[j][1];
					le.width = layoutVals[j][2];
					le.height = layoutVals[j][3];
					le.offset = offset;

					pl.onLayoutElementParsed(le, _leIdx);
					_leIdx++;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private int[][] _computeBlockLayout()
	{
		int[][] retAry = new int[_blc_pics][5];//x,y,width,height
		switch(_blc_pics){
			case 1:{
				if(_blc_style == 1){//口
					retAry[0][0] = _posX;
					retAry[0][1] = _posY;
					retAry[0][2] = _blc_width;
					retAry[0][3] = _height;
				}
				break;
			}
			case 2:{
				if(_blc_style == 1){//吕
					retAry[0][0] = _posX;
					retAry[0][1] = _halfPosY;
					retAry[0][2] = _blc_width;
					retAry[0][3] = _halfHeight;
					retAry[1][0] = _posX;
					retAry[1][1] = _posY;
					retAry[1][2] = _blc_width;
					retAry[1][3] = _halfHeight;
				}
				break;
			}
			case 3:{
				if(_blc_style == 1) {//品
					retAry[0][0] = _posX;
					retAry[0][1] = _halfPosY;
					retAry[0][2] = _blc_width;
					retAry[0][3] = _halfHeight;
					retAry[1][0] = _posX;
					retAry[1][1] = _posY;
					retAry[1][2] = _blc_halfWidth;
					retAry[1][3] = _halfHeight;
					retAry[2][0] = _posX + _spacing + _blc_halfWidth;
					retAry[2][1] = _posY;
					retAry[2][2] = _blc_halfWidth;
					retAry[2][3] = _halfHeight;
				}else if(_blc_style == 2) {//倒品
					retAry[0][0] = _posX;
					retAry[0][1] = _halfPosY;
					retAry[0][2] = _blc_halfWidth;
					retAry[0][3] = _halfHeight;
					retAry[1][0] = _posX + _spacing + _blc_halfWidth;
					retAry[1][1] = _halfPosY;
					retAry[1][2] = _blc_halfWidth;
					retAry[1][3] = _halfHeight;
					retAry[2][0] = _posX;
					retAry[2][1] = _posY;
					retAry[2][2] = _blc_width;
					retAry[2][3] = _halfHeight;
				}else if(_blc_style == 3) {//三行，一大，两小
					retAry[0][0] = _posX;
					retAry[0][1] = _halfPosY;
					retAry[0][2] = _blc_width;
					retAry[0][3] = _halfHeight;
					retAry[1][0] = _posX;
					retAry[1][1] = _quarterPosY;
					retAry[1][2] = _blc_width;
					retAry[1][3] = _quarterHeight;
					retAry[2][0] = _posX;
					retAry[2][1] = _posY;
					retAry[2][2] = _blc_width;
					retAry[2][3] = _quarterHeight;
				}
				break;
			}
			case 6:{
				if(_blc_style == 1){//6行等高矩形
//					retAry[0][0] = _posX;
//					retAry[0][1] = _posY;
//					retAry[0][2] = _blc_width;
//					retAry[0][3] = _sixHeight;
//					retAry[1][0] = _posX;
//					retAry[1][1] = _sixposY5;
//					retAry[1][2] = _blc_width;
//					retAry[1][3] = _sixHeight;
//					retAry[2][0] = _posX;
//					retAry[2][1] = _sixposY4;
//					retAry[2][2] = _blc_width;
//					retAry[2][3] = _sixHeight;
//					retAry[3][0] = _posX;
//					retAry[3][1] = _sixposY3;
//					retAry[3][2] = _blc_width;
//					retAry[3][3] = _sixHeight;
//					retAry[4][0] = _posX;
//					retAry[4][1] = _sixposY2;
//					retAry[4][2] = _blc_width;
//					retAry[4][3] = _sixHeight;
//					retAry[5][0] = _posX;
//					retAry[5][1] = _sixposY1;
//					retAry[5][2] = _blc_width;
//					retAry[5][3] = _sixHeight;

					retAry[0][0] = _posX;
					retAry[0][1] = _sixposY1;
					retAry[0][2] = _blc_width;
					retAry[0][3] = _sixHeight;
					retAry[1][0] = _posX;
					retAry[1][1] = _sixposY2;
					retAry[1][2] = _blc_width;
					retAry[1][3] = _sixHeight;
					retAry[2][0] = _posX;
					retAry[2][1] = _sixposY3;
					retAry[2][2] = _blc_width;
					retAry[2][3] = _sixHeight;
					retAry[3][0] = _posX;
					retAry[3][1] = _sixposY4;
					retAry[3][2] = _blc_width;
					retAry[3][3] = _sixHeight;
					retAry[4][0] = _posX;
					retAry[4][1] = _sixposY5;
					retAry[4][2] = _blc_width;
					retAry[4][3] = _sixHeight;
					retAry[5][0] = _posX;
					retAry[5][1] = _posY;
					retAry[5][2] = _blc_width;
					retAry[5][3] = _sixHeight;
				}
				break;
			}
		}
		_posX += (_blc_width + _spacing);
		return retAry;
	}
}
