package tvfan.tv;

/**
 * 全局常量
 **/
final public class AppGlobalConsts {
	/**
	 * APP设计分辨率，宽度
	 **/
	public final static int APP_WIDTH = 1920;

	/**
	 * APP设计分辨率，高度
	 **/
	public final static int APP_HEIGHT = 1080;

	/**
	 * 终端管理服务（TMS）入口地址
	 **/
	// public final static String TMS_URL =
	// "http://tms.ott.cibntv.net/userCenter/tms";
	// public final static String TMS_URL =
	// "http://172.16.11.246:8081/userCenter/tms/v40";
	// public final static String TMS_URL =
	// "http://114.247.94.141:8081/userCenter/tms/v40";//246外网地址
	// public final static String TMS_URL =
	// "http://172.16.11.134:8081/userCenter/tms/v40";
	// public final static String TMS_URL =
	// "http://114.247.94.15:8081/userCenter/tms/v40"; //134外网ip
	// public final static String TMS_URL =
	// "http://114.247.94.15:8081/userCenter/tms/v40";
	// public final static String TMS_URL =
	// "http://s.tms.ott.cibntv.net/userCenter/tms/v40";
//	public final static String TMS_URL = "http://172.16.40.88:8480/userCenter/tms/v40";
	public final static String TMS_URL ="http://tv.tvfan.cn:8080/userCenter/tms/v40";
	public static final String TVFAN_SERVER = "http://118.244.236.116:8081/clientProcess.do";

	/**
	 * 渠道ID ,00940000000000000000000000000012,009408624668942001003
	 **/
	public static String CHANNELS_ID = "00940000000000000000000000000012";

	public static String VERSION = "1.1";

	/**
	 * P2P控制锁
	 */
	public static int P2PLOCKED = 0;
	
	public final static String CHANNELS_FILENAME = "channel.cfg";
	/**
	 * 焦点放大比率
	 **/
	public final static float FOCUSSCALE = 0.1f;

	/**
	 * 图片圆角切度
	 **/
	public final static int CUTLENGTH = 10;

	/**
	 * 本地消息过滤字符串
	 **/
	public static enum LOCAL_MSG_FILTER {

		/**
		 * 通知显示
		 **/
		NOTICE_DISPLAY,

		/**
		 * EPG版本更新
		 **/
		EPG_UPDATE,

		/**
		 * EPG启动画面
		 **/
		EPG_BOOT_IMAGE_UPDATE,

		/**
		 * EPG界面刷新
		 **/
		EPG_REFRESH,

		/**
		 * 写日志
		 **/
		LOG_WRITE,

		/**
		 * 网络状态改变
		 **/
		NET_CHANGED,
		/**
		 * Wifi状态改变
		 **/
		RSSI_CHANGED,

		/**
		 * 支付结果
		 **/
		PAY_RESULT,

		/**
		 * 用户绑定
		 **/
		USER_BIND,

		/**
		 * 时间更新
		 **/
		DATETIME_UPDATE,

		/**
		 * 列表筛选
		 */
		LIST_FILTER,

		/**
		 * 下载服务
		 */
		DOWNLOAD_SERVICE,

		/**
		 * 背景更换
		 */
		BACKGROUND_CHANGE,

		/**
		 * 用户头像更换
		 */
		USER_IMAGE_CHANGE,

		/**
		 * 有新消息送达
		 **/
		NEW_MSG_ARRIVED
	};

	/**
	 * Android -> GDX, ActionName in Intent
	 * 
	 * @literal Example: intent.putExtra(AppGlobalConsts.INTENT_ACTION_NAME,
	 *          BasePage.ACTION_NAME.OPEN_DETAIL.name());
	 **/
	public static final String INTENT_ACTION_NAME = "actName";

	/**
	 * 全局日志服务，以本地消息发送给LogService。<br />
	 * intent.putExtra(AppGlobalConsts.INTENT_LOG_CMD_NAME,
	 * AppGlobalConsts.LOG_CMD.PLAY_START.name());
	 **/
	public static final String INTENT_LOG_CMD_NAME = "logName";

	/**
	 * 全局日志服务，http get参数 <br />
	 * 参数值需要HttpUrlEncode <br />
	 * intent.putExtra(AppGlobalConsts.INTENT_LOG_PARAM, "a=1&b=20%");
	 **/
	public static final String INTENT_LOG_PARAM = "logParam";

	/**
	 * 本地消息内容属性名 <br />
	 * 通常用于推消息的转发<br />
	 * intent.putExtra(AppGlobalConsts.INTENT_MSG_PARAM, "{...}");
	 **/
	public static final String INTENT_MSG_PARAM = "msgParam";

