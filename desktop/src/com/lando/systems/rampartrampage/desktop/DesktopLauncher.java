package com.lando.systems.rampartrampage.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.lando.systems.rampartrampage.Const;
import com.lando.systems.rampartrampage.RampartRampage;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.x = Const.window_x;
        config.y = Const.window_y;
        config.width = Const.viewport_width;
        config.height = Const.viewport_height;
        config.title = Const.title + " (" + Const.version + ")";
        new LwjglApplication(new RampartRampage(), config);
	}
}
