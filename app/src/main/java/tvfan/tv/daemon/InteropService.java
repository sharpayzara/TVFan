package tvfan.tv.daemon;

import tvfan.tv.lib.Lg;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class InteropService extends Service {

	private static final String TAG = "TVFAN.EPG.InteropService";

    static final boolean SSL = false;//System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SSL? "8443" : "8080"));

	@Override
	public IBinder onBind(Intent arg0) {
		Lg.i(TAG, "onBind");
		return null;
	}

    @Override
    public void onCreate() {
        Lg.i(TAG, "onCreate");
		super.onCreate();
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					startServermain();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}}).start();
//		new Handler().post(new Runnable(){
//			@Override
//			public void run() {
//				try {
//					startServermain();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}});
    }

    @Override
    public void onDestroy() {
        Lg.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Lg.i(TAG, "onStart");
    }

    public void startServermain() throws Exception {

    	EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
//             .handler(new LoggingHandler(LogLevel.ERROR))
             .childHandler(new InteropServiceInitializer());

            Channel ch = b.bind(PORT).sync().channel();

//            System.out.println("Open your web browser and navigate to " +
//                    (SSL? "https" : "http") + "://127.0.0.1:" + PORT + '/');

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

	public static int getPort() {
		return PORT;
	}

}
