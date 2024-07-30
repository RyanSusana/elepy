package com.elepy.hibernate;


import com.elepy.configuration.Configuration;
import com.elepy.configuration.ElepyPostConfiguration;
import com.elepy.configuration.ElepyPreConfiguration;
import com.elepy.configuration.EventHandler;

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
    public void preConfig(ElepyPreConfiguration elepy) {
        configPre.handle();
        postConf.preConfig(elepy);

    }

    @Override
    public void afterPreConfig(ElepyPreConfiguration elepy) {
        postConf.afterPreConfig(elepy);
    }

    @Override
    public void postConfig(ElepyPostConfiguration elepy) {
        postConf.postConfig(elepy);
    }

}
