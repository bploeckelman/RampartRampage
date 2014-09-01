package com.lando.systems.rampartrampage.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.lando.systems.rampartrampage.Const;
import com.lando.systems.rampartrampage.RampartRampage;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Const.viewport_width, Const.viewport_height);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new RampartRampage();
        }
}