	/**
	 * 日志服务消息 在LogService.java里统一接收处理
	 **/
	public static enum LOG_CMD {
		/**
		 * 开始播放
		 **/
		PLAY_START,

		/**
		 * 结束播放
		 **/
		PLAY_END,
		
		/**
		 * 直播开始播放
		 */
		LIVEPLAY_START,

		/**
		 * 播放出错
		 */
		PLAY_ERROR,

		/**
		 * 详情页前导路径
		 **/
		DETAIL_PAGE_PATH,

		/**
		 * EPG启动
		 **/
		EPG_LAUNCH,

		/**
		 * 友盟统计 PAGE_START
		 **/
		UMENG_PAGE_START,

		/**
		 * 友盟统计 PAGE_END
		 **/
		UMENG_PAGE_END

	};

	/**
	 * 本地持久化配置项名称，包括：背景图片ID、升级就绪标志、当前用户、默认用户等 <br />
	 * 应用在LocalData类，举例：<br />
	 * setKV(AppGlobalConsts.PERSIST_NAMES.CURRENT_USER.name(), "123"); <br />
	 * getKV(AppGlobalConsts.PERSIST_NAMES.CURRENT_USER.name());
	 **/
	public static enum PERSIST_NAMES {
		/**
		 * 背景图片ID(int), 例如：R.drawable.portal_bg
		 **/
		BACKGROUND_IMAGE,

		/**
		 * 当前登录用户(user_id)，来自微信绑定
		 **/
		CURRENT_USER,

		/**
		 * 初始化默认用户(user_id)，来自设备登录接口
		 **/
		DEFAULT_USER,

		/**
		 * 首页默认栏目
		 **/
		PORTAL_DEFAULT_ITEM,

		/**
		 * 清晰度默认值 2：超清 1：高清 0：标清
		 **/
		VIDEO_DEFAULT_DEFINITION,

		/**
		 * 是否开机启动 0,否  1:是2016.3.10
		 */
		POWERBOOT,

		/**
		 * 升级就绪，1：就绪；0：未就绪
		 **/
		READY_FOR_UPDATE,

		/**
		 * 即将更新版本号
		 **/
		UPDATE_VERSION_CODE,

		/**
		 * 是否是强制升级
		 */
		UPGRADE_PATTERN,

		/**
		 * 升级信息
		 */
		UPDATE_MESSAGE,

		/**
		 * 下载后的apk的MD5
		 */
		UPDATE_MD5,

		/**
		 * 下载后的apk版本号
		 */
		UPDATE_VERSION_NAME,

		/**
		 * apk下载后的地址
		 */
		UPDATE_ADRESS,

		/**
		 * 开机图片的本地路径(String), 例如：
		 **/
		BOOT_IMAGE,

		/**
		 * 开机图片停留时长(毫秒), 例如：6000
		 **/
		BOOT_TIMESPAN,

		/**
		 * 指南已显示次数
		 **/
		GUIDE_SHOW_TIMES,
		/**
		 * 直播界面默认是否收藏
		 */
		LIVEPLAY_FAV_DEFAILT,
		/**
		 * 直播界面默认显示比例
		 */
		LIVEPLAY_RADIO_DEFAILT,
		/**
		 * 直播界面默认左右键功能
		 */
		LIVEPLAY_LEFTRIGHRT_DEFAILT,
		/**
		 * 直播界面默认上下键功能
		 */
		LIVEPLAY_TOPBOTTOM_DEFAILT,
		/**
		 * 进入直播帮助界面显示次数
		 */
		LIVEPLAY_ENTERDIALOG_SHOW,

		/**
		 * 	屏保出现时间
 		 */
		SCREEN_PROTECT_TIME_INDEX
	};

	public final static String DOWNLOAD_START = "start"; // 下载状态，开始下载.
	public final static String DOWNLOAD_LOADING = "loading"; // 下载状态，正在下载.
	public final static String DOWNLOAD_FAILED = "failed"; // 下载状态，下载失败.
	public final static String DOWNLOAD_SUCCESS = "success"; // 下载状态，下载完成.

	public final static String DEV_LOGIN_TIME = "_DEV_LOGIN_TIME_";
	public final static String DEV_LOGIN_INFO = "_DEV_LOGIN_INFO_";

	public static int[] timeArr = {0, 2, 4, 8, 12, 30}; // 多长时间无操作后，显示屏保；0表示关闭

}
