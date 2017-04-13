package tvfan.tv.ui.gdx.widgets;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.luxtone.lib.gdx.Page;

public class TVFANImage extends Image {

	public TVFANImage(Page page) {
		super(page);
		
	}

	protected void onDraw(Canvas canvas){
		Path cliPath = new Path();
		
		float w =  this.getWidth();
		float h = this.getHeight();
		
		
		cliPath.addRoundRect(new RectF(0, 0, w, h), 10.0f, 10.0f, Path.Direction.CW);
		
		canvas.clipPath(cliPath);
		
	
	}
}
