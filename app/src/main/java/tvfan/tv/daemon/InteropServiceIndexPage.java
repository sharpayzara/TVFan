package tvfan.tv.daemon;
import tvfan.tv.App;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * Generates the demo HTML page which is served at http://localhost:8080/
 */
public final class InteropServiceIndexPage {

//	private static final String	NEWLINE			= "\r\n";
	public static final String	ENCODING		= "UTF-8";
	public static final String	HTMLFileName	= "HTML";

	/*
	 * public static ByteBuf getContent(String webSocketLocation) { return
	 * Unpooled .copiedBuffer( "<html><head><title>CIBN</title>" + NEWLINE +
	 * "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0\"></head>"
	 * + NEWLINE + "<body leftmargin=\"0\" topmargin=\"0\">" + NEWLINE +
	 * "<script type=\"text/javascript\">" + NEWLINE + "var socket;" + NEWLINE +
	 * "if (!window.WebSocket) {" + NEWLINE +
	 * "  window.WebSocket = window.MozWebSocket;" + NEWLINE + '}' + NEWLINE +
	 * "if (window.WebSocket) {" + NEWLINE + "  socket = new WebSocket(\"" +
	 * webSocketLocation + "\");" + NEWLINE +
	 * "  socket.onmessage = function(event) {" + NEWLINE +
	 * "    var ta = document.getElementById('responseText');" + NEWLINE +
	 * "    ta.value = ta.value + '\\n' + event.data" + NEWLINE + "  };" +
	 * NEWLINE + "  socket.onopen = function(event) {" + NEWLINE +
	 * "    var ta = document.getElementById('responseText');" + NEWLINE +
	 * "    ta.value = \"Web Socket opened!\";" + NEWLINE + "  };" + NEWLINE +
	 * "  socket.onclose = function(event) {" + NEWLINE +
	 * "    var ta = document.getElementById('responseText');" + NEWLINE +
	 * "    ta.value = ta.value + \"Web Socket closed\"; " + NEWLINE + "  };" +
	 * NEWLINE + "} else {" + NEWLINE +
	 * "  alert(\"Your browser does not support Web Socket.\");" + NEWLINE + '}'
	 * + NEWLINE + NEWLINE + "function send(message) {" + NEWLINE +
	 * "  if (!window.WebSocket) { return; }" + NEWLINE +
	 * "  if (socket.readyState == WebSocket.OPEN) {" + NEWLINE +
	 * "    socket.send(message);" + NEWLINE + "  } else {" + NEWLINE +
	 * "    alert(\"The socket is not open.\");" + NEWLINE + "  }" + NEWLINE +
	 * '}' + NEWLINE + "</script>" + NEWLINE +
	 * "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\" width=\"100%\" height=\"100%\" style=\"position:absolute;font-size:xx-large;text-align:center;\">"
	 * + NEWLINE +
	 * "<tr><td></td><td onclick=\"javascript:send(19)\">UP</td><td></td></tr>"
	 * + NEWLINE +
	 * "<tr><td onclick=\"javascript:send(21)\">LEFT</td><td onclick=\"javascript:send(66)\">OK</td><td onclick=\"javascript:send(22)\">RIGHT</td></tr>"
	 * + NEWLINE +
	 * "<tr><td>MENU</td><td onclick=\"javascript:send(20)\">DOWN</td><td onclick=\"javascript:send(4)\">RETURN</td></tr></table>"
	 * + NEWLINE +
	 * "<textarea id=\"responseText\" style=\"width:500px;height:300px;display:none;\"></textarea>"
	 * + NEWLINE + "</body>" + NEWLINE + "</html>" + NEWLINE,
	 * CharsetUtil.US_ASCII); }
	 */
	public static ByteBuf getContent(String webSocketLocation) {
		return Unpooled.copiedBuffer(getFromAssets(HTMLFileName).replace("_?_", webSocketLocation),
				CharsetUtil.UTF_8);
	}

	private InteropServiceIndexPage() {
		// Unused
	}

	public static String getFromAssets(String fileName) {
		return App.AppgetControlFromAssets();
	}

}
