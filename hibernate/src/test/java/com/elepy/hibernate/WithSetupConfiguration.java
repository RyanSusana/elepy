package com.elepy.hibernate;


import com.elepy.Configuration;
import com.elepy.ElepyPostConfiguration;
import com.elepy.ElepyPreConfiguration;
import com.elepy.EventHandler;

public class WithSetupConfiguration implements Configuration {
    private final EventHandler configPre;
    private final Configuration postConf;
    private final EventHandler teardown;

    public WithSetupConfiguration(EventHandler configPre, Configuration postConf, EventHandler teardown) {
        this.configPre = configPre;
        this.postConf = postConf;
        this.teardown = teardown;
    }

    public WithSetupConfiguration(EventHandler configPre, Configuration postConf) {
        this(configPre, postConf, () -> {
        });
    }

    @Override
    public void before(ElepyPreConfiguration elepy) {
        configPre.handle();
        postConf.before(elepy);

    }

    @Override
    public void after(ElepyPostConfiguration elepy) {
        postConf.after(elepy);
        elepy.onStop(teardown);
    }

}
