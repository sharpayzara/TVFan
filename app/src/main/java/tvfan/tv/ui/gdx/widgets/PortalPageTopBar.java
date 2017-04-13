package tvfan.tv.ui.gdx.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;

import tvfan.tv.App;
import tvfan.tv.R;

/**
 * Created by Administrator on 2016/4/14.
 */
public class PortalPageTopBar extends Group {

    private final String TAG = "PortalPageTopBar";

    private Page page;
    private Label topLabel;
    private Image topImage;
//    private Image bg;
    private int normalImageId;
    private int selectImageId;

    private String name;

    private int _x = 0, _y = 0;

    public PortalPageTopBar(final Page page, final int normalImageResId, final int selectImageResId, final String name) {
        super(page);
        this.page = page;
        normalImageId = normalImageResId;
        selectImageId = selectImageResId;
        this.name = name;
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
//                bg = new Image(page);
//                bg.setDrawableResource(R.drawable.leaderboard_light);
//                bg.setPosition(_x - 65, _y - 23);
//                bg.setSize(250, 100);
//                addActor(bg);
//                bg.setVisible(false);

                topImage = new Image(page);
                topImage.setDrawableResource(normalImageResId);
                topImage.setPosition(_x, _y);
                topImage.setSize(60, 60);
                addActor(topImage);

                _x += 70;
                topLabel = new Label(page);
                topLabel.setPosition(_x, _y + 12);
                topLabel.setSize(100, 36);
                topLabel.setTextSize(36);
                topLabel.setColor(Color.valueOf("2e94e8"));
                topLabel.setText(name);
//                topLabel.setAlpha(0.6f);
                addActor(topLabel);

            }
        });
    }

    @Override
    public void notifyFocusChanged(boolean getFocus) {
        super.notifyFocusChanged(getFocus);
        if (getFocus) {
            if (topImage != null)
                topImage.setDrawableResource(selectImageId);
            if (topLabel != null)
                topLabel.setColor(Color.WHITE);
//            if (bg != null)
//                bg.setVisible(true);
        } else {
            topImage.setDrawableResource(normalImageId);
            topLabel.setColor(Color.valueOf("2e94e8"));
//            bg.setVisible(false);
        }
    }

    private Image infoImg;
//    private TVFANLabel infoTxt;
    /**
     * add by wanqi,消息的条数提示
     *
     * @param msg
     */
    public void updateMsgTip(int msg) {
        if (msg == 0) {
            if (infoImg != null) {
                infoImg.setVisible(false);
            }

            return;
        }
        if (infoImg == null) {
            infoImg = new Image(page);
            infoImg.setSize(15, 15);
            infoImg.setPosition(140, 40);
            infoImg.setDrawableResource(R.drawable.reddot);
            infoImg.setVisible(true);
            addActor(infoImg);
        }

        infoImg.setVisible(true);

    }


    @Override
    public String getName() {
        return name;
    }
}
